package com.r2d2warrior.c3p0j.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;

public class WolframQuery
{
	private String appID;
	private WAEngine waEngine;
	private WAQuery waQuery;
	private WAQueryResult waQueryResult;
	@Getter
	private LinkedHashMap<String, String> fullPodMap;
	@Getter
	private String inputInterpretation;
	@Getter
	private String bestResult;
	@Getter
	private String basicURL;
	@Getter
	private LinkedHashMap<String, String> otherPodIDMap;
	@Getter
	private String bestID;
	private List<String> definitions;
	
	public WolframQuery(String query) throws WAException
	{
		// Create the engine and appID
		this.waEngine = new WAEngine();
		this.appID = "33EPX9-V4HRW2RL76";
		
		// Apply the appID and add plaintext format
		this.waEngine.setAppID(appID);
		this.waEngine.addFormat("plaintext");
		
		// Prepare the query with the input
		this.waQuery = waEngine.createQuery();
		this.waQuery.setInput(query);
		
		// Execute the query
		this.waQueryResult = waEngine.performQuery(this.waQuery);
		
		// Check for problems
		if (waQueryResult.isError() || !waQueryResult.isSuccess() || waQueryResult.getPods().length == 0)
			throw new WAException("Query failed: " + waEngine.toURL(this.waQuery));
		
		// Create the tidy pod map
		this.fullPodMap = formatResult();
		
		// Get the input interpretation
		this.inputInterpretation = fullPodMap.get("Input").trim().replaceAll(" +", " ");
		
		this.bestID = determineBestID();
		
		this.definitions = bestID.equals("Definition:WordData") ? formatDefinitions() : null;
		
		this.bestResult = determineBestResult();
		
		this.otherPodIDMap = getOtherPodIDs();
		
		try
		{
			// Try to create a URL from the query
			this.basicURL = "http://www.wolframalpha.com/input/?i=" + URLEncoder.encode(query, "UTF-8");
		}
		catch (UnsupportedEncodingException e) { }
	}
	
	private LinkedHashMap<String, String> formatResult()
	{
		LinkedHashMap<String, String> podMap = new LinkedHashMap<>();
		
		// Start pod parsing
		for (WAPod curPod : waQueryResult.getPods())
		{
			if (curPod.isError())
				continue;

			// Start subpod parsing
			List<String> subList = new ArrayList<>();
			for (WASubpod curSub : curPod.getSubpods())
			{
				subList.add(
						getSubPodData(curSub)
						);
			}
			podMap.put(curPod.getID(), StringUtils.join(subList, "; "));
		}
		return podMap;
	}
	
	private String getSubPodData(WASubpod curSub)
	{
		String subData = "";
		for (Object element : curSub.getContents())
			if (element instanceof WAPlainText)
				subData += " " + ((WAPlainText) element).getText().trim();
		return subData.trim().replace("\n", "; ");
	}
	
	private LinkedHashMap<String, String> getOtherPodIDs()
	{
		List<String> allPodIDs = new ArrayList<>(fullPodMap.keySet());
		allPodIDs.remove("Input");
		allPodIDs.remove(bestID);
		
		LinkedHashMap<String, String> idMap = new LinkedHashMap<>();
		for (String podID : allPodIDs)
		{
			String simpleID = podID.split(":")[0].toLowerCase();
			idMap.put(simpleID, podID);
		}
		return idMap;
	}
	
	private String determineBestID()
	{
		String best = "";
		Set<String> keys = fullPodMap.keySet();
		if (keys.contains("Result"))
			best = "Result";
		else if (keys.contains("DecimalApproximation"))
			best = "DecimalApproximation";
		else if (keys.contains("Definition:WordData"))
			best = "Definition:WordData";
		return best;
	}
	
	private String determineBestResult()
	{
		if (bestID.isEmpty())
			return "No best result.";
		else if (bestID.equals("Definition:WordData"))
			return definitions.get(0) + " [+" + (definitions.size()-1) + " more]";
		else
			return fullPodMap.get(bestID);
	}
	
	private List<String> formatDefinitions()
	{
		List<String> defsList = new ArrayList<>();
		String[] defsSplit = fullPodMap.get(bestID).trim().split(";\\s\\d\\s\\|\\s");
		for (int x = 0; x < defsSplit.length; x++)
		{
			String[] curDefSplit = defsSplit[x].split("\\s\\|\\s");
			
			int wordTypeIndex = (x == 0) ? 1 : 0;
			int defIndex = (x == 0) ? 2 : 1;
			
			String def = String.format("Def #%d - (%s) %s", x+1, curDefSplit[wordTypeIndex], curDefSplit[defIndex]);
			defsList.add(def);
		}
		return defsList;
	}
	
	public String getOtherResult(String simpleID)
	{
		String complexID = otherPodIDMap.get(simpleID.toLowerCase());
		return fullPodMap.get(complexID);
	}
	
	public List<String> getOtherIDs()
	{
		List<String> otherPodIDs = new ArrayList<>();
		for (String podID : new ArrayList<>(otherPodIDMap.values()))
		{
			otherPodIDs.add(podID.split(":")[0]);
		}
		return otherPodIDs;
	}
	
	public String getOtherDefinition(int num)
	{
		if (definitions == null)
			return null;
		else
			return definitions.get(num-1);
	}
}