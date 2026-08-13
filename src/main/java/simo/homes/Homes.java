package simo.homes;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import simo.homes.commands.*;
import simo.homes.managers.*;
import simo.homes.tabs.DelHomeTab;
import simo.homes.tabs.HomeAdminTab;
import simo.homes.tabs.HomeTab;
import simo.homes.tasks.SaveTask;

import java.util.Objects;

public final class Homes extends JavaPlugin {
    private DataManager dataManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        getLogger().info("Enabling...");

        saveDefaultConfig();

        ConfigManager configManager = new ConfigManager(this);
        MessageManager messageManager = new MessageManager(this);
        HomeManager homeManager = new HomeManager(configManager);
        this.dataManager = new DataManager(this, homeManager);

        CooldownManager cooldownManager = new CooldownManager(configManager, messageManager);

        dataManager.load();



        Objects.requireNonNull(getCommand("homeadmin")).setExecutor(new HomeAdminCommand(homeManager, dataManager));
        Objects.requireNonNull(getCommand("home")).setExecutor(new HomeCommand(homeManager, cooldownManager));
        Objects.requireNonNull(getCommand("homes")).setExecutor(new HomesCommand(homeManager));
        Objects.requireNonNull(getCommand("sethome")).setExecutor(new SetHomeCommand(homeManager));
        Objects.requireNonNull(getCommand("delhome")).setExecutor(new DelHomeCommand(homeManager));


        Objects.requireNonNull(getCommand("homeadmin")).setTabCompleter(new HomeAdminTab(homeManager));
        Objects.requireNonNull(getCommand("home")).setTabCompleter(new HomeTab(homeManager));
        Objects.requireNonNull(getCommand("delhome")).setTabCompleter(new DelHomeTab(homeManager));

        new SaveTask(homeManager, dataManager, this).
                runTaskTimer(this, 200L, 200L);

        getLogger().info("Enabled");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        dataManager.save();

        getLogger().info("Disabled");
    }
}
