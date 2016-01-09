package com.mak001.ircbot.irc.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mak001.ircbot.IRCBot;
import com.mak001.ircbot.SettingsManager;
import com.mak001.ircbot.api.Command;
import com.mak001.ircbot.api.Plugin;
import com.mak001.ircbot.api.listeners.ActionListener;
import com.mak001.ircbot.api.listeners.CTCPListener;
import com.mak001.ircbot.api.listeners.JoinListener;
import com.mak001.ircbot.api.listeners.KickListener;
import com.mak001.ircbot.api.listeners.MessageListener;
import com.mak001.ircbot.api.listeners.ModeListener;
import com.mak001.ircbot.api.listeners.NickChangeListener;
import com.mak001.ircbot.api.listeners.NoticeListener;
import com.mak001.ircbot.api.listeners.PartListener;
import com.mak001.ircbot.api.listeners.PrivateMessageListener;
import com.mak001.ircbot.api.listeners.QuitListener;
import com.mak001.ircbot.irc.Server;
import com.mak001.ircbot.irc.plugin.defaults.Permissions;
import com.mak001.ircbot.irc.plugin.defaults.RegularCommands;

public class PluginManager {

	private final Map<String, Plugin> plugins = new HashMap<String, Plugin>();
	private final PluginLoader pluginLoader;
	private final CommandManager commandManager;
	private final IRCBot bot;

	private final List<List<? extends Listener>> listeners = new ArrayList<List<? extends Listener>>();
	private final List<ActionListener> actionListeners = new ArrayList<ActionListener>();
	private final List<CTCPListener> ctcpListeners = new ArrayList<CTCPListener>();
	private final List<JoinListener> joinListeners = new ArrayList<JoinListener>();
	private final List<MessageListener> messageListeners = new ArrayList<MessageListener>();
	private final List<ModeListener> modeListeners = new ArrayList<ModeListener>();
	private final List<NickChangeListener> nickChangeListeners = new ArrayList<NickChangeListener>();
	private final List<NoticeListener> noticeListeners = new ArrayList<NoticeListener>();
	private final List<PartListener> partListeners = new ArrayList<PartListener>();
	private final List<PrivateMessageListener> privateMessageListeners = new ArrayList<PrivateMessageListener>();
	private final List<QuitListener> quitListeners = new ArrayList<QuitListener>();
	private final List<KickListener> kickListeners = new ArrayList<KickListener>();

	public PluginManager(IRCBot bot) {
		this.bot = bot;
		listeners.add(actionListeners);
		listeners.add(ctcpListeners);
		listeners.add(joinListeners);
		listeners.add(messageListeners);
		listeners.add(modeListeners);
		listeners.add(nickChangeListeners);
		listeners.add(noticeListeners);
		listeners.add(partListeners);
		listeners.add(privateMessageListeners);
		listeners.add(quitListeners);
		listeners.add(kickListeners);

		pluginLoader = new PluginLoader(this);
		commandManager = new CommandManager();
	}

	/**
	 * Loads the plugin in the specified file
	 * <p>
	 * File must be valid according to the current enabled Plugin interfaces
	 * 
	 * @param file
	 *            File containing the plugin to load
	 * @return The Plugin loaded, or null if it was invalid
	 * @throws InvalidPluginException
	 *             Thrown when the specified file is not a valid plugin
	 * @throws UnknownDependencyException
	 *             If a required dependency could not be found
	 */
	public synchronized Plugin loadPlugin(File file) throws InvalidPluginException {
		if (file == null)
			return null;
		Plugin result = pluginLoader.loadPlugin(file);
		if (result != null) {
			addPlugin(result);
		}
		return result;
	}

	public void reloadPlugin(String name) throws InvalidPluginException {
		pluginLoader.reloadPlugin(name);
	}

	public void reloadPlugin(Plugin plugin) throws InvalidPluginException {
		pluginLoader.reloadPlugin(plugin);
	}

	public synchronized void addPlugin(Plugin plugin) {
		if (plugins.get(plugin.getName()) == null && plugin.isValid()) {
			plugins.put(plugin.getName(), plugin);
			commandManager.addPluginCommand(plugin);
			registerListeners(plugin);
		} else {
			// TODO ???
		}
	}

	private synchronized void registerListeners(Plugin plugin) {
		if (plugin instanceof ActionListener) {
			actionListeners.add((ActionListener) plugin);
		}
		if (plugin instanceof CTCPListener) {
			ctcpListeners.add((CTCPListener) plugin);
		}
		if (plugin instanceof JoinListener) {
			joinListeners.add((JoinListener) plugin);
		}
		if (plugin instanceof MessageListener) {
			messageListeners.add((MessageListener) plugin);
		}
		if (plugin instanceof ModeListener) {
			modeListeners.add((ModeListener) plugin);
		}
		if (plugin instanceof NickChangeListener) {
			nickChangeListeners.add((NickChangeListener) plugin);
		}
		if (plugin instanceof NoticeListener) {
			noticeListeners.add((NoticeListener) plugin);
		}
		if (plugin instanceof PartListener) {
			partListeners.add((PartListener) plugin);
		}
		if (plugin instanceof PrivateMessageListener) {
			privateMessageListeners.add((PrivateMessageListener) plugin);
		}
		if (plugin instanceof QuitListener) {
			quitListeners.add((QuitListener) plugin);
		}
		if (plugin instanceof KickListener) {
			kickListeners.add((KickListener) plugin);
		}
	}

	public synchronized void removePlugin(String name) {
		removePlugin(plugins.get(name));
	}

	public synchronized void removePlugin(Plugin plugin) { // TODO - test
		if (!isPermissions(plugin) && !isDefault(plugin)) {
			for (List<? extends Listener> _listeners : listeners) {
				for (Listener l : _listeners) {
					if (l.equals(plugin))
						_listeners.remove(plugin);
				}
			}
			commandManager.removeCommands(plugin);
			plugins.remove(plugin.getName());
			pluginLoader.unloadPlugin(plugin.getManifest().name());
		}
	}

	/**
	 * registers a command
	 * 
	 * @param command
	 *            - The command to register
	 * @return - If the command could be registered
	 * 
	 * @see ArrayList#add(Object)
	 */
	public boolean registerCommand(Command command) {
		return commandManager.addCommand(command);
	}

	/**
	 * unregisters a command
	 * 
	 * @param command
	 *            - The command to unregister
	 * @return - If the command could be unregistered
	 * 
	 * @see ArrayList#remove(Object)
	 */
	public void unregisterCommand(Command command) {
		commandManager.removeCommand(command);
	}

	/**
	 * @return A map of Commands with the linked plugins.
	 */
	public Map<String, List<Command>> getCommands() {
		return commandManager.getAllCommandsByPlugin();
	}

	/**
	 * @return A full list of commands.
	 */
	public List<Command> getAllCommands() {
		return commandManager.getAllCommands();
	}

	public boolean onCommand(Server server, String channel, String sender, String login, String hostname, String message) {
		boolean wasHelp = false;
		if (message.toUpperCase().startsWith("HELP")) {
			String add = message.replaceFirst("(?i)HELP", "").replaceFirst(" ", "");
			wasHelp = !onHelp(server, channel, sender, login, hostname, add);
		} // TODO - move the about and versions?
		Command command = commandManager.getCommand(message);
		if (command == null || server.getChannelByName(channel).isDisabled(command)) {
			// TODO - doesnt seem to work
			//
			return false;
		}
		if (bot.getPermissionHandler().getUser(sender).hasPermission(command.getPermission(), channel)) {
			if (wasHelp) {
				command.onHelp(server, channel, sender, login, hostname);
				return true;
			} else {
				command.onCommand(server, channel, sender, login, hostname, commandManager.getAdditional(command, message));
				return true;
			}
		}
		return false;
	}

	private boolean onHelp(Server server, String channel, String sender, String login, String hostname, String message) {
		if (message.equals("") || message.replace(" ", "").equals("")) {
			server.sendMessage(sender, "This will list every plugin command, use " + SettingsManager.getCommandPrefix() + "HELP <PLUGIN COMMAND>   for more help with an individual plugin.");
			for (Plugin p : getPlugins()) {
				String name = p.getManifest().name();
				System.out.println(name);
				server.sendMessage(sender, name + " - " + p.GENERAL_COMMAND);
			}
			return true;
		} else {
			String com = "";
			message = message.trim();
			if (message.contains(" ")) {
				com = message.split(" ")[0];
			} else {
				com = message;
			}
			Plugin plugin = commandManager.getPluginByCommand(com);
			if (plugin != null) {
				server.sendMessage(sender, "This will list every commands from " + plugin.getName());
				for (Command c : commandManager.getCommands(plugin.getName())) {
					if (bot.getPermissionHandler().getUser(sender).hasPermission(c.getPermission(), channel)) {
						c.onHelp(server, channel, sender, login, hostname);
					}
				}
				return true;
			} else {
				return false;
			}
		}
	}

	public void triggerActionListeners(Server server, String sender, String login, String hostname, String target, String action) {
		for (ActionListener listener : actionListeners) {
			listener.onAction(server, sender, login, hostname, target, action);
		}
	}

	public void triggerJoinListeners(Server server, String channel, String sender, String login, String hostname) {
		for (JoinListener listener : joinListeners) {
			listener.onJoin(server, channel, sender, login, hostname);
		}
	}

	public void triggerMessageListeners(Server server, String channel, String sender, String login, String hostname, String message) {
		for (MessageListener listener : messageListeners) {
			listener.onMessage(server, channel, sender, login, hostname, message);
		}
	}

	public void triggerNickChangeListeners(Server server, String oldNick, String login, String hostname, String newNick) {
		for (NickChangeListener listener : nickChangeListeners) {
			listener.onNickChange(server, oldNick, login, hostname, newNick);
		}
	}

	public void triggerNoticeListeners(Server server, String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
		for (NoticeListener listener : noticeListeners) {
			listener.onNotice(server, sourceNick, sourceLogin, sourceHostname, target, notice);
		}
	}

	public void triggerPartListeners(Server server, String channel, String sender, String login, String hostname) {
		for (PartListener listener : partListeners) {
			listener.onPart(server, channel, sender, login, hostname);
		}
	}

	public void triggerPrivateMessageListeners(Server server, String sender, String login, String hostname, String message) {
		for (PrivateMessageListener listener : privateMessageListeners) {
			listener.onPrivateMessage(server, sender, login, hostname, message);
		}
	}

	public void triggerQuitListeners(Server server, String sourceNick, String sourceLogin, String sourceHostname, String reason) {
		for (QuitListener listener : quitListeners) {
			listener.onQuit(server, sourceNick, sourceLogin, sourceHostname, reason);
		}
	}

	public void triggerKickListeners(Server server, String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
		for (KickListener listener : kickListeners) {
			listener.onKick(server, channel, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
		}
	}

	public void triggerFingerListeners(Server server, String sourceNick, String sourceLogin, String sourceHostname, String target) {
		for (CTCPListener listener : ctcpListeners) {
			listener.onFinger(server, sourceNick, sourceLogin, sourceHostname, target);
		}
	}

	public void triggerPingListeners(Server server, String user, String sourceLogin, String sourceHostname, String target, String pingValue) {
		for (CTCPListener listener : ctcpListeners) {
			listener.onPing(server, user, sourceLogin, sourceHostname, target, pingValue);
		}
	}

	public void triggerVersionListeners(Server server, String sourceNick, String sourceLogin, String sourceHostname, String target) {
		for (CTCPListener listener : ctcpListeners) {
			listener.onVersion(server, sourceNick, sourceLogin, sourceHostname, target);
		}
	}

	public void triggerChannelModeListeners(Server server, String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
		for (ModeListener listener : modeListeners) {
			listener.onChannelMode(server, channel, sourceNick, sourceLogin, sourceHostname, mode);
		}
	}

	public void triggerUserModeListeners(Server server, String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
		for (ModeListener listener : modeListeners) {
			listener.onUserMode(server, channel, sourceNick, sourceLogin, sourceHostname, mode);
		}
	}

	public Plugin getPlugin(String name) {
		return plugins.get(name);
	}

	public Collection<Plugin> getPlugins() {
		return plugins.values();
	}

	private boolean isDefault(Plugin plugin) {
		return plugin instanceof RegularCommands;
	}

	private boolean isPermissions(Plugin plugin) {
		return plugin instanceof Permissions;
	}

	public void loadPluginFolder() {
		File folder = new File(SettingsManager.BOT_HOME + SettingsManager.FILE_SEPERATOR + "Plugins");
		for (File file : folder.listFiles()) {
			try {
				String path = file.getCanonicalPath();
				if (path != null && path.endsWith(".jar")) {
					this.loadPlugin(file);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
