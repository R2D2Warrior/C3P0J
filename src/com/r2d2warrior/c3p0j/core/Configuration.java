package com.r2d2warrior.c3p0j.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.pircbotx.cap.EnableCapHandler;

import lombok.Getter;
import lombok.Setter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.r2d2warrior.c3p0j.C3P0J;

@Getter
public class Configuration<B extends C3P0J> extends org.pircbotx.Configuration<C3P0J>
{
	
	protected final List<String> adminAccounts;
	protected final BiMap<String, String> prefixes;
	protected final List<String> blockedChannels;
	protected final String factoidPrefix;
	
	protected Configuration(Builder<B> builder)
	{
		super(builder);
		this.adminAccounts  = builder.getAdminAccounts();
		this.prefixes = HashBiMap.create(builder.getPrefixes());
		this.blockedChannels = builder.getBlockedChannels();
		this.factoidPrefix = builder.getFactoidPrefix();
	}
	
	@Getter
	@Setter
	public static class Builder<B extends C3P0J> extends org.pircbotx.Configuration.Builder<C3P0J>
	{
		/**
		 * List of the account names of bot admins
		 */
		protected List<String> adminAccounts = new ArrayList<>();
		/**
		 * Map of prefixes and the response command to be use in sendrawline
		 */
		protected Map<String, String> prefixes = Maps.newHashMap();
		/**
		 * List of channels not to join or send messages to
		 */
		protected List<String> blockedChannels = new ArrayList<>();
		/**
		 * The prefix used to call factoids
		 */
		protected String factoidPrefix;
		
		public Builder()
		{
			super();
			capHandlers.add(new EnableCapHandler("extended-join", true));
			capHandlers.add(new EnableCapHandler("account-notify", true));
		}
		
		public Builder(Configuration<C3P0J> configuration)
		{
			super(configuration);
			this.adminAccounts = configuration.getAdminAccounts();
			this.prefixes = configuration.getPrefixes();
			this.blockedChannels = configuration.getBlockedChannels();
			this.factoidPrefix = configuration.getFactoidPrefix();
		}
		
		public Builder(Builder<B> otherBuilder)
		{
			super(otherBuilder);
			this.adminAccounts = otherBuilder.getAdminAccounts();
			this.prefixes = otherBuilder.getPrefixes();
			this.blockedChannels = otherBuilder.getBlockedChannels();
			this.factoidPrefix = otherBuilder.getFactoidPrefix();
		}
		
		public void addAutoJoinChannels(String... channels)
		{
			for (String c : channels)
				addAutoJoinChannel(c);
		}
		
		/**
		 * Utility method for <code>{@link #getAdminAccounts().add(account)</code>
		 * @param account
		 * @return 
		 */
		public void addAdminAccount(String account)
		{
			getAdminAccounts().add(account);
		}
		
		public void addAdminAccounts(String... accounts)
		{
			getAdminAccounts().addAll(Arrays.asList(accounts));
		}
		
		/**
		 * Utility method for <code>{@link #getPrefixes().put(pre, command)</code>
		 * @param pre
		 * @param command
		 * @return 
		 */
		public void addPrefix(String pre, String command)
		{
			getPrefixes().put(pre, command);
		}
		
		/**
		 * Utility method for <code>{@link #getBlockedChannels().put(channel)</code>
		 * @param channel
		 * @return 
		 */
		public void addBlockedChannel(String channel)
		{
			getBlockedChannels().add(channel);
		}
		
		public void addBlockedChannels(String... channels)
		{
			getBlockedChannels().addAll(Arrays.asList(channels));
		}
		
		public Configuration<B> buildConfiguration()
		{
			return new Configuration<B>(this);
		}
	}
}
