package com.r2d2warrior.c3p0j.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebUtils
{
	private static HttpURLConnection getConnection(String url)
	{
		try
		{
			return (HttpURLConnection) new URL(url).openConnection();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Map<String, String>> getUrbanDictionDefinitions(String searchTerm)
	{
		try
		{
			String address = "http://api.urbandictionary.com/v0/define?term=" + searchTerm.replace(' ', '+');
			
			Scanner in = new Scanner(getConnection(address).getInputStream());
			
			String json = "";
			while (in.hasNext())
				json += in.nextLine() + "\n";
			
			JSONParser parser = new JSONParser();
			Map<?, ?> jMap = ((Map<?, ?>)parser.parse(json));
			
			@SuppressWarnings("unchecked")
			List<Map<String, String>> definitionMapList = (List<Map<String, String>>)jMap.get("list");
			
			in.close();
			return definitionMapList;
		}
		catch (IOException | ParseException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getCalculation(String input) throws IOException, ParseException, IllegalArgumentException
	{
		String address = String.format("http://api.duckduckgo.com/?q=%s&format=json", URLEncoder.encode(input.replace(" ", ""), "UTF-8"));
		InputStreamReader in = new InputStreamReader(getConnection(address).getInputStream());
		JSONObject data = (JSONObject) new JSONParser().parse(in);
		
		String answerType = (String) data.get("AnswerType");
		if (!answerType.equals("calc"))
			throw new IllegalArgumentException("Result not a calculation: " + input + ". Result here: " + address);
		
		String htmlResult = (String) data.get("Answer");
		if (htmlResult.contains("<sup>") && htmlResult.contains("</sup>"))
			htmlResult = htmlResult.replace("<sup>", " ^ ").replace("</sup>", "");
		
		String strippedResult = new HtmlToPlainText().getPlainText(Jsoup.parse(htmlResult));
		if (strippedResult.endsWith("<>"))
			strippedResult = strippedResult.replace("<>", "");
		return strippedResult;
	}
	
	public static Map<String, String> getCommitData(String name) throws IOException, ParseException
	{
		String address = "https://api.github.com/repos/" + name + "/branches/master";
		HttpURLConnection conn = (HttpURLConnection) new URL(address).openConnection();
		
		JSONObject allData = (JSONObject) new JSONParser().parse(new InputStreamReader(conn.getInputStream()));
		JSONObject allCommitData = (JSONObject) allData.get("commit");
		JSONObject commitData = (JSONObject) allCommitData.get("commit");
		
		@SuppressWarnings("unchecked")
		String authorName = (String) ((Map<String, String>) commitData.get("author")).get("name");
		String commitMessage = (String) commitData.get("message");
		String commitURL = (String) allCommitData.get("html_url");
		
		Map<String, String> results = new HashMap<>();
		results.put("author", authorName);
		results.put("message", commitMessage);
		results.put("url", commitURL);
		return results;
	}
	
	public static Map<String, String> getLocationData(String ip) throws IOException, ParseException
	{
		String address = "http://geo.liamstanley.io/json/" + ip;
		HttpURLConnection conn = (HttpURLConnection) new URL(address).openConnection();
		JSONObject data =
				(JSONObject)new JSONParser().parse(new InputStreamReader(conn.getInputStream()));
		
		Map<String, String> results = new HashMap<>();
		for (Object o : data.keySet())
		{
			String key = o.toString();
			String val = data.get(key).toString();
			if (val.equals("0") || StringUtils.isBlank(val))
				results.put(key, "N/A");
			else
				results.put(key, Utils.toTitleCase(val));
		}
		return results;
	}
	
	public static String getRandomFML()
	{
		String address = "http://www.fmylife.com/random";
		
		try
		{
			Element element = Jsoup.connect(address).get().select("li[id]").first().select("p").first();
			String fml = StringEscapeUtils.unescapeHtml4(element.html()) + ".";
			return fml;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getDefinition(String word, int defNum) throws IOException
	{
		String address = "http://ninjawords.com/" + word.replace(' ', '+');
		Document doc = Jsoup.connect(address).get();
		
		if (doc.select("p[class=error]").size() > 0)
			return "Word \"" + word + "\" not yet defined.";
		
		if (doc.select("div[class=did-you-mean]").size() > 0)
			return "Did you mean: " + doc.select("span[class=correct-word]").first().text() + "?";
		
		Elements definitions = doc.select("div[class=definition]");
		String correctWord = Utils.toTitleCase(doc.select("dt[class=title-word").first().text());
		
																				   //.substring(1) because first character is a bullet point
		String def = StringEscapeUtils.unescapeHtml4(definitions.get(defNum-1).text().substring(1));
		
		return String.format("%s [%d/%d]: %s [%s]",
				correctWord, defNum, definitions.size(), def, address);
	}
}
