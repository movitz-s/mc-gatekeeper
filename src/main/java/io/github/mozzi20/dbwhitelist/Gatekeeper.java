package io.github.mozzi20.dbwhitelist;

import static org.bukkit.event.player.PlayerLoginEvent.Result.KICK_WHITELIST;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class Gatekeeper implements Listener {

	private DatabaseManager dbManager;
	private final String KICK_MESSAGE = "Tyvärr %s, vi kunde inte hitta dig i vår databas.\nRegistrera dig via mc.ssis.nu!";
	private final String BANNED_MESSAGE = "Vi har bannat dig av någon anledning. Fråga admins för mera info.";
 
	public Gatekeeper(DatabaseManager dbManager) {
		this.dbManager = dbManager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		String name = event.getPlayer().getName();

		try {
			PreparedStatement stmt = dbManager.getConnection()
					.prepareStatement("SELECT email, banned FROM users WHERE username = ?");
			stmt.setString(1, name);
			ResultSet result = stmt.executeQuery();
			if (!result.first()) {
				String message = String.format(KICK_MESSAGE, name);
				event.disallow(KICK_WHITELIST, message);
			} else {
				if (result.getBoolean(2)) {
					event.disallow(Result.KICK_BANNED, BANNED_MESSAGE);
				} else {
					String email = result.getString(1);
					email = email.split("@")[0];
					String newName = String.format("%s (%s)", name, email);
					event.getPlayer().setPlayerListName(newName);
					event.getPlayer().setDisplayName(newName);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
