package simo.homes.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simo.homes.managers.HomeManager;
import simo.homes.models.Home;

public class SetHomeCommand implements CommandExecutor {
    private final HomeManager homeManager;

    public SetHomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("onlyPlayer");
            return true;
        }

        if(!player.hasPermission("homes.sethome")) {
            sender.sendMessage("noPermission");
            return true;
        }

        if(args.length == 0) {
            player.sendMessage("Usage: /sethome <homename>");
            return true;
        }

        int maxHomes = homeManager.getMaxHomes(player.getUniqueId());
        int homesNumber = homeManager.getUserHomeNumber(player.getUniqueId());

        if(homesNumber >= maxHomes) {
            sender.sendMessage("You have reached the max number of homes.");
            return true;
        }

        String homeName = args[0];

        if(homeManager.getHome(player.getUniqueId(), homeName) != null) {
            sender.sendMessage("You already have a home called: " + homeName);
            return true;
        }

        Location location = player.getLocation();

        homeManager.addHome(player.getUniqueId(), homeName, new Home(location));

        player.sendMessage("Home " + homeName + " has been created.");


        return true;
    }
}
