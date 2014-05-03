package com.r2d2warrior.c3p0j.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Element;

public class WebUtils
{
	
	protected final static JSONParser PARSER = new JSONParser();
	
	/**
	 * Reads a URL and trys to parse JSON from it
	 * @param formattedUrl The URL, to be used in String.format(...)
	 * @param input The argument to be used in String.format(...)
	 * @param encodeInput Set to <code>true</code> to encode the input using URLEncoder.encode(...)
	 * @return a JSONObject representing all of the JSON data on the URL
	 * @throws IOException If fails to connect to or read the URL
	 * @throws ParseException If fails to parse the URL for JSON
	 */
	public static JSONObject getJSON(String formattedUrl, String input, boolean encodeInput) throws IOException, ParseException
	{
		if (encodeInput)
			input = URLEncoder.encode(input, "UTF-8");
		String fullUrl = String.format(formattedUrl, input);
		System.out.println(fullUrl);
		HttpURLConnection conn = (HttpURLConnection) new URL(fullUrl).openConnection();
		JSONObject json = (JSONObject) PARSER.parse(new InputStreamReader(conn.getInputStream()));
		return json;
	}
	
	/**
	 * Utility method for {@link #getJSON(String formattedUrl, String input , boolean encodeInput)},
	 * setting <code>encodeInput</code> argument to <code>true</code>
	 */
	public static JSONObject getJSON(String formattedUrl, String input) throws IOException, ParseException
	{
		return getJSON(formattedUrl, input, true);
	}
	
	/**
	 * Create a short URL (goo.gl) from a long URL
	 * @param url The long url to be shortened
	 * @return The short URL
	 * @throws IOException If cannot connect to or send to google's api
	 * @throws ParseException If cannot parse the response
	 */
	public static String shortenURL(String url) throws IOException, ParseException
	{
		String apiKey = new Config("config.json").getMap().get("api").get("googl");
		String apiURL = "https://www.googleapis.com/urlshortener/v1/url?key=" + apiKey;
		URLConnection conn = new URL(apiURL).openConnection();
		
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write("{\"longUrl\":\"" + url + "\"}");
		wr.flush();
		wr.close();
		
		JSONObject json = (JSONObject) new JSONParser().parse(new InputStreamReader(conn.getInputStream()));

		String shortURL = json.get("id").toString();
		return shortURL;
	}
	/**
	 * Evaluates python code using http://tumbolia.appspot.com/py/code_here and returns the output
	 * @param code The code to execute or evaluate
	 * @return Standard output after evaluating the code
	 * @throws IOException If cannot connect to or read URL
	 */
	public static String evaluatePython(String code) throws IOException
	{
		String url = "http://tumbolia.appspot.com/py/" + URLEncoder.encode(code, "UTF-8").replace("+", "%20");
		return IOUtils.toString(new URL(url));
	}
	
	/**
	 * Looks up a search term or phrase on <a href="http://www.urbandictionary.com">urbandictionary.com</a> and get first 10 definitions
	 * <p>
	 * An String to String map entry in the return List would look as follows:
	 * <p><code>
	 * {<br>
	 * "defid":855655,<br>
	 *  "word":"Java",<br>
	 *  "author":"Nidht",<br>
	 *  "permalink":"http://java.urbanup.com/855655",<br>
	 *  "definition":"A programming language commonly used as a solution to everything and anything.",<br>
	 *  "example":"Just do it in java!\r\nFix it... with java!\r\n\r\njava;",<br>
	 *  "thumbs_up":549,<br>
	 *  "thumbs_down":166,<br>
	 *  "current_vote":""<br>
	 *  },</code>
	 * @param searchTerm The term or phrase to search for
	 * @return A List of String to String maps for each definition. See example above
	 * @throws IOException
	 * @throws ParseException
	 * @see #getJSON(String, String)
	 */
	public static List<Map<String, String>> getUrbanDictionaryDefinitions(String searchTerm) throws IOException, ParseException
	{		
		Map<?, ?> json = (Map<?, ?>) getJSON("http://api.urbandictionary.com/v0/define?term=%s", searchTerm);
		
		@SuppressWarnings("unchecked")
		List<Map<String, String>> defsMapList = (List<Map<String, String>>) json.get("list");
		
		return defsMapList;
	}
	
	/**
	 * Trys to calculate a mathmatical expression using <a href="http://www.duckduckgo.com/">www.duckduckgo.com</a>
	 * @param input The math expression, with or without spaces
	 * @return The math expression, followed by an equals sign, then the answer. Example: <i>"1 + 1 = 2"</i> 
	 * @throws IOException
	 * @throws ParseException
	 * @throws IllegalArgumentException If the search for <code>input</code> isn't a calculation. (Tried to define it)
	 * @see #getJSON(String, String)
	 */
	public static String getCalculation(String input) throws IOException, ParseException, IllegalArgumentException
	{
		JSONObject data = getJSON("http://api.duckduckgo.com/?q=%s&format=json", input);
		
		if (!data.get("AnswerType").equals("calc"))
			throw new IllegalArgumentException("Result not a calculation: " + input + ".");
		
		String htmlResult = (String) data.get("Answer");
		
		if (htmlResult.contains("<sup>") && htmlResult.contains("</sup>"))
			htmlResult = htmlResult.replaceAll("<sup>", " ^ ").replaceAll("</sup>", "");
		
		String strippedResult = new HtmlToPlainText().getPlainText(Jsoup.parse(htmlResult));
		
		if (strippedResult.endsWith("<>"))
			strippedResult = strippedResult.replace("<>", "");
		
		return strippedResult;
	}
	
	/**
	 * Looks up a search term or phrase using <a href="http://www.duckduckgo.com/">www.duckduckgo.com</a>
	 * @param input The search term or phrase
	 * @return A List of String to String maps of definitions. See <a href="http://api.duckduckgo.com/?q=java&format=json&pretty=1">this</a> for return example.
	 * @throws IOException
	 * @throws ParseException
	 * @see #getJSON(String, String)
	 */
	public static List<Map<String,String>> getDefinitions(String input) throws IOException, ParseException
	{		
		JSONObject data = getJSON("http://api.duckduckgo.com/?q=%s&format=json", input);
		
		@SuppressWarnings("unchecked")
		List<Map<String, String>> defs = (List<Map<String, String>>) data.get("RelatedTopics");
		if (defs.size() > 1)
			defs.remove(defs.size()-1);
		return defs;
	}
	
	/**
	 * Looks up an ip address and gathers location information using <code>http://geo.liamstanley.io/json/[ip-address]</code>
	 * <p>
	 * The return map might look like this:
	 * <p><code>
	 * {<br>
	 *  "ip":"70.160.210.75",<br>
	 *  "country_code":"US",<br>
	 *  "country_name":"United States",<br>
	 *  "region_code":"VA",<br>
	 *  "region_name":"Virginia",<br>
	 *  "city":"Virginia Beach",<br>
	 *  "zipcode":"23453",<br>
	 *  "latitude":36.7849,<br>
	 *  "longitude":-76.0839,<br>
	 *  "metro_code":"544",<br>
	 *  "areacode":"757"<br>
	 *  }</code>
	 * @param ip The ip address
	 * @return A String to String map of location information (see above). <i>Note: check for "country_code" being "Reserved"</i>
	 * @throws IOException
	 * @throws ParseException
	 * @see #getJSON(String, String)
	 */
	public static Map<String, String> getLocationData(String ip) throws IOException, ParseException
	{
		JSONObject data = getJSON("http://geo.liamstanley.io/json/%s", ip);
		
		Map<String, String> results = new HashMap<>();
		for (Object o : data.keySet())
		{
			String key = o.toString();
			String val = data.get(key).toString();
			if (val.equals("0") || StringUtils.isBlank(val))
				results.put(key, "N/A");
			else
			{
				if (key.equals("country_code"))
					results.put(key, val.toUpperCase());
				else
					results.put(key, Utils.toTitleCase(val));
			}
		}
		return results;
	}
	
	/**
	 * Retrieves a random FML statement from http://www.fmylife.com/random
	 * @return The random statement
	 * @throws IOException If fails to connect to or read the URL
	 */
	public static String getRandomFML() throws IOException
	{
		String address = "http://www.fmylife.com/random";
		Element element = Jsoup.connect(address).get().select("li[id]").first().select("p").first();
		String fml = StringEscapeUtils.unescapeHtml4(element.html()) + ".";
		return fml;
		
	}
}
