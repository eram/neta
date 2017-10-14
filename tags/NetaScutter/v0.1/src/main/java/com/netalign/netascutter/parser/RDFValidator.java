/**
 * NetaScutter v0.1
 */
package com.netalign.netascutter.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.netalign.netascutter.Constants;

/**
 * state-less
 * 
 * @author yoavram
 * @see IValidator
 * @see <a href="http://www.w3.org/TR/REC-rdf-syntax/#rdfxml">RDF Primer< /a>
 * @see <a href="http://www.asciitable.com/">ASCII Table</a>
 */
public class RDFValidator {
	private static Logger logger = Logger.getLogger(RDFValidator.class);
	private static final int MARK_LIMIT = 100;
	private static final int NUM_OF_TOKENS = 20;

	/**
	 * Receives an input stream at the beginning, tries to validate it, resets
	 * the input stream to the beginning and returns a boolean value. Past mark
	 * on the input stream is lost, new mark will be in current position.
	 */
	public static boolean validate(InputStream in) {
		InputStreamReader r = null;
		try {
			r = new InputStreamReader(in, Constants.UTF8_ENCODING);
		} catch (UnsupportedEncodingException e) {
			logger.error("Encoding '" + Constants.UTF8_ENCODING
					+ "' not supported: " + e);
			return false;
		}
		if (r == null) {
			logger.error("Failed creating a reader for input stream");
			return false;
		}
		if (in.markSupported()) {
			in.mark(MARK_LIMIT);
		}

		boolean valid = false;
		StreamTokenizer parser = new StreamTokenizer(r);
		parser.ordinaryChars(0, 255); // reset syntax table
		parser.eolIsSignificant(false); // end-of-line means nothing in xml
		parser.lowerCaseMode(true); // no casing of letters
		parser.ordinaryChar('\n'); // counting lines is disabled!
		parser.wordChars('A', 'z');
		parser.wordChars('0', '9');
		parser.wordChars(58, 59); // : ;
		parser.parseNumbers();
		parser.quoteChar(34); // "
		parser.quoteChar(39); // '
		parser.whitespaceChars(60, 63); // < = > ?
		parser.whitespaceChars(0, 32); // up to whitespace
		parser.slashSlashComments(false);
		parser.slashStarComments(false);

		int ttype = StreamTokenizer.TT_EOF;
		int count = 0;
		ArrayList<String> list = new ArrayList<String>();
		boolean exclamationMark = false;
		int comment = 0;
		try {
			while ((ttype = parser.nextToken()) != StreamTokenizer.TT_EOF
					&& count < NUM_OF_TOKENS) {
				count++;
				switch (ttype) {
				case StreamTokenizer.TT_EOL:
					break;
				case StreamTokenizer.TT_WORD:
					if (comment != 2) {
						if (!parser.sval.isEmpty()) {
							list.add(parser.sval);
						}
					}
					// System.out.println("w:" + parser.sval);
					break;
				case StreamTokenizer.TT_NUMBER:
					if (comment != 2) {
						list.add(Double.toString(parser.nval));
					}
					// System.out.println("n:" + parser.nval);
					break;
				default:
					if (comment != 2 && (ttype == 34 || ttype == 39)) { // " '
						list.add(parser.sval);
						// System.out.println("s:" + parser.sval);
						break;
					} else if (exclamationMark && ttype == 45) { // -
						comment++;
						if (comment == 4) { // <!-- **** -->
							comment = 0;
							exclamationMark = false;
						}
					} else if (ttype == 33) { // !
						exclamationMark = true;
					}
					// System.out.println("d:" + ttype + (char) ttype);
					break;
				}
			}
		} catch (IOException e) {
			logger.error("Valdation error: " + e);
		}

		while (list.remove(Constants.EMPTY_STRING)) {
		} // remove empty strings from list
		while (list.remove(null)) {
		} // remove null strings from list
		// following is a list of if conditions trying to fail the validation
		// each throws an exception just for program flow - the exceptions are
		// caught at the end of the
		// conditions, after the successful validation statement
		try {
			if (!list.contains("xml"))
				throw new Exception(
						"Tag 'xml' must appear at beginning of RDF document");
			if (!list.contains("version"))
				throw new Exception(
						"First tag must be have a 'version' property");
			if (!list.get(list.indexOf("version") + 1).startsWith("1.0"))
				throw new Exception("Version must be 1.0");
			if (!list.contains("rdf:rdf"))
				throw new Exception(
						"Tag 'rdf:rdf' must appear at beginning of RDF document");
			// validation succeeded
			logger.debug("Validation succeeded");
			valid = true;
		} catch (Exception e) {
			logger.debug("Validation failed: " + e.getMessage());
			valid = false;
		}

		// try reseting input stream
		if (in.markSupported()) {
			try {
				in.reset();
			} catch (IOException e) {
				logger.error("Failed reseting input stream: " + e);
			}
		}
		return valid;
	}

	/*
	 * format examples from different rdf files <?xml version="1.0"
	 * encoding="UTF-8"?> <rdf:RDF
	 * 
	 * <?xml version="1.0"?>
	 * 
	 * <rdf:RDF
	 * 
	 * <?xml version="1.0"?> <rdf:RDF
	 * 
	 * <?xml version="1.0" encoding="utf-8"?> <!--
	 * generator="WP SIOC Plugin - v. 1.25 - http://rdfs.org/sioc/wordpress/"
	 * --> <rdf:RDF
	 * 
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?> <!--
	 * generator="WP SIOC Plugin - v. 1.22 - http://rdfs.org/sioc/wordpress/"
	 * --> <rdf:RDF
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?> <!--
	 * generator="WP SIOC Plugin - v. 1.25 - http://rdfs.org/sioc/wordpress/"
	 * --> <rdf:RDF
	 */

	private static String readUntil(InputStreamReader r, char limit)
			throws IOException {
		StringBuilder builder = new StringBuilder();
		for (int i = r.read(); i != -1; i = r.read()) {
			char c = (char) i;
			builder.append(c);
			if (c == limit) {
				break;
			}
		}
		return builder.toString();
	}
}
