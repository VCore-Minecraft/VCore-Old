/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.bukkitplayerhandler.playerdata;

import de.verdox.vcore.synchronization.pipeline.annotations.*;
import de.verdox.vcore.synchronization.pipeline.datatypes.PlayerData;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.bukkitplayerhandler.BukkitPlayerHandler;
import de.verdox.vcorepaper.bukkitplayerhandler.event.PlayerInventoryRestoreEvent;
import de.verdox.vcorepaper.bukkitplayerhandler.event.PlayerInventorySaveEvent;
import de.verdox.vcorepaper.bukkitplayerhandler.model.SerializableJsonInventory;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 00:27
 */
@DataStorageIdentifier(identifier = "BukkitPlayerHandlerPlayerData")
@RequiredSubsystemInfo(parentSubSystem = BukkitPlayerHandler.class)
@VCoreDataContext(preloadStrategy = PreloadStrategy.LOAD_BEFORE, dataContext = DataContext.GLOBAL, cleanOnNoUse = false, time = 30)
public class PlayerHandlerData extends PlayerData {

    @VCorePersistentData
    private Map<String, Map<String, Object>> inventoryCache = new ConcurrentHashMap<>();

    @VCorePersistentData
    private long firstLogin = 0L;

    @VCorePersistentData
    private long playTime = 0L;

    @VCorePersistentData
    private long lastSave = 0L;

    @VCorePersistentData
    public boolean restoreVanillaInventory = true;

    @VCorePersistentData
    private String activeInventoryID = null;
    private final PreloadStrategy loadOnNeed = PreloadStrategy.LOAD_ON_NEED;

    public PlayerHandlerData(VCorePlugin<?,?> plugin, UUID playerUUID) {
        super(plugin, playerUUID);
    }

    @Override
    public void onDisconnect(UUID playerUUID) {
        updatePlayTime();
        saveInventory();
    }

    @Override
    public void onConnect(UUID playerUUID) {
        lastSave = System.currentTimeMillis();
        updatePlayTime();
    }

    @Override
    public void onSync(Map<String, Object> dataBeforeSync) {
        if(!dataBeforeSync.containsKey("activeInventoryID"))
            return;
        String invIdBeforeSync = (String) dataBeforeSync.get("activeInventoryID");
        if(invIdBeforeSync.equals(activeInventoryID))
            return;
        Player player = Bukkit.getPlayer(getObjectUUID());
        if(player == null)
            return;
        saveInventory(() -> player, invIdBeforeSync);
        restoreInventory(activeInventoryID,() -> player);
        VCorePaper.getInstance().consoleMessage("&eInventory &6"+activeInventoryID+" &eof player &b"+getObjectUUID()+" &erestored due to a &6sync&7!&b"+System.currentTimeMillis(), true);
    }

    @Override
    public void onCreate() {

    }

    public void createInventory(String inventoryName){
        if(hasInventory(inventoryName))
            return;
        Map<String, Object> invData = new SerializableJsonInventory(inventoryName, GameMode.SURVIVAL,new ItemStack[0],new ItemStack[0], new ItemStack[0], null, 20, 20, 0, 0, new HashSet<>()).getData();
        inventoryCache.put(inventoryName, invData);
    }

    public void saveInventory(){
        Player player = Bukkit.getPlayer(getObjectUUID());
        if(player == null)
            return;
        saveInventory(() -> player);
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
        ItemStack[] enderChest = player.getEnderChest().getStorageContents().clone();

        ItemStack offHand = player.getInventory().getItemInOffHand().clone();

        PlayerInventorySaveEvent playerInventorySaveEvent = new PlayerInventorySaveEvent(player,inventoryID);
        Bukkit.getPluginManager().callEvent(playerInventorySaveEvent);

        SerializableJsonInventory serializableInventory = new SerializableJsonInventory(playerInventorySaveEvent.getInventoryID(),player.getGameMode(),storageContents,armorContents, enderChest, offHand, player.getHealth(), player.getFoodLevel(), player.getLevel(), player.getTotalExperience(), new HashSet<>(player.getActivePotionEffects()));

        inventoryCache.put(inventoryID,serializableInventory.getData());
        VCorePaper.getInstance().consoleMessage("&eInventory &6"+inventoryID+" &eof player &b"+getObjectUUID()+" &esaved&7! &b"+System.currentTimeMillis(), true);
        VCorePaper.getInstance().consoleMessage(Arrays.toString(storageContents),2,true);
        VCorePaper.getInstance().consoleMessage(Arrays.toString(armorContents),2,true);
        VCorePaper.getInstance().consoleMessage(Arrays.toString(enderChest),2,true);
    }

    public void restoreInventory(@Nonnull Supplier<Player> supplier){
        if(this.activeInventoryID == null)
            restoreInventory("vanilla", supplier);
        else
            restoreInventory(activeInventoryID, supplier);
    }

    public boolean hasInventory(@Nonnull String inventoryID){
        return inventoryCache.containsKey(inventoryID);
    }

    public void restoreInventory(@Nonnull String inventoryID, @Nonnull Supplier<Player> supplier){
        Player player = supplier.get();
        if(player == null)
            return;
        if(!inventoryCache.containsKey(inventoryID)) {
            getPlugin().consoleMessage("&cInventory &e"+activeInventoryID+" &cunknown &b"+getObjectUUID(),true);
            return;
        }

        PlayerInventoryRestoreEvent playerInventoryRestoreEvent = new PlayerInventoryRestoreEvent(player,this.activeInventoryID);
        Bukkit.getPluginManager().callEvent(playerInventoryRestoreEvent);

        this.activeInventoryID = playerInventoryRestoreEvent.getInventoryID();
        if(!inventoryCache.containsKey(activeInventoryID)) {
            getPlugin().consoleMessage("&cInventory &e"+activeInventoryID+" &cunknown &b"+getObjectUUID(),true);
            return;
        }
        SerializableJsonInventory serializableInventory = new SerializableJsonInventory(inventoryCache.get(inventoryID));
        serializableInventory.restoreInventory(player, null);

        ItemStack[] storageContents = player.getInventory().getStorageContents().clone();
        ItemStack[] armorContents = player.getInventory().getArmorContents().clone();
        ItemStack[] enderChest = player.getEnderChest().getStorageContents().clone();

        VCorePaper.getInstance().consoleMessage("&eInventory &6"+inventoryID+" &eof player &b"+getObjectUUID()+" &erestored&7! &b"+System.currentTimeMillis(), true);
        VCorePaper.getInstance().consoleMessage(Arrays.toString(storageContents),2,true);
        VCorePaper.getInstance().consoleMessage(Arrays.toString(armorContents),2,true);
        VCorePaper.getInstance().consoleMessage(Arrays.toString(enderChest),2,true);
    }

    @Override
    public void onLoad() {
        if(firstLogin == 0)
            firstLogin = System.currentTimeMillis();
    }

    public void resetPlayTime(){
        playTime = 0;
        lastSave = System.currentTimeMillis();
        save(true);
    }

    @Override
    public void onCleanUp() {
        Player player = Bukkit.getPlayer(getObjectUUID());
        if(player != null)
            saveInventory(() -> player);
        else
            getPlugin().consoleMessage("&cCould not save Inventory &7-> &ePlayer already offline",false);
        updatePlayTime();
    }

    private void updatePlayTime(){
        long plus = (System.currentTimeMillis() - lastSave);
        lastSave = System.currentTimeMillis();
        playTime += plus;
    }

    public long getPlayTimeMillis(){
        return playTime + (System.currentTimeMillis() - lastSave);
    }

    public int getPlayTimeSeconds(){
        return (int) TimeUnit.MILLISECONDS.toSeconds(getPlayTimeMillis());
    }

    public void setActiveInventoryID(@Nonnull String activeInventoryID) {
        this.activeInventoryID = activeInventoryID;
    }
}
