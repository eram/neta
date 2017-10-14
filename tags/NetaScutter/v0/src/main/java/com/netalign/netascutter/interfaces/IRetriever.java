package com.netalign.netascutter.interfaces;

import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import java.net.*;
import java.util.Collection;

/**
 * The <code>IRetriever</code> interface has the relevant methods for a Scutter (RDF Crawler) retriever:
 * <li>May run as a Thread</li>
 * <li>Works on one URL, set by user</li>
 * <li>Does not throw exceptions - all exceptions are logged</li>
 * <li>May accept several aggregators and send them retrieved data</li>
 * <p><br>
 *  TODO enable aggregator to register for a specific type of object
 *  
 * @author yoavram
 * @see com.netalign.netascutter.interfaces.IAggregator
 */
public interface IRetriever extends Runnable, RDFErrorHandler {
    
	
	@Override
    void run();

    @Override
    void warning(Exception e);

    @Override
    void error(Exception e);

    @Override
    void fatalError(Exception e);
    
    /**
     * Set the {@link URL} for the retriever to retrieve and process.
     * @param url a {@link URL} object 
     */
    void setUrl(URL url);
    /**
     * Set the aggregators that will receive processed data.
     * @param aggregators objects implementing {@link IAggregator}
     */
    void setAggregators(Collection<IAggregator> aggregators);
}

