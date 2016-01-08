package com.mak001.ircbot.irc.io;

import java.io.BufferedWriter;

import com.mak001.ircbot.Boot;

public class OutputThread extends Thread {

	private BufferedWriter writer;
	private Queue queue;
	private boolean running = true;
	private long delay = 1000;

	public OutputThread(BufferedWriter writer) {
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
		try {
			while (running == true || queue.hasNext()) {
				Thread.sleep(delay);

				String line = (String) queue.next();
				if (line != null) {
					sendLine(line);
				} else {
					running = false;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * will finish outputting the queue and then stop
	 */
	public void dispose() {
		running = false;
	}
}