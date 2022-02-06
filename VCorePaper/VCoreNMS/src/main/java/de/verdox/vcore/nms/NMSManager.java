/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms;

import de.verdox.vcore.nms.api.entity.NMSEntityHandler;
import de.verdox.vcore.nms.api.player.NMSPlayerHandler;
import de.verdox.vcore.nms.api.reflection.java.ClassReflection;
import de.verdox.vcore.nms.api.server.NMSServerHandler;
import de.verdox.vcore.nms.api.world.NMSWorldHandler;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.util.Locale;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 14.06.2021 14:39
 */
public class NMSManager {
    private final VCorePaperPlugin vCorePlugin;
    private final de.verdox.vcore.nms.NMSVersion nmsVersion;

    public NMSManager(VCorePaperPlugin vCorePlugin) {
        this.vCorePlugin = vCorePlugin;
        vCorePlugin.consoleMessage("&eStarting NMS-Manager", false);
        String bukkitVersion = Bukkit.getBukkitVersion();
        nmsVersion = de.verdox.vcore.nms.NMSVersion.findNMSVersion(bukkitVersion);
        if (nmsVersion != null)
            vCorePlugin.consoleMessage("&eFound NMS-Version&7: &b" + nmsVersion.getNmsVersionTag(), false);
        else
            vCorePlugin.consoleMessage("&cCould not find proper NMS Version for Version&7: &b" + bukkitVersion + " &8| &b" + Bukkit.getVersion(), false);
    }

    public NMSVersion getNmsVersion() {
        return nmsVersion;
    }

    public NMSEntityHandler getNMSEntityHandler() {
        return findRightHandlerClass(nmsVersion, NMSEntityHandler.class, "");
    }

    public NMSPlayerHandler getNMSPlayerHandler() {
        return findRightHandlerClass(nmsVersion, NMSPlayerHandler.class, "");
    }

    public NMSServerHandler getNMSServerHandler() {
        return findRightHandlerClass(nmsVersion, NMSServerHandler.class, vCorePlugin);
    }

    public NMSWorldHandler getNMSWorldHandler() {
        return findRightHandlerClass(nmsVersion, NMSWorldHandler.class, vCorePlugin);
    }

    private <T extends NMSHandler> T findRightHandlerClass(NMSVersion nmsVersion, Class<T> nmsHandlerClass, Object... params) {
        vCorePlugin.consoleMessage("&eSearching for NMS Implementation for: &a" + nmsHandlerClass.getSimpleName() + " &8[&b" + nmsVersion + "&8]", true);
        String packageName = "de.verdox.vcore.nms.impl." + nmsVersion.getNmsVersionTag().replace("R", "").toLowerCase(Locale.ROOT) + ".impl." + nmsHandlerClass.getPackageName();
        vCorePlugin.consoleMessage("&eSearching in&7: &b" + packageName, 1, true);
        Reflections reflections = new Reflections(packageName);
        Class<? extends NMSHandler> foundClass = reflections.getSubTypesOf(nmsHandlerClass).stream().findAny().orElse(null);
        ClassReflection.ReferenceClass referenceClass = ClassReflection.toReferenceClass(foundClass);
        return (T) referenceClass.instantiate(params);
    }
}