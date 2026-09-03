package simo.homes.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import simo.homes.enums.HomeCreationResult;
import simo.homes.models.Home;
import simo.homes.records.HomeLoadResult;
import simo.homes.repositories.HomeRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class HomeManager {
    private final ConfigManager configManager;
    private final HomeRepository homeRepository;
    private final Plugin plugin;
    private final Map<UUID, Map<String, Home>> map = new HashMap<>();



    public HomeManager(ConfigManager configManager, HomeRepository homeRepository, Plugin plugin) {
        this.configManager = configManager;
        this.homeRepository = homeRepository;
        this.plugin = plugin;
    }

    public List<String> getUserHomeList(UUID uuid) {
        Map<String, Home> homeList = this.map.get(uuid);
        if(homeList == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(homeList.keySet());
    }

    public int getUserHomeNumber(UUID uuid) {

        return getUserHomeList(uuid).size();
    }

    public Home getHome(UUID uuid, String name) {
        Map<String, Home> homeList = this.map.get(uuid);
        if(homeList == null) {
            return null;
        }
        return homeList.get(name);
    }

    public CompletableFuture<HomeCreationResult> createHome(UUID uuid, String name, Home home) {
        if(!name.matches("[a-zA-Z0-9]+")) {
            return CompletableFuture.completedFuture(HomeCreationResult.INVALID_HOME_NAME);
        }

        if(getHome(uuid, name) != null) {
            return CompletableFuture.completedFuture(HomeCreationResult.HOME_ALREADY_EXISTS);
        }

        CompletableFuture<HomeCreationResult> resultFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> insertFuture = homeRepository.insertHome(uuid, name, home);
        insertFuture.thenAccept(insertResult ->
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if(insertResult) {
                        addHome(uuid, name, home);
                        resultFuture.complete(HomeCreationResult.SUCCESS);
                    } else {
                        resultFuture.complete(HomeCreationResult.DATABASE_ERROR);
                    }
                })
        );


        return resultFuture;

    }

    public void addHome(UUID uuid, String name, Home home) {
        this.map.computeIfAbsent(uuid, _ -> new HashMap<>()).put(name, home);
    }

    public boolean removeHome(UUID uuid, String name) {
        Map<String, Home> homeList = this.map.get(uuid);
        if(homeList == null) {
            return false;
        }
        if(!homeList.containsKey(name)) {
            return false;
        }

        if(!homeRepository.removeHome(uuid, name)) {
            return false;
        }



        homeList.remove(name);
        if(homeList.isEmpty()) {
            map.remove(uuid);
        }

        return true;
    }

    public int getMaxHomes(UUID uuid) {
        String role = configManager.getPlayerRole(uuid);

        if(role == null) {
            return 0;
        }

        return configManager.getMaxHomes(role);
    }

    public boolean load() {
        HomeLoadResult result = homeRepository.loadHomes();
        if(!result.success()) {
            return false;
        }
        map.putAll(result.homes());
        return true;
    }

    public boolean reload() {
        HomeLoadResult result = homeRepository.loadHomes();
        if(!result.success()) {
            return false;
        }
        map.clear();
        map.putAll(result.homes());
        return true;
    }



}
