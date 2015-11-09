package com.mak001.ircbot.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;

import com.mak001.ircbot.Boot;
import com.mak001.ircbot.IRCBot;
import com.mak001.ircbot.irc.io.InputThread;
import com.mak001.ircbot.irc.io.Logger;
import com.mak001.ircbot.irc.io.OutputThread;

public class Server {

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

	public Server(IRCBot bot, String botNick, String botPassword, String host, int port) throws Exception {
		this(bot, botNick, botPassword, host, port, "");
	}

	public Server(IRCBot bot, String botNick, String botPassword, String host, int port, String serverPassword) throws Exception {
		this.bot = bot;
		this.host = host;
		this.port = port;
		this.botNick = botNick;
		this.botPassword = botPassword;
		this.serverPassword = serverPassword;
		connect();
	}

	public String getServerPass() {
		return serverPassword;
	}

	public final HashMap<String, Channel> getChannels() {
		return channels;
	}

	public String getNick() {
		return botNick;
	}

	public String getNickPass() {
		return botPassword;
	}

	public void setNick(String newNick) {
		botNick = newNick;
	}

	public Channel getChannelByName(String chan) {
		return channels.get(chan.replace("[" + IRCBot.CHANNEL_PREFIXES + "]+", ""));
	}

	public final void addChannel(String channel) {
		synchronized (channels) {
			if (!channels.containsKey(channel)) {
				channels.put(channel, new Channel(channel));
				this.joinChannel(channel);
			}
		}
	}

	public void addChannel(String channelName, String channelPass) {
		synchronized (channels) {
			if (!channels.containsKey(channelName)) {
				channels.put(channelName, new Channel(channelName, channelPass));
				this.joinChannel(channelName, channelPass);
			}
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

	private void connect() throws Exception {
		log("Creating socket");
		socket = new Socket(host, port);
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		output = new OutputThread(writer);

		log("Starting output");
		output.start();

		if (serverPassword != null && !serverPassword.equals("")) {
			output.sendRawLine("PASS " + serverPassword);
		}

		changeNick(botNick);
		output.sendRawLine("USER " + "MAK001s.BOT" + " 8 * :" + "Mak001's bot v2.01");

		if (botPassword != null && !botPassword.equals("")) {
			identify(botPassword);
		}

		// TODO - re-write
		// Read stuff back from the server to see if we connected.
		String line = null;
		while ((line = reader.readLine()) != null) {

			bot.handleLine(this, line);

			int firstSpace = line.indexOf(" ");
			int secondSpace = line.indexOf(" ", firstSpace + 1);
			if (secondSpace >= 0) {
				String code = line.substring(firstSpace + 1, secondSpace);

				if (code.equals("004")) {
					// We're connected to the server.
					break;
				} else if (code.equals("433")) {
					/*
					 * if (_autoNickChange) { tries++; nick = getName() + tries;
					 * OutputThread.sendRawLine(this, bwriter, "NICK " + nick);
					 * } else { socket.close(); _inputThread = null; throw new
					 * NickAlreadyInUseException(line); }
					 */
				} else if (code.equals("439")) {
					// No action required.
				} else if (code.startsWith("5") || code.startsWith("4")) {
					socket.close();
					throw new Exception("Could not log into the IRC server: " + line);
				}
			}

		}
		input = new InputThread(bot, this, reader);

		log("Starting input");
		input.start();
		this.log("*** Logged onto server.");

		bot.onConnect(this);
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

	public void sendMessage(String target, String message) {
		output.sendRawLine("PRIVMSG " + target + " :" + message);
	}

	public void sendNotice(String target, String message) {
		output.sendRawLine("NOTICE " + target + " :" + message);
	}

	public void sendAction(String target, String action) {
		sendCTCPCommand(target, "ACTION " + action);
	}

	public void sendCTCPCommand(String target, String command) {
		output.sendRawLine("PRIVMSG " + target + " :\u0001" + command + "\u0001");
	}

	public void joinChannel(String channel, String key) {
		channels.put(channel, new Channel(channel, key));
		output.sendRawLine("JOIN " + channel + " " + key);
	}

	public void joinChannel(String channel) {
		channels.put(channel, new Channel(channel));
		output.sendRawLine("JOIN " + channel);
	}

	public void partChannel(String channel) {
		leaveChannel("PART " + channel);
	}

	public void leaveChannel(String channel) {
		output.sendRawLine("PART " + channel);
	}

	public void partChannel(String channel, String reason) {
		leaveChannel("PART " + channel + " :" + reason);
	}

	public void leaveChannel(String channel, String reason) {
		output.sendRawLine("PART " + channel + " :" + reason);
	}

	public void quit() {
		quit("");
	}

	public void quit(String reason) {
		output.sendRawLine("QUIT :" + reason);
	}

	public void changeNick(String newNick) {
		output.sendRawLine("NICK " + newNick);
	}

	public void identify(String password) {
		output.sendRawLine("NICKSERV IDENTIFY " + password);
	}

	public void setMode(String channel, String mode) {
		this.sendRawLine("MODE " + channel + " " + mode);
	}
}
