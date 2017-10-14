/**
 * NetaScutter v0.1
 */
package com.netalign.netascutter.interfaces;

import java.io.InputStream;
import java.util.List;

import com.netalign.netascutter.parser.SemanticRadarParser;
import com.netalign.netascutter.parser.SiocParser;

/**
 * The <code>IParser</code> interface allows the declaration of parsers that extract a specific type of data
 * from input streams.<br>
 * <code>IParser</code> are state-less, thus they may be instantiated once and used 
 * by many threads.
 * state-less
 * <p>
 * Implementations are encouraged to verify that the input data applies for the parser and if not return ASAP,
 * so that whoever is responsible of the data may still be able to reset the input stream.  
 * <p>
 * @author yoavram
 * @see SiocParser
 * @see SemanticRadarParser
 * XXX there is no indication if parser failed because the data format is not right or because no objects 
 * are there to be parsed.
 */
public interface IParser<E> {
	/**
	 * Parses the input stream that was set on the parser.
	 * @return the parsed items list or an empty list if failed. SHOULD NOT return <i>null</i>, 
	 * and should not add any <i>null</i>s to the returned list. 
	 */
	List<E> parse(InputStream inputStream, String url);
}
