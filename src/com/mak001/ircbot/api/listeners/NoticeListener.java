package com.mak001.ircbot.api.listeners;

/**
 * Listens for Notices.
 * 
 * @author MAK001
 */
public interface NoticeListener extends Listener {

	/**
	 * This method is called whenever we receive a notice.
	 * 
	 * @param sourceNick
	 *            The nick of the user that sent the notice.
	 * @param sourceLogin
	 *            The login of the user that sent the notice.
	 * @param sourceHostname
	 *            The hostname of the user that sent the notice.
	 * @param target
	 *            The target of the notice, be it our nick or a channel name.
	 * @param notice
	 *            The notice message.
	 */
	public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice);

}
