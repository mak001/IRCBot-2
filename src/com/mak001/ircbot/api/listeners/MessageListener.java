package com.mak001.ircbot.api.listeners;

import com.mak001.ircbot.irc.plugin.Listener;

/**
 * Listens for Messages sent to a channel.
 * 
 * @author MAK001
 */
public interface MessageListener extends Listener {

	/**
	 * This method is called whenever a message is sent to a channel. Do NOT use
	 * for command listening.
	 * 
	 * @param channel
	 *            The channel to which the message was sent.
	 * @param sender
	 *            The nick of the person who sent the message.
	 * @param login
	 *            The login of the person who sent the message.
	 * @param hostname
	 *            The hostname of the person who sent the message.
	 * @param message
	 *            The actual message sent to the channel.
	 */
	public void onMessage(String channel, String sender, String login, String hostname, String message);

}
