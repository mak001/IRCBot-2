package com.mak001.ircbot.irc.io;

import java.io.BufferedReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.mak001.ircbot.Boot;
import com.mak001.ircbot.IRCBot;
import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.io.Logger.LogType;

public class InputThread extends Thread {

	private final IRCBot bot;
	private BufferedReader reader;
	private Server server;

	public InputThread(IRCBot bot, Server server, BufferedReader reader) {
		this.bot = bot;
		this.reader = reader;
		this.server = server;
	}

	@Override
	public void run() {
		try {
			boolean running = true;
			while (running) {
				try {
					String line = null;
					while ((line = reader.readLine()) != null) {
						try {
							Boot.getLogger().log(Logger.LogType.IRC, ">" + line);
							bot.handleLine(server, line);
						} catch (Throwable t) {
							StringWriter sw = new StringWriter();
							PrintWriter pw = new PrintWriter(sw);
							t.printStackTrace(pw);
							pw.flush();

							String[] error = sw.toString().split("\r\n");

							Boot.getLogger().log(LogType.ERROR, "### An error occured. Please fix this.");
							for (String errorString : error) {
								Boot.getLogger().log(LogType.ERROR, "### " + errorString);
							}
						}
					}
					if (line == null) {
						running = false;
					}
				} catch (InterruptedIOException iioe) {
					server.getOutputThread().sendRawLine("PING " + (System.currentTimeMillis() / 1000));
				}
			}
		} catch (Exception e) {
		}
	}
}