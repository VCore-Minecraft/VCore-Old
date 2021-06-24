/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.bukkitplayerhandler.playerdata;

import de.verdox.vcore.pipeline.annotations.*;
import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.manager.PlayerSessionManager;
import de.verdox.vcore.dataconnection.mongodb.annotation.MongoDBIdentifier;
import de.verdox.vcore.player.VCorePlayer;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.bukkitplayerhandler.BukkitPlayerHandler;
import de.verdox.vcorepaper.bukkitplayerhandler.model.SerializableJsonInventory;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 00:27
 */
@MongoDBIdentifier(identifier = "BukkitPlayerHandlerPlayerData")
@RequiredSubsystemInfo(parentSubSystem = BukkitPlayerHandler.class)
@VCoreDataContext(preloadStrategy = PreloadStrategy.LOAD_ON_NEED, dataContext = DataContext.GLOBAL)
public class PlayerHandlerData extends PlayerData {

    @VCorePersistentData
    private Map<String, SerializableJsonInventory> inventoryCache = new ConcurrentHashMap<>();

    @VCorePersistentData
    public boolean restoreVanillaInventory = true;

    @VCorePersistentData
    private String activeInventoryID = null;

    public PlayerHandlerData(PlayerSessionManager<?> playerSessionManager, UUID playerUUID) {
        super(playerSessionManager, playerUUID);
    }

    public void saveInventory(){
        VCorePlayer vCorePlayer = VCorePaper.getInstance().getVCorePlayerManager().getPlayer(getUUID());
        if(vCorePlayer == null)
            return;
        if(!vCorePlayer.isOnThisServer())
            return;
        saveInventory(vCorePlayer::toBukkitPlayer);
    }

    public void saveInventory(Supplier<Player> supplier){
        if(activeInventoryID == null)
            saveInventory(supplier,"vanilla");
        else
            saveInventory(supplier,activeInventoryID);
    }

    public void saveInventory(Supplier<Player> supplier, String inventoryID){
        Player player = supplier.get();
        if(player == null)
            return;
        ItemStack[] storageContents = player.getInventory().getStorageContents().clone();
        ItemStack[] armorContents = player.getInventory().getArmorContents().clone();
        SerializableJsonInventory serializableInventory = new SerializableJsonInventory(inventoryID,storageContents,armorContents);
        inventoryCache.put(inventoryID,serializableInventory);
        VCorePaper.getInstance().consoleMessage("&eInventory &6"+inventoryID+" &eof player &b"+getUUID()+" &esaved&7!", true);
    }

    public void restoreInventory(){
        if(this.activeInventoryID == null)
            restoreInventory("vanilla");
        else
            restoreInventory(activeInventoryID);
    }

    public void createInventory(String inventoryID){
        inventoryCache.put(inventoryID,new SerializableJsonInventory(inventoryID,new ItemStack[0],new ItemStack[0]));
    }

    public boolean hasInventory(String inventoryID){
        return inventoryCache.containsKey(inventoryID);
    }

    public void restoreInventory(String inventoryID){
        VCorePlayer vCorePlayer = VCorePaper.getInstance().getVCorePlayerManager().getPlayer(getUUID());
        if(vCorePlayer == null)
            return;
        if(!vCorePlayer.isOnThisServer())
            return;
        if(!inventoryCache.containsKey(inventoryID))
            return;
        this.activeInventoryID = inventoryID;
        SerializableJsonInventory serializableInventory = inventoryCache.get(inventoryID);
        try{
            ItemStack[] armorContents = serializableInventory.deSerializeArmorContents();
            ItemStack[] storageContents = serializableInventory.deSerializeStorageContents();
            if(armorContents != null)
                vCorePlayer.toBukkitPlayer().getInventory().setArmorContents(armorContents);
            if(storageContents != null)
                vCorePlayer.toBukkitPlayer().getInventory().setStorageContents(storageContents);
            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(vCorePlayer.toBukkitPlayer(), ChatMessageType.ACTION_BAR, "&eInventar wurde geladen");
            VCorePaper.getInstance().consoleMessage("&eInventory &6"+inventoryID+" &eof player &b"+getUUID()+" &erestored&7!", true);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onCleanUp() {
        saveInventory();
    }
}
