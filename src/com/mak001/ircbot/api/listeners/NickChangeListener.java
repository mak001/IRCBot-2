package com.mak001.ircbot.api.listeners;

import com.mak001.ircbot.irc.plugin.Listener;

/**
 * Listens for Nick changes.
 * 
 * @author MAK001
 */
public interface NickChangeListener extends Listener {

	/**
	 * This method is called whenever someone (possibly us) changes nick on any
	 * of the channels that we are on.
	 * 
	 * @param oldNick
	 *            The old nick.
	 * @param login
	 *            The login of the user.
	 * @param hostname
	 *            The hostname of the user.
	 * @param newNick
	 *            The new nick.
	 */
	public void onNickChange(String oldNick, String login, String hostname, String newNick);

}
