/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.bukkitplayerhandler.model;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.collections.ListBsonReference;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.objects.StringBsonReference;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.primitives.numbers.DoubleBsonReference;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.primitives.numbers.IntegerBsonReference;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.custom.util.Serializer;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 00:19
 */
public class SerializableJsonInventory {

    private final Map<String, Object> data = new HashMap<>();

    public SerializableJsonInventory(String id, ItemStack[] storageContents, ItemStack[] armorContents, ItemStack[] enderChest, ItemStack offHand, double health, int foodLevel, float exp, Set<PotionEffect> potionEffects){
        new StringBsonReference(data, "id").setValue(id);
        new StringBsonReference(data, "armorContents").setValue(Serializer.itemStackArrayToBase64(armorContents));
        new StringBsonReference(data, "storageContents").setValue(Serializer.itemStackArrayToBase64(storageContents));
        new StringBsonReference(data, "enderChest").setValue(Serializer.itemStackArrayToBase64(enderChest));
        if(!offHand.getType().isEmpty())
            new StringBsonReference(data, "offHand").setValue(Serializer.itemStackArrayToBase64(new ItemStack[]{offHand}));
        new DoubleBsonReference(data, "health").setValue(health);
        new DoubleBsonReference(data, "exp").setValue((double) exp);
        new IntegerBsonReference(data, "food").setValue(foodLevel);
        List<String> serializedPotionEffects = potionEffects.stream().map(potionEffect -> VCoreUtil.getBukkitPlayerUtil().serializePotionEffect(potionEffect)).collect(Collectors.toList());
        new ListBsonReference<String>(data,"potionEffects").setValue(serializedPotionEffects);
    }

    public SerializableJsonInventory(Map<String, Object> data){
        this.data.putAll(data);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public boolean restoreInventory(@Nonnull Player player, @Nullable Runnable callback){
        try {
            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR,"&eLade Rüstung&7...");

            ItemStack [] armorContents = deSerializeArmorContents();
            if(armorContents == null){
                VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.CHAT,"&cKonnte Rüstung nicht wiederherstellen");
            }
            else{
                VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR,"&eRüstung wurde geladen");
                if(armorContents.length != 0)
                    player.getInventory().setArmorContents(armorContents);
            }

            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR,"&eLade Items&7...");
            ItemStack [] storageContents = deSerializeStorageContents();

            if(storageContents == null){
                VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.CHAT,"&cKonnte Items nicht wiederherstellen");
            }
            else{
                VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR,"&eItems wurden geladen");
                if(storageContents.length != 0)
                    player.getInventory().setStorageContents(storageContents);
            }

            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR,"&eLade EnderChest&7...");
            ItemStack[] enderChest = deSerializeEnderChest();
            if(enderChest == null){
                VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.CHAT,"&cKonnte EnderChest nicht wiederherstellen");
            }
            else{
                VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR,"&eEnderChest wurde geladen");
                if(enderChest.length != 0)
                    player.getEnderChest().setContents(enderChest);
            }

            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR,"&eLade Offhand&7...");
            ItemStack offHand = deSerializeOffHand();
            if(offHand != null){
                VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR,"&eOffhand wurden geladen");
                player.getInventory().setItemInOffHand(offHand);
            }

            player.setHealth(getHealth());
            player.setExp((float) getExp());
            player.setFoodLevel(getFoodLevel());
            List<String> serializedEffects = new ListBsonReference<String>(data,"potionEffects").getValue();
            if(!serializedEffects.isEmpty()){
                new ListBsonReference<String>(data,"potionEffects").getValue().stream()
                        .map(serializedEffect -> VCoreUtil.getBukkitPlayerUtil().deSerializePotionEffect(serializedEffect))
                        .forEach(potionEffect -> {
                            player.removePotionEffect(potionEffect.getType());
                            player.addPotionEffect(potionEffect);
                        });
            }
            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR,"&aSpielerdaten wurden geladen");
            if(callback != null)
                callback.run();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ItemStack[] deSerializeStorageContents() throws IOException {
        return Serializer.itemStackArrayFromBase64(new StringBsonReference(data, "storageContents").orElse(null));
    }

    public ItemStack[] deSerializeArmorContents() throws IOException {
        return Serializer.itemStackArrayFromBase64(new StringBsonReference(data, "armorContents").orElse(null));
    }

    public ItemStack[] deSerializeEnderChest() throws IOException {
        return Serializer.itemStackArrayFromBase64(new StringBsonReference(data, "enderChest").orElse(null));
    }

    public ItemStack deSerializeOffHand() throws IOException {
        ItemStack[] offHandArray = Serializer.itemStackArrayFromBase64(new StringBsonReference(data, "offHand").orElse(null));
        if(offHandArray == null)
            return null;
        return offHandArray[0];
    }

    public double getHealth(){
        return new DoubleBsonReference(data, "health").orElse(20d);
    }

    public double getExp(){
        return new DoubleBsonReference(data, "exp").orElse(0d);
    }

    public int getFoodLevel(){
        return new IntegerBsonReference(data, "food").orElse(20);
    }

    public String getID(){
        return new StringBsonReference(data, "storageContents").getValue();
    }
}
