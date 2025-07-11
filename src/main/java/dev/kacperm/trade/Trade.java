package dev.kacperm.trade;

import dev.kacperm.trade.mongo.MongoManager;
import dev.kacperm.trade.trade.manager.TradeManager;
import dev.kacperm.trade.utils.config.Config;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class Trade extends JavaPlugin {

    @Getter
    private static Trade instance;

    private Config configuration, language;

    private MongoManager mongoManager;
    private TradeManager tradeManager;

    @Override
    public void onEnable() {
        instance = this;

        this.loadConfiguration();
        this.loadListener();
        this.loadCommand();

        this.mongoManager = new MongoManager();
        this.tradeManager = new TradeManager();
    }

    @Override
    public void onDisable() {
        this.tradeManager.saveAll();

        instance = null;

        if (this.mongoManager != null) {
            this.mongoManager.close();
        }
    }

    private void loadConfiguration() {
        this.configuration = new Config(this, new File(getDataFolder(), "configuration.yml"),
                new YamlConfiguration(), "configuration.yml");
        this.language = new Config(this, new File(getDataFolder(), "language.yml"),
                new YamlConfiguration(), "language.yml");

        this.configuration.create();
        this.language.create();
    }

    private void loadListener() {}

    private void loadCommand() {}
}
