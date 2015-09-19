package com.mak001.ircbot;

import java.io.IOException;

import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.io.Logger;
import com.mak001.ircbot.irc.io.OutputThread;
import com.mak001.ircbot.irc.plugin.PermissionHandler;
import com.mak001.ircbot.irc.plugin.PluginManager;

public class IRCBot {

	public final static String ident = "Mak001s.bot.v2";

	private final PermissionHandler permissionHandler;
	private final PluginManager manager;

	private Server server;
	private OutputThread out;

	public IRCBot() {
		permissionHandler = new PermissionHandler();
		manager = new PluginManager(this);
		try {
			Boot.getLogger().log(Logger.LogType.BOT, "Starting server");
			server = new Server(this, "jizz_V2", "", "irc.rizon.net", 6667);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Server getServer() {
		return server;
	}

	public PermissionHandler getPermissionHandler() {
		return permissionHandler;
	}

	public PluginManager getPluginManager() {
		return manager;
	}

	public void handleLine(String line) {
		if (line.startsWith("PING ")) {
			// Respond to the ping and return immediately.
			this.onServerPing(line.substring(5));
			return;
		}

		// TODO
	}

	private void onServerPing(String response) {
		out.sendRawLine("PONG " + response);
	}

	public void sendMessage(String sender, String string) {
		out.sendRawLine(sender);
		// TODO Auto-generated method stub
	}

}