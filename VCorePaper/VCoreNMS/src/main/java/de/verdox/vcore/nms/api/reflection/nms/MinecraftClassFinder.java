/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.api.reflection.nms;

import de.verdox.vcore.nms.NMSVersion;
import de.verdox.vcore.nms.api.reflection.java.ClassReflection;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import reactor.util.annotation.Nullable;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 23:59
 */
public class MinecraftClassFinder {
    private static final NMSVersion nmsVersion = NMSVersion.findNMSVersion(Bukkit.getServer().getBukkitVersion());

    public static ClassReflection.ReferenceClass findMinecraftClass(@NotNull MinecraftPackage minecraftPackage, @Nullable String classPackage, @NotNull String className) {
        String classPath;
        if (classPackage == null || classPackage.isEmpty())
            classPath = minecraftPackage.getPackageName() + ".v" + nmsVersion.getNmsVersionTag() + "." + className;
        else
            classPath = minecraftPackage.getPackageName() + ".v" + nmsVersion.getNmsVersionTag() + "." + classPackage + "." + className;
        ClassReflection.ReferenceClass foundClass = ClassReflection.findClass(classPath);
        if (foundClass == null)
            throw new RuntimeException("Could not find class: " + classPath);
        return foundClass;
    }

    public enum MinecraftPackage {
        CraftBukkit("org.bukkit.craftbukkit"),
        NMS("net.minecraft.server"),
        NM("net.minecraft"),
        ;
        private final String packageName;

        MinecraftPackage(String packageName) {
            this.packageName = packageName;
        }

        public String getPackageName() {
            return packageName;
        }
    }

}
