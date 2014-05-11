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
	@Getter
	private List<String> definitions;
	
	public WolframQuery(String query) throws WAException
	{
		// Create the engine and appID
		this.waEngine = new WAEngine();
		this.appID = new Config("config.json").getString("api", "wolfram");
		
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
		
		// Get the ID for the most logical result
		this.bestID = determineBestID();
		
		// Get the list of definitions if the best result is definitions
		this.definitions = bestID.equals("Definition:WordData") ? formatDefinitions() : null;
		
		// Format the best result
		this.bestResult = formatBestResult();
		
		// Create the map of other pod IDs (simpleID:complexID)
		this.otherPodIDMap = getOtherPodIDs();
		
		try
		{
			// Try to create a URL from the query
			this.basicURL = "http://www.wolframalpha.com/input/?i=" + URLEncoder.encode(query, "UTF-8");
		}
		catch (UnsupportedEncodingException e) { }
	}
	
	/**
	 * Formats the {@link WAQueryResult} into a {@link LinkedHashMap}
	 * @return The formatted {@link LinkedHashMap}
	 */
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
	
	/**
	 * Formats the <code>WASubpod</code> data to a String<br>
	 * Used in {@link #formatResult()}
	 * @param curSub The <code>WASubpod</code> to format
	 * @return A String representation of the <code>WASubpod</code>
	 * @see WASubpod
	 */
	private String getSubPodData(WASubpod curSub)
	{
		String subData = "";
		for (Object element : curSub.getContents())
			if (element instanceof WAPlainText)
				subData += " " + ((WAPlainText) element).getText().trim();
		return subData.trim().replace("\n", "; ");
	}
	
	/**
	 * Builds a map of other {@link WAPod} IDs<br>
	 * Other pods are all pods except <code>Input</code> and <code>bestID</code>
	 * @return A {@link LinkedHashMap} of {@link WAPod} IDs in format <code>{simpleID:complexID}</code>
	 */
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
	
	/**
	 * Determines which {@link WAPod} ID is the most logical result for the query
	 * @return The <code>bestID</code>
	 */
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
	
	/**
	 * Formats the best result
	 * @return A String representation of the best result
	 */
	private String formatBestResult()
	{
		if (bestID.isEmpty())
			return "No best result.";
		else if (bestID.equals("Definition:WordData"))
			return definitions.get(0) + " [+" + (definitions.size()-1) + " more]";
		else
			return fullPodMap.get(bestID);
	}
	
	/**
	 * Formats the definitions result into a List of definitions
	 * @return A List of all definitions
	 */
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
	
	/**
	 * Gets a List of IDs in <code>otherPodIDMap</code>
	 * @return A List of other {@link WAPod} IDs
	 */
	public List<String> getOtherIDs()
	{
		List<String> otherPodIDs = new ArrayList<>();
		for (String podID : new ArrayList<>(otherPodIDMap.values()))
		{
			otherPodIDs.add(podID.split(":")[0]);
		}
		return otherPodIDs;
	}
	
	/**
	 * Gets a different result type from its <code>simpleID</code>
	 * @param simpleID The simplified ID of the result
	 * @return The other result
	 */
	public String getOtherResult(String simpleID)
	{
		String complexID = otherPodIDMap.get(simpleID.toLowerCase());
		return complexID.split(":")[0] + ": " + fullPodMap.get(complexID);
	}
	
	/**
	 * Gets a different definition than the first one
	 * @param num The definition number. <code>0 < num <= defintions.size()</code>
	 * @return The different definition
	 */
	public String getOtherDefinition(int num)
	{
		if (definitions == null)
			return null;
		else
			return definitions.get(num-1);
	}
}