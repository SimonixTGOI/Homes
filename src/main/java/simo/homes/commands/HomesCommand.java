package simo.homes.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simo.homes.managers.HomeManager;

import java.util.List;

public class HomesCommand implements CommandExecutor {
    private final HomeManager homeManager;

    public HomesCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(!(sender instanceof Player player)) {
            sender.sendMessage("onlyPlayers can execute this command");
            return true;
        }

        if(!player.hasPermission("homes.homes")) {
            sender.sendMessage("noPermission");
            return true;
        }

        List<String> homeList = homeManager.getUserHomeList(player.getUniqueId());

        if(homeList.isEmpty()) {
            sender.sendMessage("You don't have any homes");
            return true;
        }


        String message = String.join(", ", homeList);

        player.sendMessage(message);

        return true;
    }
}
