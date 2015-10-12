package com.mak001.ircbot.api.listeners;

import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.plugin.Listener;

/**
 * Listens for CTCP requests
 * 
 * @author MAK001
 */
public interface CTCPListener extends Listener {

	/**
	 * This is used to get a user's real name, but is just creepy.
	 */
	public void onFinger(Server server, String sourceNick, String sourceLogin, String sourceHostname, String target);

	/**
	 * This is for pings sent by a CTCP request, not by the server
	 * 
	 * @param pingValue
	 * @param target
	 * @param sourceHostname
	 * @param sourceLogin
	 */
	public void onPing(Server server, String user, String sourceLogin, String sourceHostname, String target, String pingValue);

	public void onVersion(Server server, String sourceNick, String sourceLogin, String sourceHostname, String target);

	public void onTime(Server server, String sourceNick, String sourceLogin, String sourceHostname, String target);
}
