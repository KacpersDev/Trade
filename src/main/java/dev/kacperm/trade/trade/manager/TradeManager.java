package dev.kacperm.trade.trade.manager;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.kacperm.trade.Trade;
import dev.kacperm.trade.trade.PlayerTrade;
import dev.kacperm.trade.utils.serializer.ItemStackSerializer;
import lombok.Getter;
import org.bson.Document;

import java.io.IOException;
import java.util.*;

@Getter
public class TradeManager {

    private final Map<UUID, List<PlayerTrade>> trades = new HashMap<>();

    public void load(UUID uniqueId) throws IOException {
        if (!trades.containsKey(uniqueId)) {
            FindIterable<Document> iterable = Trade.getInstance().getMongoManager().getTrades().find(new Document("tradeId", uniqueId));
            try (MongoCursor<Document> cursor = iterable.iterator()) {
                List<PlayerTrade> playerTrades = new ArrayList<>();
                while (cursor.hasNext()) {
                    Document document = cursor.next();

                    PlayerTrade playerTrade = new PlayerTrade(
                            UUID.fromString(document.getString("tradeId")),
                            UUID.fromString(document.getString("player1")),
                            UUID.fromString(document.getString("player2")),
                            ItemStackSerializer.itemStackArrayFromBase64(document.getString("items")));

                    playerTrades.add(playerTrade);
                }

                trades.put(uniqueId, playerTrades);
            }
        }
    }

    public void save(UUID uniqueId) {
        for (PlayerTrade playerTrade : trades.get(uniqueId)) {
            Document document = new Document();
            document.append("tradeId", playerTrade.getTradeId().toString())
                    .append("player1", playerTrade.getPlayer1().toString())
                    .append("player2", playerTrade.getPlayer2().toString())
                    .append("items", ItemStackSerializer.itemStackArrayToBase64(playerTrade.getTradedItems()));

            Trade.getInstance().getMongoManager().getTrades().replaceOne(Filters.eq("uuid", playerTrade.getTradeId()),
                    document, new ReplaceOptions().upsert(true));
        }
    }

    public void saveAll() {
        trades.keySet().forEach(this::save);
    }
}
