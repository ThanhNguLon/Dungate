package me.thanhmagics.core.objects;

import me.thanhmagics.DungeonGate;
import me.thanhmagics.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class SelectionGUI implements Listener {
    private List<ItemStack> content;
    public int size;
    private String display;
    public SG_OnClick onClick;
    public Inventory inventory = null;
    public static Map<UUID, SelectionGUI> guis = new HashMap<>();
    public Runnable[] clickBack = new Runnable[1];
    public SG_OnClick onClickPI;
    public int[] deco;
    public SG_Content ct_builder;


    public static boolean regEvent = false;

    public SelectionGUI() {
        if (!regEvent) {
            DungeonGate.getInstance().getServer().getPluginManager().registerEvents(this,DungeonGate.getInstance());
            regEvent = !regEvent;
        }
    }

    public SelectionGUI(SG_Content content, int size, String display, SG_OnClick run, SG_OnClick runPI, Runnable... clickBack) {
        this.size = (size % 9) == 0 ? size : 54;
        this.ct_builder = content;
        this.display = display;
        this.onClick = run;
        this.deco = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, size - 1, size - 2, size - 3, size - 4, size - 5, size - 6, size - 7, size - 8, size - 9};
        this.onClickPI = runPI;
        if (clickBack != null && clickBack.length > 0)
            this.clickBack[0] = clickBack[0];
    }

    public int page, maxPage;

    public void openGUI(PPlayer player, int page) {
        if (page == 0)
            throw new RuntimeException("");
        this.page = page;
        this.content = ct_builder.get();
        UUID invUID = UUID.randomUUID();
        Inventory inventory = Bukkit.createInventory(null, size, Utils.applyColor(display));
        ItemStack itemStack = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setDisplayName("&a").build();
        for (int i : deco)
            inventory.setItem(i, itemStack);
        maxPage = size - 18 > content.size() ? 1 : (content.size() / (size - 18)) + 1;
        ItemStack nextPage = new ItemBuilder(Material.ARROW).setDisplayName("&aTrang Kế Tiếp &7(" + page + "/" + maxPage + ")").build();
        inventory.setItem(size - 1, nextPage);
        inventory.setItem(size - 9, new ItemBuilder(Material.ARROW).setDisplayName("&aTrang Trước&7 (" + page + "/" + maxPage + ")").build());
        if (clickBack[0] != null)
            inventory.setItem(size - 5, new ItemBuilder(Material.ARROW).setDisplayName("&aTrở Lại GUI trước").build());
        for (int i = page == 1 ? 0 : (size - 18) * (page - 1); i < content.size(); i++) {
            if (Utils.isInvFull(inventory)) continue;
            ItemStack is = content.get(i);
            is.setType((is.getType()));
            inventory.setItem(inventory.firstEmpty(), is);
        }
        Bukkit.getPlayer(player.getInv()).openInventory(inventory);
        player.setInv(invUID);
        this.inventory = inventory;
        guis.put(invUID, this);
    }


    @EventHandler
    public void onEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PPlayer dgPlayer = DungeonGate.getInstance().players.get(player);
        if (dgPlayer.getInv() == null) return;
        if (!SelectionGUI.guis.containsKey(dgPlayer.getInv())) return;
        SelectionGUI sg = SelectionGUI.guis.get(dgPlayer.getInv());
        event.setCancelled(true);
        if (event.getClickedInventory() instanceof PlayerInventory) {
            if (sg.onClickPI != null) {
                sg.onClickPI.run(event.getCurrentItem(), event.getSlot(), event.getClick(), sg);
                //  if (sg.onClickPI.reopen) sg.openGUI(dgPlayer, sg.page);
            }
        } else {
            if (event.getSlot() > 8 && event.getSlot() < (sg.size - 9)) {
                ItemStack item = event.getCurrentItem();
                if (item != null) sg.onClick.run(item, event.getSlot(), event.getClick(), sg);
                // if (sg.onClick.reopen) {sg.openGUI(dgPlayer, sg.page);player.sendMessage("abc");} else player.sendMessage("xyz");
            }
            if (event.getSlot() == sg.size - 1 && sg.maxPage >= sg.page + 1)
                new SelectionGUI(sg.ct_builder, sg.size, sg.display, sg.onClick, sg.onClickPI, sg.clickBack).openGUI(dgPlayer, sg.page + 1);
            if (event.getSlot() == sg.size - 9 && sg.page != 1)
                new SelectionGUI(sg.ct_builder, sg.size, sg.display, sg.onClick, sg.onClickPI, sg.clickBack).openGUI(dgPlayer, sg.page - 1);
            if (event.getSlot() == sg.size - 5 && sg.clickBack[0] != null)
                sg.clickBack[0].run();
        }
    }

    public abstract static class SG_OnClick {


        public abstract void run(ItemStack clicked, int stt, ClickType clickType, SelectionGUI instance);
    }

    public abstract static class SG_Content {

        public abstract List<ItemStack> get();
    }

}