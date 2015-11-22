package com.mak001.ircbot.api.listeners;

import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.plugin.Listener;

public interface KickListener extends Listener {

	public void onKick(Server server, String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason);
}
