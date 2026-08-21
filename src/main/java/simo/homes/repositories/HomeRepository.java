package simo.homes.repositories;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import simo.homes.managers.DatabaseManager;
import simo.homes.models.Home;
import simo.homes.records.HomeLoadResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class HomeRepository {
    private final DatabaseManager databaseManager;
    private final Plugin plugin;


    public HomeRepository(DatabaseManager databaseManager, Plugin plugin) {
        this.databaseManager = databaseManager;
        this.plugin = plugin;

    }


    public boolean insertHome(UUID uuid, String name, Home home) {
        Connection connection = databaseManager.getConnection();

        try {
            if(connection == null || connection.isClosed()) return false;
        } catch (SQLException connectionError) {
            plugin.getLogger().log(Level.SEVERE, "[HomeRepository] SQLException: ", connectionError);
            return false;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                 """
                     INSERT INTO homes (
                        player_uuid,
                        name,
                        world,
                        x,
                        y,
                        z,
                        yaw,
                        pitch
                     )
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                     """)) {




            statement.setString(1, uuid.toString());
            statement.setString(2, name);
            statement.setString(3, home.getWorld());
            statement.setDouble(4, home.getX());
            statement.setDouble(5, home.getY());
            statement.setDouble(6, home.getZ());
            statement.setFloat(7, home.getYaw());
            statement.setFloat(8, home.getPitch());


            int modifiedRows = statement.executeUpdate();
            if (modifiedRows != 1) {
                return false;
            }

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[HomeRepository] SQL Exception", e);
            return false;
        }
        return true;
    }

    public boolean removeHome(UUID uuid, String name) {
        Connection connection = databaseManager.getConnection();

        try {
            if(connection == null || connection.isClosed()) return false;
        } catch (SQLException connectionError) {
            plugin.getLogger().log(Level.SEVERE, "[HomeRepository] SQLException: ", connectionError);
            return false;
        }

        try (PreparedStatement statement = connection.prepareStatement("""
                DELETE FROM homes WHERE player_uuid = ? AND name = ?
                """)) {


            statement.setString(1, uuid.toString());
            statement.setString(2, name);

            int modifiedRows = statement.executeUpdate();
            if (modifiedRows != 1) {
                return false;
            }

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[HomeRepository] SQL Exception", e);
            return false;
        }

        return true;
    }

    public HomeLoadResult loadHomes() {
        Map<UUID, Map<String, Home>> homes = new HashMap<>();
        Connection connection = databaseManager.getConnection();
        try {
            if(connection == null || connection.isClosed()) return new HomeLoadResult(false, new HashMap<>());
        } catch (SQLException connectionError) {
            plugin.getLogger().log(Level.SEVERE, "[HomeRepository] SQLException: ", connectionError);
            return new HomeLoadResult(false, new HashMap<>());
        }

        try (PreparedStatement statement = connection.prepareStatement("""
            SELECT player_uuid, name, x, y, z, world, yaw, pitch FROM homes
            """)) {
            try(ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String uuidString = resultSet.getString(1);
                    String homeName = resultSet.getString(2);
                    double x = resultSet.getDouble(3);
                    double y = resultSet.getDouble(4);
                    double z = resultSet.getDouble(5);
                    String worldName = resultSet.getString(6);
                    float yaw = resultSet.getFloat(7);
                    float pitch = resultSet.getFloat(8);
                    UUID uuid;
                    try {
                        uuid = UUID.fromString(uuidString);
                    } catch(IllegalArgumentException e) {
                        plugin.getLogger().severe("[HomeRepository] Invalid UUID: " + uuidString);
                        return new HomeLoadResult(false, new HashMap<>());
                    }
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        plugin.getLogger().severe("[HomeRepository] Invalid world: " + worldName);
                        return new HomeLoadResult(false, new HashMap<>());
                    }

                    Location location = new Location(world, x, y, z, yaw, pitch);
                    Home home = new Home(location);

                    homes.computeIfAbsent(uuid, k -> new HashMap<>()).put(homeName, home);
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "[HomeRepository] SQLException: ", e);
                return new HomeLoadResult(false, new HashMap<>());
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[HomeRepository] SQL Exception", e);
            return new HomeLoadResult(false, new HashMap<>());
        }
        return new HomeLoadResult(true, homes);
    }
}