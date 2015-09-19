package com.mak001.ircbot.irc.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mak001.ircbot.api.Command;
import com.mak001.ircbot.api.Plugin;

public class CommandManager {

	private final Map<String, Plugin> plugin_commands;
	private final Map<String, Command> command_strings;
	private final List<Command> full_command_list;
	private final Map<String, List<Command>> commands_by_plugin_name;

	public CommandManager() {
		full_command_list = new ArrayList<Command>();
		command_strings = new HashMap<String, Command>();
		commands_by_plugin_name = new HashMap<String, List<Command>>();
		plugin_commands = new HashMap<String, Plugin>();
	}

	public boolean addPluginCommand(Plugin plugin) {
		System.out.println("Adding plugin command " + plugin.GENERAL_COMMAND
				+ " for " + plugin.getName());
		if (commands_by_plugin_name.get(plugin.getName().toUpperCase()) == null)
			commands_by_plugin_name.put(plugin.getName().toUpperCase(),
					new ArrayList<Command>());
		return plugin_commands.put(plugin.getCommand().toUpperCase(), plugin) != null;
	}

	public Plugin getPluginByCommand(String command) {
		return plugin_commands.get(command.toUpperCase());
	}

	public boolean addCommand(Command command) {
		System.out.println("Adding command " + command.getCommand()[0] + "; "
				+ getPluginNameByCommand(command));
		if (full_command_list.contains(command))
			return false;
		if (commands_by_plugin_name.get(getPluginNameByCommand(command)) == null) {
			commands_by_plugin_name.put(getPluginNameByCommand(command),
					new ArrayList<Command>());
		}
		commands_by_plugin_name.get(getPluginNameByCommand(command)).add(
				command);
		for (String c : command.getCommand()) {
			command_strings.put(c.toUpperCase(), command);
		}
		return full_command_list.add(command);
	}

	private String getPluginNameByCommand(Command command) {
		return command.getParentPlugin().getName().toUpperCase();
	}

	public List<Command> getCommands(Plugin plugin) {
		return getCommands(plugin.getName());
	}

	public List<Command> getCommands(String plugin) {
		System.out.println(plugin);
		System.out.println(commands_by_plugin_name.get(plugin));
		for (Command c : commands_by_plugin_name.get(plugin)) {
			System.out.println(" -- " + c.getCommand()[0]);
		}
		return commands_by_plugin_name.get(plugin);
	}

	public Command getCommand(String command) {
		command = command.toUpperCase();
		if (command_strings.get(command) != null) {
			return command_strings.get(command);
		}
		for (Command c : full_command_list) {
			for (String s : c.getCommand())
				if (command.startsWith(s.toUpperCase()))
					return c;
		}
		return null;
	}

	public String getAdditional(Command command, String message) {
		for (String string : command.getCommand()) {
			if (message.endsWith(" "))
				message = message.trim();
			if (message.equalsIgnoreCase(string))
				return "";
			message = message.replaceFirst("(?i)" + string, "").trim();
		}
		return message;
	}

	public void removeCommand(Command command) {
		if (full_command_list.contains(command)) {
			full_command_list.remove(command);
			for (String c : command.getCommand()) {
				command_strings.remove(c.toUpperCase());
			}
			commands_by_plugin_name.get(getPluginNameByCommand(command))
					.remove(command);
		}
	}

	public void removeCommand(String command) {
		removeCommand(command_strings.get(command.toUpperCase()));
	}

	public void removeCommands(Plugin plugin) {
		for (Command command : commands_by_plugin_name.get(plugin.getName()
				.toUpperCase())) {
			removeCommand(command);
		}
	}

	public void dropPluginCommands(Plugin plugin) {
		removeCommands(plugin);
		commands_by_plugin_name.remove(plugin.getName().toUpperCase());
	}

	public Map<String, List<Command>> getAllCommandsByPlugin() {
		return commands_by_plugin_name;
	}

	public Map<String, Plugin> getPluginCommands() {
		return plugin_commands;
	}

	public List<Command> getAllCommands() {
		return full_command_list;
	}
}
