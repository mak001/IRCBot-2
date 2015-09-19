package com.mak001.ircbot.api;

import com.mak001.ircbot.Boot;
import com.mak001.ircbot.IRCBot;
import com.mak001.ircbot.irc.io.Logger.LogType;

/**
 * All plugins must extend this class and have a {@link Manifest} A plugin will
 * NOT load without a {@link Manifest}, however, the {@link Manifest} can have
 * default values.
 * 
 * @author mak001
 */
public abstract class Plugin {

	/**
	 * Used for setting up the help and about commands
	 */
	public final String GENERAL_COMMAND;
	protected final IRCBot bot;

	/**
	 * @param bot
	 *            - Just make your constructor Plugin(IRCBot bot)
	 * @param GENERAL_COMMAND
	 *            - The command to respond to
	 */
	public Plugin(IRCBot bot, String GENERAL_COMMAND) {
		this.GENERAL_COMMAND = GENERAL_COMMAND;
		this.bot = bot;
	}

	/**
	 * @return The manifest of the plugin
	 */
	public final Manifest getManifest() {
		return getClass().getAnnotation(Manifest.class);
	}

	/**
	 * @return The name of the plugin
	 */
	public final String getName() {
		return getManifest().name();
	}

	/**
	 * @return The version of the plugin
	 */
	public final double getVersion() {
		return getManifest().version();
	}

	/**
	 * @return The authors of the plugin
	 */
	public final String[] getAuthors() {
		return getManifest().authors();
	}

	/**
	 * @return The description of the plugin
	 */
	public final String getDiscription() {
		return getManifest().description();
	}

	/**
	 * @return The website of the plugin
	 */
	public final String getWebsite() {
		return getManifest().website();
	}

	/**
	 * @return The command of the plugin
	 */
	public final String getCommand() {
		return GENERAL_COMMAND;
	}

	/**
	 * Use this to log anything in your plugins
	 * 
	 * @param log
	 *            - The text to log
	 */
	public final void log(String log) {
		Boot.getLogger().log(LogType.PLUGIN, log);
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof Plugin) {
			if (getName().equals(((Plugin) o).getName()))
				return true;
		}
		return false;
	}

}
