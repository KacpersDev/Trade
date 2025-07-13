package dev.kacperm.trade.listener;

import dev.kacperm.trade.Trade;
import dev.kacperm.trade.trade.CurrentTrade;
import dev.kacperm.trade.trade.PlayerTrade;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TradeListener implements Listener {

    @EventHandler
    public void onTradeInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        UUID playerId = player.getUniqueId();
        CurrentTrade currentTrade = Trade.getInstance().getTradeManager().getTrade(playerId);
        if (currentTrade == null) return;

        if (event.getClickedInventory() == null) return;

        int rawSlot = event.getRawSlot();
        boolean isTopInventory = rawSlot < event.getView().getTopInventory().getSize();
        boolean isPlayer1 = currentTrade.getPlayer1().equals(playerId);

        List<Integer> player1Slots = Trade.getInstance().getConfiguration().getConfiguration().getIntegerList("slots.player-1");
        List<Integer> player2Slots = Trade.getInstance().getConfiguration().getConfiguration().getIntegerList("slots.player-2");

        if (isTopInventory) {
            if ((isPlayer1 && player2Slots.contains(rawSlot)) || (!isPlayer1 && player1Slots.contains(rawSlot))) {
                event.setCancelled(true);
                player.updateInventory();
                return;
            }
        }

        if (isPlayer1) {
            currentTrade.getPlayer1Items().add(event.getCurrentItem());
        } else {
            currentTrade.getPlayer2Items().add(event.getCurrentItem());
        }

        if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null) {
            String button = event.getCurrentItem().getItemMeta()
                    .getPersistentDataContainer()
                    .get(Trade.getInstance().getButton(), PersistentDataType.STRING);

            if (button != null) {
                event.setCancelled(true);
                player.updateInventory();

                if (button.equalsIgnoreCase("not-confirmed")) {
                    currentTrade.setAccepted(currentTrade.validateAcceptation());

                    if (currentTrade.isAccepted()) {
                        UUID tradeId = UUID.randomUUID();

                        Trade.getInstance().getTradeManager().getTrades()
                                .computeIfAbsent(currentTrade.getPlayer1(), k -> new ArrayList<>())
                                .add(new PlayerTrade(tradeId, currentTrade.getPlayer1(), currentTrade.getPlayer2(),
                                        currentTrade.getPlayer1Items().toArray(new ItemStack[54]),
                                        currentTrade.getPlayer2Items().toArray(new ItemStack[54])));

                        Trade.getInstance().getTradeManager().getTrades()
                                .computeIfAbsent(currentTrade.getPlayer2(), k -> new ArrayList<>())
                                .add(new PlayerTrade(tradeId, currentTrade.getPlayer1(), currentTrade.getPlayer2(),
                                        currentTrade.getPlayer1Items().toArray(new ItemStack[54]),
                                        currentTrade.getPlayer2Items().toArray(new ItemStack[54])));

                        currentTrade.getPlayer(currentTrade.getPlayer1()).ifPresent(p -> {
                            p.closeInventory();
                            currentTrade.getPlayer2Items().forEach(item -> p.getInventory().addItem(item));
                        });

                        currentTrade.getPlayer(currentTrade.getPlayer2()).ifPresent(p -> {
                            p.closeInventory();
                            currentTrade.getPlayer1Items().forEach(item -> p.getInventory().addItem(item));
                        });
                    }
                }
            }
        }

        currentTrade.update();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        CurrentTrade currentTrade = Trade.getInstance().getTradeManager().getTrade(playerId);
        if (currentTrade == null) return;

        List<Integer> player1Slots = Trade.getInstance().getConfiguration().getConfiguration().getIntegerList("slots.player-1");
        List<Integer> player2Slots = Trade.getInstance().getConfiguration().getConfiguration().getIntegerList("slots.player-2");

        currentTrade.getPlayer(currentTrade.getPlayer1()).ifPresent(player -> {
            Inventory inv = event.getInventory();
            for (int slot : player2Slots) {
                ItemStack item = inv.getItem(slot);
                if (item != null) {
                    player.getInventory().addItem(item.clone());
                }
            }
            player.closeInventory();
        });

        currentTrade.getPlayer(currentTrade.getPlayer2()).ifPresent(player -> {
            Inventory inv = event.getInventory();
            for (int slot : player1Slots) {
                ItemStack item = inv.getItem(slot);
                if (item != null) {
                    player.getInventory().addItem(item.clone());
                }
            }
            player.closeInventory();
        });

        Trade.getInstance().getTradeManager().getCurrentTrades().remove(currentTrade);
        Trade.getInstance().getTradeManager().getRequests().remove(playerId);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        CurrentTrade currentTrade = Trade.getInstance().getTradeManager().getTrade(playerId);
        if (currentTrade == null) return;

        Trade.getInstance().getTradeManager().getCurrentTrades().remove(currentTrade);
        Trade.getInstance().getTradeManager().getRequests().remove(playerId);

        currentTrade.getPlayer(currentTrade.getPlayer1()).ifPresent(player -> {
            currentTrade.getPlayer1Items().forEach(item -> player.getInventory().addItem(item));
            player.closeInventory();
        });

        currentTrade.getPlayer(currentTrade.getPlayer2()).ifPresent(player -> {
            currentTrade.getPlayer2Items().forEach(item -> player.getInventory().addItem(item));
            player.closeInventory();
        });
    }
}