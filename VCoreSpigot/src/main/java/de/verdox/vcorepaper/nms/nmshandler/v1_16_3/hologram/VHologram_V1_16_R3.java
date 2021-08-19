/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.nmshandler.v1_16_3.hologram;

import de.verdox.vcorepaper.nms.nmshandler.api.hologram.HologramPacketManager;
import de.verdox.vcorepaper.nms.nmshandler.api.hologram.VHologram;
import de.verdox.vcorepaper.nms.nmshandler.api.hologram.lines.HologramLine;
import de.verdox.vcorepaper.nms.nmshandler.api.hologram.lines.ItemLine;
import de.verdox.vcorepaper.nms.nmshandler.api.hologram.lines.TextLine;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.06.2021 02:49
 */
public class VHologram_V1_16_R3 implements VHologram {

    private final Map<Integer, HologramLine> lines = new ConcurrentHashMap<>();

    @Override
    public TextLine setTextLine(String line) {
        return null;
    }

    @Override
    public ItemLine setItemLine(ItemStack stack) {
        return null;
    }

    @Override
    public HologramLine getLine(int index) {
        return null;
    }

    @Override
    public void removeLine(int index) {

    }

    @Override
    public void clearLines() {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void teleport(Location location) {

    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public HologramPacketManager getHologramPacketManager() {
        return null;
    }

    @Override
    public long getCreationTimeStamp() {
        return 0;
    }

    @Override
    public void delete() {

    }

    public Map<Integer, HologramLine> getLines() {
        return lines;
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    static class HologramPacketManagerImpl implements HologramPacketManager {

        private final VHologram_V1_16_R3 vHologram;
        private boolean global;

        HologramPacketManagerImpl(VHologram_V1_16_R3 vHologram) {
            this.vHologram = vHologram;
        }

        @Override
        public VHologram getHologram() {
            return vHologram;
        }

        @Override
        public boolean isGlobal() {
            return global;
        }

        @Override
        public void setGlobal(boolean value) {
            this.global = value;
        }

        @Override
        public void showTo(Player player) {

            vHologram.getLines().keySet().stream().sorted().forEach(integer -> {


            });

            PacketPlayOutSpawnEntity packetPlayOutSpawnEntity = new PacketPlayOutSpawnEntity();
        }

        @Override
        public void hideFrom(Player player) {

        }

        @Override
        public boolean isVisibleTo(Player player) {
            return false;
        }
    }

    static abstract class HologramLineImpl implements HologramLine {

        protected final VHologram vHologram;
        protected Location location;

        HologramLineImpl(VHologram vHologram, Location location) {
            this.vHologram = vHologram;
            this.location = location;
        }

        @Override
        public VHologram getHologram() {
            return vHologram;
        }

        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        protected abstract Entity getLineEntity();
    }

    static class TextLineImpl extends HologramLineImpl implements TextLine {
        private final EntityArmorStand entityArmorStand;
        private String text;

        TextLineImpl(VHologram vHologram, Location location) {
            super(vHologram, location);
            CraftWorld craftWorld = (CraftWorld) location.getWorld();
            entityArmorStand = new EntityArmorStand(craftWorld.getHandle(), location.getX(), location.getY(), location.getZ());
            entityArmorStand.setSmall(true);
            entityArmorStand.setInvisible(true);
            entityArmorStand.setNoGravity(true);
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void setText(String text) {
            entityArmorStand.setCustomNameVisible(text != null);
            if (text != null)
                entityArmorStand.setCustomName(new ChatComponentText(text));
            this.text = text;
        }

        @Override
        public boolean isEmpty() {
            return text == null || text.isEmpty();
        }

        @Override
        protected Entity getLineEntity() {
            return entityArmorStand;
        }
    }

    static class ItemLineImpl extends HologramLineImpl implements ItemLine {
        private final EntityItem entityItem;
        private ItemStack stack;

        ItemLineImpl(VHologram vHologram, Location location) {
            super(vHologram, location);
            CraftWorld craftWorld = (CraftWorld) location.getWorld();
            entityItem = new EntityItem(craftWorld.getHandle(), location.getX(), location.getY(), location.getZ());
            entityItem.setNoGravity(true);
            entityItem.setOwner(UUID.randomUUID());
        }

        @Override
        public ItemStack getItemStack() {
            return stack;
        }

        @Override
        public void setItemStack(ItemStack stack) {
            if (stack != null)
                entityItem.setItemStack(net.minecraft.server.v1_16_R3.ItemStack.fromBukkitCopy(stack));
            this.stack = stack;
        }

        @Override
        public boolean isEmpty() {
            return stack == null || stack.getType().isEmpty();
        }

        @Override
        protected Entity getLineEntity() {
            return entityItem;
        }
    }
}
