package simo.homes.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ConfigManager {
    private final Plugin plugin;


    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public int getMaxHomes(String role) {
        return plugin.getConfig().getInt("settings."+role+".max-homes");
    }

    public int getCooldown(String role) {
        role = role.toLowerCase(Locale.ROOT);
        return plugin.getConfig().getInt("settings."+role+".cooldown");
    }

    public List<String> getRoles() {
        ConfigurationSection roles = plugin.getConfig().getConfigurationSection("settings");
        if(roles == null) {
            plugin.getLogger().warning("No roles found in config.yml");
            return new ArrayList<>();
        }
        List<String> roleList = new ArrayList<>();
        for (String role : roles.getKeys(false)) {
            roleList.add(role.toLowerCase(Locale.ROOT));
        }
        return roleList;
    }


    public String getPlayerRole(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) {
            plugin.getLogger().warning("Player not found while getting role");
            return null;
        }
        for(String role : getRoles()) {
            if(player.hasPermission("homes." + role)) {
                return role;
            }
        }
        return null;
    }

}
