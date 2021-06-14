/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 14.06.2021 14:41
 */
public enum NMSVersion {
    // 1.10
    V1_10_2("1_10_R1","1.10.2-R0.1"),
    // 1.11
    V1_11_2("1_11_R1","1.11.2-R0.1"),
    // 1.16
    V1_16_5("1_16_R3","1.16.5-R0.1"),
    // 1.17
    V1_17("1_17_R1","1.17-R0.1"),
    ;

    private final String nmsVersionTag;
    private final String bukkitVersionTag;

    NMSVersion(String nmsVersionTag, String bukkitVersionTag){
        this.nmsVersionTag = nmsVersionTag;
        this.bukkitVersionTag = bukkitVersionTag;
    }

    public String getBukkitVersionTag() {
        return bukkitVersionTag;
    }

    public String getNmsVersionTag() {
        return nmsVersionTag;
    }
}
