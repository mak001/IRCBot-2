package com.mak001.ircbot.irc.plugin.defaults;

import java.util.List;

import com.mak001.ircbot.Boot;
import com.mak001.ircbot.SettingsManager;
import com.mak001.ircbot.api.Command;
import com.mak001.ircbot.api.Command.CommandAction;
import com.mak001.ircbot.api.Manifest;
import com.mak001.ircbot.api.Plugin;
import com.mak001.ircbot.irc.Server;

@Manifest(authors = { "MAK001" }, name = "Permissions Plugin")
public class Permissions extends Plugin {

	private final String add = "perms.add";
	private final String remove = "perms.remove";
	private String prefix = SettingsManager.getCommandPrefix();

	public Permissions() {
		super("PEX");
		// TODO Auto-generated constructor stub
		registerCommand(get_user_permissions);
		registerCommand(add_user_permission);
		registerCommand(remove_user_permission);
	}

	private Command get_user_permissions = new Command(this, "GET PERMISSIONS", new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			String target = (additional != null && !additional.equals("")) ? additional : sender;
			List<String> per = Boot.getBot().getPermissionHandler().getUser(target).getPermissions();
			if (per == null) {
				server.sendMessage(sender, target + " has no permissions");
				return;
			}
			for (int i = 0; i < per.size(); i++) {
				server.sendMessage(sender, per.get(i));
			}
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(sender, "Gets the user's permissions  : Syntax: " + prefix + "GET PERMISSIONS <USER>");
		}
	});

	private Command add_user_permission = new Command(this, "ADD PERMISSION", add, new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			String[] additions = additional.split(" ");
			Boot.getBot().getPermissionHandler().addPermission(server, additions[0], additions[1]);
			server.sendMessage(sender, "Added premission node " + additions[1] + " for user " + additions[0]);
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(sender, "Adds a permission to a user  : Syntax: " + prefix + "ADD PERMISSION <USER> <PERMISSION_NODE>");
		}
	});

	private Command remove_user_permission = new Command(this, "REMOVE PERMISSION", remove, new CommandAction() {

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			String[] additions = additional.split(" ");
			if (Boot.getBot().getPermissionHandler().getUser(additions[0]) == null) {
				server.sendMessage(sender, "No user with the name " + additions[0]);
				return;
			}
			if (!Boot.getBot().getPermissionHandler().getUser(additions[0]).getPermissions().contains(additions[1])) {
				server.sendMessage(sender, "The user does not have that permission.");
				return;
			}

			Boot.getBot().getPermissionHandler().removePermission(additions[0], additions[1]);
			server.sendMessage(sender, "removed premission node " + additions[1] + " for user " + additions[0]);
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			server.sendMessage(sender, "Removes a permission to a user  : Syntax: " + prefix + "REMOVE USER PERMISSION <USER> <PERMISSION_NODE>");
		}
	});
}
