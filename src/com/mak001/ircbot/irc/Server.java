package com.mak001.ircbot.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

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
	private String botNick;
	private final String botPassword;
	private final IRCBot bot;

	private Socket socket;
	private InputThread input;
	private OutputThread output;

	private HashMap<String, Channel> channels = new HashMap<String, Channel>();

	public Server(IRCBot bot, String botNick, String botPassword, String host, int port) throws IOException {
		this(bot, botNick, botPassword, host, port, "");
	}

	public Server(IRCBot bot, String botNick, String botPassword, String host, int port, String serverPassword) throws IOException {
		this.bot = bot;
		this.host = host;
		this.port = port;
		this.botNick = botNick;
		this.botPassword = botPassword;
		this.serverPassword = serverPassword;
		connect();
	}

	public final HashMap<String, Channel> getChannels() {
		return channels;
	}

	public String getNick() {
		return botNick;
	}

	public void setNick(String newNick) {
		botNick = newNick;
	}

	public Channel getChannelByName(String chan) {
		return channels.get(chan);
	}

	public final void addChannel(String channel) {
		synchronized (channels) {
			if (!channels.containsKey(channel))
				channels.put(channel, new Channel(channel));
		}
	}

	public void removeAllChannels() {
		synchronized (channels) {
			channels.clear();
		}
	}

	public final void addUser(User user, String channel) {
		synchronized (channels) {
			if (channels.containsKey(channel))
				channels.get(channel).addUser(user);
		}
	}

	/**
	 * Removes an entire channel from our memory of users.
	 */
	public final void removeChannel(String channel) {
		channel = channel.toLowerCase();
		synchronized (channels) {
			channels.remove(channel);
		}
	}

	private void connect() throws UnknownHostException, IOException {
		log("Creating socket");
		socket = new Socket(host, port);
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
		// TODO - login
		// TODO - trigger onConnect in IRCBot
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

	public void sendRawLine(String line) {
		output.sendRawLine(line);

	}

	public void joinChannel(String channel) {
		// TODO Auto-generated method stub
	}

	public void leaveChannel(String channel) {
		// TODO
	}

	public void sendMessage(String sender, String string) {
		output.sendRawLine(sender);
		// TODO Auto-generated method stub
	}
}
