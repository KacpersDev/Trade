package dev.kacperm.trade.utils.color;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Color {

    public static Component translate(String vanillaMessage) {
        if (vanillaMessage.trim().contains("&")) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(vanillaMessage).decoration(TextDecoration.ITALIC, false);
        } else if (vanillaMessage.trim().startsWith("§")) {
            return LegacyComponentSerializer.legacySection().deserialize(vanillaMessage).decoration(TextDecoration.ITALIC, false);
        } else {
            return MiniMessage.miniMessage().deserialize(vanillaMessage).decoration(TextDecoration.ITALIC, false);
        }
    }
}