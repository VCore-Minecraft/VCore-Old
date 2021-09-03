/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.nmshandler.api.item;

import de.verdox.vcorepaper.nms.NMSVersion;
import de.verdox.vcorepaper.nms.nmshandler.v1_16_3.item.ItemHandler_V1_16_R3;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 02.09.2021 19:11
 */
public interface NMSItemHandler {

    static NMSItemHandler getRightHandler(NMSVersion nmsVersion) {
        if (nmsVersion.equals(NMSVersion.V1_16_5)) {
            return new ItemHandler_V1_16_R3();
        }
        throw new NotImplementedException("This Handler [" + NMSVersion.class.getName() + "] is not implemented for NMS version: " + nmsVersion.getNmsVersionTag());
    }

    void changeMaxStackSize(@NotNull ItemStack stack, @Positive int newStackSize);
}
