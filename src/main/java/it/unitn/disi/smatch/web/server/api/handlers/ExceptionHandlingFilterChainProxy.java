package main.java.it.unitn.disi.smatch.web.server.api.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ExceptionHandlingFilterChainProxy extends FilterChainProxy implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlingFilterChainProxy.class);

    private HandlerExceptionResolver exceptionResolver;

    public ExceptionHandlingFilterChainProxy() {
    }

    public ExceptionHandlingFilterChainProxy(SecurityFilterChain chain) {
        super(chain);
    }

    public ExceptionHandlingFilterChainProxy(List<SecurityFilterChain> filterChains) {
        super(filterChains);
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.notNull(exceptionResolver, "An exception resolver is required.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            super.doFilter(request, response, chain);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            exceptionResolver.resolveException((HttpServletRequest) request, (HttpServletResponse) response, null, e);
        }
    }

    public HandlerExceptionResolver getExceptionResolver() {
        return exceptionResolver;
    }

    public void setExceptionResolver(HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }
}