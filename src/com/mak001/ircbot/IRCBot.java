package com.mak001.ircbot;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import com.mak001.ircbot.irc.Channel;
import com.mak001.ircbot.irc.ReplyConstants;
import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.User;
import com.mak001.ircbot.irc.io.Logger;
import com.mak001.ircbot.irc.plugin.PermissionHandler;
import com.mak001.ircbot.irc.plugin.PluginManager;
import com.mak001.ircbot.irc.plugin.defaults.Permissions;
import com.mak001.ircbot.irc.plugin.defaults.RegularCommands;

public class IRCBot {

	public final static String ident = "Mak001s.bot.v2";
	public static final String CHANNEL_PREFIXES = "#&+!";
	public static final String VERSION = "2.001";

	private final PermissionHandler permissionHandler;
	private final PluginManager manager;

	private final HashMap<String, Server> servers = new HashMap<String, Server>();

	public IRCBot() {
		Boot.setBot(this);
		permissionHandler = new PermissionHandler();
		manager = new PluginManager(this);
		manager.addPlugin(new RegularCommands());
		manager.addPlugin(new Permissions());
		manager.loadPluginFolder();
	}

	public PermissionHandler getPermissionHandler() {
		return permissionHandler;
	}

	public PluginManager getPluginManager() {
		return manager;
	}

	/**
	 * From PIRC
	 * 
	 * @param server
	 *            - The server that wants to the bot to handle the line
	 * @param line
	 *            - The line to handle
	 */
	public void handleLine(Server server, String line) {
		if (line.startsWith("PING ")) {
			// Respond to the ping and return immediately.
			this.onServerPing(server, line.substring(5));
			return;
		}

		String sourceNick = "";
		String sourceLogin = "";
		String sourceHostname = "";

		String[] data = line.split("\\s");
		String senderInfo = data[0];
		String command = data[1];
		String target = null;

		int exclamation = senderInfo.indexOf("!");
		int at = senderInfo.indexOf("@");
		if (senderInfo.startsWith(":")) {
			if (exclamation > 0 && at > 0 && exclamation < at) {
				sourceNick = senderInfo.substring(1, exclamation);
				sourceLogin = senderInfo.substring(exclamation + 1, at);
				sourceHostname = senderInfo.substring(at + 1);
			} else {

				if (data.length > 2) {
					String token = command;

					int code = -1;
					try {
						code = Integer.parseInt(token);
					} catch (NumberFormatException e) {
						// Keep the existing value.
					}

					if (code != -1) {
						String errorStr = token;
						String response = line.substring(line.indexOf(errorStr, senderInfo.length()) + 4, line.length());
						this.processServerResponse(server, code, response);
						return;
					} else {
						sourceNick = senderInfo;
						target = token;
					}
				} else {
					return;
				}

			}
		}

		command = command.toUpperCase();
		if (sourceNick.startsWith(":")) {
			sourceNick = sourceNick.substring(1);
		}
		if (target == null) {
			// target = tokenizer.nextToken();
			target = data[2];
		}
		if (target.startsWith(":")) {
			target = target.substring(1);
		}

		switch (command) {
		case "PRIVMSG":
			if (line.indexOf(":\u0001") > 0 && line.endsWith("\u0001")) {
				// CTCP requests
				String fullRequest = line.substring(line.indexOf(":\u0001") + 2, line.length() - 1);
				String request = fullRequest;
				if (request.contains(" ")) {
					request = request.split(" ")[0];
				}
				switch (request) {
				case "VERSION":
					manager.triggerVersionListeners(server, sourceNick, sourceLogin, sourceHostname, target);
					break;
				case "ACTION":
					manager.triggerActionListeners(server, sourceNick, sourceLogin, sourceHostname, target, fullRequest.substring(7));
					break;
				case "PING":
					manager.triggerPingListeners(server, sourceNick, sourceLogin, sourceHostname, target, fullRequest.substring(5));
					break;
				case "TIME":
					this.onTime(server, sourceNick, sourceLogin, sourceHostname, target);
					break;
				case "FINGER":
					manager.triggerFingerListeners(server, sourceNick, sourceLogin, sourceHostname, target);
					break;
				}

			} else if (CHANNEL_PREFIXES.indexOf(target.charAt(0)) >= 0) {
				// Message to a channel
				String message = line.substring(line.indexOf(" :") + 2);
				if (isCommand(message)) {
					String s = message.replaceFirst(SettingsManager.getCommandPrefix(), "");
					manager.onCommand(server, target, sourceNick, sourceLogin, sourceHostname, s);
				} else {
					manager.triggerMessageListeners(server, target, sourceNick, sourceLogin, sourceHostname, message);
				}

			} else { // Private message to the bot
				String message = line.substring(line.indexOf(" :") + 2);
				if (isCommand(message)) {
					String s = message.replaceFirst(SettingsManager.getCommandPrefix(), "");
					manager.onCommand(server, sourceNick, sourceNick, sourceLogin, sourceHostname, s);
				} else {
					manager.triggerPrivateMessageListeners(server, sourceNick, sourceLogin, sourceHostname, message);
				}
			}
			break;

		case "JOIN":
			server.getChannelByName(target).addUser(new User(sourceNick));
			manager.triggerJoinListeners(server, target, sourceNick, sourceLogin, sourceHostname);
			break;

		case "PART":
			server.getChannelByName(target).removeUser(sourceNick);
			if (sourceNick.equals(server.getNick())) {
				server.removeChannel(target);
			}
			manager.triggerPartListeners(server, target, sourceNick, sourceLogin, sourceHostname);
			break;

		case "NICK":
			for (Channel c : server.getChannels().values()) {
				if (c.getUsers().containsKey(sourceNick)) {
					c.changeUserName(sourceNick, target);
				}
			}
			if (sourceNick.equals(server.getNick())) {
				server.setNick(target);
			}
			manager.triggerNickChangeListeners(server, sourceNick, sourceLogin, sourceHostname, target);
			break;

		case "NOTICE":
			manager.triggerNoticeListeners(server, sourceNick, sourceLogin, sourceHostname, target, line.substring(line.indexOf(" :") + 2));
			break;

		case "QUIT":
			if (sourceNick.equals(server.getNick())) {
				server.removeAllChannels();
			} else {
				for (Channel c : server.getChannels().values()) {
					if (c.getUsers().containsKey(sourceNick)) {
						c.removeUser(sourceNick);
					}
				}
			}
			manager.triggerQuitListeners(server, sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2));
			break;

		case "KICK": // TODO
			String recipient = data[3];
			this.onKick(server, target, sourceNick, sourceLogin, sourceHostname, recipient, line.substring(line.indexOf(" :") + 2));
			break;

		case "MODE":
			// Somebody is changing the mode on a channel or user.
			String mode = line.substring(line.indexOf(target, 2) + target.length() + 1);
			if (mode.startsWith(":")) {
				mode = mode.substring(1);
			}
			if (CHANNEL_PREFIXES.indexOf(target.charAt(0)) >= 0) {
				onMode(server, target, sourceNick, sourceLogin, sourceHostname, mode);
			} else {
				manager.triggerUserModeListeners(server, target, sourceNick, sourceLogin, sourceHostname, mode);
			}
			break;

		case "TOPIC":
			this.onTopic(server, target, line.substring(line.indexOf(" :") + 2), sourceNick, System.currentTimeMillis(), true);
			break;

		case "INVITE":
			server.joinChannel(line.substring(line.indexOf(" :") + 2));
			// TODO
			// this.onInvite(server, target, sourceNick, sourceLogin,
			// sourceHostname, line.substring(line.indexOf(" :") + 2));
			break;

		default: // Unknown
			Boot.getLogger().log(Logger.LogType.BOT, "Unknown --- " + line);
			break;
		}
	}

	private void processServerResponse(Server server, int code, String response) {

		if (code == ReplyConstants.RPL_LIST) {
			// This is a bit of information about a channel.
			int firstSpace = response.indexOf(' ');
			int secondSpace = response.indexOf(' ', firstSpace + 1);
			int thirdSpace = response.indexOf(' ', secondSpace + 1);
			int colon = response.indexOf(':');
			String channel = response.substring(firstSpace + 1, secondSpace);
			int userCount = 0;
			try {
				userCount = Integer.parseInt(response.substring(secondSpace + 1, thirdSpace));
			} catch (NumberFormatException e) {
				// Stick with the value of zero.
			}
			String topic = response.substring(colon + 1);
			this.onChannelInfo(channel, userCount, topic);
		} else if (code == ReplyConstants.RPL_TOPIC) {
			// This is topic information about a channel we've just joined.
			int firstSpace = response.indexOf(' ');
			int secondSpace = response.indexOf(' ', firstSpace + 1);
			int colon = response.indexOf(':');
			String channel = response.substring(firstSpace + 1, secondSpace);
			String topic = response.substring(colon + 1);

			server.getChannelByName(channel).setTopic(topic);
		} else if (code == ReplyConstants.RPL_TOPICINFO) {
			String[] data = response.split("\\s");
			// TODO
			String channel = data[1];
			String setBy = data[2];
			long date = 0;
			try {
				date = Long.parseLong(data[3]) * 1000;
			} catch (NumberFormatException e) {
				// Stick with the default value of zero.
			}

			String topic = (String) server.getChannelByName(channel).getTopic();
			server.getChannelByName(channel).setTopic("");
			this.onTopic(server, channel, topic, setBy, date, false);
		} else if (code == ReplyConstants.RPL_NAMREPLY) {
			// This is a list of nicks in a channel that we've just joined.
			int channelEndIndex = response.indexOf(" :");
			String channel = response.substring(response.lastIndexOf(' ', channelEndIndex - 1) + 1, channelEndIndex);

			// StringTokenizer tokenizer = new
			// StringTokenizer(response.substring(response.indexOf(" :") + 2));
			String[] data = response.substring(response.indexOf(" :") + 2).split("\\s");
			for (int i = 0; i < data.length; i++) {
				String nick = data[i];
				String prefix = "";
				if (nick.startsWith("@")) {
					// User is an operator in this channel.
					prefix = "@";
				} else if (nick.startsWith("+")) {
					// User is voiced in this channel.
					prefix = "+";
				} else if (nick.startsWith(".")) {
					// Some wibbly status I've never seen before...
					prefix = ".";
				}
				nick = nick.substring(prefix.length());
				server.getChannelByName(channel).addUser(new User(nick));
			}
		} else if (code == ReplyConstants.RPL_ENDOFNAMES) {
			// This is the end of a NAMES list, so we know that we've got
			// the full list of users in the channel that we just joined.
			String channel = response.substring(response.indexOf(' ') + 1, response.indexOf(" :"));
			Collection<User> users = server.getChannelByName(channel).getUsers().values();
			this.onUserList(channel, users);
			// TODO

		} else if (code == ReplyConstants.RPL_CHANNELMODEIS) {
			String[] parts = response.split(" ");
			Channel chan = server.getChannelByName(parts[1]);
			String modes = parts[2];
			if (server.getChannels().containsKey(chan)) {
				if (modes.startsWith("+")) {
					chan.addMode(modes);
				} else {
					chan.removeMode(modes);
				}
			}
		}
		// TODO - More?
	}

	private void onChannelInfo(String channel, int userCount, String topic) {
		// TODO Auto-generated method stub
	}

	private void onUserList(String channel, Collection<User> users) {
		// TODO Auto-generated method stub

	}

	private void onTopic(Server server, String target, String substring, String sourceNick, long currentTimeMillis, boolean b) {
		// TODO Auto-generated method stub

	}

	private void onKick(Server server, String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
		// TODO - add in ban-handling
		server.getChannelByName(channel).removeUser(recipientNick);
		if (server.getNick().equalsIgnoreCase(recipientNick)) {
			server.joinChannel(channel);
		}
		// if (recipientNick.equals(server.getNick())) {
		// server.removeChannel(channel);
		// }
		server.getChannelByName(channel).removeUser(recipientNick);
		// TODO - kick listener
		manager.triggerKickListeners(server, channel, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
	}

	private void onMode(Server server, String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
		Channel chan = server.getChannelByName(channel);
		if (chan != null) {
			if (mode.contains(" ")) {
				String[] ms = mode.split(" ");
				for (String m : ms) {
					if (m.contains("+") || m.contains("-")) {
						mode = m;
					}
				}
			}
			if (mode.contains("-")) {
				chan.removeMode(mode);
			} else if (mode.contains("+")) {
				chan.addMode(mode);
			}
		}
		manager.triggerChannelModeListeners(server, channel, sourceNick, sourceLogin, sourceHostname, mode);
	}

	private void onServerPing(Server server, String response) {
		// TODO - ???
		server.sendRawLine("PONG " + response);
	}

	private void onTime(Server server, String sourceNick, String sourceLogin, String sourceHostname, String target) {
		// TODO
		server.getOutputThread().sendRawLine("NOTICE " + sourceNick + " :\u0001TIME " + new Date().toString() + "\u0001");
	}

	private boolean isCommand(String message) {
		return message.substring(0, 1).equals(SettingsManager.getCommandPrefix());
	}

	public HashMap<String, Server> getServers() {
		return servers;
	}

	public Server getServer(String name) {
		return servers.get(name);
	}

	public Server addServer(Server server) {
		return servers.put(server.getServerName(), server);
	}

	public Server removeServer(Server server) {
		return removeServer(server.getServerName());
	}

	public Server removeServer(String name) {
		return servers.remove(name);
	}

	public void shutDown(Server server, String sender) {
		try {
			SettingsManager.save();
			permissionHandler.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Server s : servers.values()) {
			s.quit("Shutdown requested by " + sender + " from " + server.getServerName());
			s.dispose();
		}
	}
}