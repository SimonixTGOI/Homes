package simo.homes.tasks;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import simo.homes.managers.DataManager;
import simo.homes.managers.HomeManager;

public class SaveTask extends  BukkitRunnable {
    private final HomeManager homemanager;
    private final DataManager dataManager;
    private final Plugin plugin;

    public SaveTask(HomeManager homemanager, DataManager dataManager, Plugin plugin) {
        this.homemanager = homemanager;
        this.dataManager = dataManager;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if(homemanager.isDirty()) {
            if(dataManager.save()) {
                homemanager.setDirty(false);
            } else {
                plugin.getLogger().warning("Error occurred while saving data.");
            }

        }
    }
}
