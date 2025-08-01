package me.thanhmagics.core.commands;

import me.thanhmagics.DungeonGate;
import me.thanhmagics.core.GeneralConfig;
import me.thanhmagics.core.portals.TeleportPortal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        for (TeleportPortal portal : TeleportPortal.portals)
            portal.discard();
        TeleportPortal.portals.clear();
        DungeonGate.getInstance().config = new GeneralConfig();
        TeleportPortal.storage.clear();
        commandSender.sendMessage("Reloaded!");
        return true;
    }
}
