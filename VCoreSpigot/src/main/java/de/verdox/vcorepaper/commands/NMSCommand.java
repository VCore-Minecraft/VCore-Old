/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.commands;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.command.VCommandCallback;
import de.verdox.vcore.plugin.command.VCoreCommand;
import de.verdox.vcorepaper.VCorePaper;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 15:15
 */
public class NMSCommand extends VCoreCommand.VCoreBukkitCommand {
    public NMSCommand(VCorePlugin.Minecraft vCorePlugin, String commandName) {
        super(vCorePlugin, commandName);

        addCommandCallback("world")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .addCommandPath("sendFakeDimension")
                .withPermission("nms.world.sendFakeDimension")
                .askFor("DimensionName", VCommandCallback.CommandAskType.STRING, "&cDimension not found", "NORMAL", "NETHER", "THE_END")
                .commandCallback((commandSender, commandParameters) -> {
                    VCorePaper.getInstance().sync(() -> {
                        String env = commandParameters.getObject(0, String.class);
                        try {
                            VCorePaper.getInstance().getNmsManager().getNmsWorldHandler().sendFakeDimension((Player) commandSender, World.Environment.valueOf(env));
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDimension successfully changed!"));
                        } catch (IllegalArgumentException e) {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDimension not found"));
                        }
                    });
                });

        addCommandCallback("world")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .addCommandPath("sendFakeBiome")
                .withPermission("nms.world.sendFakeBiome")
                .askFor("Biome", VCommandCallback.CommandAskType.STRING, "&cBiome not found", Arrays.stream(Biome.values()).map(Biome::name).toArray(String[]::new))
                .commandCallback((commandSender, commandParameters) -> {
                    VCorePaper.getInstance().sync(() -> {
                        Player player = (Player) commandSender;
                        Biome biome = commandParameters.getEnum(0, Biome.class);
                        if (biome == null) {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBiome not found"));
                            return;
                        }
                        VCorePaper.getInstance().getNmsManager().getNmsWorldHandler().sendFakeBiome(player, player.getChunk(), biome);
                    });
                });

        addCommandCallback("world")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .addCommandPath("resetView")
                .withPermission("nms.world.resetView")
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    VCorePaper.getInstance().sync(() -> {
                        VCorePaper.getInstance().getNmsManager().getNmsWorldHandler().resetView(player);
                    });
                });
    }
}
