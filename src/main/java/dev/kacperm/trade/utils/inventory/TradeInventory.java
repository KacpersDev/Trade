package dev.kacperm.trade.utils.inventory;

import dev.kacperm.trade.Trade;
import dev.kacperm.trade.utils.color.Color;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TradeInventory {

    public static Inventory trade() {
        Inventory inventory = Bukkit.createInventory(null,
                Trade.getInstance().getConfiguration().getConfiguration().getInt("trade-inventory.size"),
                Color.translate(Objects.requireNonNull(Trade.getInstance().getConfiguration().getConfiguration().getString("trade-inventory.title"))));

        ItemStack notConfirmed = new ItemStack(Material.valueOf(Trade.getInstance().getConfiguration().getConfiguration().getString("trade-inventory.trade-button.not-confirmed")));
        ItemStack itemBetween = new ItemStack(Material.valueOf(Trade.getInstance().getConfiguration().getConfiguration().getString("trade-inventory.trade-button.item-between.item")));

        ItemMeta notConfirmedMeta = notConfirmed.getItemMeta();
        ItemMeta itemBetweenMeta = itemBetween.getItemMeta();

        notConfirmedMeta.displayName(Color.translate(Objects.requireNonNull(Trade.getInstance().getConfiguration().getConfiguration().getString("trade-inventory.trade-button.not-confirmed-name"))));
        itemBetweenMeta.displayName(Color.translate(Objects.requireNonNull(Trade.getInstance().getConfiguration().getConfiguration().getString("trade-inventory.trade-button.item-between.name"))));

        notConfirmedMeta.getPersistentDataContainer().set(Trade.getInstance().getButton(), PersistentDataType.STRING, "not-confirmed");
        itemBetweenMeta.getPersistentDataContainer().set(Trade.getInstance().getButton(), PersistentDataType.STRING, "item-between");

        List<Component> notConfirmedLore = new ArrayList<>();
        List<Component> itemBetweenLore = new ArrayList<>();

        for (final String s : Trade.getInstance().getConfiguration().getConfiguration().getStringList("trade-inventory.trade-button.not-confirmed-lore") ) {
            notConfirmedLore.add(Color.translate(s));
        }
        for (final String s : Trade.getInstance().getConfiguration().getConfiguration().getStringList("trade-inventory.trade-button.item-between.lore") ) {
            itemBetweenLore.add(Color.translate(s));
        }

        notConfirmedMeta.lore(notConfirmedLore);
        itemBetweenMeta.lore(itemBetweenLore);

        notConfirmed.setItemMeta(notConfirmedMeta);
        itemBetween.setItemMeta(itemBetweenMeta);

        Trade.getInstance().getLanguage().getConfiguration().getStringList("trade-inventory.trade-button.button-slots").forEach(slot ->
                inventory.setItem(Integer.parseInt(slot), notConfirmed));

        for (int i : Trade.getInstance().getConfiguration().getConfiguration().getIntegerList("trade-inventory.trade-button.item-between.slots")) {
            inventory.setItem(i, itemBetween);
        }

        return inventory;
    }
}
