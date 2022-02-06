/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.workernpc.professions;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.verdox.vcore.gui.book.DialogBuilder;
import de.verdox.vcore.workernpc.NPCProfession;
import de.verdox.vcore.workernpc.WorkerNPC;
import de.verdox.vcore.workernpc.VCoreWorkerNPCModule;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.09.2021 21:45
 */
public class VanillaTrader extends NPCProfession {
    public static final String TRADING = "trading";
    private final int offerSize;

    public VanillaTrader(@NotNull WorkerNPC workerNPC, @NotNull NBTCompound dataContainer,@NotNull VCoreWorkerNPCModule workerNPCModule) {
        super(workerNPC, dataContainer,workerNPCModule);
        if (!workerNPC.getDataHolder().getType().equals(EntityType.VILLAGER))
            throw new IllegalStateException("VanillaTrader Profession only fits onto Villager Type");
        offerSize = workerNPCModule.getVCoreNMSModule().getNmsManager().getNMSEntityHandler().getOffers((Villager) workerNPC.getDataHolder());
    }

    @Override
    public void addDialogContent(@NotNull DialogBuilder dialogBuilder, @NotNull Player openingPlayer) {
        if (isTradingEnabled() && offerSize > 0)
            dialogBuilder.addButton(">> Handeln", player -> workerNPCModule.getVCoreNMSModule().getNmsManager().getNMSEntityHandler().openTradingGUI((Villager) getWorkerNPC().getDataHolder(), player));
    }

    public int getOfferSize() {
        return offerSize;
    }

    @Override
    public void onProfessionAdd(@NotNull WorkerNPC workerNPC) {
        setTrading(true);
    }

    @Override
    public void onProfessionRemove(@NotNull WorkerNPC workerNPC) {

    }

    public void setTrading(boolean trading) {
        dataContainer.setBoolean(TRADING, trading);
    }

    public boolean isTradingEnabled() {
        if (!dataContainer.hasKey(TRADING))
            return false;
        return dataContainer.getBoolean(TRADING);
    }
}
