package it.unitn.disi.smatch.web.server.api.controllers;

import it.unitn.disi.smatch.web.server.api.service.IMatchService;
import it.unitn.disi.smatch.web.shared.api.IMatchAPI;
import it.unitn.disi.smatch.web.shared.model.exceptions.SMatchWebException;
import it.unitn.disi.smatch.web.shared.model.tasks.MatchingTask;
import it.unitn.disi.smatch.web.shared.model.trees.BaseContextPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
@Controller
public class MatchController implements IMatchAPI {

    @Autowired
    private IMatchService matchService;

    @Override
    @ResponseBody
    @RequestMapping(value = IMatchAPI.MATCH, method = RequestMethod.POST, produces = {"text/plain"})
    public String match(@RequestBody BaseContextPair contexts, @PathVariable String mode) throws SMatchWebException {
        return matchService.match(contexts, mode);
    }

    @Override
    @ResponseBody
    @RequestMapping(value = IMatchAPI.TASK_READ, method = RequestMethod.GET)
    public MatchingTask readTask(@PathVariable String taskID) throws SMatchWebException {
        return matchService.readTask(taskID);
    }

    @Override
    @ResponseBody
    @RequestMapping(value = IMatchAPI.DEMO_CONTEXTS, method = RequestMethod.GET)
    public BaseContextPair demoContextPair() {
        return matchService.demoContextPair();
    }
}
