package com.mak001.ircbot.irc;

import java.util.ArrayList;
import java.util.List;

/**
 * A channel object. Used to store the channel name, prefix and a list of users
 * and modes.
 * 
 * @author MAK001
 */
public class Channel {

	private final char PREFIX;
	private final String NAME;
	private final List<Mode> MODES = new ArrayList<Mode>();

	public Channel(char prefix, String name) {
		PREFIX = prefix;
		NAME = name;
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
	 * @return The prefix and the name of the channel
	 */
	public String getFullName() {
		return PREFIX + NAME;
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
	 *            - Modes to add.
	 * @return - If the modes were added.
	 */
	public boolean addModes(Mode[] modeList) {
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
	 *            - Modes to add.
	 * @return - If the modes were added.
	 */
	public boolean addModes(List<Mode> modeList) {
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
	 *            - Modes to remove.
	 * @return - If the modes were removed.
	 */
	public boolean removeModes(Mode[] modeList) {
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
	 *            - Modes to remove.
	 * @return - If the modes were removed.
	 */
	public boolean removeModes(List<Mode> modeList) {
		return MODES.removeAll(modeList);
	}

	/**
	 * List of channel modes that can be added or removed from the channel
	 * object.
	 * 
	 * @author MAK001
	 */
	public enum Mode {
		NO_CONTROL_CODES('c'), NO_EXTERNAL_MESSAGES('n'), OPS_TOPIC('t'), SECRET(
				's'), PARANOIA('p'), MODERATED('m'), INVITE_ONLY('i'), BANDWIDTH_SAVER(
				'B'), NO_CTCPS('C'), MODREG('M'), NO_NOTICES('N'), REGISTERED_ONLY(
				'R'), SSL_ONLY('S'), PERSIST_ONLY('z'), OPER_ONLY('O');

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
}
