package dev.kacperm.trade;

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

    @Override
    public void onEnable() {
        instance = this;

        this.loadConfiguration();
        this.loadListener();
        this.loadCommand();
    }

    @Override
    public void onDisable() {
        instance = null;
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
