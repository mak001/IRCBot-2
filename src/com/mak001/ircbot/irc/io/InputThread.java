package com.mak001.ircbot.irc.io;

import java.io.BufferedReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import com.mak001.ircbot.Boot;
import com.mak001.ircbot.IRCBot;
import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.io.Logger.LogType;

public class InputThread extends Thread {

	private final IRCBot bot;
	private BufferedReader reader;
	private Server server;

	public InputThread(IRCBot bot, BufferedReader reader) {
		this.bot = bot;
		this.reader = reader;
		this.server = bot.getServer();
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
							Boot.getLogger()
									.log(Logger.LogType.IRC, ">" + line);
							bot.handleLine(line);
						} catch (Throwable t) {
							// Stick the whole stack trace into a String so we
							// can output it nicely.
							StringWriter sw = new StringWriter();
							PrintWriter pw = new PrintWriter(sw);
							t.printStackTrace(pw);
							pw.flush();
							StringTokenizer tokenizer = new StringTokenizer(
									sw.toString(), "\r\n");

							Boot.getLogger().log(LogType.BOT,
									"### An error occured. Please fix this.");
							while (tokenizer.hasMoreTokens()) {
								Boot.getLogger().log(LogType.BOT,
										"### " + tokenizer.nextToken());
							}
						}
					}
					if (line == null) {
						running = false;
					}
				} catch (InterruptedIOException iioe) {
					server.getOutputThread().sendRawLine(
							"PING " + (System.currentTimeMillis() / 1000));
				}
			}
		} catch (Exception e) {
		}
	}
}