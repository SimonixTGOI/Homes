package simo.homes.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simo.homes.enums.HomeCreationResult;
import simo.homes.managers.HomeManager;
import simo.homes.models.Home;

import java.util.concurrent.CompletableFuture;

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

        if(!player.hasPermission("homes.bypasshomeslimit")) {
            if(homesNumber >= maxHomes) {
                sender.sendMessage("You have reached the max number of homes.");
                return true;
            }
        }


        String homeName = args[0];

        Location location = player.getLocation();


        homeManager.createHome(player.getUniqueId(), homeName, new Home(location))
            .thenAccept(result -> {
            switch (result) {
                case SUCCESS:
                    player.sendMessage("Home " + homeName + " has been created.");
                    break;
                case HOME_ALREADY_EXISTS:
                    player.sendMessage("Home " + homeName + " already exists.");
                    break;
                case INVALID_HOME_NAME:
                    player.sendMessage("Homes name can only contain alphanumeric characters.");
                    break;
                case DATABASE_ERROR:
                    player.sendMessage("Error while trying to create home named " + homeName);
                    break;
            }
        });






        return true;
    }
}
