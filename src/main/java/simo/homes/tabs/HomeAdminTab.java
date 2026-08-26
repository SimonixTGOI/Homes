package simo.homes.tabs;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simo.homes.managers.HomeManager;

import java.util.ArrayList;
import java.util.List;

public class HomeAdminTab implements TabCompleter {
    private final HomeManager homeManager;

    public HomeAdminTab(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> suggestions = new ArrayList<>();
        if(args.length == 1) { //commandtype
            String scritto = args[0].toLowerCase();
            for(String commandString : List.of("sethome", "delhome", "list", "tp", "reload"))
                if(commandString.toLowerCase().startsWith(scritto)) {
                    suggestions.add(commandString);
                }
        } else if(args.length == 2) { //user
            String scrittoPlayer = args[1].toLowerCase();
            if(List.of("sethome", "delhome", "list", "tp").contains(args[0].toLowerCase())) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getName().toLowerCase().startsWith(scrittoPlayer)) {
                        suggestions.add(player.getName());
                    }
                }
            }
        } else if(args.length == 3) { //homename
            if(List.of("delhome", "tp").contains(args[0].toLowerCase())) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1].toLowerCase());
                String scrittoHome = args[2].toLowerCase();
                for(String homeName : homeManager.getUserHomeList(offlinePlayer.getUniqueId())) {
                    if(homeName.toLowerCase().startsWith(scrittoHome)) {
                        suggestions.add(homeName);
                    }
                }
            }
        }

        return suggestions;
    }
}
