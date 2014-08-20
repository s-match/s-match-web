package main.java.it.unitn.disi.smatch.web.server.api.handlers;

import main.java.it.unitn.disi.smatch.web.shared.model.exceptions.ExceptionDetails;
import main.java.it.unitn.disi.smatch.web.shared.model.exceptions.HTTPResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Default {@code ExceptionDetailsResolver} implementation that converts discovered Exceptions to
 * {@link it.unitn.disi.smatch.web.shared.model.exceptions.ExceptionDetails} instances.
 *
 * @author Les Hazlewood
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class DefaultExceptionDetailsResolver implements ExceptionDetailsResolver, MessageSourceAware, InitializingBean {

    public static final String DEFAULT_EXCEPTION_MESSAGE_VALUE = "_exmsg";
    public static final String EXCEPTION_CLASS_PLACEHOLDER = "_exclass";

    private static final Logger log = LoggerFactory.getLogger(DefaultExceptionDetailsResolver.class);

    private Map<String, ExceptionDetails> exceptionMappings = Collections.emptyMap();

    private Map<String, String> exceptionMappingDefinitions = Collections.emptyMap();

    private MessageSource messageSource;
    private LocaleResolver localeResolver;

    private String defaultExplanationMessage;
    private String defaultWhatToDoMessage;

    public DefaultExceptionDetailsResolver() {
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setLocaleResolver(LocaleResolver resolver) {
        this.localeResolver = resolver;
    }

    public void setExceptionMappingDefinitions(Map<String, String> exceptionMappingDefinitions) {
        this.exceptionMappingDefinitions = exceptionMappingDefinitions;
    }

    public String getDefaultExplanationMessage() {
        return defaultExplanationMessage;
    }

    public void setDefaultExplanationMessage(String defaultExplanationMessage) {
        this.defaultExplanationMessage = defaultExplanationMessage;
    }

    public String getDefaultWhatToDoMessage() {
        return defaultWhatToDoMessage;
    }

    public void setDefaultWhatToDoMessage(String defaultWhatToDoMessage) {
        this.defaultWhatToDoMessage = defaultWhatToDoMessage;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //populate with some defaults:
        Map<String, String> definitions = createDefaultExceptionMappingDefinitions();

        //add in user-specified mappings (will override defaults as necessary):
        if (this.exceptionMappingDefinitions != null && !this.exceptionMappingDefinitions.isEmpty()) {
            definitions.putAll(this.exceptionMappingDefinitions);
        }

        this.exceptionMappings = toExceptionDetailsMap(definitions);
    }

    @Override
    public ExceptionDetails resolveError(ServletWebRequest request, Object handler, Exception ex) {
        ExceptionDetails template = getExceptionDetailsTemplate(ex);
        ExceptionDetails result = new ExceptionDetails();
        if (null != template) {
            result.setStatus(getStatusValue(template, request, ex));
            result.setErrorMessage(getMessage(template, request, ex));
            result.setExplanationMessage(getExplanationMessage(template, request, ex));
            result.setWhatToDoMessage(getWhatToDoMessage(template, request, ex));
            if (null == template.getExceptionClass()) {
                result.setExceptionClass(ex.getClass().getName());
            } else {
                result.setExceptionClass(template.getExceptionClass());
            }
        }

        return result;
    }

    private Map<String, String> createDefaultExceptionMappingDefinitions() {
        Map<String, String> m = new LinkedHashMap<String, String>();

        // 400
        applyDef(m, HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST);
        applyDef(m, MissingServletRequestParameterException.class, HttpStatus.BAD_REQUEST);
        applyDef(m, TypeMismatchException.class, HttpStatus.BAD_REQUEST);
        applyDef(m, "javax.validation.ValidationException", HttpStatus.BAD_REQUEST);

        // 404
        applyDef(m, NoSuchRequestHandlingMethodException.class, HttpStatus.NOT_FOUND);
        applyDef(m, "org.hibernate.ObjectNotFoundException", HttpStatus.NOT_FOUND);

        // 405
        applyDef(m, HttpRequestMethodNotSupportedException.class, HttpStatus.METHOD_NOT_ALLOWED);

        // 406
        applyDef(m, HttpMediaTypeNotAcceptableException.class, HttpStatus.NOT_ACCEPTABLE);

        // 409
        //can't use the class directly here as it may not be an available dependency:
        applyDef(m, "org.springframework.dao.DataIntegrityViolationException", HttpStatus.CONFLICT);

        // 415
        applyDef(m, HttpMediaTypeNotSupportedException.class, HttpStatus.UNSUPPORTED_MEDIA_TYPE);

        return m;
    }

    private void applyDef(Map<String, String> m, Class clazz, HttpStatus status) {
        applyDef(m, clazz.getName(), status);
    }

    private void applyDef(Map<String, String> m, String key, HttpStatus status) {
        m.put(key, definitionFor(status));
    }

    private String definitionFor(HttpStatus status) {
        return "status=" + status.value() + ", msg=" + DEFAULT_EXCEPTION_MESSAGE_VALUE;
    }

    private int getStatusValue(ExceptionDetails template, ServletWebRequest request, Exception ex) {
        if (ex.getClass().isAnnotationPresent(HTTPResponseStatus.class)) {
            return ex.getClass().getAnnotation(HTTPResponseStatus.class).value();
        }
        return template.getStatus();
    }

    private String getMessage(ExceptionDetails template, ServletWebRequest request, Exception ex) {
        return getMessage(template.getErrorMessage(), request, ex);
    }

    /**
     * Returns the response status message to return to the client, or {@code null} if no
     * status message should be returned.
     *
     * @return the response status message to return to the client, or {@code null} if no
     *         status message should be returned.
     */
    private String getMessage(String msg, ServletWebRequest webRequest, Exception ex) {
        if ("null".equalsIgnoreCase(msg) || "off".equalsIgnoreCase(msg)) {
            return null;
        }
        if (DEFAULT_EXCEPTION_MESSAGE_VALUE.equalsIgnoreCase(msg)) {
            msg = ex.getMessage();
        }
        if (null != messageSource) {
            Locale locale = null;
            if (null != localeResolver) {
                locale = localeResolver.resolveLocale(webRequest.getRequest());
            }
            msg = messageSource.getMessage(msg, null, msg, locale);
            msg = messageSource.getMessage("error." + ex.getClass().getSimpleName() + ".message", null, msg, locale);
        }

        return msg;
    }

    private String getWhatToDoMessage(ExceptionDetails template, ServletWebRequest request, Exception ex) {
        String msg = template.getWhatToDoMessage();
        if ("null".equalsIgnoreCase(msg) || "off".equalsIgnoreCase(msg)) {
            return null;
        }
        if (null == msg) {
            msg = getDefaultWhatToDoMessage();
        }
        if (null != messageSource) {
            Locale locale = null;
            if (null != localeResolver) {
                locale = localeResolver.resolveLocale(request.getRequest());
            }
            msg = messageSource.getMessage(msg, null, msg, locale);
            msg = messageSource.getMessage("error." + ex.getClass().getSimpleName() + ".whattodo", null, msg, locale);
        }

        return msg;
    }

    private String getExplanationMessage(ExceptionDetails template, ServletWebRequest request, Exception ex) {
        String msg = template.getExplanationMessage();
        if ("null".equalsIgnoreCase(msg) || "off".equalsIgnoreCase(msg)) {
            return null;
        }
        if (null == msg) {
            msg = getDefaultExplanationMessage();
        }
        if (null != messageSource) {
            Locale locale = null;
            if (null != localeResolver) {
                locale = localeResolver.resolveLocale(request.getRequest());
            }
            msg = messageSource.getMessage(msg, null, msg, locale);
            msg = messageSource.getMessage("error." + ex.getClass().getSimpleName() + ".explanation", null, msg, locale);
        }

        return msg;
    }


    /**
     * Returns the config-time 'template' ExceptionDetails instance configured for the specified Exception, or
     * {@code null} if a match was not found.
     * <p/>
     * The config-time template is used as the basis for the ExceptionDetails constructed at runtime.
     *
     * @param ex exception
     * @return the template to use for the ExceptionDetails instance to be constructed.
     */
    private ExceptionDetails getExceptionDetailsTemplate(Exception ex) {
        Map<String, ExceptionDetails> mappings = this.exceptionMappings;
        if (CollectionUtils.isEmpty(mappings)) {
            return null;
        }
        ExceptionDetails template = null;
        String dominantMapping = null;
        int deepest = Integer.MAX_VALUE;
        for (Map.Entry<String, ExceptionDetails> entry : mappings.entrySet()) {
            String key = entry.getKey();
            int depth = getDepth(key, ex);
            if (depth >= 0 && depth < deepest) {
                deepest = depth;
                dominantMapping = key;
                template = entry.getValue();
            }
        }
        if (template != null && log.isDebugEnabled()) {
            log.debug("Resolving to ExceptionDetails template '" + template + "' for exception of type [" + ex.getClass().getName() +
                    "], based on exception mapping [" + dominantMapping + "]");
        }
        return template;
    }

    /**
     * Return the depth to the superclass matching.
     * <p>0 means ex matches exactly. Returns -1 if there's no match.
     * Otherwise, returns depth. Lowest depth wins.
     */
    private int getDepth(String exceptionMapping, Exception ex) {
        return getDepth(exceptionMapping, ex.getClass(), 0);
    }

    private int getDepth(String exceptionMapping, Class exceptionClass, int depth) {
        if (exceptionClass.getName().contains(exceptionMapping)) {
            // Found it!
            return depth;
        }
        // If we've gone as far as we can go and haven't found it...
        if (exceptionClass.equals(Throwable.class)) {
            return -1;
        }
        return getDepth(exceptionMapping, exceptionClass.getSuperclass(), depth + 1);
    }


    private Map<String, ExceptionDetails> toExceptionDetailsMap(Map<String, String> smap) {
        if (CollectionUtils.isEmpty(smap)) {
            return Collections.emptyMap();
        }

        Map<String, ExceptionDetails> map = new LinkedHashMap<String, ExceptionDetails>(smap.size());

        for (Map.Entry<String, String> entry : smap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            ExceptionDetails template = toExceptionDetails(value);
            map.put(key, template);
        }

        return map;
    }

    private ExceptionDetails toExceptionDetails(String exceptionConfig) {
        String[] values = StringUtils.commaDelimitedListToStringArray(exceptionConfig);
        if (values == null || values.length == 0) {
            throw new IllegalStateException("Invalid config mapping.  Exception names must map to a string configuration.");
        }

        ExceptionDetails result = new ExceptionDetails();

        for (String value : values) {
            String trimmedVal = StringUtils.trimWhitespace(value);

            //check to see if the value is an explicitly named key/value pair:
            String[] pair = StringUtils.split(trimmedVal, "=");
            if (pair != null) {
                //explicit attribute set:
                String pairKey = StringUtils.trimWhitespace(pair[0]);
                if (!StringUtils.hasText(pairKey)) {
                    pairKey = null;
                }
                String pairValue = StringUtils.trimWhitespace(pair[1]);
                if (!StringUtils.hasText(pairValue)) {
                    pairValue = null;
                }
                if ("status".equalsIgnoreCase(pairKey)) {
                    result.setStatus(getRequiredInt(pairKey, pairValue));
                } else if ("msg".equalsIgnoreCase(pairKey)) {
                    result.setErrorMessage(pairValue);
                } else if ("emsg".equalsIgnoreCase(pairKey)) {
                    result.setExplanationMessage(pairValue);
                } else if ("wmsg".equalsIgnoreCase(pairKey)) {
                    result.setWhatToDoMessage(pairValue);
                } else if ("target".equalsIgnoreCase(pairKey)) {
                    result.setExceptionClass(pairValue);
                }
            }
        }

        return result;
    }

    private static int getRequiredInt(String key, String value) {
        try {
            int anInt = Integer.valueOf(value);
            return Math.max(-1, anInt);
        } catch (NumberFormatException e) {
            String msg = "Configuration element '" + key + "' requires an integer value.  The value " +
                    "specified: " + value;
            throw new IllegalArgumentException(msg, e);
        }
    }
}
