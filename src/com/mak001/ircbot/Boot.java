package com.mak001.ircbot;

import com.mak001.ircbot.irc.io.Logger;

public class Boot {

	private static final Logger logger = new Logger();

	public static void main(String[] argsList) {
		new IRCBot();
	}

	public static Logger getLogger() {
		return logger;
	}

}
