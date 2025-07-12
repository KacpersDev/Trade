package dev.kacperm.trade.command;

import dev.kacperm.trade.Trade;
import dev.kacperm.trade.trade.CurrentTrade;
import dev.kacperm.trade.utils.color.Color;
import dev.kacperm.trade.utils.inventory.TradeInventory;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
            return false;
        } else if (args[0].equalsIgnoreCase("request")) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null || !target.isOnline()) {
                sender.sendMessage(Color.translate(Objects.requireNonNull(Trade.getInstance().getLanguage()
                        .getConfiguration().getString("player-offline")).replace("{player}", args[0])));
                return false;
            }

            if (target.equals(player)) {
                player.sendMessage(Color.translate(Objects.requireNonNull(Trade.getInstance().getLanguage()
                        .getConfiguration().getString("trade.self"))));
                return false;
            }

            Map<UUID, List<UUID>> requests = Trade.getInstance().getTradeManager().getRequests();
            List<UUID> senderRequests = requests.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());

            if (senderRequests.contains(target.getUniqueId())) {
                player.sendMessage(Color.translate(Objects.requireNonNull(Trade.getInstance().getLanguage()
                        .getConfiguration().getString("trade.already-sent"))));
                return false;
            }

            senderRequests.add(target.getUniqueId());

            player.sendMessage(Color.translate(Objects.requireNonNull(Trade.getInstance().getLanguage().getConfiguration()
                    .getString("trade-request-sent")).replace("{player}", target.getName())));
            target.sendMessage(Color.translate((Objects.requireNonNull(Trade.getInstance().getLanguage().getConfiguration()
                    .getString("trade-request-received")).replace("{player}", player.getName()))));

            return true;
        } else if (args[0].equalsIgnoreCase("accept")) {
            if (args.length == 1) {
                usage(sender);
                return false;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage(Color.translate(Objects.requireNonNull(Trade.getInstance().getLanguage()
                        .getConfiguration().getString("player-offline")).replace("{player}", args[1])));
                return false;
            }

            Map<UUID, List<UUID>> requests = Trade.getInstance().getTradeManager().getRequests();
            List<UUID> senderRequests = requests.get(target.getUniqueId());

            if (senderRequests == null || !senderRequests.contains(player.getUniqueId())) {
                player.sendMessage(Color.translate(Objects.requireNonNull(Trade.getInstance().getLanguage()
                        .getConfiguration().getString("trade.not-sent"))));
                return false;
            }

            senderRequests.remove(player.getUniqueId());
            Inventory inventory = TradeInventory.trade();
            Trade.getInstance().getTradeManager().getCurrentTrades().add(new CurrentTrade(player.getUniqueId(), target.getUniqueId(), inventory,
                    new ArrayList<>(), new ArrayList<>(), false,
                    Trade.getInstance().getConfiguration().getConfiguration().getInt("trade-countdown")));

            player.openInventory(inventory);
            target.openInventory(inventory);

        } else {
            usage(sender);
        }

        return false;
    }

    private void usage(CommandSender sender) {
        for (final String s : Trade.getInstance().getLanguage().getConfiguration().getStringList("trade-usage")) {
            sender.sendMessage(Color.translate(s));
        }
    }
}
