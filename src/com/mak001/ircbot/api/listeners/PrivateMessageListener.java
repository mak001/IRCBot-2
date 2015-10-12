package com.mak001.ircbot.api.listeners;

import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.plugin.Listener;

/**
 * Listens for private messages.
 * 
 * @author MAK001
 */
public interface PrivateMessageListener extends Listener {

	/**
	 * This method is called whenever a private message is sent to the PircBot.
	 * Do NOT use for command listening.
	 * @param server 
	 * 
	 * @param sender
	 *            The nick of the person who sent the private message.
	 * @param login
	 *            The login of the person who sent the private message.
	 * @param hostname
	 *            The hostname of the person who sent the private message.
	 * @param message
	 *            The actual message.
	 */
	public void onPrivateMessage(Server server, String sender, String login, String hostname, String message);

}
