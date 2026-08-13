package simo.homes.managers;

import org.bukkit.plugin.Plugin;

public class MessageManager {
    private final Plugin plugin;
    public MessageManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void pluginWarning(String message) {
        plugin.getLogger().warning(message);
    }
}
