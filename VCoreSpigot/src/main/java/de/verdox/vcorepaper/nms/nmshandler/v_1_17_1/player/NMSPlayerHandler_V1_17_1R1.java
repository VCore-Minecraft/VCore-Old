/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.nmshandler.v_1_17_1.player;

import de.verdox.vcore.nms.nmshandler.api.player.NMSPlayerHandler;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.AdvancementDataPlayer;
import org.bukkit.advancement.Advancement;
import org.bukkit.craftbukkit.v1_17_R1.advancement.CraftAdvancement;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 16.09.2021 18:26
 */
public class NMSPlayerHandler_V1_17_1R1 implements NMSPlayerHandler {
    @Override
    public boolean silentlyGrantAdvancementProgress(@NotNull Player player, @NotNull Advancement advancement, @NotNull String criterionName) {

        CraftAdvancement craftAdvancement = (CraftAdvancement) advancement;
        net.minecraft.advancements.Advancement nmsAdvancement = craftAdvancement.getHandle();
        AdvancementDataPlayer advancementDataPlayer = ((CraftPlayer) player).getHandle().getAdvancementData();

        boolean flag = false;
        AdvancementProgress advancementprogress = advancementDataPlayer.getProgress(nmsAdvancement);
        boolean flag1 = advancementprogress.isDone();
        try {
            if (advancementprogress.a(criterionName)) {


                Method dMethod = AdvancementDataPlayer.class.getDeclaredMethod("d", net.minecraft.advancements.Advancement.class);
                // advancementDataPlayer.d(advancement);
                dMethod.setAccessible(true);
                dMethod.invoke(advancementDataPlayer, nmsAdvancement);
                Field kField = AdvancementDataPlayer.class.getDeclaredField("k");
                kField.setAccessible(true);
                Set<net.minecraft.advancements.Advancement> advancementSet = (Set<net.minecraft.advancements.Advancement>) kField.get(advancementDataPlayer);
                // advancementDataPlayer.k.add(advancement);
                advancementSet.add(nmsAdvancement);


                flag = true;
                if (!flag1 && advancementprogress.isDone())
                    nmsAdvancement.d().a(((CraftPlayer) player).getHandle());
            }

            if (advancementprogress.isDone()) {
                Method eMethod = AdvancementDataPlayer.class.getDeclaredMethod("e", net.minecraft.advancements.Advancement.class);
                eMethod.setAccessible(true);
                // advancementDataPlayer.e(advancement);
                eMethod.invoke(advancementDataPlayer, nmsAdvancement);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return flag;
    }
}
