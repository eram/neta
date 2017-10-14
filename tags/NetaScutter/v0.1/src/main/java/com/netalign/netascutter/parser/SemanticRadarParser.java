/**
 * 
 */
package com.netalign.netascutter.parser;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.*;

import org.apache.log4j.Logger;

import com.netalign.netascutter.Constants;
import com.netalign.netascutter.interfaces.IParser;

/**
 * The <code>SemanticRadarParser</code> parses HTML files beginnings, looking
 * for XML tags of this format:
 * 
 * <pre>
 * &lt;link rel=&quot;meta&quot; type=&quot;application/rdf+xml&quot; title=&quot;FOAF&quot; href=&quot;http://anya1976.livejournal.com/data/foaf&quot; /&gt;
 * </pre>
 * 
 * It applies the following rules on <code>link</code> tags: <li>The
 * <code>rel</code> property can be anything.</li> <li>The <code>type</code>
 * property must be as the above.</li> <li>The <code>title</code> property must
 * be wither <i>SIOC</i> or <i>FOAF</i>.</li> <li>The <code>href</code>
 * property's data is extracted and added to the return list of the
 * <code>parse</code> method. if the other rules apply.
 * <p>
 * 
 * @author yoavram
 * @see <a
 *      href="http://www.w3.org/TR/rdf-syntax-grammar/#section-rdf-in-HTML">RDF
 *      Auto Discovery</a>
 * 
 */
public class SemanticRadarParser implements IParser<URL> {

	private static final int CHAR_BUFFFER_SIZE = 1000; // shouldnt be bigger
														// than the marker size
														// of the stream
	private static Logger logger = Logger.getLogger(SemanticRadarParser.class);
	private static Pattern pattern = Pattern.compile("<link.+>");

	@Override
	public List<URL> parse(InputStream inputStream, String url) {
		logger.debug("Parsing stream from " + url);
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(inputStream,
					Constants.UTF8_ENCODING));
		} catch (UnsupportedEncodingException e) {
			logger.error("Failed opening reader: " + e);
			return Collections.emptyList();
		}
		if (r == null) {
			logger.warn("Failed opening reader - reader retunred null");
			return Collections.emptyList();
		}
		char[] cbuf = new char[CHAR_BUFFFER_SIZE];
		int cbufLen = 0;
		try {
			cbufLen = r.read(cbuf);
			if (cbufLen == -1) {
				throw new IOException("End of stream reached prematurely");
			}
		} catch (IOException e) {
			logger.warn("Failed reading from input stream: " + e);
			return Collections.emptyList();
		}

		List<URL> list = new ArrayList<URL>();
		Matcher matcher = pattern.matcher(new String(cbuf));
		while (matcher.find()) {
			String t = matcher.group();
			if (!t.isEmpty()) {
				Link link = new Link(t);
				if (link.valid()) {
					URL linkUrl = null;
					try {
						linkUrl = new URL(link.getHref());
						list.add(linkUrl);
					} catch (MalformedURLException e) {
						; // nothing to do
					}
				}
			}
		}
		if (list.size() > 0) {
			logger.debug("Parsed " + list.size() + " URLs");
		} else {
			logger.debug("No URLs parsed");
		}
		return list;
	}

	/**
	 * Used to parse a link tag - example: <link rel="meta"
	 * type="application/rdf+xml" title="FOAF"
	 * href="http://anya1976.livejournal.com/data/foaf" />
	 */
	private class Link {
		public static final String META = "meta";
		public static final String APPLICATION_RDF_XML = "application/rdf+xml";
		public static final String SIOC = "sioc";
		public static final String FOAF = "foaf";

		public static final String REL = "rel";
		public static final String TYPE = "type";
		public static final String TITLE = "title";
		public static final String HREF = "href";

		public String rel;
		public String type;
		public String title;
		public String href;

		public Link(String str) {
			StringTokenizer st = new StringTokenizer(str, "= \"");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (token.equals(REL) && st.hasMoreTokens()) {
					setRel(st.nextToken());
				} else if (token.equals(TYPE) && st.hasMoreTokens()) {
					setType(st.nextToken());
				} else if (token.equals(TITLE) && st.hasMoreTokens()) {
					setTitle(st.nextToken());
				} else if (token.equals(HREF) && st.hasMoreTokens()) {
					setHref(st.nextToken());
				}
			}
		}

		public void setRel(String rel) {
			if (rel != null)
				this.rel = rel.toLowerCase();
		}

		public void setType(String type) {
			if (type != null)
				this.type = type.toLowerCase();
		}

		public void setTitle(String title) {
			if (title != null)
				this.title = title.toLowerCase();
		}

		public void setHref(String href) {
			if (href != null)
				this.href = href.toLowerCase();
		}

		public String getRel() {
			if (rel == null)
				return Constants.EMPTY_STRING;
			return rel;
		}

		public String getType() {
			if (type == null)
				return Constants.EMPTY_STRING;
			return type;
		}

		public String getTitle() {
			if (title == null)
				return Constants.EMPTY_STRING;
			return title;
		}

		public String getHref() {
			if (href == null)
				return Constants.EMPTY_STRING;
			return href;
		}

		public boolean valid() {
			if (!getType().equals(APPLICATION_RDF_XML))
				return false;
			if (!getTitle().equals(SIOC) && !getTitle().equals(FOAF))
				return false;
			return true;
		}
	}
}
