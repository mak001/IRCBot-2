package com.mak001.ircbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mak001.ircbot.gui.SetUp;
import com.mak001.ircbot.irc.Channel;
import com.mak001.ircbot.irc.Server;

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

	// Only thing that should persist over multiple servers
	private static final String COMMAND_PREFIX = "COMMAND_PREFIX";

	// network details, including nick and pass
	private static final String NETWORKS = "NETWORKS";
	private static final String NETWORK_NAME = "NETWORK_NAME";
	private static final String NETWORK_PASS = "NETWORK_PASS";
	private static final String USER_NAME = "USER_NAME";
	private static final String USER_PASS = "USER_PASS";

	// channel details, including disabled commands
	private static final String CHANNELS = "CHANNELS";
	private static final String CHANNEL_NAME = "CHANNEL_NAME";
	private static final String CHANNEL_PASS = "CHANNEL_PASS";
	private static final String DISABLED_COMMANDS = "DISABLED_COMMANDS";

	private static final String SETTINGS_FILE_STRING = SETTINGS_FOLDER + "settings.json";
	private static final File SETTINGS_FILE = new File(SETTINGS_FILE_STRING);

	// default prefix is stored
	private static String prefix = "!";

	// -list of networks/servers
	// - network name
	// - network pass
	// - user name
	// - user pass
	// - list of channels
	// -- channel name
	// -- channel pass
	// -- disabled commands

	/**
	 * Loads the settings
	 * 
	 * @throws IOException
	 * @throws JSONException
	 * 
	 * @throws Exception
	 */
	public static void load() throws JSONException, IOException {
		// TODO
		if (SETTINGS_FILE.exists()) {
			JSONObject obj = new JSONObject(getFileText(SETTINGS_FILE));
			prefix = obj.getString(COMMAND_PREFIX);

			JSONArray networks = obj.getJSONArray(NETWORKS);
			for (int i = 0; i < networks.length(); i++) {
				JSONObject network = networks.getJSONObject(i);

				String serverName = network.getString(NETWORK_NAME);
				String serverPass = network.getString(NETWORK_PASS);
				String userName = network.getString(USER_NAME);
				String userPass = network.getString(USER_PASS);

				Boot.getBot().addServer(new Server(Boot.getBot(), userName, userPass, serverName, 6667, serverPass));

				JSONArray channels = network.getJSONArray(CHANNELS);
				for (int j = 0; j < channels.length(); j++) {
					JSONObject channel = channels.getJSONObject(i);
					String channelName = channel.getString(CHANNEL_NAME);
					String channelPass = channel.getString(CHANNEL_PASS);
					Boot.getBot().getServer(serverName).addChannel(channelName, channelPass);
				}
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
				String u_name;
				String u_pass;
				String network;
				String channel;

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
				channel = setUp.getJTextFields()[1].getText();
				u_name = setUp.getJTextFields()[2].getText();
				u_pass = setUp.getJTextFields()[3].getText();
				prefix = setUp.getJTextFields()[4].getText();
				setUp.dispose();

				try {
					Boot.getBot().addServer(new Server(Boot.getBot(), u_name, u_pass, network, 6667));
					Boot.getBot().getServer(network).addChannel(channel);
				} catch (Exception e) {
					e.printStackTrace();
				}
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

		JSONArray networks = new JSONArray();
		for (Server server : Boot.getBot().getServers().values()) {
			JSONObject network = new JSONObject();

			JSONArray chans = new JSONArray();
			for (Channel channel : server.getChannels().values()) {
				JSONObject chan = new JSONObject();
				chan.put(CHANNEL_NAME, channel.getName());
				chan.put(CHANNEL_PASS, channel.getPass());

				JSONArray disabledCommands = new JSONArray();
				for (String command : channel.getDisabledCommands()) {
					disabledCommands.put(command);
				}
				chan.put(DISABLED_COMMANDS, disabledCommands);
				chans.put(chan);
			}

			network.put(CHANNELS, chans);
			network.put(USER_PASS, server.getNickPass());
			network.put(USER_NAME, server.getNick());
			network.put(NETWORK_PASS, server.getServerPass());
			network.put(NETWORK_NAME, server.getServerName());
			networks.put(network);
		}

		obj.put(NETWORKS, networks);

		FileWriter writer = new FileWriter(SETTINGS_FILE);
		writer.write(obj.toString(5));
		writer.close();
	}

	public static String getCommandPrefix() {
		return prefix;
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
