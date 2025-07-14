package dev.kacperm.trade;

import dev.kacperm.trade.command.TradeAdminCommand;
import dev.kacperm.trade.command.TradeCommand;
import dev.kacperm.trade.listener.DatabaseListener;
import dev.kacperm.trade.listener.TradeListener;
import dev.kacperm.trade.mongo.MongoManager;
import dev.kacperm.trade.trade.manager.TradeManager;
import dev.kacperm.trade.utils.config.Config;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

@Getter
public final class Trade extends JavaPlugin {

    @Getter
    private static Trade instance;

    private Config configuration, language;

    private MongoManager mongoManager;
    private TradeManager tradeManager;

    private final NamespacedKey button = new NamespacedKey(this, "button");

    @Override
    public void onEnable() {
        instance = this;

        this.loadConfiguration();
        this.loadListener(Bukkit.getPluginManager());
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

    private void loadListener(PluginManager pluginManager) {
        pluginManager.registerEvents(new TradeListener(), this);
        pluginManager.registerEvents(new DatabaseListener(), this);
    }

    private void loadCommand() {
        Objects.requireNonNull(getCommand("trade")).setExecutor(new TradeCommand());
        Objects.requireNonNull(getCommand("tradeadmin")).setExecutor(new TradeAdminCommand());
    }
}
