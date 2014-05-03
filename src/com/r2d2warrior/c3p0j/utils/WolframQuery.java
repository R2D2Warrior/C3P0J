package com.r2d2warrior.c3p0j.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private WAEngine engine;
	private WAQuery query;
	private WAQueryResult queryResult;
	@Getter
	private Map<String, List<String>> podData;
	@Getter
	private String inputInterpretation;
	@Getter
	private String result;
	@Getter
	private List<String> otherPods;
	@Getter
	private String basicURL;
	
	public WolframQuery(String query) throws WAException
	{
		this.engine = new WAEngine();
		this.appID = new Config("config.json").getMap().get("api").get("wolfram");
		
		this.engine.setAppID(appID);
		this.engine.addFormat("plaintext");
		
		this.query = engine.createQuery();
		this.query.setInput(query);
		
		this.queryResult = engine.performQuery(this.query);
		
		if (queryResult.isError() || !queryResult.isSuccess() || queryResult.getPods().length == 0)
			throw new WAException("Query failed: " + engine.toURL(this.query));
		
		this.podData = formatResult();
		
		this.inputInterpretation = StringUtils.join(podData.get("Input"), " ").trim().replaceAll(" +", " ");
	
		sortResults();
		
		try
		{
			this.basicURL = "http://www.wolframalpha.com/input/?i=" + URLEncoder.encode(query, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			this.basicURL = "";
			e.printStackTrace();
		}
	}
	
	private Map<String, List<String>> formatResult()
	{
		/* String[] goodPods = {"Input", "Result", "DecimalApproximation", "NumberName","Definition:WordData",
		"UnitConversion", "AdditionalConversions", "DifferenceConversions"};*/
		Map<String, List<String>> podMap = new HashMap<>();
		for (WAPod curPod : queryResult.getPods())
		{
			if (curPod.isError())
				continue;
			List<String> subList = new ArrayList<>();
			for (WASubpod curSub : curPod.getSubpods())
			{
				String subData = "";
				for (Object element : curSub.getContents())
					if (element instanceof WAPlainText)
						subData += ((WAPlainText) element).getText().trim() + " ";
				subList.add(subData.replace("\n", "").trim());
			}
			podMap.put(curPod.getID(), subList);
		}
		return podMap;
	}
	
	private void sortResults()
	{
		Set<String> keys = podData.keySet();
		String bestKey = "";
		if (keys.contains("Result"))
			bestKey = "Result";
		else if (keys.contains("DecimalApproximation"))
			bestKey = "DecimalApproximation";
		else if (keys.contains("Definition:WordData"))
			bestKey = "Definition:WordData";
		
		this.result = bestKey.isEmpty() ? "No best result." : StringUtils.join(podData.get(bestKey), "; ");
		
		this.otherPods = getOtherPods(bestKey);
	}
	
	private List<String> getOtherPods(String bestKey)
	{
		List<String> others = new ArrayList<>();
		for (WAPod curPod : queryResult.getPods())
		{
			String id = curPod.getID();
			if (!id.equals("Input") && !id.equals(bestKey))
				others.add(id);
		}
		return others;
	}
}
