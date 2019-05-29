package io.github.mozzi20.dbwhitelist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Whois implements CommandExecutor {

	private DatabaseManager dbManager;

	public Whois(DatabaseManager dbManager) {
		this.dbManager = dbManager;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			sender.sendMessage("Du måste ange användarnamn.");
			return false;
		}
		Connection conn = dbManager.getConnection();
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT firstname, lastname, klass FROM users WHERE username = ? LIMIT 1");
			stmt.setString(1, args[0]);
			ResultSet result = stmt.executeQuery();
			if(result.first()) {
				String firstname = result.getString("firstname");
				String lastname = result.getString("lastname");
				String klass = result.getString("klass");
				
				String responseTemplate;
				if(firstname != null) {
					responseTemplate = "%s heter %s %s och går i %s.";
				} else {
					responseTemplate = "Vi hittade användarnamnet i databasen, men ingen yttligare information. (%s, %s, %s)";
				}
				sender.sendMessage(String.format(responseTemplate, firstname, lastname, klass));

			} else {
				sender.sendMessage("Kunde inte hitta användaren du sökte.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

}
