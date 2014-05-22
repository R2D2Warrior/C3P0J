package com.r2d2warrior.c3p0j.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AccessLevel;

@Getter
public class Permissions
{
	private Config config;
	private Map<String, Object> groupConfig;
	private List<Group> groups;
	private List<String> groupNames;
	@Getter(AccessLevel.NONE)
	public static final Group DEFAULT_GROUP = new Permissions().new Group("DEFAULT", new ArrayList<String>(), 100);
	
	public Permissions()
	{
		this.config = new Config();
		this.groupConfig = config.getMap().get("groups");
		init();
	}
	
	@SuppressWarnings("unchecked")
	private List<Group> parseGroups()
	{
		int groupCount = groupConfig.keySet().size();
		List<Group> groups = new ArrayList<>(Collections.nCopies(groupCount, new Group()));
		for (String groupName : groupConfig.keySet())
		{
			Map<String, Object> group = (Map<String, Object>) groupConfig.get(groupName);
			int rank = (int) (long) group.get("rank");
			List<String> users = (List<String>) group.get("users");
			groups.set(rank-1, new Group(groupName, users, rank));
		}
		return groups;
	}
	
	public Group getGroup(String name)
	{
		for (Group g : groups)
			if (g.getName().equalsIgnoreCase(name))
				return g;
		return null;
	}
	
	public Group getUserGroup(String account)
	{
		for (Group g : groups)
		{
			for (String user : g.getUsers())
				if (user.equalsIgnoreCase(account))
					return g;
		}
		return DEFAULT_GROUP;
	}
	
	public void setUserGroup(String account, String groupName)
	{
		removeUser(account);
		getUsersFromMap(groupName).add(account);
		update();
	}
	
	public void removeUser(String account)
	{
		Group userGroup = getUserGroup(account);
		if (userGroup.equals(DEFAULT_GROUP))
				return;
		
		Iterator<String> iter = getUsersFromMap(userGroup.getName()).iterator();
		while (iter.hasNext())
		{
			if (iter.next().equalsIgnoreCase(account))
			{
				iter.remove();
				break;
			}
		}
		update();
	}
	
	public void update()
	{
		config.getMap().remove("groups");
		config.getMap().put("groups", groupConfig);
		config.updateFile();
		
		init();
	}
	
	private void init()
	{
		this.groups = parseGroups();
		this.groupNames = new ArrayList<String>();
		for (Group g : groups)
			groupNames.add(g.getName().toLowerCase());
		
		groups.add(DEFAULT_GROUP);
		groupNames.add("DEFAULT");
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getUsersFromMap(String groupName)
	{
		Map<String, Object> group = (Map<String, Object>) groupConfig.get(groupName);
		List<String> users = (List<String>) group.get("users");
		return users;
	}
	
	@Getter
	@ToString
	@AllArgsConstructor
	@NoArgsConstructor
	public class Group
	{
		private String name;
		private List<String> users;
		private int rank;
	}
}