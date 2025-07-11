package dev.kacperm.trade.listener;

import dev.kacperm.trade.Trade;
import dev.kacperm.trade.utils.color.Color;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DatabaseListener implements Listener {

    @EventHandler
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        try {
            Trade.getInstance().getTradeManager().load(event.getUniqueId());
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Color.translate("&cCould not load trade data for player &e" + event.getName()));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(Trade.getInstance(), () ->
                Trade.getInstance().getTradeManager().save(event.getPlayer().getUniqueId()));
    }
}
