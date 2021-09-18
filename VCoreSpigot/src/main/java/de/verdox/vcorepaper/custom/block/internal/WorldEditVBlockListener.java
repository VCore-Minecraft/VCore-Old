/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.block.internal;

import com.sk89q.worldedit.WorldEdit;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 17.09.2021 19:13
 */
public class WorldEditVBlockListener extends VCoreListener.VCoreBukkitListener {
    public WorldEditVBlockListener(VCorePlugin.Minecraft plugin) {
        super(plugin);
        WorldEdit.getInstance().getEventBus().register(this);
        plugin.consoleMessage("&eVBlocks hooking into &bWorldEdit", false);
    }
}
