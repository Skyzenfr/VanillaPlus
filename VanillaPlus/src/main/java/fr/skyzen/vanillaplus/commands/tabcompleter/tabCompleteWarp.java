package fr.skyzen.vanillaplus.commands.tabcompleter;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

public class tabCompleteWarp implements TabCompleter {
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("create");
            completions.add("delete");
        } else if (args.length == 2) {
            completions.add("nom du warp");
        }
        return completions;
    }
}