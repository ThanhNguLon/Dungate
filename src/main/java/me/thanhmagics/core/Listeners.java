package me.thanhmagics.core;

import me.thanhmagics.core.portals.TeleportPortal;
import me.thanhmagics.utils.Utils;
import me.thanhmagics.utils.WEUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Listeners implements Listener {

    public static Map<Player,TeleportPortal> insiders = new HashMap<>();

    @EventHandler
    public void portalEvent(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        for (TeleportPortal teleportPortal : TeleportPortal.portals) {
            if (teleportPortal.enable && WEUtils.isInsideSchematic(player.getLocation(),teleportPortal.schFile,teleportPortal.gate_loc)) {
                event.setCancelled(true);
                if (insiders.size() >= Integer.parseInt(teleportPortal.max_player) &&
                !player.hasPermission(teleportPortal.permRequire)) {
                    teleportPortal.tpDenialMessage.forEach(e -> {
                        player.sendMessage(Utils.applyColor(e));
                    });
                    return;
                }
                insiders.put(player,teleportPortal);
                player.teleport(teleportPortal.to);
                for (String s : teleportPortal.onTpMessage)
                    player.sendMessage(Utils.applyColor(s));
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TeleportPortal tp = TeleportPortal.getTPbyWorldName(event.getPlayer().getWorld().getName());
        if (tp != null && tp.enable) {
            insiders.remove(player,tp);
        }
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        TeleportPortal tp = TeleportPortal.getTPbyWorldName(event.getFrom().getName());
        if (tp != null && tp.enable) {
            insiders.remove(player,tp);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        TeleportPortal tp = TeleportPortal.getTPbyWorldName(player.getWorld().getName());
        if (tp != null && tp.enable) {
            AsyncAFKChecker.update(player.getUniqueId(), System.currentTimeMillis());
        }
    }

}
