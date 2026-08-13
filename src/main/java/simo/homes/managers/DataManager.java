package simo.homes.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import simo.homes.models.Home;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class DataManager {
    private final Plugin plugin;
    private final HomeManager homeManager;

    public DataManager(Plugin plugin, HomeManager homeManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
    }

    public boolean load() { //
        File dataFile = new File(plugin.getDataFolder(), "data.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection section = config.getConfigurationSection("homes");
        if (section == null) {
            plugin.getLogger().warning("Couldn't find homes path in data.yml");
            return false;
        }

        for (String uuidString : section.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID: " + uuidString);
                continue;
            }

            ConfigurationSection playerSection = section.getConfigurationSection(uuidString);
            if (playerSection == null) {
                plugin.getLogger().warning("Couldn't find player section for " + uuidString);
                continue;
            }
            for (String homeName : playerSection.getKeys(false)) {
                ConfigurationSection homeSection = playerSection.getConfigurationSection(homeName);
                if (homeSection == null) {
                    plugin.getLogger().warning("Couldn't find home section for " + homeName + " uuid: " + uuidString);
                    continue;
                }


                if (!(homeSection.get("x") instanceof Number xN)) {
                    plugin.getLogger().warning("Invalid x on uuid: " + uuidString + "home name: " + homeName);
                    continue;
                }
                if (!(homeSection.get("y") instanceof Number yN)) {
                    plugin.getLogger().warning("Invalid y on uuid: " + uuidString + "home name: " + homeName);
                    continue;
                }
                if (!(homeSection.get("z") instanceof Number zN)) {
                    plugin.getLogger().warning("Invalid z on uuid: " + uuidString + "home name: " + homeName);
                    continue;
                }
                if (!(homeSection.get("yaw") instanceof Number yawN)) {
                    plugin.getLogger().warning("Invalid yaw on uuid: " + uuidString + "home name: " + homeName);
                    continue;
                }
                if (!(homeSection.get("pitch") instanceof Number pitchN)) {
                    plugin.getLogger().warning("Invalid pitch on uuid: " + uuidString + "home name: " + homeName);
                    continue;
                }

                String worldStr = homeSection.getString("world");
                if (worldStr == null) {
                    plugin.getLogger().warning("Couldn't find world on uuid: " + uuidString);
                    continue;
                }
                World worldObj = Bukkit.getWorld(worldStr);
                if (worldObj == null) {
                    plugin.getLogger().warning("Invalid world on uuid: " + uuidString);
                    continue;
                }


                double x = xN.doubleValue();
                double y = yN.doubleValue();
                double z = zN.doubleValue();
                float yaw = yawN.floatValue();
                float pitch = pitchN.floatValue();

                Location location = new Location(worldObj, x, y, z, yaw, pitch);

                homeManager.addHome(uuid, homeName, new Home(location));
            }
        }

        homeManager.setDirty(false);
        return true;
    }


    public boolean save() {
        File dataFile = new File(plugin.getDataFolder(), "data.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        config.set("homes", null);

        ConfigurationSection section = config.getConfigurationSection("homes");
        if (section == null) {
            section = config.createSection("homes");
        }


        for(Map.Entry<UUID, Map<String, Home>> entry : homeManager.getMap().entrySet()) {

            ConfigurationSection playerSection = section.createSection(entry.getKey().toString()); //homes.uuid


            for(Map.Entry<String, Home> homeMap : entry.getValue().entrySet()) {

                ConfigurationSection homeSection = playerSection.createSection(homeMap.getKey()); //homes.uuid.name



                Home home = homeMap.getValue();

                homeSection.set("x", home.getX());
                homeSection.set("y", home.getY());
                homeSection.set("z", home.getZ());
                homeSection.set("world", home.getWorld());
                homeSection.set("yaw", home.getYaw());
                homeSection.set("pitch", home.getPitch());

            }

        }

        try {
            config.save(dataFile);
            return true;
        }  catch (IOException e) {
            plugin.getLogger().warning("Couldn't save data.yml (save)");
            plugin.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public boolean reload() {

        if(homeManager.isDirty()) {
            plugin.getLogger().info("Pending edits in memory, retry in a few seconds.");
            return false;
        }

        homeManager.clearMap();

        plugin.getLogger().info("Memory has been cleared.");

        if(!load()) {
            plugin.getLogger().warning("Couldn't load homes data.yml");
            return false;
        }

        plugin.getLogger().info("Memory has been loaded.");

        return true;
    }
}
