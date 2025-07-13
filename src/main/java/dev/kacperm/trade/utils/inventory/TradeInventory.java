package dev.kacperm.trade.utils.inventory;

import dev.kacperm.trade.Trade;
import dev.kacperm.trade.trade.PlayerTrade;
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

        ItemStack notConfirmed = new ItemStack(Material.valueOf(Trade.getInstance().getConfiguration().getConfiguration().getString("trade-inventory.trade-button.not-confirmed.item")));
        ItemStack itemBetween = new ItemStack(Material.valueOf(Trade.getInstance().getConfiguration().getConfiguration().getString("trade-inventory.trade-button.item-between.item")));

        ItemMeta notConfirmedMeta = notConfirmed.getItemMeta();
        ItemMeta itemBetweenMeta = itemBetween.getItemMeta();

        notConfirmedMeta.displayName(Color.translate(Objects.requireNonNull(Trade.getInstance().getConfiguration().getConfiguration().getString("trade-inventory.trade-button.not-confirmed.name"))));
        itemBetweenMeta.displayName(Color.translate(Objects.requireNonNull(Trade.getInstance().getConfiguration().getConfiguration().getString("trade-inventory.trade-button.item-between.name"))));

        notConfirmedMeta.getPersistentDataContainer().set(Trade.getInstance().getButton(), PersistentDataType.STRING, "not-confirmed");
        itemBetweenMeta.getPersistentDataContainer().set(Trade.getInstance().getButton(), PersistentDataType.STRING, "item-between");

        List<Component> notConfirmedLore = new ArrayList<>();
        List<Component> itemBetweenLore = new ArrayList<>();

        for (final String s : Trade.getInstance().getConfiguration().getConfiguration().getStringList("trade-inventory.trade-button.not-confirmed.lore") ) {
            notConfirmedLore.add(Color.translate(s));
        }
        for (final String s : Trade.getInstance().getConfiguration().getConfiguration().getStringList("trade-inventory.trade-button.item-between.lore") ) {
            itemBetweenLore.add(Color.translate(s));
        }

        notConfirmedMeta.lore(notConfirmedLore);
        itemBetweenMeta.lore(itemBetweenLore);

        notConfirmed.setItemMeta(notConfirmedMeta);
        itemBetween.setItemMeta(itemBetweenMeta);

        Trade.getInstance().getConfiguration().getConfiguration().getIntegerList("trade-inventory.trade-button.button-slots").forEach(slot ->
                inventory.setItem(slot, notConfirmed));

        for (int i : Trade.getInstance().getConfiguration().getConfiguration().getIntegerList("trade-inventory.trade-button.item-between.slots")) {
            inventory.setItem(i, itemBetween);
        }

        return inventory;
    }

    public static Inventory playerTrade(PlayerTrade trade) {
        Inventory inventory = Bukkit.createInventory(null,
                Trade.getInstance().getConfiguration().getConfiguration().getInt("trade-view-inventory.size"),
                Color.translate(Objects.requireNonNull(Trade.getInstance().getConfiguration().getConfiguration().getString("trade-view-inventory.title"))));

        ItemStack itemBetween = new ItemStack(Material.valueOf(Trade.getInstance().getConfiguration().getConfiguration().getString("trade-inventory.trade-button.item-between.item")));

        ItemMeta itemBetweenMeta = itemBetween.getItemMeta();

        itemBetweenMeta.displayName(Color.translate(Objects.requireNonNull(Trade.getInstance().getConfiguration().getConfiguration().getString("trade-inventory.trade-button.item-between.name"))));

        itemBetweenMeta.getPersistentDataContainer().set(Trade.getInstance().getButton(), PersistentDataType.STRING, "item-between");

        List<Component> itemBetweenLore = new ArrayList<>();

        for (final String s : Trade.getInstance().getConfiguration().getConfiguration().getStringList("trade-inventory.trade-button.item-between.lore") ) {
            itemBetweenLore.add(Color.translate(s));
        }

        itemBetweenMeta.lore(itemBetweenLore);

        itemBetween.setItemMeta(itemBetweenMeta);

        for (int i : Trade.getInstance().getConfiguration().getConfiguration().getIntegerList("trade-inventory.trade-button.item-between.slots")) {
            inventory.setItem(i, itemBetween);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (trade.getPlayer1Items()[i] != null) {
                inventory.setItem(i, trade.getPlayer1Items()[i]);
            } else if (trade.getPlayer2Items()[i] != null) {
                inventory.setItem(i, trade.getPlayer2Items()[i]);
            }
        }

        return inventory;
    }
}
