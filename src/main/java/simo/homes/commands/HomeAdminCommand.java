package simo.homes.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simo.homes.managers.HomeManager;
import simo.homes.models.Home;

import java.util.List;
import java.util.UUID;

public class HomeAdminCommand implements CommandExecutor {
    private final HomeManager homeManager;

    public HomeAdminCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!sender.hasPermission("homes.admin")) {
            sender.sendMessage("No permission");
            return true;
        }

        boolean isPlayer = sender instanceof Player;

        if(args.length == 0) {
            sender.sendMessage("Usage: /homeadmin <sethome|delete|list|tp>");
            return true;
        }

        switch(args[0].toLowerCase()) {
            case "delhome" -> {
                if(args.length < 2) {
                    sender.sendMessage("Usage: /homeadmin delhome <player> <home>");
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);



                if(args.length < 3) {
                    sender.sendMessage("Usage: /homeadmin delhome <player> <home name>");
                    return true;
                }

                String homeName = args[2];


                if(homeManager.getHome(target.getUniqueId(), homeName) == null) {
                    sender.sendMessage("Home not found");
                    return true;
                }
                if(!homeManager.removeHome(target.getUniqueId(), homeName)) {
                    sender.sendMessage("Error while trying to remove home named " + homeName);
                    return true;
                }

                sender.sendMessage("Home " + homeName + " has been deleted.");
            }
            case "list" -> {
                if(args.length < 2) {
                    sender.sendMessage("Usage: /homeadmin list <player>");
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);



                List<String> homeList= homeManager.getUserHomeList(target.getUniqueId());
                if(homeList.isEmpty()) {
                    sender.sendMessage("This player doesn't have any home");
                    return true;
                }

                String message = args[1] + """
                        's homes:
                        
                        """;

                String homeListString = String.join(", ", homeList);

                message = message.concat(homeListString);

                sender.sendMessage(message);
            }
            case "sethome" -> {
                if(!isPlayer) {
                    sender.sendMessage("Only players may use this command");
                    return true;
                }

                Player player = (Player) sender;

                if(args.length < 2) {
                    sender.sendMessage("Usage: /homeadmin sethome <player>");
                    return true;
                }
                OfflinePlayer target  = Bukkit.getOfflinePlayer(args[1]);

                if(args.length < 3) {
                    sender.sendMessage("Usage: /homeadmin sethome <player> <home name>");
                    return true;
                }


                String homeName = args[2];

                Location location = player.getLocation();

                switch (homeManager.createHome(target.getUniqueId(), homeName, new Home(location))) {
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

            }
            case "tp" -> {
                if(!isPlayer) {
                    sender.sendMessage("Only players may use this command");
                    return true;
                }

                Player player = (Player) sender;

                if(args.length < 2) {
                    sender.sendMessage("Usage: /homeadmin tp <player>");
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                UUID uuid =  target.getUniqueId();

                if(args.length < 3) {
                    sender.sendMessage("Usage: /homeadmin tp <player> <home name>");
                    return true;
                }
                String homeName = args[2];

                Home home = homeManager.getHome(uuid, homeName);
                if(home == null) {
                    sender.sendMessage("Home not found");
                    return true;
                }

                if(player.teleport(home.getLocation())) {
                    player.sendMessage("Teleported to " + homeName);
                } else {
                    player.sendMessage("An error occurred while teleporting to " + homeName);
                }



            }

            case "reload" -> {
                if(!homeManager.reload()) {
                    sender.sendMessage("An error occurred while reloading the homes.");
                    return true;
                }

                sender.sendMessage("Homes Reloaded");
            }

            default -> {
                sender.sendMessage("Usage: /homeadmin <sethome|delhome|list|tp>");
                return true;
            }
        }




        return true;
    }
}
