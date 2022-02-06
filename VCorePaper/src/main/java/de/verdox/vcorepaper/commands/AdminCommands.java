/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.commands;

import de.verdox.vcore.plugin.command.VCoreCommandCallback;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.impl.command.VCorePaperCommand;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class AdminCommands extends VCorePaperCommand {
    private final VCorePaper vCorePaper;

    public AdminCommands(VCorePaper vCorePlugin, String commandName) {
        super(vCorePlugin, commandName);
        this.vCorePaper = vCorePlugin;

        //addCommandCallback("debugBookGUI")
        //        .withPermission("vcore.debug")
        //        .setExecutor(VCoreCommandCallback.CommandExecutorType.PLAYER)
        //        .commandCallback((commandSender, commandParameters) -> {
        //            Player player = (Player) commandSender;
        //            new DialogBuilder(VCorePaper.getInstance(), player)
        //                    .addText("Hallo Fremder, dies ist ein Super cooler Text, versuche mal den Button unter dieser Line")
        //                    .addButton("TestButton", player1 -> player1.sendMessage("hi"))
        //                    .addText("Dann geht es direkt mit einem nächsten Teil weiter :D")
        //                    .addButton("TestButton2", player1 -> player1.sendMessage("hi2"))
        //                    .openDialog();
        //        });

        addCommandCallback("debugNetworkInfo")
                .withPermission("vcore.debug")
                .setExecutor(VCoreCommandCallback.CommandExecutorType.CONSOLE)
                .commandCallback((commandSender, commandParameters) -> {
                    vCorePaper.async(() -> {
                        vCorePaper.consoleMessage("", false);
                        vCorePaper.consoleMessage("&eDebugging Network", false);
                        vCorePaper.consoleMessage("", false);


                        vCorePaper.consoleMessage("&eProxy Server", false);
                        vCorePaper.consoleMessage("", false);
                        vCorePaper.getNetworkManager().getProxyPlayers().forEach((serverName, vCorePlayers) -> {
                            vCorePaper.consoleMessage("&8[&a" + serverName + "&8]", 1, false);
                            vCorePlayers.forEach(vCorePlayer -> {
                                vCorePaper.consoleMessage("&a" + vCorePlayer.getDisplayName(), 2, false);
                            });
                        });

                        vCorePaper.consoleMessage("&eGame Server", false);
                        vCorePaper.consoleMessage("", false);
                        vCorePaper.getNetworkManager().getGameServerPlayers().forEach((serverName, vCorePlayers) -> {
                            vCorePaper.consoleMessage("&8[&a" + serverName + "&8]", 1, false);
                            vCorePlayers.forEach(vCorePlayer -> {
                                vCorePaper.consoleMessage("&a" + vCorePlayer.getDisplayName(), 2, false);
                            });
                        });
                    });
                });

        addCommandCallback("listPlugins").withPermission("vcore.debug")
                .commandCallback((commandSender, commandParameters) -> {
                    for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                        if (!(plugin instanceof VCorePaperPlugin))
                            continue;
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + plugin.getName()));
                    }
                });
        addCommandCallback("debugMode")
                .withPermission("vcore.debug")
                .askFor("pluginName", VCoreCommandCallback.CommandAskType.STRING, "&cVCore-Plugin not found",
                        Arrays.stream(Bukkit.getPluginManager().getPlugins())
                                .filter(plugin -> plugin instanceof VCorePaperPlugin).map(Plugin::getName).toArray(String[]::new))
                .askFor("boolean", VCoreCommandCallback.CommandAskType.BOOLEAN, "&cWrong Input")
                .commandCallback((commandSender, commandParameters) -> {
                    String pluginName = commandParameters.getObject(0, String.class);
                    boolean debug = commandParameters.getObject(1, Boolean.class);
                    Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
                    if (!(plugin instanceof VCorePaperPlugin foundVCorePlugin)) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cVCore-Plugin not found"));
                        return;
                    }
                    foundVCorePlugin.setDebugMode(debug);
                    if (debug)
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b" + pluginName + " &edebugMode&7: &a" + true));
                    else
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b" + pluginName + " &edebugMode&7: &c" + false));
                });

        //addCommandCallback("debugItem")
        //        .withPermission("vcore.debug")
        //        .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
        //        .commandCallback((commandSender, commandParameters) -> {
        //            Player player = (Player) commandSender;
        //            ItemStack stack = player.getInventory().getItemInMainHand();
        //            if (stack.getType().isAir()) {
        //                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cHold an item in your main hand&7!"));
        //                return;
        //            }
        //            VCoreItem vCoreItem = VCorePaper.getInstance().getCustomItemManager().wrap(VCoreItem.class, stack);
        //            vCoreItem.sendDebugInformation(player);
        //        });
//
        //addCommandCallback("debugBlock")
        //        .withPermission("vcore.debug")
        //        .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
        //        .addCommandPath("addDebugInfo")
        //        .askFor("SavingTechnique", VCommandCallback.CommandAskType.STRING, "&cTechnique unknown", "LocationBased", "BlockBased", "ALL")
        //        .commandCallback((commandSender, commandParameters) -> {
        //            Player player = (Player) commandSender;
        //            String technique = commandParameters.getObject(0, String.class);
//
        //            if (!technique.equalsIgnoreCase("LocationBased") && !technique.equalsIgnoreCase("BlockBased") && !technique.equalsIgnoreCase("ALL")) {
        //                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cTechnique unknown"));
        //                return;
        //            }
//
//
        //            RayTraceResult rayTraceResult = player.rayTraceBlocks(7);
        //            if (rayTraceResult == null) {
        //                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue einen Block an&7!"));
        //                return;
        //            }
        //            Block hitBlock = rayTraceResult.getHitBlock();
        //            if (hitBlock == null) {
        //                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue einen Block an&7!"));
        //                return;
        //            }
//
        //            if (technique.equalsIgnoreCase("BlockBased") || technique.equalsIgnoreCase("ALL")) {
        //                VBlock.BlockBased vBlock = VCorePaper.getInstance().getCustomBlockDataManager().wrap(VBlock.BlockBased.class, hitBlock);
        //                vBlock.storeCustomData(BlockDebugData.class, System.currentTimeMillis(), null);
        //            }
        //            if (technique.equalsIgnoreCase("LocationBased") || technique.equalsIgnoreCase("ALL")) {
        //                VBlock.LocationBased vBlock = VCorePaper.getInstance().getCustomLocationDataManager().wrap(VBlock.LocationBased.class, hitBlock.getLocation());
        //                vBlock.storeCustomData(BlockDebugData.class, System.currentTimeMillis(), null);
        //            }
        //            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDebug Info gespeichert&7!"));
        //        });
        //addCommandCallback("debugBlock")
        //        .withPermission("vcore.debug")
        //        .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
        //        .askFor("SavingTechnique", VCommandCallback.CommandAskType.STRING, "&cTechnique unknown", "LocationBased", "BlockBased", "ALL")
        //        .commandCallback((commandSender, commandParameters) -> {
        //            Player player = (Player) commandSender;
        //            String technique = commandParameters.getObject(0, String.class);
//
        //            if (!technique.equalsIgnoreCase("LocationBased") && !technique.equalsIgnoreCase("BlockBased") && !technique.equalsIgnoreCase("ALL")) {
        //                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cTechnique unknown"));
        //                return;
        //            }
//
        //            RayTraceResult rayTraceResult = player.rayTraceBlocks(7);
        //            if (rayTraceResult == null) {
        //                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue einen Block an&7!"));
        //                return;
        //            }
        //            Block hitBlock = rayTraceResult.getHitBlock();
        //            if (hitBlock == null) {
        //                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue einen Block an&7!"));
        //                return;
        //            }
//
        //            vCorePlugin.sync(() -> {
        //                if (technique.equalsIgnoreCase("BlockBased") || technique.equalsIgnoreCase("ALL")) {
        //                    VBlock.BlockBased vBlock = VCorePaper.getInstance().getCustomBlockDataManager().wrap(VBlock.BlockBased.class, hitBlock);
        //                    vBlock.sendDebugInformation(player);
        //                }
        //                if (technique.equalsIgnoreCase("LocationBased") || technique.equalsIgnoreCase("ALL")) {
        //                    VBlock.LocationBased vBlock = VCorePaper.getInstance().getCustomLocationDataManager().wrap(VBlock.LocationBased.class, hitBlock.getLocation());
        //                    vBlock.sendDebugInformation(player);
        //                }
        //            });
        //        });
//
        //addCommandCallback("debugEntity")
        //        .withPermission("vcore.debug")
        //        .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
        //        .commandCallback((commandSender, commandParameters) -> {
        //            Player player = (Player) commandSender;
        //            vCorePlugin.sync(() -> {
        //                RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 7, entity -> !entity.getType().equals(EntityType.PLAYER));
        //                if (rayTraceResult == null) {
        //                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue ein Entity an&7!"));
        //                    return;
        //                }
        //                Entity hitEntity = rayTraceResult.getHitEntity();
        //                if (hitEntity == null) {
        //                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue ein Entity an&7!"));
        //                    return;
        //                }
        //                VCoreEntity vCoreEntity = VCorePaper.getInstance().getCustomEntityManager().wrap(VCoreEntity.class, hitEntity);
        //                vCoreEntity.sendDebugInformation(player);
        //            });
        //        });
        //addCommandCallback("debugChunk")
        //        .withPermission("vcore.debug")
        //        .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
        //        .commandCallback((commandSender, commandParameters) -> {
        //            Player player = (Player) commandSender;
        //            Chunk chunk = player.getChunk();
        //            WorldChunk worldChunk = new WorldChunk(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        //            player.sendMessage("");
        //            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDebugging World Chunk"));
        //            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bWorldChunk&7: &e" + worldChunk));
        //            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bWorldRegion&7: &e" + worldChunk.getRegion()));
        //            player.sendMessage("");
        //        });
//
        //addCommandCallback("testItem")
        //        .withPermission("vcore.debug")
        //        .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
        //        .commandCallback((commandSender, commandParameters) -> {
        //            Player player = (Player) commandSender;
//
        //            VCoreItem vCoreItem = VCorePaper.getInstance().getCustomItemManager()
        //                    .createItemBuilder(Material.GOLD_INGOT)
        //                    .displayName(ChatColor.translateAlternateColorCodes('&', "&eTest Debug Item"))
        //                    .lore("", "&eZeile 1", "&fZeile 2")
        //                    .buildItem();
        //            vCoreItem.toNBTHolder().getPersistentDataContainer().setObject("debugNBT", true);
        //            player.getInventory().addItem(vCoreItem.getDataHolder());
        //        });
    }
}
