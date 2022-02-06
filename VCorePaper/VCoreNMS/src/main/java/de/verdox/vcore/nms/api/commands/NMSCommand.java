/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.api.commands;

import de.verdox.vcore.nms.VCoreNMSModule;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.command.VCoreCommand;
import de.verdox.vcore.plugin.command.VCoreCommandCallback;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.impl.command.VCorePaperCommand;
import de.verdox.vcorepaper.impl.command.VCorePaperCommandCallback;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import de.verdox.vcorepaper.utils.BukkitPlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.util.RayTraceResult;

import java.util.Arrays;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 15:15
 */
public class NMSCommand extends VCorePaperCommand {
    private final VCoreNMSModule nmsModule;

    public NMSCommand(VCoreNMSModule nmsModule, VCorePaperPlugin plugin, String commandName) {
        super(plugin, commandName);
        this.nmsModule = nmsModule;

        addCommandCallback("world")
                .setExecutor(VCorePaperCommandCallback.CommandExecutorType.PLAYER)
                .addCommandPath("sendFakeDimension")
                .withPermission("nms.world.sendFakeDimension")
                .askFor("DimensionName", VCoreCommandCallback.CommandAskType.STRING, "&cDimension not found", "NORMAL", "NETHER", "THE_END")
                .commandCallback((commandSender, commandParameters) -> {
                    vCorePlugin.sync(() -> {
                        String env = commandParameters.getObject(0, String.class);
                        try {
                            nmsModule.getNmsManager().getNMSWorldHandler().sendFakeDimension((Player) commandSender, World.Environment.valueOf(env));
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDimension successfully changed!"));
                        } catch (IllegalArgumentException e) {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDimension not found"));
                        }
                    });
                });

        addCommandCallback("world")
                .setExecutor(VCoreCommandCallback.CommandExecutorType.PLAYER)
                .addCommandPath("sendFakeBiome")
                .withPermission("nms.world.sendFakeBiome")
                .askFor("Biome", VCoreCommandCallback.CommandAskType.STRING, "&cBiome not found", Arrays.stream(Biome.values()).map(Biome::name).toArray(String[]::new))
                .commandCallback((commandSender, commandParameters) -> {
                    vCorePlugin.sync(() -> {
                        Player player = (Player) commandSender;
                        Biome biome = commandParameters.getEnum(0, Biome.class);
                        if (biome == null) {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBiome not found"));
                            return;
                        }
                        nmsModule.getNmsManager().getNMSWorldHandler().sendFakeBiome(player, player.getChunk(), biome);
                    });
                });

        addCommandCallback("world")
                .setExecutor(VCoreCommandCallback.CommandExecutorType.PLAYER)
                .addCommandPath("resetView")
                .withPermission("nms.world.resetView")
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    vCorePlugin.sync(() -> {
                        nmsModule.getNmsManager().getNMSWorldHandler().resetView(player);
                    });
                });

        addCommandCallback("entity")
                .setExecutor(VCoreCommandCallback.CommandExecutorType.PLAYER)
                .addCommandPath("villager")
                .addCommandPath("changeProfession")
                .withPermission("nms.entity.changeProfession")
                .askFor("Profession", VCoreCommandCallback.CommandAskType.STRING, "&cProfession not found&7!", Arrays.stream(Villager.Profession.values()).map(Enum::name).toArray(String[]::new))
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;

                    Villager.Profession profession = commandParameters.getEnum(0, Villager.Profession.class);
                    if (profession == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cProfession not found&7!"));
                        return;
                    }

                    vCorePlugin.sync(() -> {
                        RayTraceResult rayTraceResult = BukkitPlayerUtil.rayTraceEntities(player, 15);
                        if (rayTraceResult == null || rayTraceResult.getHitEntity() == null || !rayTraceResult.getHitEntity().getType().equals(EntityType.VILLAGER)) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cLook at an villager&7!"));
                            return;
                        }
                        Villager villager = (Villager) rayTraceResult.getHitEntity();
                        villager.setProfession(profession);
                    });
                });

        addCommandCallback("entity")
                .setExecutor(VCoreCommandCallback.CommandExecutorType.PLAYER)
                .addCommandPath("villager")
                .addCommandPath("changeType")
                .withPermission("nms.entity.changeType")
                .askFor("Type", VCoreCommandCallback.CommandAskType.STRING, "&cType not found&7!", Arrays.stream(Villager.Type.values()).map(Enum::name).toArray(String[]::new))
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;

                    Villager.Type type = commandParameters.getEnum(0, Villager.Type.class);
                    if (type == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cType not found&7!"));
                        return;
                    }
                    vCorePlugin.sync(() -> {
                        RayTraceResult rayTraceResult = BukkitPlayerUtil.rayTraceEntities(player, 15);
                        if (rayTraceResult == null || rayTraceResult.getHitEntity() == null || !rayTraceResult.getHitEntity().getType().equals(EntityType.VILLAGER)) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cLook at an villager&7!"));
                            return;
                        }
                        Villager villager = (Villager) rayTraceResult.getHitEntity();
                        villager.setVillagerType(type);
                    });
                });
    }
}
