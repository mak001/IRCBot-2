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
import com.mak001.ircbot.api.Manifest;
import com.mak001.ircbot.api.Plugin;

public class PluginLoader {

	private final Map<String, String> loaded_plugins = new HashMap<String, String>();
	private final PluginManager manager;

	public PluginLoader(PluginManager manager) {
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
		return clazz.getSuperclass() == Plugin.class && clazz.getAnnotation(Manifest.class) != null;
	}

	private Plugin addPluginClass(Class<?> clazz, File file) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, InvalidPluginException {
		if (isPluginClass(clazz)) {
			Constructor<?>[] cs = clazz.getDeclaredConstructors();
			Constructor<?> constructor = null;

			for (int i = 0; i < cs.length; i++) {
				if (cs[i].getParameterTypes().length == 0) {
					constructor = cs[i];
					constructor.setAccessible(true);
				}
			}
			if (constructor == null) {
				throw new InvalidPluginException();
			}
			Plugin plugin = (Plugin) constructor.newInstance();

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
				if (isPluginClass(clazz)) {
					return clazz;
				}
			}
		}
		return null;
	}
}
