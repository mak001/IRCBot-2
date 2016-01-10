package com.mak001.ircbot.irc;

import java.util.ArrayList;
import java.util.List;

/**
 * A user object. Used to store what modes a user has.
 * 
 * @author MAK001
 */
public class User {

	private String nick;
	private final List<Mode> MODES = new ArrayList<Mode>();

	/**
	 * Creates a user.
	 * 
	 * @param nick
	 *            - The nick the user has at the creation of the user object.
	 */
	public User(String nick) {
		this.nick = nick;
	}

	/**
	 * Changes the user's nick.
	 * 
	 * @param newNick
	 *            - The new nick the user has.
	 * @return - The old nick the user had.
	 */
	public String updateName(String newNick) {
		String oldNick = nick;
		nick = newNick;
		return oldNick;
	}

	/**
	 * @return - The nick of the user
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * @return - The nick of the user
	 */
	public String getName() {
		return nick;
	}

	/**
	 * Adds a mode to the user.
	 * 
	 * @param mode
	 *            - Mode to add.
	 * @return - If the mode was added.
	 */
	public boolean addMode(Mode mode) {
		if (MODES.contains(mode))
			return true;
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
			if (!addMode(mode))
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

	public List<Mode> getModes() {
		return MODES;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof User)
			if (((User) o).getName().equalsIgnoreCase(nick))
				return true;
		return false;
	}

	/**
	 * List of user modes that can be added or removed from the user object.
	 * 
	 * @author MAK001
	 */
	public static enum Mode {
		VOICE("v", "+"),
		HALF_OP("h", "%"),
		OP("o", "@"),
		PROTECTED_OP("a", "&"),
		FOUNDER("q", "~");

		private final String charCode;
		private final String symbolCode;

		private Mode(String code, String symbol) {
			charCode = code;
			symbolCode = symbol;
		}

		/**
		 * @return A String representation of the character code of the mode.
		 */
		public String getCode() {
			return charCode;
		}

		/**
		 * @return character code of the mode.
		 */
		public char getCharCode() {
			return charCode.charAt(0);
		}

		/**
		 * @return A String representation of the character code of the symbol
		 *         of the mode.
		 */
		public String getSymbol() {
			return symbolCode;
		}

		/**
		 * @return character code of the representation of the mode.
		 */
		public char getCharSymbol() {
			return symbolCode.charAt(0);
		}
	}
}
