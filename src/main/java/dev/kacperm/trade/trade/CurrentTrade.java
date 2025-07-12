package dev.kacperm.trade.trade;

import dev.kacperm.trade.Trade;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CurrentTrade {

    private UUID player1, player2;
    private Inventory inventory;
    private List<ItemStack> player1Items, player2Items;
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

    public Optional<Player> getPlayer(UUID uuid) {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    public void update() {
        Optional<Player> player = getPlayer(player1);
        player.ifPresent(Player::updateInventory);
    }
}
