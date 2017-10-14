package com.netalign.netascutter.utils;

import java.io.FileReader;
import java.util.List;

import com.netalign.netascutter.interfaces.IUrlListener;
import com.netalign.netascutter.utils.PtswReader.IUrlFilter;
import com.netalign.rdf.vocabulary.*;

/**
 * The <code>PtswReaderThread</code> class start a thread with a {@link QDParser} as an XML parser
 * and a {@link PtswReader} as a document handler for the XML parser, receives a path for an PTSW XML file and 
 * a {@IUrlListener} for the PtswReader, and starts parsing the XML document in a thread, while extracted URLs 
 * are added to the IUrlListener.
 *  
 * @author yoavram
 *
 */
public class PtswReaderThread extends Thread {
	
	private PtswReader handler;
	private String filepath;
	
	public PtswReaderThread() {
		super();
		handler = new PtswReader();    	  
    	//handler.setCount(false); // uncomment if you don't want to log objects counting
	}
	
	public PtswReaderThread(IUrlListener listener, String filepath, List<IUrlFilter> filters) {
		this();
		setListener(listener);
		setFilepath(filepath);
		setFilters(filters);
	}
	
	public PtswReaderThread(IUrlListener listener, String filepath) {
		this();
		setListener(listener);
		setFilepath(filepath);
	}
	/**
	 * Sets the listener of the PtswReader
	 * @param listener 
	 */
	public void setListener(IUrlListener listener) {
		handler.setListener(listener);
	}
	/**
	 * Sets the filepath of the PTSW XML file
	 * @param filepath
	 */
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	/**
	 * Sets the filters of the PtswReader. The filters filter outgoing URLs.
	 * @param filters
	 */
	public void setFilters(List<IUrlFilter> filters ) {
		handler.setFilters(filters);
	}
	/**
	 * The default initialization uses SIOC and FOAF namespaces for the {@link NsFilter} and adds
	 * "www.talkdigger" and "www.ecademy.com" to the {@link DomainBlacklistFilter}.
	 */
	public void defaultInit() {
    	PtswReader.NsFilter filter1 = new PtswReader.NsFilter();
    	filter1.addNs(SIOC.NS);
    	filter1.addNs(FOAF.NS);    	
    	handler.addFilter(filter1);
    	PtswReader.DomainBlacklistFilter filter2 = new PtswReader.DomainBlacklistFilter();
    	filter2.addToBlacklist("www.talkdigger.com");	
    	handler.addFilter(filter2); 
    	PtswReader.DomainBlacklistFilter filter3 = new PtswReader.DomainBlacklistFilter();
    	filter3.addToBlacklist("www.ecademy.com");	
    	handler.addFilter(filter3);
	}

	@Override
	public void run() {		
		try {
			QDParser.parse(handler, new FileReader(filepath));
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
}
