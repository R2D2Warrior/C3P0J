package com.r2d2warrior.c3p0j.listeners;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.ReplyConstants;
import org.pircbotx.User;
import org.pircbotx.UserChannelDao;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.RemoveChannelBanEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.hooks.events.SetChannelBanEvent;

@AddListener
public class BanListListener extends ListenerAdapter<PircBotX>
{
	public void onJoin(JoinEvent<PircBotX> event)
	{
		if (event.getUser().equals(event.getBot().getUserBot()))
			// Get the banlist for the channel
			event.getBot().sendRaw().rawLine("MODE " + event.getChannel().getName() + " b");
	}
	
	public void onServerResponse(ServerResponseEvent<PircBotX> event)
	{
		UserChannelDao<User, Channel> dao = event.getBot().getUserChannelDao();
		
		// This is triggered for each ban list entry
		if (event.getCode() == ReplyConstants.RPL_BANLIST)
		{
			// event.getParsedResponse() = [botNick, #chan, banMask, setterHostmask, timestamp]
			if (!dao.channelExists(event.getParsedResponse().get(1)))
				return;
			
			Channel channel = dao.getChannel(event.getParsedResponse().get(1));
			channel.getBanList().add(event.getParsedResponse().get(2));
		}
	}
	
	public void onSetChannelBan(SetChannelBanEvent<PircBotX> event)
	{
		event.getChannel().getBanList().add(event.getHostmask());
	}
	
	public void onRemoveChannelBan(RemoveChannelBanEvent<PircBotX> event)
	{
		event.getChannel().getBanList().remove(event.getHostmask());
	}
}
