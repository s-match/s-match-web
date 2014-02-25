package it.unitn.disi.smatch.web.server;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) {
        XmlWebApplicationContext rootContext = new XmlWebApplicationContext();
        rootContext.setConfigLocation("classpath:conf/spring/root-context.xml");
        rootContext.setServletContext(container);
        rootContext.setDisplayName("Root Context");

        container.addListener(new ContextLoaderListener(rootContext));

        XmlWebApplicationContext webContext = new XmlWebApplicationContext();
        webContext.setServletContext(container);
        webContext.setDisplayName("Web Context");
        webContext.setParent(rootContext);
        webContext.setConfigLocation("classpath:conf/spring/web-servlet/web-servlet.xml");
        DispatcherServlet webServlet = new DispatcherServlet(webContext);
        ServletRegistration.Dynamic web = container.addServlet("web", webServlet);
        Set<String> mappingConflicts = web.addMapping("/");
        if (!mappingConflicts.isEmpty()) {
            throw new IllegalStateException("'mvcServlet' could not be mapped to '/' due "
                    + "to an existing mapping. This is a known issue under Tomcat versions "
                    + "<= 7.0.14; see https://issues.apache.org/bugzilla/show_bug.cgi?id=51278");
        }

        XmlWebApplicationContext apiContext = new XmlWebApplicationContext();
        apiContext.setServletContext(container);
        apiContext.setDisplayName("API Context");
        apiContext.setParent(rootContext);
        apiContext.setConfigLocation("classpath:conf/spring/api-servlet/api-servlet.xml");
        Servlet apiServlet = new DispatcherServlet(apiContext);
        ServletRegistration.Dynamic api = container.addServlet("api", apiServlet);
        api.addMapping("/webapi/*");

        // common filter chain - provides anonymous authentication for all
        DelegatingFilterProxy commonFilterChainProxy = new DelegatingFilterProxy("commonFilterChainProxy");
        FilterRegistration frCommonFilterChainProxy = container.addFilter("commonFilterChainProxy", commonFilterChainProxy);
        frCommonFilterChainProxy.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), true, "/*");

        // api filter chain
        DelegatingFilterProxy apiFilterChainProxy = new DelegatingFilterProxy("apiFilterChainProxy", apiContext);
        FilterRegistration frApiFilterChainProxy = container.addFilter("apiFilterChainProxy", apiFilterChainProxy);
        frApiFilterChainProxy.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), true, "/webapi/*");
    }
}