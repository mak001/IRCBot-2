package com.mak001.ircbot;

import com.mak001.ircbot.irc.io.Logger;

public class Boot {

	private static final Logger logger = new Logger();
	private static IRCBot bot;

	public static void main(String[] argsList) {
		bot = new IRCBot();
	}

	public static Logger getLogger() {
		return logger;
	}
	
	public static IRCBot getBot(){
		return bot;
	}

}
