package com.mak001.ircbot.api.listeners;

/**
 * Listens for private messages.
 * 
 * @author MAK001
 */
public interface PrivateMessageListener extends Listener {

	/**
	 * This method is called whenever a private message is sent to the PircBot.
	 * Do NOT use for command listening.
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
	public void onPrivateMessage(String sender, String login, String hostname, String message);

}
