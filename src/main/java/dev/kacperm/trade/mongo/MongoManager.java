package dev.kacperm.trade.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.kacperm.trade.Trade;
import lombok.Getter;
import org.bson.Document;

import java.util.Objects;

@Getter
public class MongoManager {

    private final MongoClient mongoClient;
    private final MongoCollection<Document> trades;

    public MongoManager() {
        mongoClient = MongoClients.create(new ConnectionString(Objects.requireNonNull(Trade.getInstance()
                .getConfiguration().getConfiguration().getString("mongo-uri"))));
        MongoDatabase database = mongoClient.getDatabase("trade");
        this.trades = database.getCollection("trades");
    }

    public void close() {
        mongoClient.close();
    }
}
