package com.mak001.ircbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mak001.ircbot.gui.SetUp;

/**
 * The settings manager.
 * 
 * @author Mak001
 * 
 */
public final class SettingsManager {

	public static final String LINE_SEPERATOR = System.getProperty("line.separator");
	public static final String FILE_SEPERATOR = System.getProperty("file.separator");
	public static final String BOT_HOME = System.getProperty("user.home") + FILE_SEPERATOR + "IRCBot";
	public static final String SETTINGS_FOLDER = BOT_HOME + FILE_SEPERATOR + "Settings" + FILE_SEPERATOR;

	private static final String USER_NAME = "USER NAME";
	private static final String USER_PASS = "USER PASS";
	private static final String COMMAND_PREFIX = "COMMAND PREFIX";
	private static final String NETWORK = "NETWORK";
	private static final String CHANNELS = "CHANNELS";
	private static final String CHANNEL_NAME = "NAME";
	private static final String CHANNEL_PASS = "PASS";

	private static final String SETTINGS_FILE_STRING = SETTINGS_FOLDER + "settings.json";
	private static final File SETTINGS_FILE = new File(SETTINGS_FILE_STRING);

	private static String u_name = "jizz_V2";
	private static String u_pass;
	private static String prefix = "!";
	//TODO - make it save multiple networks
	private static String network;
	// name, pass
	private static HashMap<String, String> channels = new HashMap<String, String>();

	/**
	 * Loads the settings
	 * 
	 * @throws IOException
	 */
	public static void load() throws IOException {
		if (SETTINGS_FILE.exists()) {
			JSONObject obj = new JSONObject(getFileText(SETTINGS_FILE));
			u_name = obj.getString(USER_NAME);
			u_pass = obj.getString(USER_PASS);
			prefix = obj.getString(COMMAND_PREFIX);

			network = obj.getString(NETWORK);

			JSONArray chans = obj.getJSONArray(CHANNELS);
			for (int i = 0; i < chans.length(); i++) {
				JSONObject chan = chans.getJSONObject(i);
				channels.put(chan.getString(CHANNEL_NAME), chan.has(CHANNEL_PASS) ? chan.getString(CHANNEL_PASS) : null);
			}
		} else {
			SETTINGS_FILE.createNewFile();
			genDefault();
		}
	}

	private static String getFileText(File file) throws IOException {
		StringBuilder response = new StringBuilder();
		BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
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
	public static void genDefault() throws IOException {
		// TODO

		new Thread(new Runnable() {

			@Override
			public void run() {
				SetUp setUp = new SetUp();
				setUp.setVisible(true);
				while (setUp.isVisible()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				network = setUp.getJTextFields()[0].getText();
				channels.put(setUp.getJTextFields()[1].getText(), "");
				u_name = setUp.getJTextFields()[2].getText();
				u_pass = setUp.getJTextFields()[3].getText();
				prefix = setUp.getJTextFields()[4].getText();
				setUp.dispose();
			}
		}).run();
		save();
	}

	/**
	 * Saves all the users to a file
	 * 
	 * @throws IOException
	 */
	public static void save() throws IOException {

		JSONObject obj = new JSONObject();
		obj.put(COMMAND_PREFIX, prefix);
		obj.put(USER_NAME, u_name);
		obj.put(USER_PASS, u_pass);
		obj.put(NETWORK, network);

		JSONArray chans = new JSONArray();

		for (Entry<String, String> entry : channels.entrySet()) {
			JSONObject chan = new JSONObject();
			chan.put(CHANNEL_NAME, entry.getKey());
			chan.put(CHANNEL_PASS, entry.getValue());
			chans.put(chan);
		}
		obj.put(CHANNELS, chans);

		FileWriter writer = new FileWriter(SETTINGS_FILE);
		writer.write(obj.toString(5));
		writer.close();
	}

	public static String getServer() {
		return network;
	}

	public static String getCommandPrefix() {
		return prefix;
	}

	public static String getNick() {
		return u_name;
	}

	public static String getNickPass() {
		return u_pass;
	}

	public static HashMap<String, String> getChannels() {
		return channels;
	}

	public static void addChannel(String chan) {
		channels.put(chan, "");
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void removeChannel(String chan) {
		channels.remove(chan);
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void changeNick(String nick) {
		u_name = nick;
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void changeCommandPrefix(String _prefix) {
		prefix = _prefix;
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
