package simo.homes.managers;

import simo.homes.models.Home;
import simo.homes.records.HomeLoadResult;
import simo.homes.repositories.HomeRepository;

import java.util.*;

public class HomeManager {
    private final ConfigManager configManager;
    private final HomeRepository homeRepository;
    private final Map<UUID, Map<String, Home>> map = new HashMap<>();



    public HomeManager(ConfigManager configManager, HomeRepository homeRepository) {
        this.configManager = configManager;
        this.homeRepository = homeRepository;
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

    public int createHome(UUID uuid, String name, Home home) {
        if(!name.matches("[a-zA-Z0-9]+")) {
            return 2;
        }
        if(homeRepository.insertHome(uuid, name, home)) {
            addHome(uuid, name, home);
            return 0;
        } else {
            return 1;
        }

    }

    public void addHome(UUID uuid, String name, Home home) {
        this.map.computeIfAbsent(uuid, k -> new HashMap<>()).put(name, home);
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

    public Map<UUID, Map<String, Home>> getMap() {
        return map;
    }

    public void clearMap() {
        this.map.clear();
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
