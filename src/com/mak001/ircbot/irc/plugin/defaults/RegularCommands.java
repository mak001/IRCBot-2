package com.mak001.ircbot.irc.plugin.defaults;

import com.mak001.ircbot.IRCBot;
import com.mak001.ircbot.SettingsManager;
import com.mak001.ircbot.api.Command;
import com.mak001.ircbot.api.Command.CommandAction;
import com.mak001.ircbot.api.Manifest;
import com.mak001.ircbot.api.Plugin;
import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.plugin.InvalidPluginException;
import com.mak001.ircbot.irc.plugin.PermissionHandler.RankPermission;

@Manifest(authors = { "MAK001" }, name = "Default Commands")
public class RegularCommands extends Plugin {

	private final String admin = "main.admin";
	private final String plugins = "main.plugins";
	private final String shutdown_node = "main.shutdown";
	private String prefix = SettingsManager.getCommandPrefix();

	public RegularCommands() {
		super("CMD");
		registerCommand(say);
		registerCommand(join);
		registerCommand(part);
		registerCommand(nick);
		registerCommand(disableCommand);
		registerCommand(enableCommand);
		registerCommand(set);
		registerCommand(broadcast);
		registerCommand(load_plugin);
		registerCommand(reload_plugin);
		registerCommand(unload_plugin);
		registerCommand(shutdown);
		registerCommand(about);
	}

	private boolean isChannel(String string) {
		return string.substring(0, 1).equals("#");
	}

	private Command say = new Command(this, "SAY", admin, new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			String s[] = additional.split(" ");
			if (isChannel(s[0])) {
				server.sendMessage(s[0], additional.replace(s[0] + " ", ""));
			} else {
				server.sendMessage(channel == null ? sender : channel, additional);
			}
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(sender, "Send a message to the current channel : Syntax: " + prefix + "SAY <stuff to say>");
			server.sendMessage(sender, "Send a message to a channel : Syntax: " + prefix + "SAY #CHANNEL <stuff to say>");
		}
	});

	private Command broadcast = new Command(this, "BROADCAST", admin, new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			for (String chan : server.getChannels().keySet()) {
				server.sendMessage(chan, additional);
			}
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(sender, "Broadcasts a specified sting in all joined channels");
		}
	});

	private Command join = new Command(this, "JOIN", admin, new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			if (!additional.contains(" ")) {
				if (isChannel(additional)) {
					server.addChannel(additional);
				} else {
					server.addChannel("#" + additional);
				}
			} else {
				server.sendMessage(sender, additional + " is not a valid channel");
			}
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(sender, "Joins a channel : Syntax: " + prefix + "JOIN <CHANNEL_NAME>");
		}
	});

	private Command part = new Command(this, new String[] { "LEAVE", "PART" }, admin, new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			if (additional != null && additional.equals("") && channel != null && !channel.equals("")) {
				// server.removeChannel(channel);
			} // TODO?
			if (!additional.contains(" ")) {
				if (isChannel(additional)) {
					server.removeChannel(additional);
				} else {
					server.removeChannel("#" + additional);
				}
			} else {
				server.sendMessage(sender, additional + " is not a valid channel");
			}
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(sender, "Leaves a channel : Syntax: " + prefix + "[LEAVE | PART] <CHANNEL_NAME>");
		}
	});

	private Command disableCommand = new Command(this, "disableCommand", RankPermission.OP.toString(), new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			if (server.getChannelByName(channel).disableCommand(additional.replace(SettingsManager.getCommandPrefix(), ""))) {
				server.sendMessage(channel, additional.replace(SettingsManager.getCommandPrefix(), "") + " has been disabled for " + channel);
			} else {
				server.sendMessage(channel, additional.replace(SettingsManager.getCommandPrefix(), "") + " has failed to be disabled for " + channel);
			}
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(channel, "Disables a command for the channel it is run in.");
		}
	});

	private Command enableCommand = new Command(this, "enableCommand", RankPermission.OP.toString(), new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			if (server.getChannelByName(channel).enableCommand(additional.replace(SettingsManager.getCommandPrefix(), ""))) {
				server.sendMessage(channel, additional.replace(SettingsManager.getCommandPrefix(), "") + " has been re-enabled for " + channel);
			} else {
				server.sendMessage(channel, additional.replace(SettingsManager.getCommandPrefix(), "") + " has failed to be re-enabled for " + channel);
			}
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(channel, "Re-enables a command for the channel it is run in.");
		}
	});

	private Command nick = new Command(this, "NICK", admin, new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			if (additional != null && !additional.equals("") && !additional.contains(" ")) {
				server.changeNick(additional);
			} else {
				server.sendMessage(sender, additional + " is not a valid nick");
			}
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(sender, "Changes the bot's current nick : Syntax: " + prefix + "NICK <NEW_NICK>");
		}
	});

	private Command set = new Command(this, "SET", admin, new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			String[] command_array = additional.split(" ");
			if (command_array[0].equalsIgnoreCase("NICK")) {
				server.changeNick(command_array[1]);

			} else if (command_array[0].equalsIgnoreCase("COMMAND_PREFIX")) {
				SettingsManager.changeCommandPrefix(command_array[1]);

			}
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(sender, "Changes the server's default nick to use : Syntax: " + prefix + "SET NICK <NEW_NICK>");
			server.sendMessage(sender, "Changes the bot's default command <PREFIX> (what commands start with)  : Syntax: " + prefix + "SET COMMAND_PREFIX <PREFIX>");
		}
	});

	private Command reload_plugin = new Command(this, "RELOAD PLUGIN", plugins, new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			Plugin plugin = getBot().getPluginManager().getPlugin(additional);
			if (plugin != null) {
				try {
					getBot().getPluginManager().reloadPlugin(plugin);
				} catch (InvalidPluginException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				server.sendMessage(sender, "No plugin named " + additional + ".");
			}
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(sender, "Reloads a plugin : Syntax: " + prefix + "RELOAD PLUGIN <PLUGIN'S_GENERAL_COMMAND>");
		}
	});

	private Command unload_plugin = new Command(this, "UNLOAD PLUGIN", plugins, new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			Plugin plugin = getBot().getPluginManager().getPlugin(additional);
			if (plugin != null) {
				getBot().getPluginManager().removePlugin(plugin);
			} else {
				server.sendMessage(sender, "No plugin named " + additional + ".");
			}
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(sender, "Unloads a plugin : Syntax: " + prefix + "UNLOAD PLUGIN <PLUGIN'S_GENERAL_COMMAND>");
		}
	});

	private Command load_plugin = new Command(this, "LOAD PLUGIN", plugins, new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			// bot.loadPlugin(additional, sender, 0);
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(sender, "Loads a plugin : Syntax: " + prefix + "LOAD PLUGIN <PLUGIN_CLASS_OR_JAR_NAME>");
		}
	});

	private Command shutdown = new Command(this, "SHUTDOWN", shutdown_node, new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			getBot().shutDown(server, sender);
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(sender, "Shuts down the bot : Syntax: " + prefix + "SHUTDOWN");
		}
	});

	private Command about = new Command(this, "ABOUT", new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			String target = channel == null ? sender : channel;
			if (additional != null && !additional.equals("")) {
				for (Plugin p : getBot().getPluginManager().getPlugins()) {
					if (p.GENERAL_COMMAND.equalsIgnoreCase(additional)) {
						server.sendMessage(sender, getPluginInfo(p));
						return;
					}
				}
			} else {
				server.sendMessage(target, "MAK001's bot built on PIRC " + IRCBot.VERSION);
			}
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(sender, "Gets info on the bot or plugin : Syntax: " + prefix + "ABOUT <GENERAL PLUGIN COMMAND>");
		}
	});

	private String getPluginInfo(Plugin p) {
		Manifest pluginManifest = p.getManifest();
		String authors = "";
		String site = pluginManifest.website().equals("") ? "" : "  ,  site: " + pluginManifest.website();
		String description = pluginManifest.description().equals("") ? "   no description" : "  description: " + pluginManifest.description();
		for (String author : pluginManifest.authors()) {
			authors = authors + authors == "" ? "" : ", " + author;
		}
		return pluginManifest.name() + " by: " + authors + "   version: " + pluginManifest.version() + " - " + site + " - " + description;
	}
}
