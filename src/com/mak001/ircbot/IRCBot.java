package com.mak001.ircbot;

import java.io.IOException;

import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.io.Logger;

public class IRCBot {

	public IRCBot() {
		try {
			Boot.getLogger().log(Logger.LogType.BOT, "Starting server");
			new Server("irc.rizon.net", 6667);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
