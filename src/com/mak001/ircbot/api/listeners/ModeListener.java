package com.mak001.ircbot.api.listeners;

import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.plugin.Listener;

/**
 * A listener for mode changes
 * 
 * @author MAK001
 */
public interface ModeListener extends Listener {

	/**
	 * This method is called whenever a channel mode is changed.
	 * 
	 * @param server
	 * 
	 * @param channel
	 *            - The channel the mode change occurs for
	 * @param sourceNick
	 *            - The nick that set the mode
	 * @param sourceLogin
	 *            - The login of the user who changed thew channel mode
	 * @param sourceHostname
	 *            - The hostname of the user who changed the channel mode
	 * @param mode
	 *            - The mode that was changed
	 */
	void onChannelMode(Server server, String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode);

	/**
	 * This method is called whenever a user mode is changed.
	 * 
	 * @param channel
	 *            - The channel the user mode change occurs for
	 * @param sourceNick
	 *            - The nick that set the mode
	 * @param sourceLogin
	 *            - The login of the user who changed thew channel mode
	 * @param sourceHostname
	 *            - The hostname of the user who changed the channel mode
	 * @param mode
	 *            - The mode that was changed
	 */
	void onUserMode(Server server, String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode);

}
