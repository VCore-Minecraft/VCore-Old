/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.commands;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.command.VCommandCallback;
import de.verdox.vcore.plugin.command.VCoreCommand;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.talkingnpc.TalkingNPC;
import me.nemo_64.spigotutilities.playerinputs.chatinput.PlayerChatInput;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.RayTraceResult;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 13.09.2021 22:56
 */
public class TalkingNPCCommand extends VCoreCommand.VCoreBukkitCommand {
    public TalkingNPCCommand(VCorePlugin.Minecraft vCorePlugin, String commandName) {
        super(vCorePlugin, commandName);

        addCommandCallback()
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .withPermission("vcore.talkingNpc.changeTalking")
                .addCommandPath("changeTalking")
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    vCorePlugin.sync(() -> {
                        RayTraceResult rayTraceResult = VCoreUtil.BukkitUtil.getBukkitPlayerUtil().rayTraceEntities(player, 10);
                        if (rayTraceResult == null || rayTraceResult.getHitEntity() == null) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cLook at a TalkingNPC"));
                            return;
                        }
                        Entity entity = rayTraceResult.getHitEntity();
                        TalkingNPC talkingNPC = VCorePaper.getInstance().getCustomEntityManager().getTalkingNPCService().getTalkingNPC(entity);
                        if (!talkingNPC.verify()) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cLook at a TalkingNPC"));
                            return;
                        }
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aType in what the NPC is going to say&7!"));
                        new PlayerChatInput.PlayerChatInputBuilder<String>(VCorePaper.getInstance(), player)
                                .isValidInput((player1, s) -> true)
                                .setValue((player1, s) -> s)
                                .onFinish((player1, s) -> {
                                    talkingNPC.setTalking(s);
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTalking successfully changed&7!"));
                                })
                                .onCancel(player1 -> {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCancelled&7!"));
                                })
                                .build()
                                .start();
                    });
                });

        addCommandCallback()
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .withPermission("vcore.talkingNpc.create")
                .addCommandPath("create")
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    vCorePlugin.sync(() -> {
                        RayTraceResult rayTraceResult = player.rayTraceBlocks(10);
                        if (rayTraceResult == null || rayTraceResult.getHitBlock() == null) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cLook at a block&7!"));
                            return;
                        }
                        Entity entity = player.getWorld().spawnEntity(rayTraceResult.getHitBlock().getLocation().add(0, 1, 0), EntityType.VILLAGER, CreatureSpawnEvent.SpawnReason.CUSTOM);
                        TalkingNPC talkingNPC = VCorePaper.getInstance().getCustomEntityManager().getTalkingNPCService().getTalkingNPC(entity);
                        talkingNPC.toNBTHolder().getVanillaCompound().setBoolean("NoAI", true);
                        talkingNPC.initialize();
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTalking NPC created&7!"));
                    });
                });

        addCommandCallback()
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .withPermission("vcore.talkingNpc.setName")
                .addCommandPath("setName")
                .askFor("npcName", VCommandCallback.CommandAskType.STRING, "")
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    String newNPCName = commandParameters.getObject(0, String.class);
                    vCorePlugin.sync(() -> {
                        RayTraceResult rayTraceResult = VCoreUtil.BukkitUtil.getBukkitPlayerUtil().rayTraceEntities(player, 10);
                        if (rayTraceResult == null || rayTraceResult.getHitEntity() == null) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cLook at a TalkingNPC"));
                            return;
                        }
                        Entity entity = rayTraceResult.getHitEntity();
                        TalkingNPC talkingNPC = VCorePaper.getInstance().getCustomEntityManager().getTalkingNPCService().getTalkingNPC(entity);
                        if (!talkingNPC.verify()) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cLook at a TalkingNPC"));
                            return;
                        }
                        talkingNPC.setNPCName(newNPCName);
                        talkingNPC.updateNameTag();
                    });
                });

        addCommandCallback()
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .withPermission("vcore.talkingNpc.delete")
                .addCommandPath("delete")
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    vCorePlugin.sync(() -> {
                        RayTraceResult rayTraceResult = VCoreUtil.BukkitUtil.getBukkitPlayerUtil().rayTraceEntities(player, 10);
                        if (rayTraceResult == null || rayTraceResult.getHitEntity() == null) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cLook at a TalkingNPC"));
                            return;
                        }
                        Entity entity = rayTraceResult.getHitEntity();
                        TalkingNPC talkingNPC = VCorePaper.getInstance().getCustomEntityManager().getTalkingNPCService().getTalkingNPC(entity);
                        if (!talkingNPC.verify()) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cLook at a TalkingNPC"));
                            return;
                        }
                        talkingNPC.getDataHolder().remove();
                    });
                });
    }
}
