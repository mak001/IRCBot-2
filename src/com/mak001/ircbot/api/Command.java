package com.mak001.ircbot.api;

import com.mak001.ircbot.irc.Server;

public class Command {

	private String[] command;
	private String permission;
	private Plugin plugin;
	private CommandAction commandAction;

	/**
	 * Creates a Command without any permissions required to use
	 * 
	 * @param plugin
	 *            - The plugin that made the command
	 * @param command
	 *            - The command
	 * @param commandAction
	 *            - This determines what the command will do when called
	 */
	public Command(Plugin plugin, String command, CommandAction commandAction) {
		this(plugin, new String[] { command }, null, commandAction);
	}

	/**
	 * Creates a Command that has aliases and does not require any permissions
	 * required to use
	 * 
	 * @param plugin
	 *            - The plugin that made the command
	 * @param command
	 *            - The command and aliases
	 * @param commandAction
	 *            - This determines what the command will do when called
	 */
	public Command(Plugin plugin, String[] command, CommandAction commandAction) {
		this(plugin, command, null, commandAction);
	}

	/**
	 * Creates a Command that requires permissions to use
	 * 
	 * @param plugin
	 *            - The plugin that made the command
	 * @param command
	 *            - The command
	 * @param permission
	 *            - The permission node required to use the command
	 * @param commandAction
	 *            - This determines what the command will do when called
	 */
	public Command(Plugin plugin, String command, String permission, CommandAction commandAction) {
		this(plugin, new String[] { command }, permission, commandAction);
	}

	/**
	 * Creates a Command with aliases that requires permissions to use
	 * 
	 * @param plugin
	 *            - The plugin that made the command
	 * @param command
	 *            - The command and aliases
	 * @param permission
	 *            - The permission node required to use the command
	 * @param commandAction
	 *            - This determines what the command will do when called
	 */
	public Command(Plugin plugin, String[] command, String permission, CommandAction commandAction) {
		this.plugin = plugin;
		this.command = command;
		this.permission = permission;
		this.commandAction = commandAction;
	}

	/**
	 * @return The plugin the command was made from
	 */
	public Plugin getParentPlugin() {
		return plugin;
	}

	/**
	 * @return The command and any aliases
	 */
	public String[] getCommand() {
		return command;
	}

	/**
	 * @return The permission node to use the command
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * Checks if the sender has the permission to use the command
	 * 
	 * @param channel
	 *            - The channel the command was sent to
	 * @param sender
	 *            - The sender of the command
	 * @param login
	 *            - The login of the sender of the command
	 * @param hostname
	 *            - The host name of the sender of the command
	 */
	public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
		commandAction.onCommand(server, channel, sender, login, hostname, additional);
	}

	/**
	 * Checks if the sender has the permission to use the command, then sends
	 * the help data
	 * 
	 * @param channel
	 *            - The channel the command was sent to [same as sender if its
	 *            in a PM]
	 * @param sender
	 *            - The sender of the command
	 * @param login
	 *            - The login of the sender of the command
	 * @param hostname
	 *            - The host name of the sender of the command
	 */
	public void onHelp(Server server, String channel, String sender, String login, String hostname) {
		commandAction.onHelp(server, channel, sender, login, hostname);
	}

	/**
	 * Determines what the command will do when called
	 * 
	 * @author MAK001
	 */
	public interface CommandAction {

		/**
		 * What the command does
		 * 
		 * @param server
		 *            - The server the command was issued on
		 * 
		 * @param channel
		 *            - The channel the command was sent to
		 * @param sender
		 *            - The sender of the command
		 * @param login
		 *            - The login of the sender of the command
		 * @param hostname
		 *            - The host name of the sender of the command
		 * @param additional
		 *            - Any arguments (make sure to handle invalid ones, because
		 *            people are idiots)
		 */
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional);

		/**
		 * What the command sends for help. Could be syntax, what it does, or
		 * both.
		 * 
		 * @param server
		 *            - The server the command was issued on
		 * 
		 * @param channel
		 *            - The channel the command was sent to
		 * @param sender
		 *            - The sender of the command
		 * @param login
		 *            - The login of the sender of the command
		 * @param hostname
		 *            - The host name of the sender of the command
		 */
		public void onHelp(Server server, String channel, String sender, String login, String hostname);
	}
}
