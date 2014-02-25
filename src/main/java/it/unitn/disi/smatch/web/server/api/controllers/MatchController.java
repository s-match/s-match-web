package it.unitn.disi.smatch.web.server.api.controllers;

import it.unitn.disi.common.components.ConfigurableException;
import it.unitn.disi.smatch.SMatchException;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.web.server.api.IMatchAPI;
import it.unitn.disi.smatch.web.shared.model.trees.BaseContextPair;
import it.unitn.disi.smatch.web.server.api.service.IMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
@Controller
public class MatchController implements IMatchAPI {

    @Autowired
    private IMatchService matchService;

    @Override
    @ResponseBody
    @RequestMapping(value = IMatchAPI.MATCH, method = RequestMethod.POST)
    public IContextMapping<INode> match(@RequestBody BaseContextPair contexts) throws SMatchException {
        return matchService.match(contexts);
    }

    // TODO AA remove demo method
    @ResponseBody
    @RequestMapping(value = "ctx", method = RequestMethod.GET)
    public String ctx() throws ConfigurableException, IOException {
        return "test";
    }

}
