package simo.homes;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import simo.homes.commands.*;
import simo.homes.managers.*;
import simo.homes.repositories.HomeRepository;
import simo.homes.tabs.DelHomeTab;
import simo.homes.tabs.HomeAdminTab;
import simo.homes.tabs.HomeTab;

import java.util.Objects;

public final class Homes extends JavaPlugin {
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        getLogger().info("Enabling...");

        saveDefaultConfig();


        this.databaseManager = new DatabaseManager(this);
        HomeRepository homeRepository = new HomeRepository(databaseManager, this);

        ConfigManager configManager = new ConfigManager(this);
        MessageManager messageManager = new MessageManager(this);
        HomeManager homeManager = new HomeManager(configManager, homeRepository, this);
        CooldownManager cooldownManager = new CooldownManager(configManager, messageManager);


        if(!databaseManager.connect()) {
            this.getLogger().severe("Could not connect to database!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if(!databaseManager.createTables()) {
            this.getLogger().severe("Could not create tables!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if(!homeManager.load()) {
            this.getLogger().severe("Could not load homes!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }



        Objects.requireNonNull(getCommand("homeadmin")).setExecutor(new HomeAdminCommand(homeManager));
        Objects.requireNonNull(getCommand("home")).setExecutor(new HomeCommand(homeManager, cooldownManager));
        Objects.requireNonNull(getCommand("homes")).setExecutor(new HomesCommand(homeManager));
        Objects.requireNonNull(getCommand("sethome")).setExecutor(new SetHomeCommand(homeManager));
        Objects.requireNonNull(getCommand("delhome")).setExecutor(new DelHomeCommand(homeManager));


        Objects.requireNonNull(getCommand("homeadmin")).setTabCompleter(new HomeAdminTab(homeManager));
        Objects.requireNonNull(getCommand("home")).setTabCompleter(new HomeTab(homeManager));
        Objects.requireNonNull(getCommand("delhome")).setTabCompleter(new DelHomeTab(homeManager));



        getLogger().info("Enabled");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        databaseManager.disconnect();




        getLogger().info("Disabled");
    }
}
