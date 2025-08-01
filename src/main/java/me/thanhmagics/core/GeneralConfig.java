package me.thanhmagics.core;

import me.thanhmagics.DungeonGate;
import me.thanhmagics.core.portals.TeleportPortal;
import me.thanhmagics.utils.ConfigUtils;
import me.thanhmagics.utils.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class GeneralConfig {

    private ConfigUtils configUtils;

    public Location spawn;

    public GeneralConfig() {
        this.configUtils = new ConfigUtils("config.yml");
        if (configUtils.getDatas().isEmpty()) {

        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(DungeonGate.getInstance().getDataFolder(), "config.yml"));
        spawn = Utils.str2Loc(config.getString("spawn"));

        for (String s1 : config.getConfigurationSection("gates").getKeys(false)) {
            new TeleportPortal(new File(DungeonGate.getInstance().getDataFolder(),
                    config.getString("gates." + s1 + ".sch")),
                    config.getString("gates." + s1 + ".live-time"),
                    Utils.str2Loc(config.getString("gates." + s1 + ".to")),
                    Utils.str2Loc(config.getString("gates." + s1 + ".gate")),
                    config.getStringList("gates." + s1 + ".message"),
                    config.getString("gates." + s1 + ".max_player"),
                    config.getString("gates." + s1 + ".perm"),
                    config.getStringList("gates." + s1 + ".onDeny"));
        }
    }

    public ConfigUtils getConfigUtils() {
        return configUtils;
    }
}
