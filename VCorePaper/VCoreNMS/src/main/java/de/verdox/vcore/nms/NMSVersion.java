/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 14.06.2021 14:41
 */
public enum NMSVersion {
    // 1.10
    V1_10_2("1_10_R1", "1.10.2-R0.1-SNAPSHOT"),
    // 1.11
    V1_11_2("1_11_R1", "1.11.2-R0.1-SNAPSHOT"),
    // 1.16
    V1_16_3("1_16_R3", "1.16.3-R0.1-SNAPSHOT"),
    // 1.17
    V1_17("1_17_R1", "1.17-R0.1-SNAPSHOT"),
    // 1.17.1
    V1_17_1("1_17_1_R1", "1.17.1-R0.1-SNAPSHOT"),
    ;

    private final String nmsVersionTag;
    private final String bukkitVersionTag;

    NMSVersion(String nmsVersionTag, String bukkitVersionTag) {
        this.nmsVersionTag = nmsVersionTag;
        this.bukkitVersionTag = bukkitVersionTag;
    }

    public static NMSVersion findNMSVersion(String bukkitVersionTag) {
        for (NMSVersion value : NMSVersion.values()) {
            if (value.bukkitVersionTag.equals(bukkitVersionTag))
                return value;
        }
        return null;
    }

    @Override
    public String toString() {
        return nmsVersionTag;
    }

    public String getBukkitVersionTag() {
        return bukkitVersionTag;
    }

    public String getNmsVersionTag() {
        return nmsVersionTag;
    }
}
