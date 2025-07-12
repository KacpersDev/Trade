package dev.kacperm.trade.trade;

import dev.kacperm.trade.Trade;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CurrentTrade {

    private UUID player1, player2;
    private Inventory inventory;
    private boolean isAccepted;
    private int countdown;

    public boolean validateAcceptation() {
        Material notConfirmed = Material.valueOf(Trade.getInstance().getConfiguration()
                .getConfiguration().getString("trade-inventory.trade-button.not-confirmed.item"));

        for (int i : Trade.getInstance().getConfiguration().getConfiguration().getIntegerList("trade-inventory.trade-button.button-slots")) {
            if (inventory.getItem(i) != null && Objects.requireNonNull(inventory.getItem(i)).getType().equals(notConfirmed)) {
                return false;
            }
        }

        return true;
    }
}
