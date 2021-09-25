/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.commands;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.command.VCommandCallback;
import de.verdox.vcore.plugin.command.VCoreCommand;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.workernpc.NPCProfession;
import de.verdox.vcorepaper.custom.workernpc.WorkerNPC;
import me.nemo_64.spigotutilities.playerinputs.chatinput.PlayerChatInput;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 22.09.2021 12:55
 */
public class WorkingNPCCommand extends VCoreCommand.VCoreBukkitCommand {
    public WorkingNPCCommand(VCorePlugin.Minecraft vCorePlugin, String commandName) {
        super(vCorePlugin, commandName);

        addCommandCallback("create")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .withPermission("workingNPC.create")
                .commandCallback((commandSender, commandParameters) -> {
                    vCorePlugin.sync(() -> {
                        Player player = (Player) commandSender;
                        RayTraceResult rayTraceResult = player.rayTraceBlocks(10);
                        if (rayTraceResult == null || rayTraceResult.getHitBlock() == null) {
                            player.sendMessage(ChatColor.RED + "Look at a block!");
                            return;
                        }
                        Location spawnLocation = rayTraceResult.getHitBlock().getLocation().clone().add(0.5, 1, 0.5);
                        Villager villager = player.getWorld().spawn(spawnLocation, Villager.class, CreatureSpawnEvent.SpawnReason.CUSTOM);

                        WorkerNPC workerNPC = VCorePaper.getInstance().getCustomEntityManager().wrap(WorkerNPC.class, villager);
                        workerNPC.initialize();
                        player.sendMessage(ChatColor.GREEN + "NPC created!");
                    });
                });

        addCommandCallback("delete")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .withPermission("workingNPC.delete")
                .commandCallback((commandSender, commandParameters) -> {
                    vCorePlugin.sync(() -> {
                        Player player = (Player) commandSender;
                        WorkerNPC workerNPC = lookAtWorkerNPC(player);
                        if (workerNPC == null)
                            return;
                        workerNPC.getDataHolder().remove();
                        player.sendMessage(ChatColor.GREEN + "NPC deleted!");
                    });
                });

        addCommandCallback("setName")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .withPermission("workingNPC.setName")
                .askFor("npcName", VCommandCallback.CommandAskType.STRING, "")
                .commandCallback((commandSender, commandParameters) -> {
                    vCorePlugin.sync(() -> {
                        Player player = (Player) commandSender;
                        WorkerNPC workerNPC = lookAtWorkerNPC(player);
                        if (workerNPC == null) {
                            player.sendMessage("§cSchaue einen WorkingNPC an.");
                            return;
                        }
                        String newName = commandParameters.getObject(0, String.class);
                        workerNPC.setName(newName);
                        player.sendMessage(ChatColor.GREEN + "Name changed!");
                    });
                });

        addCommandCallback("changeText")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .withPermission("workingNPC.changeText")
                .askFor("npcName", VCommandCallback.CommandAskType.STRING, "")
                .commandCallback((commandSender, commandParameters) -> {
                    vCorePlugin.sync(() -> {
                        Player player = (Player) commandSender;
                        WorkerNPC workerNPC = lookAtWorkerNPC(player);
                        if (workerNPC == null)
                            return;
                        new PlayerChatInput.PlayerChatInputBuilder<String>(VCorePaper.getInstance(), player)
                                .isValidInput((player1, s) -> true)
                                .setValue((player1, s) -> s)
                                .sendValueMessage("Write the new text in the chat")
                                .onFinish((player1, s) -> {
                                    workerNPC.setText(s);
                                    player.sendMessage(ChatColor.GREEN + "Text changed!");
                                }).build().start();
                    });
                });

        addCommandCallback("addProfession")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .withPermission("workingNPC.addProfession")
                .askFor("profession", VCommandCallback.CommandAskType.STRING, "&cProfession unknown", VCorePaper.getInstance().getCustomEntityManager().getProfessionRegistry().getProfessions().toArray(String[]::new))
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    vCorePlugin.sync(() -> {
                        WorkerNPC workerNPC = lookAtWorkerNPC(player);
                        if (workerNPC == null)
                            return;
                        String professionID = commandParameters.getObject(0, String.class);
                        Class<? extends NPCProfession> profession = VCorePaper.getInstance().getCustomEntityManager().getProfessionRegistry().getProfessionClass(professionID);
                        if (profession == null) {
                            player.sendMessage(ChatColor.RED + "Profession unknown!");
                            return;
                        }
                        workerNPC.addProfession(profession);
                        player.sendMessage(ChatColor.GREEN + "Profession added!");
                    });
                });
    }

    private WorkerNPC lookAtWorkerNPC(@NotNull Player player) {
        RayTraceResult rayTraceResult = VCoreUtil.BukkitUtil.getBukkitPlayerUtil().rayTraceEntities(player, 10);
        if (rayTraceResult == null || rayTraceResult.getHitEntity() == null) {
            player.sendMessage(ChatColor.RED + " Look at a Working NPC!");
            return null;
        }
        WorkerNPC workerNPC = VCorePaper.getInstance().getCustomEntityManager().wrap(WorkerNPC.class, rayTraceResult.getHitEntity());
        if (!workerNPC.verify()) {
            player.sendMessage(ChatColor.RED + " Look at a Working NPC!");
            return null;
        }
        return workerNPC;
    }
}
