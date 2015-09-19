package com.mak001.ircbot.irc.io;

import java.io.BufferedWriter;

import com.mak001.ircbot.Boot;
import com.mak001.ircbot.IRCBot;

public class OutputThread extends Thread {

	private IRCBot bot;
	private BufferedWriter writer;
	private Queue queue;

	public OutputThread(IRCBot bot, BufferedWriter writer) {
		this.bot = bot;
		this.writer = writer;
		queue = new Queue();
	}

	public void sendRawLine(String line) {
		queue.add(line);
	}

	private void sendLine(String line) {
		synchronized (writer) {
			try {
				writer.write(line + "\r\n");
				writer.flush();
				Boot.getLogger().log(Logger.LogType.IRC, ">>>" + line);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void run() {
		boolean running = true;
		while (running == true) {
			// TODO - message delay
			// Thread.sleep(_bot.getMessageDelay());

			String line = (String) queue.next();
			if (line != null) {
				sendLine(line);
			} else {
				running = false;
			}
		}
	}
}
