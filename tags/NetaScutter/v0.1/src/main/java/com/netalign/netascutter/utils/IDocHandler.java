package com.netalign.netascutter.utils;

import java.util.*;

/**
 * The <code>IDocHandler</code> interface is the event handler for the {@link QDParser} static class.
 * It recieves XML parsing events:
 * <li>Start of document</li>
 * <li>End of document</li>
 * <li>Start of XML element (tag)</li>
 * <li>End of XML element (tag)</li>  
 * <li>Text element (literal)</li>
 * <br>
 * The handler may throw exceptions.
 * <p>
 * @see QDParser
 * @see PtswReader
 *
 */
public interface IDocHandler {
	/**
	 * Called at an XML opening tag (<tag name="taggy">).
	 * @param tag	the name of the XML tag ("tag" in the example)
	 * @param h		an Hashtable of the tag's properties' keys and values. {name  : "taggy"} in the example)
	 * @throws Exception	
	 */
	void startElement(String tag, Hashtable<String,String> h) throws Exception;
	/**
	 * Called at an XML closing tag (</tag>)/
	 * @param tag	the name of the tag ("tag" in example)
	 * @throws Exception
	 */
	void endElement(String tag) throws Exception;
	/**
	 * Called at the beginning of an XML document
	 * @throws Exception
	 */
	void startDocument() throws Exception;
	/**
	 * Called at the end of an XML document
	 * @throws Exception
	 */
	void endDocument() throws Exception;
	/**
	 * Called at an open text element (Literal element).
	 * @param str	the text string contained in the text element
	 * @throws Exception
	 */
	void text(String str) throws Exception;
}
