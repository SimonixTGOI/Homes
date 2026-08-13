package simo.homes.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public CooldownManager(ConfigManager configManager, MessageManager messageManager) {
        this.configManager = configManager;
        this.messageManager = messageManager;
    }

    public int getPlayerCooldown(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            messageManager.pluginWarning("An error occurred getting the home cooldown of the uuid: " + uuid);
            return -1;
        }
        for(String role : configManager.getRoles()) {
            if(player.hasPermission("homes." + role)) {
                return configManager.getCooldown(role);
            }
        }
        return -2;
    }

    public void setCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }

    public int getRemainingCooldown(UUID uuid) {

        int cooldown = getPlayerCooldown(uuid);
        if(cooldown < 0) {
            messageManager.pluginWarning("Player cooldown was not valid.");
            return 0;
        }
        Long used = cooldowns.get(uuid);
        if(used == null) {
            return 0;
        }
        Long now = System.currentTimeMillis();

        long passedms = now-used;
        int passedTime = (int) (passedms / 1000);
        int result = cooldown - passedTime;

        if(result < 0) {
            result = 0;
        }
        return result;
    }
}