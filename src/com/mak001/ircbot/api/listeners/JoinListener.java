package com.mak001.ircbot.api.listeners;

/**
 * Listens for Joins to a channel.
 * 
 * @author MAK001
 */
public interface JoinListener extends Listener {

	/**
	 * This method is called whenever someone (possibly us) joins a channel
	 * which we are on.
	 * 
	 * @param channel
	 *            The channel which somebody joined.
	 * @param sender
	 *            The nick of the user who joined the channel.
	 * @param login
	 *            The login of the user who joined the channel.
	 * @param hostname
	 *            The hostname of the user who joined the channel.
	 */
	public void onJoin(String channel, String sender, String login,
			String hostname);

}
