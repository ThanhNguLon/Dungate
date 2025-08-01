package me.thanhmagics.utils;

import me.thanhmagics.DungeonGate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatEditing implements Listener {

    private static Map<UUID,Handler> handlers = new HashMap<>();

    public static void newHandler(Handler handler,UUID player) {
        if (!b) run();
        if (!handlers.containsKey(player)) {
            handlers.put(player,handler);
        }
    }

    private static boolean b = false;

    private static void run() {
        b = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (handlers.isEmpty()) return;
                handlers.forEach((k,v) -> {
                    Bukkit.getPlayer(k).sendTitle(Utils.applyColor("chat '-cancel' để thoát chế độ chỉnh sửa"),"");
                });
            }
        }.runTaskTimer(DungeonGate.getInstance(),0,20);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (handlers.containsKey(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEvent(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (handlers.containsKey(player.getUniqueId())) {
            Handler handler = handlers.get(player.getUniqueId());
            handlers.remove(player.getUniqueId());
            boolean b = handler.onChat(player,message);
            if (b) event.setCancelled(true);
        }
    }


    public static abstract class Handler {
        public abstract boolean onChat(Player player,String input);
    }



}
