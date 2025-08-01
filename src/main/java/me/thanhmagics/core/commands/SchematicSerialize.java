package me.thanhmagics.core.commands;

import me.thanhmagics.utils.WEUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.Map;

public class SchematicSerialize implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (strings.length == 1) {
            String name = strings[0];
            hashMap.remove(player);
            nname.remove(player);
            nname.put(player,name);
            player.sendMessage("đập 2 block...");
        }
        return true;
    }

    static Map<Player, Location> hashMap = new HashMap<>();
    static Map<Player,String> nname = new HashMap<>();
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (nname.containsKey(player)) {
            event.setCancelled(true);
            if (hashMap.containsKey(player)) {
                event.getBlock().setType(Material.AIR);
                WEUtils.copy2File(nname.get(player), hashMap.get(player), event.getBlock().getLocation());
                hashMap.remove(player);
                nname.remove(player);
                player.sendMessage("lưu thành công!");
            } else {
                hashMap.put(player, event.getBlock().getLocation());
                player.sendMessage("đặt pos 1 thành công");
            }
        }
    }
}
