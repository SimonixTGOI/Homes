package simo.homes.tabs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simo.homes.managers.HomeManager;

import java.util.ArrayList;
import java.util.List;

public class HomeTab implements TabCompleter {
    private final HomeManager homeManager;

    public HomeTab(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> suggestions = new ArrayList<>();


        if(args.length == 1) {
            if(sender instanceof Player player) {
                String scritto = args[0].toLowerCase();
                for(String homeName : homeManager.getUserHomeList(player.getUniqueId())) {
                    if(homeName.toLowerCase().startsWith(scritto)) {
                        suggestions.add(homeName);
                    }
                }
            }

        }



        return suggestions;
    }
}
