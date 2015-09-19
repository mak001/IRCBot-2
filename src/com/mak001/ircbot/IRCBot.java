package com.mak001.ircbot;

import java.io.IOException;

import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.io.Logger;

public class IRCBot {

	public final static String ident = "Mak001s.bot.v2";
	private Server server;

	public IRCBot() {
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
	
	

}
