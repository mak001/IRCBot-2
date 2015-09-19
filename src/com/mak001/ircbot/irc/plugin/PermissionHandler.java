package com.mak001.ircbot.irc.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mak001.ircbot.SettingsManager;
import com.mak001.ircbot.irc.User;

// TODO - make permissions be able to take wild cards
public class PermissionHandler {

	private final List<User> users = new ArrayList<User>();
	private final static String USERS = "USERS";
	private static final String USER_FILE_STRING = SettingsManager.SETTINGS_FOLDER
			+ "user.json";
	private static final File USER_FILE = new File(USER_FILE_STRING);

	private final User DEFAULT_USER = new User("default");

	public PermissionHandler() {
		try {
			load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<User> getUsers() {
		return users;
	}

	public User getUser(String name) {
		for (User u : users) {
			if (u.getName().equalsIgnoreCase(name))
				return u;
		}
		return DEFAULT_USER;
	}

	/**
	 * Adds a permission node to a user
	 * 
	 * @param user
	 *            - The user to add the permission to
	 * @param permission
	 *            - The permission node to add
	 */
	public void addPermission(String user, String permission) {
		if (getUser(user).equals(DEFAULT_USER)) {
			User u = new User(user);
			u.addPermission(permission);
			users.add(u);
		} else {
			getUser(user).addPermission(permission);
		}
	}

	/**
	 * Adds a permission node to a user
	 * 
	 * @param user
	 *            - The user to add the permission to
	 * @param permission
	 *            - The permission node to add
	 */
	public void removePermission(String user, String permission) {
		if (!getUser(user).equals(DEFAULT_USER)) {
			getUser(user).removePermission(permission);
		}
	}

	/**
	 * Loads all the users from a file
	 * 
	 * @throws IOException
	 */
	private void load() throws IOException {
		if (USER_FILE.exists()) {
			JSONObject obj = new JSONObject(getFileText(USER_FILE));
			JSONArray _users = obj.getJSONArray(USERS);

			for (int i = 0; i < _users.length(); i++) {
				JSONObject user = _users.getJSONObject(i);
				JSONArray user_perms = user.getJSONArray(User.PERMISSIONS);

				List<String> perms = new ArrayList<String>();
				for (int j = 0; j < user_perms.length(); j++) {
					perms.add(user_perms.getString(j));
				}

				User u = new User(user.getString(User.NAME), perms);
				if (!users.contains(u))
					users.add(u);
			}
		} else {
			USER_FILE.createNewFile();
			genDefault();
		}
	}

	private String getFileText(File file) throws IOException {
		StringBuilder response = new StringBuilder();
		BufferedReader input = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		String strLine = null;
		while ((strLine = input.readLine()) != null) {
			response.append(strLine);
		}
		input.close();
		return response.toString();
	}

	/**
	 * Generates the default permissions and saves.
	 * 
	 * @throws IOException
	 */
	public void genDefault() throws IOException {
		List<String> perms = new ArrayList<String>();
		perms.add("main.admin");
		perms.add("main.plugins");
		perms.add("main.shutdown");
		perms.add("perms.add");
		perms.add("perms.remove");
		users.add(new User("mak001", perms));
		users.add(new User("import", perms));
		save();
	}

	/**
	 * Saves all the users to a file
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException { // TODO
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		for (User user : users) {
			array.put(user.getSaveObject());
		}
		obj.put(USERS, array);
		FileWriter writer = new FileWriter(USER_FILE);
		writer.write(obj.toString(5));
		writer.close();
	}
}