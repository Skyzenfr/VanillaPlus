package fr.skyzen.vanillaplus.commands.tabcompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class tabCompleteMoney implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.isOp()) {
                completions.add("add");
                completions.add("remove");
            }
        } else if (args.length == 2 && sender.isOp()) {
            // Auto-complétion des pseudos pour OPs (add/remove <joueur>)
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        } else if (args.length == 3 && sender.isOp()) {
            // Auto-complétion des montants pour add/remove
            completions.add("100");
            completions.add("500");
            completions.add("1000");
            completions.add("5000");
        }

        return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
    }
}