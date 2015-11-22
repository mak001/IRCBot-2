package com.mak001.ircbot.irc.plugin;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class PermissionUser {

	public static final String NAME = "NAME";
	public static final String PERMISSIONS = "PERMISSIONS";
	public static final String NO_PERMISSIONS = "NO.PERMISSIONS";

	private String nick;
	private final List<String> permissions;

	public PermissionUser(String nick) {
		this(nick, new ArrayList<String>());
	}

	public PermissionUser(String nick, List<String> perms) {
		this.nick = nick;
		permissions = perms;
	}

	public String getName() {
		return nick;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	// TODO
	public boolean addPermission(String permission) {
		if (permissions.contains(permission))
			return false;
		return permissions.add(permission);
	}

	// TODO
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
	public boolean hasPermission(String permission) {
		if (getPermissions().contains(NO_PERMISSIONS)) {
			return false;
		}
		if (permission == null || permission.equals("")) {
			return true;
		}
		return getPermissions().contains(permission);
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
		obj.put(PERMISSIONS, permissions);
		return obj;
	}

}
