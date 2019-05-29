package io.github.mozzi20.dbwhitelist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

	private Connection connection;
	private Plugin plugin;
	private String password;
	private String username;
	private final String url = "jdbc:mysql://localhost:3306/mc?autoReconnect=true";

	public DatabaseManager(Plugin plugin) {
		this.plugin = plugin;
		this.username = plugin.getConfig().getString("database.username");
		this.password = plugin.getConfig().getString("database.password");

		if (username != null || username.equals("")) {
			try {
				connection = DriverManager.getConnection(url, username, password);
				plugin.getLogger().info("Connected to database");
			} catch (SQLException e) {
				plugin.getLogger().warning("Couldn't connect do database");
				e.printStackTrace();
			}
		} else {
			plugin.getLogger().warning("No username in config.");
		}
	}

	private void checkConnection() {
		try {
			if (connection.isClosed()) {
				plugin.getLogger().info("DB conn was down. Reconnecting.");
				connection = DriverManager.getConnection(url, username, password);
				plugin.getLogger().info("DB conn is up again.");

			}
		} catch (SQLException e) {
			e.printStackTrace();
			plugin.getLogger().warning("Could not reconnect to database.");
		}
	}

	public Connection getConnection() {
		checkConnection();
		return connection;
	}

	public void close() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
