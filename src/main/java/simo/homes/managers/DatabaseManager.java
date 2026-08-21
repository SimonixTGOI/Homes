package simo.homes.managers;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;

public class DatabaseManager {
    private final Plugin plugin;
    private Connection connection;

    public DatabaseManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean connect() {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe(e.getMessage());
            return false;
        }


        try{
            File file = new File(plugin.getDataFolder(), "homes.db");
            String url = file.getAbsolutePath();
            connection = DriverManager.getConnection("jdbc:sqlite:" + url);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Homes] SQL Exception", e);
            return false;
        }

        return true;
    }


    public void disconnect() {
        if(connection == null) {
            return;
        }

        try {
            if(connection.isClosed()) {
                return;
            }
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Homes] SQL Exception", e);
        }
    }


    public boolean createTables() {
        try {
            if(connection == null || connection.isClosed()) {
                return false;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Homes] SQL Exception", e);
            return false;
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS homes (
                        player_uuid TEXT NOT NULL,
                        name TEXT NOT NULL,
                        world TEXT NOT NULL,
                        x REAL NOT NULL,
                        y REAL NOT NULL,
                        z REAL NOT NULL,
                        yaw REAL NOT NULL,
                        pitch REAL NOT NULL,
                        PRIMARY KEY (player_uuid, name)
                    )
                    """);

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Homes] SQL Exception", e);
            return false;
        }
        return true;
    }

    public Connection getConnection() {
        return connection;
    }
}
