package dev.kacperm.trade.trade;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerTrade {

    private UUID tradeId, player1, player2;
    private ItemStack[] player1Items, player2Items;
}
