package com.mak001.ircbot.irc.plugin;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.mak001.ircbot.Boot;
import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.User;
import com.mak001.ircbot.irc.User.Mode;
import com.mak001.ircbot.irc.io.Logger.LogType;
import com.mak001.ircbot.irc.plugin.PermissionHandler.RankPermission;

public class PermissionUser {

	public static final String NAME = "NAME";
	public static final String SERVER = "SERVER";
	public static final String PERMISSIONS = "PERMISSIONS";
	public static final String NO_PERMISSIONS = "NO.PERMISSIONS";

	private String nick;
	private final Server server;
	private final List<String> permissions;

	public PermissionUser(String nick, Server server) {
		this(nick, new ArrayList<String>(), server);
	}

	public PermissionUser(String nick, List<String> perms, Server server) {
		this.nick = nick;
		permissions = perms;
		this.server = server;
		if (server != null) {
			Boot.getLogger().log(LogType.BOT, nick + " :: " + server.getServerName());
		} else {
			Boot.getLogger().log(LogType.BOT, nick + " :: null");
		}
	}

	public String getName() {
		return nick;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public boolean addPermission(String permission) {
		if (permissions.contains(permission))
			return false;
		return permissions.add(permission);
	}

	public boolean removePermission(String permission) {
		if (permissions.contains(permission))
			return false;
		return permissions.remove(permission);
	}

	/**
	 * 
	 * @param permission
	 *            - The permission to check for
	 * @return - If the user has the permission
	 */
	public boolean hasPermission(String permission, String channel) {
		if (getPermissions().contains(NO_PERMISSIONS)) {
			return false;
		}
		if (permission == null || permission.equals("")) {
			return true;
		}
		return getPermissions().contains(permission) || hasPermssionInChannel(permission, channel);
	}

	private boolean hasPermssionInChannel(String permission, String channel) {
		RankPermission p = RankPermission.getEnum(permission);
		Boot.getLogger().log(LogType.BOT, p.toString());

		if (server == null) {
			Boot.getLogger().log(LogType.BOT, "Server is null for " + nick);
			return false;
		}
		User user = server.getChannelByName(channel).getUserByName(nick);
		if (user.getNick().equalsIgnoreCase("mak001")) {
			user.addMode(Mode.OP);
			Boot.getLogger().log(LogType.BOT, "gave  " + nick + " founder");
		}

		List<Mode> modes = user.getModes();

		for (Mode m : modes) {
			Boot.getLogger().log(LogType.BOT, m.toString());
		}

		switch (p) { // TODO - fails to trickle down
		case FOUNDER:
			if (modes.contains(Mode.FOUNDER)) {
				Boot.getLogger().log(LogType.BOT, nick + " is founder");
				return true;
			}
		case PROTECTED_OP:
			if (modes.contains(Mode.PROTECTED_OP)) {
				Boot.getLogger().log(LogType.BOT, nick + " is SOP");
				return true;
			}
		case OP:
			if (modes.contains(Mode.OP)) {
				Boot.getLogger().log(LogType.BOT, nick + " is OP");
				return true;
			}
		case HALF_OP:
			if (modes.contains(Mode.HALF_OP)) {
				Boot.getLogger().log(LogType.BOT, nick + " is HOP");
				return true;
			}
		case VOICE:
			if (modes.contains(Mode.VOICE)) {
				Boot.getLogger().log(LogType.BOT, nick + " is voice");
				return true;
			}
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * Creates and returns a JSONObject if, and only if, the user has at least
	 * on permission.
	 * 
	 * @return - A JSONObject if the user has at least one permission, otherwise
	 *         it returns null.
	 */
	public JSONObject getSaveObject() {
		if (permissions.isEmpty())
			return null;
		JSONObject obj = new JSONObject();
		obj.put(NAME, nick);
		if (server != null) {
			obj.put(SERVER, server.getServerName());
		} else {
			obj.put(SERVER, "null");
		}
		obj.put(PERMISSIONS, permissions);
		return obj;
	}

}
