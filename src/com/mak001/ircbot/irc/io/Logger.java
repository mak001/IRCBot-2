package com.mak001.ircbot.irc.io;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Logger {

	private LogLevel level;
	private final SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");

	public Logger() {
		this(LogLevel.ALL);
	}

	public Logger(LogLevel level) {
		this.level = level;
	}

	public void setLoggerLevel() {
		setLoggerLevel(LogLevel.ALL);
	}

	public void setLoggerLevel(LogLevel level) {
		this.level = level;
	}

	public void log(LogType type, String logString) {
		if (level.getAllowedLogTypes().contains(type)) {
			Date resultdate = new Date(System.currentTimeMillis());
			String time = sdf.format(resultdate);
			System.out.println(time + " " + logString);
		}
	}

	public enum LogLevel {
		NONE,
		PLUGINS_ONLY(LogType.PLUGIN),
		IRC_ONLY(LogType.IRC),
		BOT_ONLY(LogType.BOT),
		BOT_AND_IRC(LogType.BOT, LogType.IRC),
		BOT_AND_PLUGINS(LogType.BOT, LogType.PLUGIN),
		PLUGINS_AND_IRC(LogType.PLUGIN, LogType.IRC),
		ALL(LogType.BOT, LogType.PLUGIN, LogType.IRC);

		private final List<LogType> logTypes;

		LogLevel(LogType... logTypes) {
			this.logTypes = Arrays.asList(logTypes);
		}

		public List<LogType> getAllowedLogTypes() {
			return logTypes;
		}
	}

	public enum LogType {
		PLUGIN,
		BOT,
		IRC
	}

}
