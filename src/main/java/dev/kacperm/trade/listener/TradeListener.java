package dev.kacperm.trade.listener;

import dev.kacperm.trade.Trade;
import dev.kacperm.trade.trade.CurrentTrade;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.persistence.PersistentDataType;

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
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        CurrentTrade currentTrade = Trade.getInstance().getTradeManager().getTrade(event.getPlayer().getUniqueId());
        if (currentTrade == null) return;

        Trade.getInstance().getTradeManager().getCurrentTrades().remove(currentTrade);
        Trade.getInstance().getTradeManager().getRequests().remove(event.getPlayer().getUniqueId());

        // TODO refund...
    }
}
