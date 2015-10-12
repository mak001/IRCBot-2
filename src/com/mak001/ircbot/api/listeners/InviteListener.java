package com.mak001.ircbot.api.listeners;

import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.plugin.Listener;

//TODO
public interface InviteListener extends Listener {

	public void onInvite(Server server, String target, String sourceNick, String sourceLogin, String sourceHostname, String substring);
}
