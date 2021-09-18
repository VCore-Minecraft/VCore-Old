/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.custompipelinedata.inventories;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.collections.ListBsonReference;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.objects.StringBsonReference;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.primitives.numbers.DoubleBsonReference;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.primitives.numbers.IntegerBsonReference;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.util.Serializer;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 00:19
 */
public class SerializablePlayerInventory extends SerializableInventory {

    public SerializablePlayerInventory(@NotNull String id, @NotNull GameMode gameMode, @NotNull ItemStack[] storageContents, @NotNull ItemStack[] armorContents, @NotNull ItemStack[] enderChest, @Nullable ItemStack offHand, double health, int foodLevel, int level, float exp, @NotNull Set<PotionEffect> potionEffects) {
        super(id, storageContents);
        saveArmorContents(armorContents);
        saveEnderChest(enderChest);
        saveOffHand(offHand);
        saveHealth(health);
        saveExp(exp);
        saveFoodLevel(foodLevel);
        saveLevel(level);
        saveGameMode(gameMode);
        List<String> serializedPotionEffects = potionEffects.stream().map(potionEffect -> VCoreUtil.BukkitUtil.getBukkitPlayerUtil().serializePotionEffect(potionEffect)).collect(Collectors.toList());
        new ListBsonReference<String>(data, "potionEffects").setValue(serializedPotionEffects);
    }

    public SerializablePlayerInventory(Map<String, Object> data) {
        super(data);
    }

    public SerializablePlayerInventory() {
        super(new ConcurrentHashMap<>());
    }

    public boolean restoreInventory(@NotNull Player player, @Nullable Runnable callback) {
        VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR, "&eLade Rüstung&7...");

        ItemStack[] armorContents = deSerializeArmorContents();
        if (armorContents == null) {
            VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.CHAT, "&cKonnte Rüstung nicht wiederherstellen");
        } else {
            VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR, "&eRüstung wurde geladen");
            player.getInventory().setArmorContents(armorContents);
        }

        VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR, "&eLade Items&7...");
        ItemStack[] storageContents = deSerializeStorageContents();

        if (storageContents == null) {
            VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.CHAT, "&cKonnte Items nicht wiederherstellen");
        } else {
            VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR, "&eItems wurden geladen");
            player.getInventory().setStorageContents(storageContents);
        }

        VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR, "&eLade EnderChest&7...");
        ItemStack[] enderChest = deSerializeEnderChest();
        if (enderChest == null) {
            VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.CHAT, "&cKonnte EnderChest nicht wiederherstellen");
        } else {
            VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR, "&eEnderChest wurde geladen");
            player.getEnderChest().setContents(enderChest);
        }

        VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR, "&eLade Offhand&7...");
        VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR, "&eOffhand wurden geladen");
        player.getInventory().setItemInOffHand(deSerializeOffHand());

        player.setHealth(getHealth());
        player.setTotalExperience((int) getExp());
        player.setFoodLevel(getFoodLevel());
        VCorePaper.getInstance().sync(() -> player.setGameMode(getGameMode()));
        int level = getLevel();
        if (level > 0)
            player.setLevel(level);
        List<String> serializedEffects = new ListBsonReference<String>(data, "potionEffects").getValue();
        if (!serializedEffects.isEmpty()) {
            new ListBsonReference<String>(data, "potionEffects").getValue().stream()
                    .map(serializedEffect -> VCoreUtil.BukkitUtil.getBukkitPlayerUtil().deSerializePotionEffect(serializedEffect))
                    .forEach(potionEffect -> {
                        VCorePaper.getInstance().sync(() -> {
                            player.removePotionEffect(potionEffect.getType());
                            player.addPotionEffect(potionEffect);
                        });
                    });
        } else {
            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                VCorePaper.getInstance().sync(() -> player.removePotionEffect(activePotionEffect.getType()));
            }
        }
        VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR, "&aSpielerdaten wurden geladen");
        if (callback != null)
            callback.run();
        return true;
    }

    public void saveArmorContents(ItemStack[] armorContents) {
        new StringBsonReference(data, "armorContents").setValue(Serializer.itemStackArrayToBase64(armorContents));
    }

    public void saveAdvancements() {

    }

    @NotNull
    public ItemStack[] deSerializeArmorContents() {
        try {
            ItemStack[] armorContents = Serializer.itemStackArrayFromBase64(new StringBsonReference(data, "armorContents").orElse(null));
            if (armorContents != null)
                return armorContents;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ItemStack[0];
    }

    public void saveEnderChest(@NotNull ItemStack[] enderChest) {
        new StringBsonReference(data, "enderChest").setValue(Serializer.itemStackArrayToBase64(enderChest));
    }

    @NotNull
    public ItemStack[] deSerializeEnderChest() {
        try {
            ItemStack[] enderChest = Serializer.itemStackArrayFromBase64(new StringBsonReference(data, "enderChest").orElse(null));
            if (enderChest != null)
                return enderChest;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ItemStack[0];
    }

    public void saveOffHand(@Nullable ItemStack offHand) {
        if (offHand != null && !offHand.getType().isEmpty())
            new StringBsonReference(data, "offHand").setValue(Serializer.itemStackArrayToBase64(new ItemStack[]{offHand}));
    }

    @NotNull
    public ItemStack deSerializeOffHand() {
        try {
            ItemStack[] offHandArray = Serializer.itemStackArrayFromBase64(new StringBsonReference(data, "offHand").orElse(null));
            if (offHandArray == null)
                return null;
            return offHandArray[0];
        } catch (IOException e) {
            e.printStackTrace();
            return new ItemStack(Material.AIR);
        }
    }

    public void saveGameMode(@NotNull GameMode gameMode) {
        new StringBsonReference(data, "gameMode").setValue(gameMode.name());
    }

    public GameMode getGameMode() {
        return GameMode.valueOf(new StringBsonReference(data, "gameMode").orElse(GameMode.SURVIVAL.name()));
    }

    public void saveLevel(int level) {
        new IntegerBsonReference(data, "level").setValue(level);
    }

    public int getLevel() {
        return new IntegerBsonReference(data, "level").orElse(0);
    }

    public void saveHealth(double health) {
        new DoubleBsonReference(data, "health").setValue(health);
    }

    public double getHealth() {
        return new DoubleBsonReference(data, "health").orElse(20d);
    }

    public void saveExp(double exp) {
        new DoubleBsonReference(data, "exp").setValue(exp);
    }

    public double getExp() {
        return new DoubleBsonReference(data, "exp").orElse(0d);
    }

    public void saveFoodLevel(int foodLevel) {
        new IntegerBsonReference(data, "food").setValue(foodLevel);
    }

    public int getFoodLevel() {
        return new IntegerBsonReference(data, "food").orElse(20);
    }

    @Override
    public String toString() {
        return "SerializablePlayerInventory{" +
                "id=" + getID() + "" +
                ", storageContents=" + Arrays.toString(deSerializeStorageContents()) + "" +
                ", armor=" + Arrays.toString(deSerializeArmorContents()) +
                ", enderChest=" + Arrays.toString(deSerializeEnderChest()) +
                ", offHand=" + deSerializeOffHand() +
                ", exp=" + getExp() +
                ", foodLevel=" + getFoodLevel() +
                ", level=" + getLevel() +
                ", Health=" + getHealth() +
                ", gameMode=" + getGameMode() +
                "}";
    }
}
