package me.thanhmagics.core;

import me.thanhmagics.DungeonGate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AsyncAFKChecker {

    public static Map<UUID, Long> players = new HashMap<>();

    public static int i = 1000 * 60 * 30;


    public static void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                List<UUID> wr = new ArrayList<>();
                players.forEach((k,v) -> {
                    if (System.currentTimeMillis() - v > i) {
                        wr.add(k);
                    }
                    if (!wr.isEmpty()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                wr.forEach(e -> {
                                    players.remove(e);
                                    Player p = Bukkit.getPlayer(e);
                                    assert p != null;
                                    p.teleport(DungeonGate.getInstance().config.spawn);
                                });
                            }
                        }.runTask(DungeonGate.getInstance());
                    }
                });
            }
        }.runTaskTimerAsynchronously(DungeonGate.getInstance(),0,20);
    }

    public static void update(UUID uuid,long t) {
        players.replace(uuid,t);
    }

}
