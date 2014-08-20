package main.java.it.unitn.disi.smatch.web.server.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
}