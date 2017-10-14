package com.netalign.netascutter.interfaces;

/**
 * The <code>IAggregator</code> interface is designed to be a generic processor of {@link IRetriever} 
 * retrieved data and a feeder for an {@link IUrlListener}.
 * <p>
 * <code>IAggregator</code> may receive any type of objects accompanied by a URL 
 * representing the object resource. 
 * 
 * @author yoavram
 * @see IUrlListener
 * @see IRetriever
 * @see RESTPostAggregator
 * @see RESTPersonAggregator
 * @see ForumAggreagator
 */
public interface IAggregator {
	/**
	 * Aggregate an object.
	 * @param incoming an object of any type to aggregation - 
	 * an aggregator may dump the object if it can not handle it without prompting or logging.
	 * @param urlStr
	 */
    void add(Object incoming, String urlStr);

    /**
     * Set an {@link IUrlListener} for this aggregtor.
     * @param listener
     */
	void setListener(IUrlListener listener);
}
