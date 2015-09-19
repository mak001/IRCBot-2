package com.mak001.ircbot.irc.io;

import java.io.BufferedReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import com.mak001.ircbot.Boot;
import com.mak001.ircbot.IRCBot;

public class InputThread extends Thread {

	private IRCBot bot;
	private BufferedReader reader;

	public InputThread(IRCBot bot, BufferedReader reader) {
		this.bot = bot;
		this.reader = reader;
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
							// handle line
						} catch (Throwable t) {
							// Stick the whole stack trace into a String so we
							// can output it nicely.
							StringWriter sw = new StringWriter();
							PrintWriter pw = new PrintWriter(sw);
							t.printStackTrace(pw);
							pw.flush();
							StringTokenizer tokenizer = new StringTokenizer(
									sw.toString(), "\r\n");
							/*
							 * synchronized (bot) { bot.log(
							 * "### Your implementation of PircBot is faulty and you have"
							 * ); bot.log(
							 * "### allowed an uncaught Exception or Error to propagate in your"
							 * ); bot.log(
							 * "### code. It may be possible for PircBot to continue operating"
							 * ); bot.log(
							 * "### normally. Here is the stack trace that was produced: -"
							 * ); bot.log("### "); while
							 * (tokenizer.hasMoreTokens()) { bot.log("### " +
							 * tokenizer.nextToken()); } }
							 */
						}
					}
					if (line == null) {
						running = false;
					}
				} catch (InterruptedIOException iioe) {
					// this.sendRawLine("PING "
					// + (System.currentTimeMillis() / 1000));
				}
			}
		} catch (Exception e) {
		}
	}
}
