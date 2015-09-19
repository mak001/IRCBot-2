package com.mak001.ircbot.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.mak001.ircbot.Boot;
import com.mak001.ircbot.irc.io.InputThread;
import com.mak001.ircbot.irc.io.Logger;
import com.mak001.ircbot.irc.io.OutputThread;

public class Server {
	// PIRC's Input and output thread could be put together

	private final int port;
	private final String host;
	private final String password;

	private final Socket socket;
	private final InputThread input;
	private final OutputThread output;

	public Server(String host, int port) throws IOException {
		this(host, port, "");
	}

	public Server(String host, int port, String password) throws IOException {
		this.host = host;
		this.port = port;
		this.password = password;
		Boot.getLogger().log(Logger.LogType.BOT, "Creating socket");
		socket = new Socket(host, port);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		input = new InputThread(null, reader);
		output = new OutputThread(null, writer);
		Boot.getLogger().log(Logger.LogType.BOT, "Starting input");
		input.start();
		Boot.getLogger().log(Logger.LogType.BOT, "Starting output");
		output.start();

		if (password != null && !password.equals("")) {
			output.sendRawLine("PASS " + password);
		}
	}

	public InputThread getInputThread() {
		return input;
	}

	public OutputThread getOutputThread() {
		return output;
	}

	public String getServerName() {
		return host;
	}
}
