package me.thanhmagics.core.portals;

import com.sk89q.worldedit.math.BlockVector3;
import jdk.jshell.execution.Util;
import me.thanhmagics.DungeonGate;
import me.thanhmagics.core.Listeners;
import me.thanhmagics.utils.Utils;
import me.thanhmagics.utils.WEUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;

public class TeleportPortal {

    private static final Logger log = LogManager.getLogger(TeleportPortal.class);
    public static List<TeleportPortal> portals = new ArrayList<>();

    public File schFile;

    public String liveTime,max_player, permRequire;

    public Location to,gate_loc;

    public List<String> onTpMessage = new ArrayList<>(), tpDenialMessage;

    public boolean enable = false;

    public TeleportPortal(File schFile, String liveTime, Location to,Location gate,List<String> onTpMessage
    , String max_player,String permRequire,List<String> dm) {
        this.schFile = schFile;
        this.liveTime = liveTime;
        this.to = to;
        this.gate_loc = gate;
        this.onTpMessage = onTpMessage;
        this.max_player = max_player;this.permRequire = permRequire;
        this.tpDenialMessage = dm;
        portals.add(this);
    }


    public long t1() {
        return Utils.stringToMs(liveTime.split("-")[0]);
    }

    public long t2() {
        return Utils.stringToMs(liveTime.split("-")[1]);
    }

    public void discard() {
        enable = false;
        Map<Vector, Material> blocks = WEUtils.getBlocks(schFile);
        for (Vector k : blocks.keySet()) {
            gate_loc.getWorld().getBlockAt(gate_loc.getBlockX() + k.getBlockX(),
                            gate_loc.getBlockY() + k.getBlockY(),
                            gate_loc.getBlockZ() + k.getBlockZ())
                    .setType(Material.AIR);
        }
    }

    public void active() {
        WEUtils.paste(schFile,gate_loc);
        List<Player> wr = new ArrayList<>();
        Listeners.insiders.forEach((k,v) -> {
            if (v.equals(this)) wr.add(k);
        });
        wr.forEach(e -> Listeners.insiders.remove(e));
        enable = true;
    }

    public static void scheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long formatedTime = Utils.stringToMs(Utils.getTime().getHours() + "h" + Utils.getTime().getMinutes() + "m");
                for (TeleportPortal each : portals) {
                    if (each.enable) {
                        if (formatedTime > each.t2() || formatedTime < each.t1()) {
                            if (each.enable) each.discard();
                        }
                    } else {
                        if (formatedTime >= each.t1() && formatedTime <= each.t2()) {
                            if (!each.enable) each.active();
                        }
                    }
                }
            }
        }.runTaskTimer(DungeonGate.getInstance(),0,20 * 10);
    }

    public static Map<String,TeleportPortal> storage = new HashMap<>();

    public static TeleportPortal getTPbyWorldName(String wn) {
        if (storage.isEmpty()) {
            for (TeleportPortal g : portals) {
                if (g.to.equals(wn)) return g;
            }
        } else {
            return storage.get(wn);
        }
        return null;
    }
}
