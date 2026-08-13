package simo.homes.managers;

import simo.homes.models.Home;

import java.util.*;

public class HomeManager {
    private final ConfigManager configManager;
    private final Map<UUID, Map<String, Home>> map = new HashMap<>();
    private boolean dirty = false;

    public HomeManager(ConfigManager configManager) {
        this.configManager = configManager;
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

    public void addHome(UUID uuid, String name, Home home) {
        this.map.computeIfAbsent(uuid, k -> new HashMap<>()).put(name, home);
        dirty = true;
    }

    public void removeHome(UUID uuid, String name) {
        Map<String, Home> homeList = this.map.get(uuid);
        if(homeList == null) {
            return;
        }
        homeList.remove(name);
        dirty = true;
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

    public void setDirty(boolean value) {
        dirty = value;
    }

    public boolean isDirty() {
        return dirty;
    }

}
