/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.workernpc;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.entities.CustomEntityManager;
import de.verdox.vcorepaper.custom.entities.VCoreEntity;
import de.verdox.vcorepaper.custom.gui.book.DialogBuilder;
import de.verdox.vcorepaper.custom.gui.book.event.PlayerPreOpenDialogEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.09.2021 21:21
 */
public class WorkerNPC extends VCoreEntity {
    public static final String KEY_COMPOUND = "talkingNPC";

    public static final String NPC_NAME = "name";
    public static final String TEXT = "text";
    public static final String KEY_PROFESSIONS = "professions";

    public WorkerNPC(@NotNull Entity entity, @NotNull CustomEntityManager customEntityManager) {
        super(entity, customEntityManager);
    }

    public static WorkerNPC spawnNPC(@NotNull Location location) {
        Villager villager = location.getWorld().spawn(location, Villager.class, CreatureSpawnEvent.SpawnReason.CUSTOM);
        WorkerNPC workerNPC = VCorePaper.getInstance().getCustomEntityManager().wrap(WorkerNPC.class, villager);
        workerNPC.initialize();
        return workerNPC;
    }

    public boolean verify() {
        return toNBTHolder().getPersistentDataContainer().hasKey(KEY_COMPOUND);
    }

    public void initialize() {
        setName("§7Villager");
        setText("§fIch grüße dich Reisender!");
        ((Villager) getDataHolder()).setAI(false);
        ((Villager) getDataHolder()).setCollidable(false);
        getDataHolder().setInvulnerable(true);
        getDataHolder().setPersistent(true);
    }

    public void openDialog(@NotNull Player player) {
        DialogBuilder dialogBuilder = new DialogBuilder(VCorePaper.getInstance(), player);
        if (getName() != null)
            dialogBuilder.addText(getName()).newLine();
        if (getText() != null)
            dialogBuilder.addText(getText()).newLine();
        //dialogBuilder.addButton(">> Gespräch beenden", player1 -> VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR, "&7Du wendest dich ab..."));
        //dialogBuilder.newLine();
        getAllContent(dialogBuilder, player).forEach((textComponent, consumer) -> dialogBuilder.addButton(textComponent.content(), consumer));
        dialogBuilder.newLine();

        PlayerPreOpenDialogEvent playerPreOpenDialogEvent = new PlayerPreOpenDialogEvent(player, this, dialogBuilder);
        Bukkit.getPluginManager().callEvent(playerPreOpenDialogEvent);
        if (!playerPreOpenDialogEvent.isCancelled())
            playerPreOpenDialogEvent.getDialogBuilder().openDialog();
    }

    public <T extends NPCProfession> T addProfession(@NotNull Class<? extends T> type) {
        String id = VCorePaper.getInstance().getCustomEntityManager().getProfessionRegistry().getID(type);
        if (id == null)
            throw new NullPointerException(type + " not yet registered to ProfessionRegistry");
        getMainCompound().getOrCreateCompound(KEY_PROFESSIONS).getOrCreateCompound(id);
        NPCProfession npcProfession = instantiateProfession(id, type);
        npcProfession.onProfessionAdd(this);
        return type.cast(npcProfession);
    }

    public boolean deleteProfession(Class<? extends NPCProfession> type) {
        String id = VCorePaper.getInstance().getCustomEntityManager().getProfessionRegistry().getID(type);
        if (id == null || !hasProfession(type))
            return false;
        getMainCompound().getOrCreateCompound(KEY_PROFESSIONS).removeKey(id);
        return true;
    }

    public boolean hasProfession(Class<? extends NPCProfession> type) {
        String id = VCorePaper.getInstance().getCustomEntityManager().getProfessionRegistry().getID(type);
        return getMainCompound().getOrCreateCompound(KEY_PROFESSIONS).hasKey(id);
    }

    public <T extends NPCProfession> T getProfession(Class<? extends T> type) {
        String id = VCorePaper.getInstance().getCustomEntityManager().getProfessionRegistry().getID(type);
        if (id == null || !hasProfession(type))
            return null;
        return type.cast(instantiateProfession(id, type));
    }

    public List<String> getJobIdentifier() {
        if (!getMainCompound().hasKey(NPC_NAME))
            return List.of("");
        return new ArrayList<>(getMainCompound().getOrCreateCompound(KEY_PROFESSIONS).getKeys());
    }

    @Nullable
    public String getName() {
        if (!getMainCompound().hasKey(NPC_NAME))
            return null;
        return getMainCompound().getString(NPC_NAME);
    }

    public void setName(@NotNull String name) {
        getMainCompound().setString(NPC_NAME, name);
        getDataHolder().setCustomNameVisible(true);
        getDataHolder().setCustomName(ChatColor.translateAlternateColorCodes('&', name));
    }

    @Nullable
    public String getText() {
        if (!getMainCompound().hasKey(TEXT))
            return null;
        return getMainCompound().getString(TEXT);
    }

    public void setText(@NotNull String text) {
        getMainCompound().setString(TEXT, text);
    }

    private NBTCompound getMainCompound() {
        return toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND);
    }

    public List<NPCProfession> getProfessions() {
        List<NPCProfession> professions = new ArrayList<>();
        for (String id : getJobIdentifier()) {
            Class<? extends NPCProfession> npcProfessionClass = VCorePaper.getInstance().getCustomEntityManager().getProfessionRegistry().getProfessionClass(id);
            if (npcProfessionClass == null)
                continue;
            NPCProfession npcProfession = instantiateProfession(id, npcProfessionClass);
            professions.add(npcProfession);
        }
        return professions;
    }

    private Map<TextComponent, Consumer<Player>> getAllContent(@NotNull DialogBuilder dialogBuilder, @NotNull Player player) {
        Map<TextComponent, Consumer<Player>> buttons = new LinkedHashMap<>();
        for (NPCProfession profession : getProfessions())
            profession.addDialogContent(dialogBuilder, player);
        return buttons;
    }

    @NotNull
    private NPCProfession instantiateProfession(@NotNull String id, @NotNull Class<? extends NPCProfession> type) {
        NBTCompound nbtCompound = getMainCompound().getOrCreateCompound(KEY_PROFESSIONS).getOrCreateCompound(id);
        try {
            return type.getConstructor(WorkerNPC.class, NBTCompound.class).newInstance(this, nbtCompound);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}
