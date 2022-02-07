/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.workernpc;


import de.verdox.vcore.nbt.api.*;
import de.verdox.vcore.gui.book.DialogBuilder;
import de.verdox.vcore.nbt.entities.CustomEntityManager;
import de.verdox.vcore.nbt.entities.VCoreEntity;
import de.verdox.vcore.workernpc.events.PlayerPreOpenDialogEvent;
import de.verdox.vcorepaper.VCorePaper;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
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

    private VCoreWorkerNPCModule workerNPCModule;

    public WorkerNPC(@NotNull Entity entity, @NotNull CustomEntityManager customEntityManager) {
        super(entity, customEntityManager);
    }

    void setModule(VCoreWorkerNPCModule workerNPCModule) {
        this.workerNPCModule = workerNPCModule;
    }

    public boolean verify() {
        Objects.requireNonNull(workerNPCModule, "WorkerNPC not initialized via Module");
        return toNBTHolder().getPersistentDataContainer().hasKey(KEY_COMPOUND);
    }

    void initialize() {
        setName("§7Villager");
        setText("§fIch grüße dich Reisender!");
        ((Villager) getDataHolder()).setAI(false);
        ((Villager) getDataHolder()).setCollidable(false);
        getDataHolder().setInvulnerable(true);
        getDataHolder().setPersistent(true);
    }

    public void openDialog(@NotNull Player player) {
        Objects.requireNonNull(workerNPCModule, "WorkerNPC not initialized via Module");
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
        Objects.requireNonNull(workerNPCModule, "WorkerNPC not initialized via Module");
        String id = workerNPCModule.getProfessionRegistry().getID(type);
        if (id == null)
            throw new NullPointerException(type + " not yet registered to ProfessionRegistry");
        getMainCompound().getOrCreateCompound(KEY_PROFESSIONS).getOrCreateCompound(id);
        NPCProfession npcProfession = instantiateProfession(id, type);
        npcProfession.onProfessionAdd(this);
        return type.cast(npcProfession);
    }

    public boolean deleteProfession(Class<? extends NPCProfession> type) {
        Objects.requireNonNull(workerNPCModule, "WorkerNPC not initialized via Module");
        String id = workerNPCModule.getProfessionRegistry().getID(type);
        if (id == null || !hasProfession(type))
            return false;
        getMainCompound().getOrCreateCompound(KEY_PROFESSIONS).removeKey(id);
        return true;
    }

    public boolean hasProfession(Class<? extends NPCProfession> type) {
        Objects.requireNonNull(workerNPCModule, "WorkerNPC not initialized via Module");
        String id = workerNPCModule.getProfessionRegistry().getID(type);
        return getMainCompound().getOrCreateCompound(KEY_PROFESSIONS).hasKey(id);
    }

    public <T extends NPCProfession> T getProfession(Class<? extends T> type) {
        Objects.requireNonNull(workerNPCModule, "WorkerNPC not initialized via Module");
        String id = workerNPCModule.getProfessionRegistry().getID(type);
        if (id == null || !hasProfession(type))
            return null;
        return type.cast(instantiateProfession(id, type));
    }

    public List<String> getJobIdentifier() {
        Objects.requireNonNull(workerNPCModule, "WorkerNPC not initialized via Module");
        if (!getMainCompound().hasKey(NPC_NAME))
            return List.of("");
        return new ArrayList<>(getMainCompound().getOrCreateCompound(KEY_PROFESSIONS).getKeys());
    }

    @Nullable
    public String getName() {
        Objects.requireNonNull(workerNPCModule, "WorkerNPC not initialized via Module");
        if (!getMainCompound().hasKey(NPC_NAME))
            return null;
        return getMainCompound().getString(NPC_NAME);
    }

    public void setName(@NotNull String name) {
        Objects.requireNonNull(workerNPCModule, "WorkerNPC not initialized via Module");
        getMainCompound().setString(NPC_NAME, name);
        getDataHolder().setCustomNameVisible(true);
        getDataHolder().setCustomName(ChatColor.translateAlternateColorCodes('&', name));
    }

    @Nullable
    public String getText() {
        Objects.requireNonNull(workerNPCModule, "WorkerNPC not initialized via Module");
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
            Class<? extends NPCProfession> npcProfessionClass = workerNPCModule.getProfessionRegistry().getProfessionClass(id);
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
        Objects.requireNonNull(workerNPCModule, "WorkerNPC not initialized via Module");
        NBTCompound nbtCompound = getMainCompound().getOrCreateCompound(KEY_PROFESSIONS).getOrCreateCompound(id);
        try {
            return type.getConstructor(WorkerNPC.class, NBTCompound.class, VCoreWorkerNPCModule.class).newInstance(this, nbtCompound,workerNPCModule);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}
