package com.mak001.ircbot.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.mak001.ircbot.Boot;
import com.mak001.ircbot.IRCBot;
import com.mak001.ircbot.irc.io.InputThread;
import com.mak001.ircbot.irc.io.Logger;
import com.mak001.ircbot.irc.io.OutputThread;

public class Server {
	// PIRC's Input and output thread could be put together

	private final int port;
	private final String host;
	private final String serverPassword;
	private final String botNick;
	private final String botPassword;
	private final IRCBot bot;

	private Socket socket;
	private InputThread input;
	private OutputThread output;

	public Server(IRCBot bot, String botNick, String botPassword, String host,
			int port) throws IOException {
		this(bot, botNick, botPassword, host, port, "");
	}

	public Server(IRCBot bot, String botNick, String botPassword, String host,
			int port, String serverPassword) throws IOException {
		this.bot = bot;
		this.host = host;
		this.port = port;
		this.botNick = botNick;
		this.botPassword = botPassword;
		this.serverPassword = serverPassword;
		connect();
	}

	private void connect() throws UnknownHostException, IOException {
		log("Creating socket");
		socket = new Socket(host, port);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		input = new InputThread(bot, reader);
		output = new OutputThread(writer);

		log("Starting input");
		input.start();
		log("Starting output");
		output.start();

		if (serverPassword != null && !serverPassword.equals("")) {
			output.sendRawLine("PASS " + serverPassword);
		}

		output.sendRawLine("NICK " + botNick);
		output.sendRawLine("USER " + IRCBot.ident + " 8 * :" + "V 2.0");
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

	private void log(String log) {
		Boot.getLogger().log(Logger.LogType.BOT, log);
	}
}
