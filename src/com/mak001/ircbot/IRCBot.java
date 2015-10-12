package com.mak001.ircbot;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.StringTokenizer;

import com.mak001.ircbot.irc.Channel;
import com.mak001.ircbot.irc.ReplyConstants;
import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.User;
import com.mak001.ircbot.irc.io.Logger;
import com.mak001.ircbot.irc.plugin.PermissionHandler;
import com.mak001.ircbot.irc.plugin.PluginManager;

public class IRCBot {

	public final static String ident = "Mak001s.bot.v2";
	private static final String CHANNEL_PREFIXES = "#&+!";

	private final PermissionHandler permissionHandler;
	private final PluginManager manager;

	private Server server;

	public IRCBot() {
		permissionHandler = new PermissionHandler();
		manager = new PluginManager(this);
		try {
			// TODO - redo
			Boot.getLogger().log(Logger.LogType.BOT, "Connecting to server");
			server = new Server(this, "jizz_V2", "", "irc.rizon.net", 6667);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Server getServer() {
		return server;
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

		// TODO - remove StringTokenizer as it is a legacy class
		StringTokenizer tokenizer = new StringTokenizer(line);
		String senderInfo = tokenizer.nextToken();
		String command = tokenizer.nextToken();
		String target = null;
		
		int exclamation = senderInfo.indexOf("!");
		int at = senderInfo.indexOf("@");
		if (senderInfo.startsWith(":")) {
			if (exclamation > 0 && at > 0 && exclamation < at) {
				sourceNick = senderInfo.substring(1, exclamation);
				sourceLogin = senderInfo.substring(exclamation + 1, at);
				sourceHostname = senderInfo.substring(at + 1);
			} else {

				if (tokenizer.hasMoreTokens()) {
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
			target = tokenizer.nextToken();
		}
		if (target.startsWith(":")) {
			target = target.substring(1);
		}

		// Check for CTCP requests.
		if (command.equals("PRIVMSG") && line.indexOf(":\u0001") > 0 && line.endsWith("\u0001")) {
			String request = line.substring(line.indexOf(":\u0001") + 2, line.length() - 1);
			if (request.equals("VERSION")) {
				// VERSION request
				this.onVersion(server, sourceNick, sourceLogin, sourceHostname, target);
			} else if (request.startsWith("ACTION ")) {
				// ACTION request
				this.onAction(server, sourceNick, sourceLogin, sourceHostname, target, request.substring(7));
			} else if (request.startsWith("PING ")) {
				// PING request
				this.onPing(server, sourceNick, sourceLogin, sourceHostname, target, request.substring(5));
			} else if (request.equals("TIME")) {
				// TIME request
				this.onTime(server, sourceNick, sourceLogin, sourceHostname, target);
			} else if (request.equals("FINGER")) {
				// FINGER request
				this.onFinger(server, sourceNick, sourceLogin, sourceHostname, target);
			} else if ((tokenizer = new StringTokenizer(request)).countTokens() >= 5 && tokenizer.nextToken().equals("DCC")) {
				// This is a DCC request.
			} else {
				// An unknown CTCP message - ignore it.
			}
		} else if (command.equals("PRIVMSG") && CHANNEL_PREFIXES.indexOf(target.charAt(0)) >= 0) {
			// This is a normal message to a channel.
			this.onMessage(server, target, sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2));
		} else if (command.equals("PRIVMSG")) {
			// This is a private message to us.
			this.onPrivateMessage(server, sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2));
		} else if (command.equals("JOIN")) {
			// Someone is joining a channel.
			this.onJoin(server, target, sourceNick, sourceLogin, sourceHostname);
		} else if (command.equals("PART")) {
			// Someone is parting from a channel.
			this.onPart(server, target, sourceNick, sourceLogin, sourceHostname);
		} else if (command.equals("NICK")) {
			// Somebody is changing their nick.
			this.onNickChange(server, sourceNick, sourceLogin, sourceHostname, target);
		} else if (command.equals("NOTICE")) {
			// Someone is sending a notice.
			this.onNotice(server, sourceNick, sourceLogin, sourceHostname, target, line.substring(line.indexOf(" :") + 2));
		} else if (command.equals("QUIT")) {
			// Someone has quit from the IRC server.
			this.onQuit(server, sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2));
		} else if (command.equals("KICK")) {
			// Somebody has been kicked from a channel.
			String recipient = tokenizer.nextToken();
			this.onKick(server, target, sourceNick, sourceLogin, sourceHostname, recipient, line.substring(line.indexOf(" :") + 2));
		} else if (command.equals("MODE")) {
			// Somebody is changing the mode on a channel or user.
			String mode = line.substring(line.indexOf(target, 2) + target.length() + 1);
			if (mode.startsWith(":")) {
				mode = mode.substring(1);
			}
			if (CHANNEL_PREFIXES.indexOf(target.charAt(0)) >= 0) {
				onMode(server, target, sourceNick, sourceLogin, sourceHostname, mode);
			} else {
				onUserMode(server, target, sourceNick, sourceLogin, sourceHostname, mode);
			}
		} else if (command.equals("TOPIC")) {
			// Someone is changing the topic.
			this.onTopic(server, target, line.substring(line.indexOf(" :") + 2), sourceNick, System.currentTimeMillis(), true);
		} else if (command.equals("INVITE")) {
			// Somebody is inviting somebody else into a channel.
			this.onInvite(server, target, sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2));
		} else {
			// unknown
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
			StringTokenizer tokenizer = new StringTokenizer(response);
			tokenizer.nextToken();
			String channel = tokenizer.nextToken();
			String setBy = tokenizer.nextToken();
			long date = 0;
			try {
				date = Long.parseLong(tokenizer.nextToken()) * 1000;
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

			StringTokenizer tokenizer = new StringTokenizer(response.substring(response.indexOf(" :") + 2));
			while (tokenizer.hasMoreTokens()) {
				String nick = tokenizer.nextToken();
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

	private void onInvite(Server server, String target, String sourceNick, String sourceLogin, String sourceHostname, String substring) {
		// TODO Auto-generated method stub

	}

	private void onTopic(Server server, String target, String substring, String sourceNick, long currentTimeMillis, boolean b) {
		// TODO Auto-generated method stub

	}

	private void onMessage(Server server, String channel, String sender, String login, String hostname, String message) {
		if (isCommand(message)) {
			String s = message.replaceFirst(SettingsManager.getCommandPrefix(), "");
			manager.onCommand(server, channel, sender, login, hostname, s);
		} else {
			manager.triggerMessageListeners(server, channel, sender, login, hostname, message);
		}
	}

	private void onPrivateMessage(Server server, String sender, String login, String hostname, String message) {
		if (isCommand(message)) {
			String s = message.replaceFirst(SettingsManager.getCommandPrefix(), "");
			manager.onCommand(server, sender, sender, login, hostname, s);
		} else {
			manager.triggerPrivateMessageListeners(server, sender, login, hostname, message);
		}
	}

	private void onAction(Server server, String sender, String login, String hostname, String target, String action) {
		manager.triggerActionListeners(server, sender, login, hostname, target, action);
	}

	private void onNotice(Server server, String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
		manager.triggerNoticeListeners(server, sourceNick, sourceLogin, sourceHostname, target, notice);
	}

	private void onJoin(Server server, String channel, String sender, String login, String hostname) {
		server.getChannelByName(channel).addUser(new User(sender));
		manager.triggerJoinListeners(server, channel, sender, login, hostname);
	}

	private void onPart(Server server, String channel, String sender, String login, String hostname) {
		server.getChannelByName(channel).removeUser(sender);
		if (sender.equals(server.getNick())) {
			server.removeChannel(channel);
		}
		manager.triggerPartListeners(server, channel, sender, login, hostname);
	}

	private void onNickChange(Server server, String oldNick, String login, String hostname, String newNick) {
		for (Channel c : server.getChannels().values()) {
			if (c.getUsers().containsKey(oldNick)) {
				c.changeUserName(oldNick, newNick);
			}
		}
		if (oldNick.equals(server.getNick())) {
			server.setNick(newNick);
		}
		manager.triggerNickChangeListeners(server, oldNick, login, hostname, newNick);
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

	private void onUserMode(Server server, String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
		manager.triggerUserModeListeners(server, channel, sourceNick, sourceLogin, sourceHostname, mode);
	}

	private void onQuit(Server server, String sourceNick, String sourceLogin, String sourceHostname, String reason) {
		if (sourceNick.equals(server.getNick())) {
			server.removeAllChannels();
		} else {
			for (Channel c : server.getChannels().values()) {
				if (c.getUsers().containsKey(sourceNick)) {
					c.removeUser(sourceNick);
				}
			}
		}
		manager.triggerQuitListeners(server, sourceNick, sourceLogin, sourceHostname, reason);
	}

	private void onVersion(Server server, String sourceNick, String sourceLogin, String sourceHostname, String target) {
		manager.triggerVersionListeners(server, sourceNick, sourceLogin, sourceHostname, target);
	}

	private void onPing(Server server, String sourceNick, String sourceLogin, String sourceHostname, String target, String pingValue) {
		manager.triggerPingListeners(server, sourceNick, sourceLogin, sourceHostname, target, pingValue);
	}

	private void onServerPing(Server server, String response) {
		// TODO - ???
		server.sendRawLine("PONG " + response);
	}

	private void onTime(Server server, String sourceNick, String sourceLogin, String sourceHostname, String target) {
		// TODO
		server.getOutputThread().sendRawLine("NOTICE " + sourceNick + " :\u0001TIME " + new Date().toString() + "\u0001");
	}

	private void onFinger(Server server, String sourceNick, String sourceLogin, String sourceHostname, String target) {
		manager.triggerFingerListeners(server, sourceNick, sourceLogin, sourceHostname, target);
	}

	private boolean isCommand(String message) {
		return message.substring(0, 1).equals(SettingsManager.getCommandPrefix());
	}
}