package io.github.mozzi20.dbwhitelist;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin implements Listener {

	private DatabaseManager dbManager;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		dbManager = new DatabaseManager(this);
		getServer().getPluginManager().registerEvents(new Gatekeeper(dbManager), this);
		getCommand("whois").setExecutor(new Whois(dbManager));
	}

	@Override
	public void onDisable() {
		dbManager.close();
	}

}
