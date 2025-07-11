package dev.kacperm.trade.command;

import dev.kacperm.trade.Trade;
import dev.kacperm.trade.utils.color.Color;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TradeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Color.translate(Objects.requireNonNull(Trade.getInstance().getLanguage()
                    .getConfiguration().getString("player-required"))));
            return false;
        }

        if (args.length == 0) {
            usage(sender);
        } else {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(Color.translate(Objects.requireNonNull(Objects.requireNonNull(Trade.getInstance().getLanguage()
                        .getConfiguration().getString("player-offline")).replace("{player}", args[0]))));
                return false;
            }

            // TODO send request...
        }

        return true;
    }

    private void usage(CommandSender sender) {

    }
}
