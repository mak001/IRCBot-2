package com.mak001.ircbot.irc.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.mak001.ircbot.Boot;
import com.mak001.ircbot.IRCBot;
import com.mak001.ircbot.api.Plugin;


public class PluginLoader {

	private final Map<String, String> loaded_plugins = new HashMap<String, String>();
	private final IRCBot bot;
	private final PluginManager manager;

	public PluginLoader(IRCBot bot, PluginManager manager) {
		this.bot = bot;
		this.manager = manager;
	}

	public Plugin loadPlugin(String file_name) throws InvalidPluginException {
		return loadPlugin(new File(file_name));
	}

	public Plugin loadPlugin(File file) throws InvalidPluginException {
		System.out.println("Now loading: " + file.getName());
		if (file != null && file.isFile() && file.toString().toLowerCase().endsWith(".jar")) {
			try {
				return addPluginClass(getPluginMainClass(file), file);
			} catch (Exception e) {
				throw new InvalidPluginException(e);
			}
		}
		throw new InvalidPluginException();
	}

	public Plugin reloadPlugin(Plugin plugin) throws InvalidPluginException {
		return reloadPlugin(plugin.getName());
	}

	public Plugin reloadPlugin(String plugin_name) throws InvalidPluginException {
		return loadPlugin(loaded_plugins.get(plugin_name));
	}

	public void unloadPlugin(Plugin plugin) {
		unloadPlugin(plugin.getName());
	}

	public void unloadPlugin(String plugin_name) {
		loaded_plugins.remove(plugin_name);
	}

	private boolean isPluginClass(Class<?> clazz) {
		return clazz.getSuperclass() == Plugin.class;
	}

	private Plugin addPluginClass(Class<?> clazz, File file) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		if (isPluginClass(clazz)) {
			Constructor<?>[] cs = clazz.getConstructors();
			Object invoke = cs[0].newInstance(bot);
			Plugin plugin = (Plugin) (invoke);
			if (plugin.getManifest() == null) {
				System.out.println("Failed to load " + file.toString() + ".  No manifest.");
			} else {
				manager.addPlugin(plugin);
				if (loaded_plugins.get(plugin.getName()) == null) {
					loaded_plugins.put(plugin.getName(), file.getCanonicalPath());
				}
			}
		}
		return null;
	}

	private Class<?> getPluginMainClass(File file) throws ClassNotFoundException, IOException {
		return getPluginMainClass(file.getCanonicalPath());
	}

	private Class<?> getPluginMainClass(String file_path) throws ClassNotFoundException, IOException {
		URL url = new URL("jar:file:///" + file_path + "!/");
		URLConnection connection = url.openConnection();
		JarFile file = ((JarURLConnection) connection).getJarFile();
		Enumeration<JarEntry> eje = file.entries();

		ClassLoader STAFLoader = Boot.class.getClassLoader();
		URLClassLoader URLLoader = new URLClassLoader(new URL[] { url }, STAFLoader);

		while (eje.hasMoreElements()) {
			JarEntry je = eje.nextElement();

			if (je.getName().endsWith(".class") && !je.getName().contains("$")) {
				String className = je.getName().replaceAll("/", "\\.");
				className = className.substring(0, className.length() - 6);
				Class<?> clazz = Class.forName(className, true, URLLoader);
				if (isPluginClass(clazz)) return clazz;
			}
		}
		return null;
	}
}
