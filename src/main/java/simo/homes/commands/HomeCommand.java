package simo.homes.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simo.homes.managers.CooldownManager;
import simo.homes.managers.HomeManager;
import simo.homes.models.Home;

public class HomeCommand implements CommandExecutor {
    private final HomeManager homeManager;
    private final CooldownManager cooldownManager;

    public HomeCommand(HomeManager homeManager, CooldownManager cooldownManager) {
        this.homeManager = homeManager;
        this.cooldownManager = cooldownManager;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("onlyPlayers");
            return true;
        }
        if(!player.hasPermission("homes.tp")) {
            player.sendMessage("noPermission");
            return true;
        }

        if(args.length == 0) {
            player.sendMessage("usage");
            return true;
        }
        String homeName = args[0];
        Home home = homeManager.getHome(player.getUniqueId(), homeName);
        if(home == null) {
            player.sendMessage("You don't have a home called " + homeName);
            return true;
        }

        int cooldown = cooldownManager.getRemainingCooldown(player.getUniqueId());

        if(!player.hasPermission("homes.bypasscooldown")) {
            if(cooldown > 0) {
                player.sendMessage("You are still in cooldown. " + cooldown + "s left.");
                return true;
            }
        }



        if(player.teleport(home.getLocation())) {
            cooldownManager.setCooldown(player.getUniqueId());
            player.sendMessage("Teleported to " + homeName);
        } else {
            player.sendMessage("An error occurred while teleporting to " + homeName);
        }





        return true;
    }
}
