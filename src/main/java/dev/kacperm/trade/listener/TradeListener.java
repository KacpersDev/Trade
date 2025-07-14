package dev.kacperm.trade.listener;

import dev.kacperm.trade.Trade;
import dev.kacperm.trade.trade.CurrentTrade;
import dev.kacperm.trade.trade.PlayerTrade;
import dev.kacperm.trade.utils.inventory.TradeInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

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

        int confirmSlotOne = Trade.getInstance().getConfiguration().getConfiguration().getInt("slots.confirm-slot-1");
        int confirmSlotTwo = Trade.getInstance().getConfiguration().getConfiguration().getInt("slots.confirm-slot-2");

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
                    if (isTopInventory && canClick(player, event.getSlot(), currentTrade, confirmSlotOne, confirmSlotTwo)) {
                        event.setCurrentItem(TradeInventory.confirmed());
                        player.updateInventory();
                    }

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
                            currentTrade.getPlayer2Items().forEach(item -> {
                                if (item != null) {
                                    p.getInventory().addItem(item);
                                }
                            });
                        });

                        currentTrade.getPlayer(currentTrade.getPlayer2()).ifPresent(p -> {
                            p.closeInventory();
                            currentTrade.getPlayer1Items().forEach(item -> {
                                if (item != null) {
                                    p.getInventory().addItem(item);
                                }
                            });
                        });
                    }
                }
            }
        }

        currentTrade.update();
    }

    private boolean canClick(Player player, int slot, CurrentTrade currentTrade, int slotOne, int slotTwo) {
        if (currentTrade.getPlayer2().equals(player.getUniqueId())) {
            return slotTwo == slot;
        } else {
            return slotOne == slot;
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        CurrentTrade currentTrade = Trade.getInstance().getTradeManager().getTrade(playerId);
        if (currentTrade == null || event.getReason().equals(InventoryCloseEvent.Reason.PLUGIN)) return;

        Inventory inventory = event.getInventory();

        List<Integer> player1Slots = Trade.getInstance().getConfiguration().getConfiguration().getIntegerList("slots.player-1");
        List<Integer> player2Slots = Trade.getInstance().getConfiguration().getConfiguration().getIntegerList("slots.player-2");

        Player first = currentTrade.getPlayer(currentTrade.getPlayer1()).orElse(null);
        Player second = currentTrade.getPlayer(currentTrade.getPlayer2()).orElse(null);

        if (first != null && second != null) {
            for (int slot : player1Slots) {
                ItemStack item = inventory.getItem(slot);
                if (item != null && !item.getItemMeta().getPersistentDataContainer().has(Trade.getInstance().getButton(), PersistentDataType.STRING)) {
                    first.getInventory().addItem(item.clone());
                    first.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                }
            }

            for (int slot : player2Slots) {
                ItemStack item = inventory.getItem(slot);
                if (item != null && !item.getItemMeta().getPersistentDataContainer().has(Trade.getInstance().getButton(), PersistentDataType.STRING)) {
                    second.getInventory().addItem(item.clone());
                    first.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                }
            }
        }

        Trade.getInstance().getTradeManager().getCurrentTrades().remove(currentTrade);
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        CurrentTrade currentTrade = Trade.getInstance().getTradeManager().getTrade(playerId);
        if (currentTrade == null) return;

        Inventory inventory = event.getPlayer().getOpenInventory().getTopInventory();

        List<Integer> player1Slots = Trade.getInstance().getConfiguration().getConfiguration().getIntegerList("slots.player-1");
        List<Integer> player2Slots = Trade.getInstance().getConfiguration().getConfiguration().getIntegerList("slots.player-2");

        Player first = currentTrade.getPlayer(currentTrade.getPlayer1()).orElse(null);
        Player second = currentTrade.getPlayer(currentTrade.getPlayer2()).orElse(null);

        if (first != null && second != null) {
            for (int slot : player1Slots) {
                ItemStack item = inventory.getItem(slot);
                if (item != null && !item.getItemMeta().getPersistentDataContainer().has(Trade.getInstance().getButton(), PersistentDataType.STRING)) {
                    first.getInventory().addItem(item.clone());
                    first.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                }
            }

            for (int slot : player2Slots) {
                ItemStack item = inventory.getItem(slot);
                if (item != null && !item.getItemMeta().getPersistentDataContainer().has(Trade.getInstance().getButton(), PersistentDataType.STRING)) {
                    second.getInventory().addItem(item.clone());
                    first.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                }
            }
        }

        Trade.getInstance().getTradeManager().getCurrentTrades().remove(currentTrade);
    }
}