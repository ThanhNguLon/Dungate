package me.thanhmagics.utils;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import me.thanhmagics.DungeonGate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.WeakHashMap;

public class WEUtils {
    public static void copy2File(String name, Location pos1, Location pos2) {
        BlockVector3 loc1 = BlockVector3.at(pos1.getBlockX(),pos1.getBlockY(),pos1.getBlockZ());
        BlockVector3 loc2 = BlockVector3.at(pos2.getBlockX(),pos2.getBlockY(),pos2.getBlockZ());
        World world = FaweAPI.getWorld(pos1.getWorld().getName());
        CuboidRegion region = new CuboidRegion(world, loc1, loc2);

        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1);
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard,region.getMinimumPoint());
        forwardExtentCopy.setCopyingBiomes(false);
        forwardExtentCopy.setCopyingEntities(false);
        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
        File file = new File(DungeonGate.getInstance().getDataFolder(),  name + ".sch");
        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Clipboard getClipboard(File schematic) {
        try (FileInputStream fis = new FileInputStream(schematic);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ClipboardReader reader = BuiltInClipboardFormat.FAST.getReader(bis)) {
            return reader.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Map<Vector, Material> getBlocks(File file) {
        Clipboard clipboard = getClipboard(file);
        Map<Vector, Material> rs = new WeakHashMap<>();
        if (clipboard != null) {
            BlockVector3 min = clipboard.getRegion().getMinimumPoint();
            clipboard.getRegion().forEach(blockVector3 -> {
                BlockState blockState = clipboard.getBlock(blockVector3);
                BlockType blockType = blockState.getBlockType();
                Material material = Material.matchMaterial(blockType.id());
                BlockVector3 v = blockVector3.subtract(min);
                if (material != null) {
                    rs.put(new Vector(v.x(),v.y(),v.z()),
                            material);
                }
            });
        }
        return rs;
    }

    public static boolean isInsideSchematic(Location location,File file,Location schCenter) {
        Clipboard clipboard = getClipboard(file);
        BlockVector3 dimension = clipboard.getDimensions();
        return Utils.insideBox(location,new Location(location.getWorld(),
                        schCenter.getBlockX() + dimension.x(),schCenter.getBlockY() + dimension.z(), schCenter.getBlockZ() + dimension.z()),
                schCenter,1);
    }

    public static void paste(File schematic, Location location) {
        Clipboard clipboard = getClipboard(schematic);
        World faweWorld = FaweAPI.getWorld(location.getWorld().getName());
        clipboard.paste(faweWorld, BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()), false, false, false, null);
        clipboard.close();
    }
}
