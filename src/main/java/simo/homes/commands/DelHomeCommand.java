package simo.homes.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simo.homes.managers.HomeManager;
import simo.homes.models.Home;

public class DelHomeCommand implements CommandExecutor {
    private final HomeManager homeManager;

    public DelHomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("onlyPlayer");
            return true;
        }
        if(!player.hasPermission("homes.delhome")) {
            sender.sendMessage("noPermission");
            return true;
        }
        if(args.length == 0) {
            player.sendMessage("usage");
            return true;
        }

        String homeName = args[0];

        Home home = homeManager.getHome(player.getUniqueId(), homeName);
        if(home == null) {
            player.sendMessage("You don't have a home named " + homeName);
            return true;
        }

        homeManager.removeHome(player.getUniqueId(), homeName);

        player.sendMessage("Home " + homeName + " has been deleted.");

        return true;
    }
}
