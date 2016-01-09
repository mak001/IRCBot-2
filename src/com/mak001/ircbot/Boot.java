package com.mak001.ircbot;

import java.io.IOException;

import com.mak001.ircbot.irc.io.Logger;

public class Boot {

	private static final Logger logger = new Logger();
	private static IRCBot bot;

	public static void main(String[] argsList) throws Exception {
		bot = new IRCBot();
		SettingsManager.load();
		bot.pluginManager();
		bot.permissionHandler();

		bot.connect();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					bot.getPermissionHandler().save();
					SettingsManager.save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}));

	}

	public static Logger getLogger() {
		return logger;
	}

	public static IRCBot getBot() {
		return bot;
	}
}
