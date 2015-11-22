package com.mak001.ircbot.irc.plugin.defaults;

import com.mak001.ircbot.api.Command;
import com.mak001.ircbot.api.Command.CommandAction;
import com.mak001.ircbot.api.Manifest;
import com.mak001.ircbot.api.Plugin;
import com.mak001.ircbot.irc.Server;

@Manifest(authors = { "MAK001" }, name = "Permissions Plugin")
public class Permissions extends Plugin {

	public Permissions() {
		super("PEX");
		// TODO Auto-generated constructor stub
		registerCommand(addPermission);
	}

	Command addPermission = new Command(this, "add permission", "perms.add", new CommandAction(){

		@Override
		public void onCommand(Server server, String channel, String sender, String login, String hostname, String additional) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onHelp(Server server, String channel, String sender, String login, String hostname) {
			// TODO Auto-generated method stub
			
		}
	});
	
}
