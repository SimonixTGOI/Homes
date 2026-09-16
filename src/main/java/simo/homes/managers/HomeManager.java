package simo.homes.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import simo.homes.enums.HomeCreationResult;
import simo.homes.enums.HomeDeletionResult;
import simo.homes.models.Home;
import simo.homes.records.HomeLoadResult;
import simo.homes.repositories.HomeRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class HomeManager {
    private final ConfigManager configManager;
    private final HomeRepository homeRepository;
    private final Plugin plugin;
    private final Map<UUID, Map<String, Home>> map = new HashMap<>();
    private final Set<UUID> executionMap = ConcurrentHashMap.newKeySet();
    private final Set<UUID> removingMap = ConcurrentHashMap.newKeySet();



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

        if(!executionMap.add(uuid)) {
            return CompletableFuture.completedFuture(HomeCreationResult.IN_EXECUTION);
        }

        CompletableFuture<HomeCreationResult> resultFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> dbInsertFuture = homeRepository.insertHome(uuid, name, home);
        dbInsertFuture.thenAccept(dbInsertResult -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (dbInsertResult) {
                    addHome(uuid, name, home);
                    resultFuture.complete(HomeCreationResult.SUCCESS);
                } else {
                    resultFuture.complete(HomeCreationResult.DATABASE_ERROR);
                }
            });
            executionMap.remove(uuid);
        });


        return resultFuture;

    }

    public void addHome(UUID uuid, String name, Home home) {
        this.map.computeIfAbsent(uuid, _ -> new HashMap<>()).put(name, home);
    }

    public CompletableFuture<HomeDeletionResult> removeHome(UUID uuid, String name) {

        Map<String, Home> homeList = this.map.get(uuid);

        if(homeList == null) {
            return CompletableFuture.completedFuture(HomeDeletionResult.HOME_DOES_NOT_EXIST);
        }
        if(!homeList.containsKey(name)) {
            return CompletableFuture.completedFuture(HomeDeletionResult.HOME_DOES_NOT_EXIST);
        }

        if(!removingMap.add(uuid)) {
            return CompletableFuture.completedFuture(HomeDeletionResult.IN_EXECUTION);
        }

        CompletableFuture<HomeDeletionResult> resultFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> dbRemoveFuture = homeRepository.removeHome(uuid, name);
        dbRemoveFuture.thenAccept(dbRemoveResult -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (dbRemoveResult) {
                    homeList.remove(name);
                    resultFuture.complete(HomeDeletionResult.SUCCESS);
                } else {
                    resultFuture.complete(HomeDeletionResult.DATABASE_ERROR);
                }
            });
            removingMap.remove(uuid);
        });

        return resultFuture;

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
