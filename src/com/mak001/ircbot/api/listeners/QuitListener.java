package com.mak001.ircbot.api.listeners;

/**
 * Listens for quits.
 * 
 * @author MAK001
 */
public interface QuitListener extends Listener {

	/**
	 * This method is called whenever someone (possibly us) quits from the
	 * server. We will only observe this if the user was in one of the channels
	 * to which we are connected.
	 * 
	 * @param sourceNick
	 *            The nick of the user that quit from the server.
	 * @param sourceLogin
	 *            The login of the user that quit from the server.
	 * @param sourceHostname
	 *            The hostname of the user that quit from the server.
	 * @param reason
	 *            The reason given for quitting the server.
	 */
	public void onQuit(String sourceNick, String sourceLogin,
			String sourceHostname, String reason);

}
