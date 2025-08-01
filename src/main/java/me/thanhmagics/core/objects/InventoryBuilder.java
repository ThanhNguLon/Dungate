package me.thanhmagics.core.objects;

import me.thanhmagics.DungeonGate;
import me.thanhmagics.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryBuilder implements Listener {

    public static boolean regEvent = false;

    public InventoryBuilder() {
        if (!regEvent) {
            DungeonGate.getInstance().getServer().getPluginManager().registerEvents(this,DungeonGate.getInstance());
            regEvent = !regEvent;
        }
    }

    public int size = 54;

    public String display = "";

    private UUID uuid = UUID.randomUUID();

    public Map<Integer, ItemStack> decorItems = new HashMap<>();

    public Map<Integer,IBConfig> actionItems = new HashMap<>();

    private static Map<UUID,InventoryBuilder> inventoryBuilders = new HashMap<>();

    public InventoryBuilder setSize(int size) {
        this.size = size;
        return this;
    }

    public InventoryBuilder setTitle(String title) {
        display = Utils.applyColor(title);
        return this;
    }

    private Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null,size,Utils.applyColor(display));
        for (Integer integer : decorItems.keySet()) {
            inventory.setItem(integer,decorItems.get(integer));
        }
        for (Integer integer : actionItems.keySet()) {
            inventory.setItem(integer,actionItems.get(integer).itemStack());
        }
        return inventory;
    }

    public InventoryBuilder addActionItem(int i, IBConfig config) {
        actionItems.remove(i);
        actionItems.put(i,config);
        return this;
    }

    public InventoryBuilder addDecorItem(int i, ItemStack itemStack) {
        decorItems.remove(i);
        decorItems.put(i,itemStack);
        return this;
    }

    public InventoryBuilder open(PPlayer... playerData) {
        for (PPlayer data : playerData) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(data.getUuid());
            if (player.isOnline()) {
                player.getPlayer().openInventory(getInventory());
                data.setInv(uuid);
                inventoryBuilders.put(uuid, this);
            }
        }
        return this;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PPlayer playerData = DungeonGate.getInstance().players.get(player);
        if (playerData.getInv() == null) return;
        if (inventoryBuilders.containsKey(playerData.getInv())) {
            event.setCancelled(true);
            if (event.getClickedInventory() instanceof PlayerInventory) return;
            InventoryBuilder inventoryBuilder = inventoryBuilders.get(playerData.getInv());
            if (inventoryBuilder.actionItems.containsKey(event.getSlot())) {
                inventoryBuilder.actionItems.get(event.getSlot()).onClick(event.getCurrentItem(),
                        event.getSlot(),event.getClick(),inventoryBuilder);
            }
        }
    }

    public static abstract class IBConfig {

        public abstract ItemStack itemStack();

        public abstract void onClick(ItemStack clicked, int stt, ClickType clickType, InventoryBuilder instance);

    }

}
