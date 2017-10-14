package com.netalign.netascutter.handler;

import com.netalign.sioc.IFoafPerson;
import com.netalign.sioc.ISiocContainer;
import com.netalign.sioc.ISiocForum;
import com.netalign.sioc.ISiocPost;

import org.apache.log4j.*;

import com.netalign.netascutter.Constants;
import com.netalign.netascutter.interfaces.IConverter;
import com.netalign.netascutter.interfaces.IEncryptor;
import com.netalign.netascutter.utils.SHA1Encryptor;
import com.netalign.netascutter.utils.W3CTime;
import com.netalign.rdf.vocabulary.*;
import java.util.*;
import java.util.Map.Entry;

import com.hp.hpl.jena.rdf.model.*;

/**
 * The <code>DrupalMapper</code> class converts {@link ISiocPost} and
 * {@link IFoafPerson} objects to a {@link Map}, suitable to be sent to
 * {@IHttpClient}. The map maps objects fields with key-value
 * pairs, adding some key-values needed by the Drupal machenism (such as
 * password for a user).
 * <p>
 * The mapper is spesific for Drupal and more so for a Drupal specific
 * configuration, as it relies on CCK and Profile fields to map person and post
 * fields to keys that basic Drupal configuration does not contain.
 * <p>
 * The mapper maps:
 * <li>IFoafPerson to a user map</li>
 * <li>ISiocPost to a node or comment map</li>
 * 
 * @author yoavram
 * @see IConverter
 * @see <a href="http://www.drupal.org">Drupal< /a>
 * @see IFoafPerson
 * @see ISiocPost
 */
public class DrupalMapper implements IConverter {

	public static final String NODE_STATUS_PUBLISHED = "1";
	public static final String INPUT_FILTER_FULL_HTML = "3";
	public static final int TITLE_LEN = 20;
	public static final String ALLOW_COMMENTS = "2";
	public static final String ANNONYMOUS_ID = "0";
	private static final Map<String, String> PREFIX_TABLE = initHashtable();
	public static final String PROMOTED_TO_FRONT_PAGE = "1";
	private static final int NUM_OF_TITLE_WORDS = 4;

	/**
	 * This function creates the hashtable that maps namespaces into prefixes
	 * used in drupal cck and profile field names.
	 * 
	 * @return
	 */
	private static HashMap<String, String> initHashtable() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(FOAF.NS, "foaf_");
		map.put(DC.NS, "dc_");
		map.put(SIOC.NS, "sioc_");
		map.put(Content.NS, "content_");
		map.put(DCTerms.NS, "dcterms_");
		return map;
	}

	private static Logger logger = Logger.getLogger(DrupalMapper.class);
	private IEncryptor encryptor;
	private W3CTime w3cTime;

	public DrupalMapper() {
		encryptor = new SHA1Encryptor();
		w3cTime = new W3CTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.IConverter#convertPerson(com.netalign.sioc.IFoafPerson
	 * , java.lang.String)
	 */
	public Map<String, String> convertPerson(IFoafPerson person,
			String personUrl) {
		if (person == null) {
			return Collections.emptyMap();
		}
		Map<String, String> map = new HashMap<String, String>();

		// FOAF
		map.put(getFieldName(FOAF.aimChatID), person.getAimChatId());
		map.put(getFieldName(FOAF.depiction), person.getDepiction());
		map.put(getFieldName(FOAF.mbox), person.getMbox());
		map.put(getFieldName(FOAF.mbox_sha1sum), person.getMboxSha1Sum());
		map.put(getFieldName(FOAF.family_name), person.getFamilyname());
		map.put(getFieldName(FOAF.firstName), person.getFirstName());
		map.put(getFieldName(FOAF.gender), person.getGender());
		map.put(getFieldName(FOAF.givenname), person.getGivenname());
		map.put(getFieldName(FOAF.homepage), person.getHomepage());
		map.put(getFieldName(FOAF.icqChatID), person.getIcqChatId());
		map.put(getFieldName(FOAF.jabberID), person.getJabberId());
		map.put(getFieldName(FOAF.msnChatID), person.getMsnChatId());
		map.put(getFieldName(FOAF.name), person.getName());
		map.put(getFieldName(FOAF.nick), person.getNick());
		map.put(getFieldName(FOAF.schoolHomepage), person.getSchool());
		map.put(getFieldName(FOAF.surname), person.getSurname());
		map.put(getFieldName(FOAF.phone), person.getPhone());
		map.put(getFieldName(FOAF.tipjar), person.getTipJar());
		map.put(getFieldName(FOAF.title), person.getTitle());
		map.put(getFieldName(FOAF.weblog), person.getWeblog());
		map.put(getFieldName(FOAF.workplaceHomepage), person.getWorkHomepage());
		map.put(getFieldName(FOAF.workInfoHomepage), person.getWorkInfoPage());
		map.put(getFieldName(FOAF.yahooChatID), person.getYahooChatId());
		map.put(getFieldName(FOAF.img), person.getImg());
		map.put(getFieldName(FOAF.myersBriggs), person.getMyersBriggs());

		// Drupal & NetaScutter specifics
		String name = getNameForPerson(person);
		map.put("name", name);
		map.put("pass", name);
		map.put("mail", person.getMboxSha1Sum() + "@local");
		map.put("init", "NetaScutter");

		// URL & URI
		String sha1;
		map.put("url", personUrl);
		if (personUrl.isEmpty()) {
			sha1 = UUID.randomUUID().toString();
		} else {
			sha1 = encryptor.encrypt(personUrl);
		}
		map.put("url_sha1sum", sha1);
		//
		String personUri = person.getURI();
		map.put("uri", personUri);
		if (personUri.isEmpty()) {
			sha1 = UUID.randomUUID().toString();
		} else {
			sha1 = encryptor.encrypt(personUri);
		}
		map.put(Constants.USER_URI_FIELD, sha1);

		// remove empty strings TODO put this in the
		map = removeEmptyValues(map);

		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.IConverter#convertPost(com.netalign.sioc.ISiocPost,
	 * java.lang.String)
	 */
	public Map<String, String> convertPost(ISiocPost post, String postUrl) {
		if (post == null) {
			return Collections.emptyMap();
		}
		Map<String, String> map = new HashMap<String, String>();

		// SIOC
		map.put("field_about_value", post.getAbout());
		map.put("field_ip_address_value", post.getIpAddress());
		map.put("field_note_value", post.getNote());
		map.put("field_attachment_value", implodeStringList(post.getAttachment()));
		map.put("field_topic_value", implodeStringList(post.getTopic()));

		// Other NS
		if (post.getCreator() != null) {
			map.put("field_creator_value", post.getCreator().getSeeAlso());
		}
		if (post.getMaker() != null) {
			map.put("field_maker_value", post.getMaker().getSeeAlso());
		}
		// Drupal & NetaScutter specifics
		String body = post.getContentEncoded();
		if (body == null || body.isEmpty()) {
			body = post.getContent();
		}
		String title = post.getTitle();
		if (title == null || title.isEmpty()) {
			title = startingWords(post.getContent(), NUM_OF_TITLE_WORDS);
		}
		map.put("title", title);
		map.put("body", body);
		map.put("type", Constants.NODE_TYPE);
		map.put("uid", DrupalMapper.ANNONYMOUS_ID);
		map.put("status", NODE_STATUS_PUBLISHED);
		map.put("format", INPUT_FILTER_FULL_HTML);
		map.put("promote", PROMOTED_TO_FRONT_PAGE);
		map.put("comment", ALLOW_COMMENTS);
		String date = post.getCreated();
		if (date != null && !(date.isEmpty())) {
			map.put("created", w3cTime.w3cToUnixAsString(date));
		}

		// URL & URI
		String sha1;
		map.put("field_url_value", postUrl);
		if (postUrl.isEmpty()) {
			sha1 = UUID.randomUUID().toString();
		} else {
			sha1 = encryptor.encrypt(postUrl);
		}
		map.put("field_url_sha1sum_value", sha1);
		//
		String postUri = post.getURI();
		map.put("field_uri_value", postUri);
		if (postUri == null || postUri.isEmpty()) {
			sha1 = UUID.randomUUID().toString();
		} else {
			sha1 = encryptor.encrypt(postUri);
		}
		map.put(Constants.NODE_URI_FIELD, sha1);

		// remove empty strings TODO put this in the
		map = removeEmptyValues(map);

		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.IConverter#convertComment(com.netalign.sioc.ISiocPost,
	 * java.lang.String)
	 */
	public Map<String, String> convertComment(ISiocPost post, String postUrl) {
		if (post == null) {
			return Collections.emptyMap();
		}
		Map<String, String> map = new HashMap<String, String>();

		// SIOC
		map.put(getFieldName(SIOC.about), post.getAbout());
		map.put(getFieldName(SIOC.content), post.getContent());
		map.put(getFieldName(SIOC.ip_address), post.getIpAddress());
		map.put(getFieldName(SIOC.note), post.getNote());
		map.put(getFieldName(SIOC.attachment), implodeStringList(post
				.getAttachment()));
		map.put(getFieldName(SIOC.topic), implodeStringList(post.getTopic()));

		// Other NS
		map.put(getFieldName(Content.encoded), post.getContentEncoded());
		map.put(getFieldName(DC.title), post.getTitle());
		map.put(getFieldName(DCTerms.created), post.getCreated());
		if (post.getCreator() != null) {
			map.put(getFieldName(SIOC.has_creator), post.getCreator()
					.getSeeAlso());
		}
		if (post.getMaker() != null) {
			map.put(getFieldName(FOAF.maker), post.getMaker().getSeeAlso());
		}

		// Drupal & NetaScutter specifics
		String body = post.getContentEncoded();
		if (body.isEmpty()) {
			body = post.getContent();
		}
		map.put("subject", post.getTitle());
		map.put("comment", body);
		map.put("uid", DrupalMapper.ANNONYMOUS_ID); // Anonymous
		map.put("format", INPUT_FILTER_FULL_HTML); // full html
		String date = post.getCreated();
		if (date != null && !(date.isEmpty())) {
			map.put("timestamp", w3cTime.w3cToUnixAsString(date));
		}
		map.put("hostname", post.getIpAddress()); // TODO not tested

		// URL & URI - no URL in comments
		String sha1;
		String postUri = post.getURI();
		if (postUri.isEmpty()) {
			sha1 = UUID.randomUUID().toString();
		} else {
			sha1 = encryptor.encrypt(postUri);
		}
		map.put(Constants.COMMENT_URI_FIELD, sha1);
		map.put("name", postUri); // TODO constant

		// add direct-parent urisha1sum if exists
		if (post.getReplyOf() != null && post.getReplyOf().getURI() != null 
				&& !post.getReplyOf().getURI().isEmpty()) {
			map.put(Constants.PARENT_ID_FIELD, encryptor.encrypt(post.getReplyOf().getURI()));
		}
			
		// remove empty strings TODO put this in the
		map = removeEmptyValues(map);

		return map;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.IConverter#convertForum(com.netalign.sioc.ISiocPost,
	 * java.lang.String)
	 */
	@Override
	public Map<String, String> convertContainer(ISiocContainer container, String urlStr) {
		if (container == null) {
			return Collections.emptyMap();
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("description", container.getDescription());
		map.put("parent", ANNONYMOUS_ID); // default parent is root
		map.put(Constants.VID, Constants.FORUMS_VID); // default vocabulary is forums
		
		// URL & URI 
		String sha1;
		String uri = container.getURI();
		if (uri.isEmpty()) {
			sha1 = UUID.randomUUID().toString();
		} else {
			sha1 = encryptor.encrypt(uri);
		}		
		// this field for terms in drupal is imploded on \n
		map.put(Constants.TERM_URI_FIELD, sha1+"\n"+uri); 
		String name = container.getTitle();
		if (name == null || name.isEmpty()) {
			name = uri;
		}		
		map.put("name", name);
		// TODO see if possible to add a field for the uri/url
				
		// remove empty strings TODO put this in the
		map = removeEmptyValues(map);

		return map;
	}

	private String implodeStringList(List<String> list) {
		if (list == null || list.isEmpty()) {
			return Constants.EMPTY_STRING;
		}
		StringBuilder txt = new StringBuilder();
		for (String str : list) {
			txt.append(str + ";");
		}
		txt = txt.deleteCharAt(txt.length() - 1);
		txt.trimToSize();
		return txt.toString();
	}

	/**
	 * Get the name of a JENA resource with a prefix of the namespace.
	 * 
	 * @param res
	 *            the resource to get the name of.
	 * @return namepspace_resource name (e.g. "foaf_mbox_sha1sum")
	 */
	public String getFieldName(Resource res) {
		return PREFIX_TABLE.get(res.getNameSpace()) + res.getLocalName();
	}

	/**
	 * extract a name for a foaf person.
	 * 
	 * @param person
	 *            the person to extract a name for
	 * @return a name for that person
	 */
	private String getNameForPerson(IFoafPerson person) {
		String name = Constants.EMPTY_STRING;

		if (!(name = person.getName()).isEmpty()) { // name field
			;
		} else if (!(name = person.getGivenname()).isEmpty()) { // givenname
			;
		} else if (!person.getSurname().isEmpty()
				&& !person.getFirstName().isEmpty()) { // surname & firstname
			name = person.getFirstName() + " " + person.getSurname();
		} else if (!person.getFamilyname().isEmpty()
				&& !person.getFirstName().isEmpty()) { // familyname & firstname
			name = person.getFamilyname() + " " + person.getSurname();
		} else if (!(name = person.getNick()).isEmpty()) { // nick
			;
		}
		return name;
	}

	private Map<String, String> removeEmptyValues(Map<String, String> map) {
		Map<String, String> newMap = new HashMap<String, String>();
		Iterator<Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> e = it.next();
			if (e.getValue() != null && !e.getValue().isEmpty()) {
				newMap.put(e.getKey(), e.getValue());
			}
		}
		return newMap;
	}

	private String startingWords(String str, int numOfWords) {
		if (str == null || str.isEmpty()) {
			return Constants.EMPTY_STRING;
		}
		int index = 0;
		int spaces = 0;
		while (spaces < numOfWords && index < str.length()) {
			if (Character.isWhitespace(str.charAt(index++))) {
				spaces++;
			}
		}
		return str.substring(0, index).trim();
	}
}
