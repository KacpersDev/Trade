package dev.kacperm.trade.command;

import dev.kacperm.trade.Trade;
import dev.kacperm.trade.utils.color.Color;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class TradeAdminCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("trade.admin")) {
            sender.sendMessage(Color.translate(Objects.requireNonNull(Trade.getInstance().getLanguage()
                    .getConfiguration().getString("no-permissions"))));
            return false;
        }

        if (args.length == 0) {
            usage(sender);
        } else if (args[0].equalsIgnoreCase("view")) {

        } else if (args[0].equalsIgnoreCase("history")) {
            if (args.length == 1) {
                usage(sender);
                return false;
            } else {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

                if (!Trade.getInstance().getTradeManager().getTrades().containsKey(player.getUniqueId())) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            Trade.getInstance().getTradeManager().load(player.getUniqueId());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).thenRun(() -> {

                    });
                } else {

                }
            }
        } else {
            usage(sender);
        }

        return true;
    }

    private void usage(CommandSender sender) {
        for (final String s : Trade.getInstance().getLanguage().getConfiguration().getStringList("trade-usage")) {
            sender.sendMessage(Color.translate(s));
        }
    }
}
