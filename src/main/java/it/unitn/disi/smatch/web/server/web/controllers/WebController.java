package it.unitn.disi.smatch.web.server.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Handles web page requests.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
@Controller
public class WebController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home() {
        return "index";
    }

    @RequestMapping(value = "apidocs", method = RequestMethod.GET)
    public RedirectView apidocs() {
        return new RedirectView("/apidocs/", true);
    }

    @RequestMapping(value = "apidocs/", method = RequestMethod.GET)
    public ModelAndView apidocsHome() {
        return new ModelAndView("apidocs");
    }
}