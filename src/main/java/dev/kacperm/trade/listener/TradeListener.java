package dev.kacperm.trade.listener;

import dev.kacperm.trade.Trade;
import dev.kacperm.trade.trade.CurrentTrade;
import dev.kacperm.trade.trade.PlayerTrade;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.UUID;

public class TradeListener implements Listener {

    @EventHandler
    public void onTradeInventory(InventoryClickEvent event) {
        CurrentTrade currentTrade = Trade.getInstance().getTradeManager().getTrade(event.getWhoClicked().getUniqueId());
        if (currentTrade == null) return;

        if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;
        String button = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(Trade.getInstance().getButton(), PersistentDataType.STRING);

        if (button != null) {
            event.setCancelled(true);

            if (button.equalsIgnoreCase("not-confirmed")) {
                currentTrade.setAccepted(currentTrade.validateAcceptation());

                if (currentTrade.isAccepted()) {
                    Trade.getInstance().getTradeManager().getTrades().putIfAbsent(currentTrade.getPlayer1(), new ArrayList<>());
                    Trade.getInstance().getTradeManager().getTrades().putIfAbsent(currentTrade.getPlayer2(), new ArrayList<>());

                    UUID tradeId = UUID.randomUUID();

                    Trade.getInstance().getTradeManager().getTrades().get(currentTrade.getPlayer1()).add(new PlayerTrade(tradeId, currentTrade.getPlayer1(), currentTrade.getPlayer2(),
                            currentTrade.getPlayer1Items().toArray(new ItemStack[54]), currentTrade.getPlayer2Items().toArray(new ItemStack[54])));
                    Trade.getInstance().getTradeManager().getTrades().get(currentTrade.getPlayer2()).add(new PlayerTrade(tradeId, currentTrade.getPlayer1(), currentTrade.getPlayer2(),
                            currentTrade.getPlayer1Items().toArray(new ItemStack[54]), currentTrade.getPlayer2Items().toArray(new ItemStack[54])));

                    currentTrade.getPlayer(currentTrade.getPlayer1()).ifPresent(player -> {
                        player.closeInventory();
                        currentTrade.getPlayer2Items().forEach(i -> player.getInventory().addItem(i));
                    });

                    currentTrade.getPlayer(currentTrade.getPlayer2()).ifPresent(player -> {
                        player.closeInventory();
                        currentTrade.getPlayer1Items().forEach(i -> player.getInventory().addItem(i));
                    });
                }
            }
        }

        currentTrade.update();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        CurrentTrade currentTrade = Trade.getInstance().getTradeManager().getTrade(event.getPlayer().getUniqueId());
        if (currentTrade == null) return;

        Trade.getInstance().getTradeManager().getCurrentTrades().remove(currentTrade);
        Trade.getInstance().getTradeManager().getRequests().remove(event.getPlayer().getUniqueId());

        currentTrade.getPlayer(currentTrade.getPlayer1()).ifPresent(player -> {
            player.closeInventory();
            currentTrade.getPlayer2Items().forEach(i -> player.getInventory().addItem(i));
        });

        currentTrade.getPlayer(currentTrade.getPlayer2()).ifPresent(player -> {
            currentTrade.getPlayer1Items().forEach(i -> player.getInventory().addItem(i));
            player.closeInventory();
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        CurrentTrade currentTrade = Trade.getInstance().getTradeManager().getTrade(event.getPlayer().getUniqueId());
        if (currentTrade == null) return;

        Trade.getInstance().getTradeManager().getCurrentTrades().remove(currentTrade);
        Trade.getInstance().getTradeManager().getRequests().remove(event.getPlayer().getUniqueId());

        currentTrade.getPlayer(currentTrade.getPlayer1()).ifPresent(player -> {
            player.closeInventory();
            currentTrade.getPlayer1Items().forEach(i -> player.getInventory().addItem(i));
        });

        currentTrade.getPlayer(currentTrade.getPlayer2()).ifPresent(player -> {
            currentTrade.getPlayer2Items().forEach(i -> player.getInventory().addItem(i));
            player.closeInventory();
        });
    }
}
