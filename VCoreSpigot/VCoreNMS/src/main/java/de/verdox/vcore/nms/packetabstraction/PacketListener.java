/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.packetabstraction;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import de.verdox.vcorepaper.VCorePaper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 17.06.2021 11:34
 */
public abstract class PacketListener {

    private final Map<UUID, PacketInstruction> packetInstructionMap = new ConcurrentHashMap<>();
    private boolean applyGlobal;

    public PacketListener(ListenerPriority listenerPriority, boolean applyGlobal, PacketType... packetTypes) {
        this.applyGlobal = applyGlobal;


        PacketAdapter adapter = new PacketAdapter(VCorePaper.getInstance(), listenerPriority, packetTypes) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!applyGlobal) {
                    if (!packetInstructionMap.containsKey(event.getPlayer().getUniqueId()))
                        return;
                }
                onSend(event, packetInstructionMap.get(event.getPlayer().getUniqueId()));
                packetInstructionMap.remove(event.getPlayer().getUniqueId());
            }

            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (!applyGlobal) {
                    if (!packetInstructionMap.containsKey(event.getPlayer().getUniqueId()))
                        return;
                }
                onReceive(event, packetInstructionMap.get(event.getPlayer().getUniqueId()));
                packetInstructionMap.remove(event.getPlayer().getUniqueId());
            }
        };

        VCorePaper.getInstance().getProtocolManager().addPacketListener(adapter);
    }

    public abstract void onSend(PacketEvent event, PacketInstruction packetInstruction);

    public abstract void onReceive(PacketEvent event, PacketInstruction packetInstruction);

    public void addPlayerInstruction(UUID playerUUID, Object... dataToSend) {
        packetInstructionMap.put(playerUUID, new PacketInstruction(playerUUID, dataToSend));
    }

    public void removePlayerInstruction(UUID playerUUID) {
        packetInstructionMap.remove(playerUUID);
    }

    public final boolean isApplyGlobal() {
        return applyGlobal;
    }

    public final void setApplyGlobal(boolean applyGlobal) {
        this.applyGlobal = applyGlobal;
    }

    public static class PacketInstruction {
        private final UUID playerUUID;
        private final Object[] instructionData;

        public PacketInstruction(@NotNull UUID playerUUID, @NotNull Object... instructionData) {
            this.playerUUID = playerUUID;
            this.instructionData = instructionData;
        }

        public UUID getPlayerUUID() {
            return playerUUID;
        }

        public <S> boolean isTypeOf(int index, Class<? extends S> type) {
            if (index < 0)
                return false;
            else if (index >= instructionData.length)
                return false;
            return instructionData[index].getClass().equals(type);
        }

        public <S> S getData(int index, Class<? extends S> type) {
            if (!isTypeOf(index, type))
                throw new NoSuchElementException("No element with type " + type + " was found at index " + index);
            return type.cast(instructionData[index]);
        }
    }
}
