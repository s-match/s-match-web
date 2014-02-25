package it.unitn.disi.smatch.web.server.api.service.impl;

import it.unitn.disi.smatch.SMatchException;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.web.shared.model.trees.BaseContextPair;
import it.unitn.disi.smatch.web.server.api.service.IMatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
@Service
public class MatchService implements IMatchService {

    private static final Logger log = LoggerFactory.getLogger(MatchService.class);

    @Override
    public IContextMapping<INode> match(BaseContextPair contexts) throws SMatchException {
        throw new SMatchException("test");
        //return null;
    }
}
