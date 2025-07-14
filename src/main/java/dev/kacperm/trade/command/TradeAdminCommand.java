package dev.kacperm.trade.command;

import dev.kacperm.trade.Trade;
import dev.kacperm.trade.trade.PlayerTrade;
import dev.kacperm.trade.utils.color.Color;
import dev.kacperm.trade.utils.inventory.TradeInventory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class TradeAdminCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("trade.admin")) {
            sender.sendMessage(Color.translate(Objects.requireNonNull(Trade.getInstance().getLanguage()
                    .getConfiguration().getString("no-permissions"))));
            return false;
        }

        if (!(sender instanceof Player staff)) {
            sender.sendMessage(Color.translate(Objects.requireNonNull(Trade.getInstance().getLanguage()
                    .getConfiguration().getString("player-required"))));
            return false;
        }

        if (args.length == 0) {
            usage(sender);
        } else if (args[0].equalsIgnoreCase("view")) {
            if (args.length == 1) {
                usage(sender);
                return false;
            } else {
                try {
                    UUID uuid = UUID.fromString(args[1]);

                    PlayerTrade trade = Trade.getInstance().getTradeManager().getTradeById(uuid);

                    if (trade == null) {
                        sender.sendMessage(Color.translate(Objects.requireNonNull(Objects.requireNonNull(Trade.getInstance().getLanguage()
                                        .getConfiguration().getString("trade.not-found"))
                                .replace("{uuid}", args[1]))));
                        return false;
                    }

                    staff.openInventory(TradeInventory.playerTrade(trade));
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Color.translate(Objects.requireNonNull(Trade.getInstance().getLanguage()
                            .getConfiguration().getString("invalid-uuid"))));
                    return false;
                }
            }
        } else if (args[0].equalsIgnoreCase("history")) {
            if (args.length == 1) {
                usage(sender);
                return false;
            } else {
                int limit = 10;

                if (args.length > 2) {
                    try {
                        limit = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Color.translate(Objects.requireNonNull(Trade.getInstance().getLanguage()
                                .getConfiguration().getString("invalid-number"))));
                    }
                }

                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

                if (!Trade.getInstance().getTradeManager().getTrades().containsKey(player.getUniqueId())) {
                    AtomicInteger finalLimit = new AtomicInteger(limit);
                    CompletableFuture.runAsync(() -> {
                        try {
                            Trade.getInstance().getTradeManager().load(player.getUniqueId());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).thenRun(() -> {
                        if (!Trade.getInstance().getTradeManager().getTrades().containsKey(player.getUniqueId())) {
                            sender.sendMessage(Color.translate(Objects.requireNonNull(Trade.getInstance().getLanguage()
                                    .getConfiguration().getString("player-not-found")).replace("{player}", args[1])));
                            return;
                        }

                        List<PlayerTrade> trades = Trade.getInstance().getTradeManager().getTrades().get(player.getUniqueId());

                        if (trades.size() < finalLimit.get()) {
                            finalLimit.set(trades.size());
                        }

                        sender.sendMessage(Color.translate(Objects.requireNonNull(Trade.getInstance().getLanguage()
                                .getConfiguration().getString("trade.limit-history"))
                                .replace("{limit}", String.valueOf(finalLimit.get()))
                                .replace("{player}", args[1])));

                        for (int i = 0; i < finalLimit.get(); i++) {
                            sender.sendMessage(Color.translate(Objects.requireNonNull(Objects.requireNonNull(Trade.getInstance().getLanguage()
                                            .getConfiguration().getString("trade.history-item"))
                                    .replace("{index}", String.valueOf(i + 1))
                                    .replace("{trade_id}", trades.get(i).getTradeId().toString()))));
                        }
                    });
                } else {
                    List<PlayerTrade> trades = Trade.getInstance().getTradeManager().getTrades().get(player.getUniqueId());
                    int finalLimit = limit;

                    if (trades.size() < finalLimit) {
                        finalLimit = trades.size();
                    }

                    sender.sendMessage(Color.translate(Objects.requireNonNull(Trade.getInstance().getLanguage()
                                    .getConfiguration().getString("trade.limit-history"))
                            .replace("{limit}", String.valueOf(finalLimit))
                            .replace("{player}", args[1])));

                    for (int i = 0; i < finalLimit; i++) {
                        sender.sendMessage(Color.translate(Objects.requireNonNull(Objects.requireNonNull(Trade.getInstance().getLanguage()
                                        .getConfiguration().getString("trade.history-item"))
                                .replace("{index}", String.valueOf(i + 1))
                                .replace("{trade_id}", trades.get(i).getTradeId().toString()))));
                    }
                }
             }
        } else {
            usage(sender);
        }

        return true;
    }

    private void usage(CommandSender sender) {
        for (final String s : Trade.getInstance().getLanguage().getConfiguration().getStringList("trade-admin.usage")) {
            sender.sendMessage(Color.translate(s));
        }
    }
}
