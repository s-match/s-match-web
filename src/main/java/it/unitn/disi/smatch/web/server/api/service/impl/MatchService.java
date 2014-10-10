package it.unitn.disi.smatch.web.server.api.service.impl;

import it.unitn.disi.smatch.AsyncMatchManager;
import it.unitn.disi.smatch.IAsyncMatchManager;
import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.Context;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.data.trees.Node;
import it.unitn.disi.smatch.web.server.api.service.IMatchService;
import it.unitn.disi.smatch.web.shared.api.IMatchAPI;
import it.unitn.disi.smatch.web.shared.model.exceptions.*;
import it.unitn.disi.smatch.web.shared.model.mappings.NodesMatrixMapping;
import it.unitn.disi.smatch.web.shared.model.tasks.MatchingTask;
import it.unitn.disi.smatch.web.shared.model.trees.BaseContext;
import it.unitn.disi.smatch.web.shared.model.trees.BaseContextPair;
import it.unitn.disi.smatch.web.shared.model.trees.BaseNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.concurrent.*;

/**
 * S-Match service.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
@Service
public class MatchService implements IMatchService, InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(MatchService.class);

    @Autowired
    @Qualifier(value = "defaultMatcher")
    private IAsyncMatchManager defaultMatcher;

    @Autowired
    @Qualifier(value = "minimalMatcher")
    private IAsyncMatchManager minimalMatcher;

    @Autowired
    @Qualifier(value = "spsmMatcher")
    private IAsyncMatchManager spsmMatcher;

    @Value("${event.threads}")
    private int eventThreads;

    @Value("${task.limit}")
    private int taskLimit;

    @Value("${node.limit}")
    private int nodeLimit;

    @Value("${task.result.timeout}")
    private int taskResultTimeout;

    @Value("${task.timeout}")
    private int taskTimeout;

    private ExecutorService eventExecutor;
    private ExecutorService taskExecutor;
    private ScheduledExecutorService scheduledExecutorService;
    private Semaphore taskLimiter;

    private final Map<String, MatchingTask> matchingResults = new ConcurrentHashMap<>();
    private final Map<String, AsyncTask<IContextMapping<INode>, IMappingElement<INode>>> matchingTasks = new ConcurrentHashMap<>();

    @Override
    public String match(BaseContextPair contexts, final String mode) throws SMatchWebException {
        // check node limit
        if (nodeLimit < contexts.getSourceContext().nodesCount()) {
            throw new SMatchWebNodeLimitException("Source context exceeds node limit [" + nodeLimit + "]: " +
                    contexts.getSourceContext().nodesCount());
        }

        if (nodeLimit < contexts.getTargetContext().nodesCount()) {
            throw new SMatchWebNodeLimitException("Target context exceeds node limit [" + nodeLimit + "]: " +
                    contexts.getTargetContext().nodesCount());
        }

        // check task limit
        if (!taskLimiter.tryAcquire()) {
            throw new SMatchWebTaskLimitException("Task queue exceeds limit [" + taskLimit + "].");
        }

        IAsyncMatchManager mm;
        switch (mode) {
            case MODE_MINIMAL: {
                mm = minimalMatcher;
                break;
            }
            case MODE_SPSM: {
                mm = spsmMatcher;
                break;
            }
            case MODE_DEFAULT: {
                mm = defaultMatcher;
                break;
            }
            default: {
                throw new SMatchWebUnknownMatchingModeException();
            }
        }

        final String taskID = UIDGenerator.getUID();
        final AsyncTask<IContextMapping<INode>, IMappingElement<INode>> matchingTask =
                mm.asyncMatch(convertContext(contexts.getSourceContext()), convertContext(contexts.getTargetContext()));
        matchingTasks.put(taskID, matchingTask);

        final MatchingTask result = new MatchingTask();
        result.setId(taskID);
        result.setSubmitTime(new Date());
        matchingResults.put(taskID, result);

        // event handlers
        matchingTask.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    Long newProgress = (Long) evt.getNewValue();
                    result.setProgress(100 * (newProgress / (double) matchingTask.getTotal()));
                }

                if ("state".equals(evt.getPropertyName())) {
                    AsyncTask.StateValue oldState = (AsyncTask.StateValue) evt.getOldValue();
                    AsyncTask.StateValue newState = (AsyncTask.StateValue) evt.getNewValue();
                    result.setStatus(newState.toString());
                    if (AsyncTask.StateValue.PENDING == oldState && AsyncTask.StateValue.STARTED == newState) {
                        if (log.isInfoEnabled()) {
                            log.info("Starting task [id=" + taskID + "]");
                        }
                        result.setStartTime(new Date());
                    }
                    if (AsyncTask.StateValue.STARTED == oldState && AsyncTask.StateValue.DONE == newState) {
                        result.setCompleteTime(new Date());
                        result.setProgress(100.0);
                        try {
                            result.setResult(convertResult(matchingTask.get()));
                            matchingTasks.remove(result.getId());
                            if (log.isInfoEnabled()) {
                                log.info("Completed task [id=" + taskID + "]. Mapping items: " + result.getResult().size());
                            }
                        } catch (InterruptedException e) {
                            result.setErrorMessage(e.getMessage());
                            if (log.isErrorEnabled()) {
                                log.error("Task interrupted [id=" + taskID + "]: " + e);
                            }
                        } catch (ExecutionException e) {
                            result.setErrorMessage(e.getCause().getMessage());
                            if (log.isErrorEnabled()) {
                                log.error("Task failed [id=" + taskID + "]: " + e.getCause());
                            }
                        }
                    }
                }
            }
        });

        taskExecutor.execute(matchingTask);

        if (log.isInfoEnabled()) {
            log.info("Enqueued task [id=" + taskID + "]. Nodes: " +
                    contexts.getSourceContext().nodesCount() + " x " + contexts.getTargetContext().nodesCount());
        }

        return taskID;
    }

    @Override
    public MatchingTask readTask(String taskID) throws SMatchWebException {
        MatchingTask result = matchingResults.get(taskID);
        if (null == result) {
            throw new SMatchWebTaskNotFoundException("Task not found.");
        } else {
            if (null != result.getCompleteTime()) {
                removeTask(result.getId());
            }
        }
        return result;
    }

    @Override
    @ResponseBody
    @RequestMapping(value = IMatchAPI.DEMO_CONTEXTS, method = RequestMethod.GET)
    public BaseContextPair demoContextPair() {
        BaseContext source = new BaseContext();
        source.createRoot("Courses");
        BaseNode s1 = source.getRoot().createChild("College of Arts and Sciences");
        s1.createChild("Earth and Atmospheric Sciences");
        s1.createChild("Economics");
        s1.createChild("English");
        s1.createChild("Classics");
        s1.createChild("Asian Languages");
        s1.createChild("History");
        s1.createChild("Mathematics");
        s1.createChild("Astronomy");
        s1.createChild("Computer Science");
        s1.createChild("Linguistics");
        BaseNode s2 = source.getRoot().createChild("College of Engineering");
        s2.createChild("Chemical Engineering");
        s2.createChild("Civil and Environmental Engineering");
        s2.createChild("Electrical Computer Engineering");
        s2.createChild("Materials Science and Engineering");
        s2.createChild("Earth and Atmospheric Sciences");

        BaseContext target = new BaseContext();
        target.createRoot("Course");
        BaseNode t1 = target.getRoot().createChild("College of Arts and Sciences");
        t1.createChild("English");
        t1.createChild("Earth Sciences");
        t1.createChild("Computer Science");
        t1.createChild("Economics");
        t1.createChild("Astronomy");
        t1.createChild("Asian Languages");
        t1.createChild("Classics");
        t1.createChild("History");
        t1.createChild("Linguistics");
        t1.createChild("Mathematics");
        t1.createChild("History and Philosophy Science");
        BaseNode t2 = target.getRoot().createChild("College Engineering");
        t2.createChild("Civil and Environmental Engineering");
        t2.createChild("Electrical Engineering");
        t2.createChild("Chemical Engineering");
        t2.createChild("Materials Science and Engineering");

        BaseContextPair contextPair = new BaseContextPair();
        contextPair.setSourceContext(source);
        contextPair.setTargetContext(target);

        return contextPair;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        taskLimiter = new Semaphore(taskLimit);

        // event executor
        ThreadFactory eventThreadFactory = new ThreadFactory() {
            final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

            public Thread newThread(final Runnable r) {
                Thread thread = defaultFactory.newThread(r);
                thread.setName("S-Match-Event-" + thread.getName());
                thread.setDaemon(true);
                return thread;
            }
        };
        eventExecutor = new ThreadPoolExecutor(eventThreads, eventThreads,
                10L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>(),
                eventThreadFactory);
        AsyncMatchManager.setEventExecutor(eventExecutor);

        // task executor
        ThreadFactory taskThreadFactory = new ThreadFactory() {
            final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

            public Thread newThread(final Runnable r) {
                Thread thread = defaultFactory.newThread(r);
                thread.setName("S-Match-Task-" + thread.getName());
                thread.setDaemon(true);
                return thread;
            }
        };
        // a task can use up to 5 threads (peaks at preprocessing)
        taskExecutor = new ThreadPoolExecutor(5 * taskLimit, 5 * taskLimit,
                10L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>(),
                taskThreadFactory);
        AsyncMatchManager.setTaskExecutor(taskExecutor);

        ThreadFactory scheduledThreadFactory = new ThreadFactory() {
            final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

            public Thread newThread(final Runnable r) {
                Thread thread = defaultFactory.newThread(r);
                thread.setName("S-Match-QueueCleaner-" + thread.getName());
                thread.setDaemon(true);
                return thread;
            }
        };
        scheduledExecutorService = Executors.newScheduledThreadPool(1, scheduledThreadFactory);
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                log.debug("Cleaning timed out results and tasks...");
                List<String> expiredResults = new ArrayList<>();
                for (Map.Entry<String, MatchingTask> entry : matchingResults.entrySet()) {
                    MatchingTask mt = entry.getValue();

                    // task timed out - cancel
                    if (mt.getStartTime().before(new Date(System.currentTimeMillis() - taskTimeout))) {
                        AsyncTask<IContextMapping<INode>, IMappingElement<INode>> at = matchingTasks.get(entry.getKey());
                        if (null != at && !at.isCancelled() && !at.isDone()) {
                            at.cancel(true);
                        }
                        mt.setCompleteTime(new Date());
                        mt.setErrorMessage("Task was running longer than " + taskTimeout + " and was cancelled");

                        if (log.isWarnEnabled()) {
                            log.warn("Timed out task [id=" + entry.getKey() + "]");
                        }
                    }

                    // task result expired - remove
                    if (null != mt.getCompleteTime() && mt.getCompleteTime().before(new Date(System.currentTimeMillis() - taskResultTimeout))) {
                        AsyncTask<IContextMapping<INode>, IMappingElement<INode>> at = matchingTasks.get(entry.getKey());
                        if (null != at && !at.isCancelled() && !at.isDone()) {
                            at.cancel(true);
                        }
                        expiredResults.add(entry.getKey());
                        if (log.isWarnEnabled()) {
                            log.warn("Removing timed out task result [id=" + entry.getKey() + "]");
                        }
                    }
                }

                for (String taskID : expiredResults) {
                    removeTask(taskID);
                }
            }
        }, 1L, 1L, TimeUnit.MINUTES);
    }

    private void removeTask(String taskID) {
        matchingResults.remove(taskID);
        matchingTasks.remove(taskID);
        taskLimiter.release();
    }

    @Override
    public void destroy() throws Exception {
        if (null != scheduledExecutorService) {
            scheduledExecutorService.shutdown();
        }
        if (null != taskExecutor) {
            taskExecutor.shutdown();
        }
        if (null != eventExecutor) {
            eventExecutor.shutdown();
        }
    }

    public static ExecutorService newMatcherThreadPool(int nThreads) {
        ThreadFactory matcherThreadFactory = new ThreadFactory() {
            final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

            public Thread newThread(final Runnable r) {
                Thread thread = defaultFactory.newThread(r);
                thread.setName("S-Match-Matcher-" + thread.getName());
                thread.setDaemon(true);
                return thread;
            }
        };

        return new ThreadPoolExecutor(4, nThreads,
                10L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>(),
                matcherThreadFactory);
    }

    private IContext convertContext(BaseContext sourceContext) {
        IContext result = new Context();
        INode root = result.createRoot(sourceContext.getRoot().getName());
        root.nodeData().setId(sourceContext.getRoot().getId());
        copyNodes(sourceContext.getRoot(), result.getRoot());
        return result;
    }

    private void copyNodes(BaseNode source, INode target) {
        if (0 < source.getChildCount()) {
            List<INode> children = new ArrayList<>(source.getChildCount());
            for (Iterator<BaseNode> i = source.childrenIterator(); i.hasNext(); ) {
                BaseNode sourceChild = i.next();
                INode targetChild = new Node(sourceChild.getName());
                targetChild.setParent(target);
                targetChild.nodeData().setId(sourceChild.getId());
                children.add(targetChild);
                copyNodes(sourceChild, targetChild);
            }
            target.setChildren(children);
        }
    }

    private NodesMatrixMapping convertResult(IContextMapping<INode> mapping) {
        it.unitn.disi.smatch.data.mappings.NodesMatrixMapping.indexContext(mapping.getSourceContext());
        it.unitn.disi.smatch.data.mappings.NodesMatrixMapping.indexContext(mapping.getTargetContext());
        NodesMatrixMapping result = new NodesMatrixMapping(
                convertContext(mapping.getSourceContext()),
                convertContext(mapping.getTargetContext()));
        for (IMappingElement<INode> e : mapping) {
            result.setRelation(e.getSource().getIndex(), e.getTarget().getIndex(), e.getRelation());
        }
        return result;
    }

    private BaseContext convertContext(IContext sourceContext) {
        BaseContext result = new BaseContext();
        BaseNode root = result.createRoot(sourceContext.getRoot().nodeData().getName());
        root.setId(sourceContext.getRoot().nodeData().getId());
        root.setIndex(sourceContext.getRoot().getIndex());
        copyNodes(sourceContext.getRoot(), result.getRoot());
        return result;
    }

    private void copyNodes(INode source, BaseNode target) {
        if (0 < source.getChildCount()) {
            List<BaseNode> children = new ArrayList<>(source.getChildCount());
            for (Iterator<INode> i = source.childrenIterator(); i.hasNext(); ) {
                INode sourceChild = i.next();
                BaseNode targetChild = new BaseNode(sourceChild.nodeData().getName());
                targetChild.setId(sourceChild.nodeData().getId());
                targetChild.setIndex(sourceChild.getIndex());
                targetChild.setParent(target);
                children.add(targetChild);
                copyNodes(sourceChild, targetChild);
            }
            target.setChildren(children);
        }
    }
}