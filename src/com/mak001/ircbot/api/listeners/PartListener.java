package com.mak001.ircbot.api.listeners;

import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.plugin.Listener;

/**
 * Listens for parts from a channel.
 * 
 * @author MAK001
 */
public interface PartListener extends Listener {

	/**
	 * This method is called whenever someone (possibly us) parts a channel
	 * which we are on.
	 * 
	 * @param channel
	 *            The channel which somebody parted from.
	 * @param sender
	 *            The nick of the user who parted from the channel.
	 * @param login
	 *            The login of the user who parted from the channel.
	 * @param hostname
	 *            The hostname of the user who parted from the channel.
	 */
	public void onPart(Server server, String channel, String sender, String login, String hostname);

}
