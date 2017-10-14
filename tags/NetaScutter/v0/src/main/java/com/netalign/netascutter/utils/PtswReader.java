package com.netalign.netascutter.utils;

import java.net.*;
import java.util.*;

import org.apache.log4j.Logger;

import com.netalign.netascutter.interfaces.IUrlListener;

/**
 * The <code>PtswReader</code> is an implementation of {@link IDocHandler} for 
 * <a href="http://www.pingthesemanticweb.com">Ping The Semantic Web</a> XML files.<br>
 * <i>PTSW</i> is a service that records links to RDF files across the web. These links are then available for
 * registered users via XML files on <a href="http://www.pingthesemanticweb.com/export">http://www.pingthesemanticweb.com/export</a>
 * <p>
 * The XML files format:
 * <pre>
 * &lt;pingthesemanticwebUpdate version="1.4" updated="2007-07-31 11:20:54"&gt;<br />
 * &lt;rdfdocument url="http://b4mad.net/datenbrei/index.php?sioc_type=post&amp;sioc_id=300" created="2006-08-11 11:21:00" updated="2006-08-14 09:57:26" serialization="xml" ns="http://www.w3.org/1999/02/22-rdf-syntax-ns# http://xmlns.com/foaf/0.1/ ..."/&gt;<br />
 * &lt;/pingthesemanticwebUpdate&gt;
 * </pre>
 * 
 * <code>PtswReader</code> reacts on start and end of XML elements. <br>
 * On element start it checks that the element is an <i>rdfdocument</i> element. 
 * If it is, the reader parses it into the private class {@link rdfdocument} 
 * which in turn is saved to the reader's state.
 * <p>
 * On element end 
 * <li>checks that the element is an <i>rdfdocument</i> element</li>
 * <li>increment counter</li>
 * <li>if counter % COUNTER_MODULO ==0, log the counter</li> 
 * <li>passes the saved <i>rdfdocument</i> state through the filters (see {@link IUrlFilter}</li>
 * <li>wait until the listener's queue is empty</li>
 * <li>send the URL to the listener</li> 
 * <p>
 * If the <i>rdfdocument</i> fails any of the filters it is dumped.<br>
 * If the listener's URL queue is not empty then the reader sleeps for SLEEP_TIME milliseconds.
 * <p>
 * There is no way to stop the <i>QDParser</i>, therefore no way to stop the <code>PtswReader</code>
 * <p>
 * 
 * @author yoavram
 *
 * @see IDocHandler
 * @see QDParser
 * @see IUrlListener
 * @see rdfdocument 
 * @see IUrlFilter
 * @see NsFilter
 * @see DomainBlacklistFilter
 * @see <a href="http://www.pingthesemanticweb.com">Ping The Semantic Web site</a>
 * 
 */
public class PtswReader implements IDocHandler {

	/**
	 * Number of counters to pass between each log line.
	 */
	public static final long COUNTER_MODULO = 250;
	/**
	 * Number of milliseconds to wait between each check for an empty listener queue.
	 */
	public static final long SLEEP_TIME = 1000;
	
	private static Logger logger = Logger.getLogger(PtswReader.class);	
	private long counter;
	private rdfdocument current; // state
	private IUrlListener listener;
	private List<IUrlFilter> filters;
	private boolean count = true;
	
	/**
	 * Returns a boolean stating if the reader is counting successfully parsed <i>rdfdocument</i> elements.
	 */
	public boolean isCount() {
		return count;
	}
	/**
	 * Sets a boolean determining if the reader is counting successfully parsed <i>rdfdocument</i> elements.
	 * @param count
	 */
	public void setCount(boolean count) {
		this.count = count;
	}
	/**
	 * Sets the filter list through which <i>rdfdocument</i> objects must pass in order to be sent to the listener.
	 * @param filters
	 */
	public void setFilters(List<IUrlFilter> filters) {
		this.filters = filters;
	}
	/**
	 * Add a filter to the filter list through which <i>rdfdocument</i> objects must pass in order to be sent to the listener.
	 * @param filter
	 */
	public void addFilter(IUrlFilter filter) {
		if (this.filters == null) {
			this.filters = new ArrayList<IUrlFilter>();
		}
		this.filters.add(filter);
	}
	/**
	 * Sets the {@link IUrlListener} to which URLs that have passed the filters are sent if the listener's queue is empty
	 * @param listener
	 */
	public void setListener(IUrlListener listener) {
		this.listener = listener;
	}

	@Override
	public void startElement(String tag, Hashtable<String,String> h) throws Exception {		
		if (tag.equals(rdfdocument.NAME)) {
			current = new rdfdocument();
			current.setUrl(h.get(rdfdocument.URL));
			current.setCreated(h.get(rdfdocument.CREATED));
			current.setUpdated(h.get(rdfdocument.UPDATED));
			current.setSerialization(h.get(rdfdocument.SERIALIZATION));
			for ( String ns : h.get(rdfdocument.NS).split(" ")) {
				current.addNs(ns);
			}
		}		
	}
	
	@Override
	public void endElement(String tag) throws Exception {
		if (tag.equals(rdfdocument.NAME)) {
			counter++;
			if (count && counter%COUNTER_MODULO == 0) {
				logger.debug("Read " + Long.toString(counter) + " rdf links");
			}
			if (listener != null && filter(current)) {
				while (listener.getNumOfUrls() != 0) {
					Thread.sleep(SLEEP_TIME);
				}
				listener.addURL(current.url);
			}
		}				
	}

	@Override
	public void startDocument() throws Exception {
		current = null;		
	}
	
	@Override
	public void endDocument() throws Exception {
		// nothing to do here	
	}

	@Override
	public void text(String str) throws Exception {
		// nothing to do here				
	}
	
	private boolean filter(rdfdocument doc) {
		for (IUrlFilter filter : filters) {
			if (!filter.filterUrl(doc)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Returns the number of encountered <i>rdfdocument</i> tags
	 * @return
	 */
	public long getCounter() {
		return counter;
	}
	/**
	 * The <code>UrlFilter</code> interface allows a user to send an {@link rdfdocument} object for validation.
	 * The filter can implement any logic but must return for any specific {@link rdfdocument} a boolean value fo true or false. 
	 * 
	 * @author yoavram
	 * @see rdfdocument
	 */
	public interface IUrlFilter {
		/**
		 * Returns true if the {@link rdfdocument} object passes the filter,
		 * or false if it does not.
		 * @param doc {@link rdfdocument} object for validation
		 * @return true or false
		 */
		boolean filterUrl(rdfdocument doc);	
	}
	/**
	 * The <code>NsFilter</code> class checks that a list of RDF NameSpaces are contained in the {@link rdfdocument}'s
	 * list of namespaces.
	 * All namespaces must exist in the {@link rdfdocument} in order for the object to be validated and pass the filter.
	 * <p>
	 * A namespace example is FOAF: <a href="http://xmlns.com/foaf/0.1/">http://xmlns.com/foaf/0.1/</a>
	 * 
	 * @author yoavram
	 * @see IUrlFilter	
	 */
	public class NsFilter implements IUrlFilter {
		private List<String> ns;
		/**
		 * Set the namespaces list of the filter. These namespaces must exist in valid {@link rdfdocument}.
		 * @param ns
		 */
		public void setNs(List<String> ns) {
			this.ns = ns;
		}
		/**
		 * Adds a namespace to the namespaces list
		 * @param ns to add
		 */
		public void addNs(String ns) {
			if (this.ns == null) {
				this.ns = new ArrayList<String>();
			}
			this.ns.add(ns);
		}
		
		@Override
		public boolean filterUrl(rdfdocument doc) {
			for (String filterNs : this.ns) {
				if (!doc.ns.contains(filterNs)) {
					return false;
				}
			}
			return true;
		}
		
	}
	/**
	 * The <code>DomainBlacklistFilter</code> class checks that the host name (domain name) of the {@link rdfdocument}
	 * object's URL does not appear in a <a href="http://en.wikipedia.org/wiki/Blacklist">blacklist</a>.
	 * <p>
	 * @author user
	 * @see IUrlFilter
	 */
	public class DomainBlacklistFilter implements IUrlFilter {
		private List<String> blacklist;
		/**
		 * Set the blacklist 
		 * @param blacklist
		 */
		public void setBlacklist(List<String> blacklist) {
			this.blacklist = blacklist;
		}
		/**
		 * Add a domain name to the blacklist
		 * @param domainname
		 */
		public void addToBlacklist(String domainname) {
			if (this.blacklist == null) {
				this.blacklist = new ArrayList<String>();
			}
			this.blacklist.add(domainname);
		}

		@Override
		public boolean filterUrl(rdfdocument doc) {
			try {
				URL url = new URL(doc.url);	
				if (blacklist.contains(url.getHost())) {
					return false;
				}
			} catch (MalformedURLException e) {}			
			
			return true;
		}
	}
	
	/**
	 * The <code>rdfdocument</code> class is a java representation of the PingTheSemanticWeb export XML format tags.
	 * The PTSW XML contains multiple tags of this format:
	 *  <pre>
	 * &lt;rdfdocument url="http://b4mad.net/datenbrei/index.php?sioc_type=post&amp;sioc_id=300" created="2006-08-11 11:21:00" updated="2006-08-14 09:57:26" serialization="xml" ns="http://www.w3.org/1999/02/22-rdf-syntax-ns# http://xmlns.com/foaf/0.1/ ..."/&gt;
	 * </pre>
	 * This class doesn't parse a string. All it does in to contain the data and the constant names of the tag properties.
	 * Thus, it is basically a "struct".
	 * 
	 * @author yoavram
	 * @see PtswReader
	 */
	private class rdfdocument {
		public static final String NAME = "rdfdocument";
		public static final String URL = "url";
		public static final String CREATED = "created";
		public static final String UPDATED = "updated";
		public static final String SERIALIZATION = "serialization";
		public static final String NS = "ns";
				
		private String url;
		private String created;
		private String updated;
		private String serialization;
		private List<String> ns;
		
		public rdfdocument() {}
		
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getCreated() {
			return created;
		}
		public void setCreated(String created) {
			this.created = created;
		}
		public String getUpdated() {
			return updated;
		}
		public void setUpdated(String updated) {
			this.updated = updated;
		}
		public List<String> getNs() {
			return ns;
		}
		public void setNs(List<String> ns) {
			this.ns = ns;
		}
		public void addNs(String ns) {
			if (this.ns == null) {
				this.ns = new ArrayList<String>();
			}
			this.ns.add(ns);
		}
		public String getSerialization() {
			return serialization;
		}

		public void setSerialization(String serialization) {
			this.serialization = serialization;
		}
	}



}
