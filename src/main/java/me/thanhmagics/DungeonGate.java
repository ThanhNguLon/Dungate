package me.thanhmagics;

import me.thanhmagics.core.AsyncAFKChecker;
import me.thanhmagics.core.GeneralConfig;
import me.thanhmagics.core.Listeners;
import me.thanhmagics.core.commands.ReloadCommand;
import me.thanhmagics.core.commands.SchematicSerialize;
import me.thanhmagics.core.objects.PPlayer;
import me.thanhmagics.core.portals.TeleportPortal;
import me.thanhmagics.utils.WEUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DungeonGate extends JavaPlugin {

    private static DungeonGate instance;

    public Map<Player, PPlayer> players = new HashMap<>();

    public GeneralConfig config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        this.config = new GeneralConfig();
        saveResource("usage.yml",true);
        TeleportPortal.scheduler();
        getServer().getPluginManager().registerEvents(new SchematicSerialize(),this);
        getServer().getPluginManager().registerEvents(new Listeners(),this);
        getCommand("newschematic").setExecutor(new SchematicSerialize());
        getCommand("dnGateReload").setExecutor(new ReloadCommand());
        AsyncAFKChecker.run();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (TeleportPortal portal : TeleportPortal.portals) {
            portal.discard();
        }
    }


    public static DungeonGate getInstance() {
        return instance;
    }

    public GeneralConfig getPluginConfig() {
        return config;
    }
}
