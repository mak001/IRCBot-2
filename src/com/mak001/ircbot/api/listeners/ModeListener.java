package com.mak001.ircbot.api.listeners;

/**
 * A listener for mode changes
 * 
 * @author MAK001
 */
public interface ModeListener extends Listener {

	/**
	 * This method is called whenever a channel mode is changed.
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
	void onChannelMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode);

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
	void onUserMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode);

}
