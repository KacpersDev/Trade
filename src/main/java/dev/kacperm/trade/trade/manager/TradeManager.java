package dev.kacperm.trade.trade.manager;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.kacperm.trade.Trade;
import dev.kacperm.trade.trade.CurrentTrade;
import dev.kacperm.trade.trade.PlayerTrade;
import dev.kacperm.trade.utils.serializer.ItemStackSerializer;
import lombok.Getter;
import org.bson.Document;

import java.io.IOException;
import java.util.*;

@Getter
public class TradeManager {

    private final Map<UUID, List<PlayerTrade>> trades = new HashMap<>();
    private final Set<CurrentTrade> currentTrades = new HashSet<>();

    private final Map<UUID, List<UUID>> requests = new HashMap<>();

    public void load(UUID uniqueId) throws IOException {
        if (!trades.containsKey(uniqueId)) {
            FindIterable<Document> iterable = Trade.getInstance().getMongoManager().getTrades()
                    .find(Filters.or(
                            Filters.eq("player1", uniqueId.toString()),
                            Filters.eq("player2", uniqueId.toString())
                    ));
            try (MongoCursor<Document> cursor = iterable.iterator()) {
                List<PlayerTrade> playerTrades = new ArrayList<>();
                while (cursor.hasNext()) {
                    Document document = cursor.next();

                    PlayerTrade playerTrade = new PlayerTrade(
                            UUID.fromString(document.getString("tradeId")),
                            UUID.fromString(document.getString("player1")),
                            UUID.fromString(document.getString("player2")),
                            ItemStackSerializer.itemStackArrayFromBase64(document.getString("items1")),
                            ItemStackSerializer.itemStackArrayFromBase64(document.getString("items2"))
                    );
                    playerTrades.add(playerTrade);
                }

                trades.put(uniqueId, playerTrades);
            }

            trades.computeIfAbsent(uniqueId, k -> new ArrayList<>());
        }
    }

    public void save(UUID uniqueId) {
        if (trades.containsKey(uniqueId)) {
            for (PlayerTrade playerTrade : trades.get(uniqueId)) {
                Document document = new Document();
                document.append("tradeId", playerTrade.getTradeId().toString())
                        .append("player1", playerTrade.getPlayer1().toString())
                        .append("player2", playerTrade.getPlayer2().toString())
                        .append("items1", ItemStackSerializer.itemStackArrayToBase64(playerTrade.getPlayer1Items()))
                        .append("items2", ItemStackSerializer.itemStackArrayToBase64(playerTrade.getPlayer2Items()));

                Trade.getInstance().getMongoManager().getTrades().replaceOne(Filters.eq("tradeId", playerTrade.getTradeId().toString()),
                        document, new ReplaceOptions().upsert(true));
            }
        }
    }

    public void saveAll() {
        trades.keySet().forEach(this::save);
    }

    public PlayerTrade getTradeById(UUID uuid) {
        for (List<PlayerTrade> trade : trades.values()) {
            for (PlayerTrade playerTrade : trade) {
                if (playerTrade.getTradeId().equals(uuid)) return playerTrade;
            }
        }

        return null;
    }

    public CurrentTrade getTrade(UUID uuid) {
        for (CurrentTrade currentTrade : currentTrades) {
            if (currentTrade.getPlayer1().equals(uuid) || currentTrade.getPlayer2().equals(uuid)) return currentTrade;
        }

        return null;
    }
}
