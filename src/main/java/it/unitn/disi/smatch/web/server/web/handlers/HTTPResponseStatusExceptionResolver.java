package main.java.it.unitn.disi.smatch.web.server.web.handlers;

import main.java.it.unitn.disi.smatch.web.shared.model.exceptions.HTTPResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HTTPResponseStatusExceptionResolver extends AbstractHandlerExceptionResolver {

    private static final Logger log = LoggerFactory.getLogger(HTTPResponseStatusExceptionResolver.class);

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
                                              Object handler, Exception ex) {

        HTTPResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), HTTPResponseStatus.class);
        if (null != responseStatus) {
            try {
                response.sendError(responseStatus.value(), ex.getMessage());
                return new ModelAndView();
            } catch (Exception resolveEx) {
                log.warn("Handling of HTTPResponseStatus resulted in Exception", resolveEx);
            }
        }
        return null;
    }

}
