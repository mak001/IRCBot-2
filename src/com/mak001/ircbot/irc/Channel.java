package com.mak001.ircbot.irc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import com.mak001.ircbot.Boot;
import com.mak001.ircbot.SettingsManager;
import com.mak001.ircbot.api.Command;
import com.mak001.ircbot.irc.io.Logger.LogType;
import com.mak001.ircbot.irc.plugin.defaults.Permissions;
import com.mak001.ircbot.irc.plugin.defaults.RegularCommands;

/**
 * A channel object. Used to store the channel name, prefix and a list of users
 * and modes.
 * 
 * @author MAK001
 */
public class Channel {

	private final char PREFIX;
	private final String NAME;
	private final String PASS;
	private final List<Mode> MODES = new ArrayList<Mode>();
	private final HashMap<String, User> users = new HashMap<String, User>();

	private final ArrayList<String> disabledCommands = new ArrayList<String>();

	private String topic;

	public Channel(Server server, String name) {
		this(server, name, "");
	}

	public Channel(Server server, String name, String key) {
		PREFIX = name.charAt(0);
		NAME = name;
		PASS = key;

		try {
			disabledCommands.addAll(SettingsManager.getDisabledCommands(server.getServerName(), name));
			SettingsManager.save();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}

	/**
	 * @return The prefix of the channel
	 */
	public char getPrefix() {
		return PREFIX;
	}

	/**
	 * @return The name of the channel
	 */
	public String getName() {
		return NAME;
	}

	/**
	 * Adds a mode to the user.
	 * 
	 * @param mode
	 *            - Mode to add.
	 * @return - If the mode was added.
	 */
	public boolean addMode(Mode mode) {
		return MODES.add(mode);
	}

	/**
	 * Adds modes to the user.
	 * 
	 * @param modeList
	 *            - Mode to add.
	 * @return - If the modes were added.
	 */
	public boolean addMode(Mode[] modeList) {
		boolean b = true;
		for (Mode mode : modeList) {
			if (!MODES.add(mode))
				b = false;
		}
		return b;
	}

	/**
	 * Adds modes to the user.
	 * 
	 * @param modeList
	 *            - Mode to add.
	 * @return - If the modes were added.
	 */
	public boolean addMode(List<Mode> modeList) {
		return MODES.addAll(modeList);
	}

	/**
	 * Removes a mode from the user.
	 * 
	 * @param mode
	 *            - Mode to remove.
	 * @return - If the mode was removed.
	 */
	public boolean removeMode(Mode mode) {
		return MODES.remove(mode);
	}

	/**
	 * Remove modes from the user.
	 * 
	 * @param modeList
	 *            - Mode to remove.
	 * @return - If the modes were removed.
	 */
	public boolean removeMode(Mode[] modeList) {
		boolean b = true;
		for (Mode mode : modeList) {
			if (!MODES.remove(mode))
				b = false;
		}
		return b;
	}

	/**
	 * Remove modes from the user.
	 * 
	 * @param modeList
	 *            - Mode to remove.
	 * @return - If the modes were removed.
	 */
	public boolean removeMode(List<Mode> modeList) {
		return MODES.removeAll(modeList);
	}

	public void addMode(String mode) {
		System.out.println(mode);
		char[] ms = mode.toCharArray();
		for (char m : ms) {
			if (!Character.isWhitespace(m) && m != '+' && m != ' ' && !hasMode(m)) {
				MODES.add(getMode(m));
				System.out.println("Adding mode " + m + " to channel " + NAME);
			}
		}
	}

	public void removeMode(String mode) {
		System.out.println(mode);
		char[] ms = mode.toCharArray();
		for (char m : ms) {
			if (m != '-') {
				if (MODES.contains(getMode(m))) {
					MODES.remove(getMode(m));
				}
			}
		}
	}

	public Mode getMode(char c) {
		for (Mode m : Mode.values()) {
			if (c == m.getCharSymbol())
				return m;
		}
		return null;
	}

	public boolean hasMode(char mode) {
		return MODES.contains(getMode(mode));
	}

	public boolean hasMode(Mode mode) {
		return MODES.contains(mode);
	}

	/**
	 * List of channel modes that can be added or removed from the channel
	 * object.
	 * 
	 * @author MAK001
	 */
	public enum Mode {
		NO_CONTROL_CODES('c'),
		NO_EXTERNAL_MESSAGES('n'),
		OPS_TOPIC('t'),
		SECRET('s'),
		PARANOIA('p'),
		MODERATED('m'),
		INVITE_ONLY('i'),
		BANDWIDTH_SAVER('B'),
		NO_CTCPS('C'),
		MODREG('M'),
		NO_NOTICES('N'),
		REGISTERED_ONLY('R'),
		SSL_ONLY('S'),
		PERSIST_ONLY('z'),
		OPER_ONLY('O');

		private int charCode;
		private char symbolCode;

		private Mode(char code) {
			symbolCode = code;
			charCode = (int) code;
		}

		/**
		 * @return An integer representation of the character code of the mode.
		 */
		public int getCode() {
			return charCode;
		}

		/**
		 * @return character code of the representation of the mode.
		 */
		public char getCharSymbol() {
			return symbolCode;
		}
	}

	public void addUser(User user) {
		synchronized (users) {
			users.put(user.getName(), user);
		}
	}

	public void removeUser(String name) {
		synchronized (users) {
			users.remove(name);
		}
	}

	public void changeUserName(String oldNick, String newNick) {
		synchronized (users) {
			if (users.containsKey(oldNick)) {
				User u = users.get(oldNick);
				u.updateName(newNick);
				users.remove(oldNick);
				users.put(newNick, u);
			} else {
				Boot.getLogger().log(LogType.BOT, "###### Failed to find user " + oldNick + " to change name to " + newNick);
			}
		}
	}

	public HashMap<String, User> getUsers() {
		return users;
	}

	public User getUserByName(String name) {
		return users.get(name);
	}

	public ArrayList<String> getDisabledCommands() {
		return disabledCommands;
	}

	public boolean disableCommand(String command) {
		if (Boot.getBot().getPluginManager().getCommandByName(command) != null) {
			if (Boot.getBot().getPluginManager().getCommandByName(command).getParentPlugin() instanceof RegularCommands
					|| Boot.getBot().getPluginManager().getCommandByName(command).getParentPlugin() instanceof Permissions) {
				Boot.getLogger().log(LogType.BOT, "Cannot disbale a default command");
				return false;
			}
			Boot.getLogger().log(LogType.BOT, "disabled " + command + " on " + getName());
			if (disabledCommands.add(command.toUpperCase())) {
				try {
					SettingsManager.save();
				} catch (IOException e) {
				}
				return true;
			}
		}
		return false;
	}

	public boolean isDisabled(Command command) {
		for (String s : command.getCommand()) {
			if (disabledCommands.contains(s.toUpperCase())) {
				return true;
			}
		}
		return false;
	}

	public boolean isDisabled(String command) {
		Command c = Boot.getBot().getPluginManager().getCommandByName(command);
		if (c != null) {
			for (String s : c.getCommand()) {
				if (disabledCommands.contains(s.toUpperCase())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean enableCommand(String command) {
		if (isDisabled(command)) {
			Command c = Boot.getBot().getPluginManager().getCommandByName(command);
			boolean b = false;
			if (c != null) {
				for (String s : c.getCommand()) {
					if (disabledCommands.remove(s.toUpperCase())) {
						b = true;
					}
				}
			}
			if (b) {
				try {
					SettingsManager.save();
				} catch (IOException e) {
				}
			}
			return b;
		}
		return false;
	}

	public String getPass() {
		return PASS;
	}
}
