/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.commands;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.command.VCommandCallback;
import de.verdox.vcore.plugin.command.VCoreCommand;
import de.verdox.vcore.plugin.wrapper.types.WorldChunk;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.block.VBlock;
import de.verdox.vcorepaper.custom.block.data.debug.BlockDebugData;
import de.verdox.vcorepaper.custom.entities.VCoreEntity;
import de.verdox.vcorepaper.custom.gui.book.BookGUI;
import de.verdox.vcorepaper.custom.items.VCoreItem;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;

import java.util.Arrays;

public class AdminCommands extends VCoreCommand.VCoreBukkitCommand {
    private final VCorePaper vCorePaper;

    public AdminCommands(VCorePaper vCorePlugin, String commandName) {
        super(vCorePlugin, commandName);
        this.vCorePaper = vCorePlugin;

        addCommandCallback("debugBookGUI")
                .withPermission("vcore.debug")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    BookGUI bookGUI = new BookGUI(VCorePaper.getInstance(), player);

                    TextComponent component = bookGUI.createResponsiveCallbackText(Component.text("Klicke mich als Test"), System.out::println);

                    bookGUI.provideBook(() -> Book.builder()
                            .title(Component.text("test"))
                            .addPage(component).build());

                    bookGUI.openBook();
                });

        addCommandCallback("debugNetworkInfo")
                .withPermission("vcore.debug")
                .setExecutor(VCommandCallback.CommandExecutorType.CONSOLE)
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
                        if (!(plugin instanceof VCorePlugin.Minecraft))
                            continue;
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + plugin.getName()));
                    }
                });
        addCommandCallback("debugMode")
                .withPermission("vcore.debug")
                .askFor("pluginName", VCommandCallback.CommandAskType.STRING, "&cVCore-Plugin not found",
                        Arrays.stream(Bukkit.getPluginManager().getPlugins())
                                .filter(plugin -> plugin instanceof VCorePlugin.Minecraft).map(Plugin::getName).toArray(String[]::new))
                .askFor("boolean", VCommandCallback.CommandAskType.BOOLEAN, "&cWrong Input")
                .commandCallback((commandSender, commandParameters) -> {
                    String pluginName = commandParameters.getObject(0, String.class);
                    boolean debug = commandParameters.getObject(1, Boolean.class);
                    Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
                    if (!(plugin instanceof VCorePlugin.Minecraft)) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cVCore-Plugin not found"));
                        return;
                    }
                    VCorePlugin.Minecraft foundVCorePlugin = (VCorePlugin.Minecraft) plugin;
                    foundVCorePlugin.setDebugMode(debug);
                    if (debug)
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b" + pluginName + " &edebugMode&7: &a" + true));
                    else
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b" + pluginName + " &edebugMode&7: &c" + false));
                });
        addCommandCallback("debugItem")
                .withPermission("vcore.debug")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    ItemStack stack = player.getInventory().getItemInMainHand();
                    if (stack.getType().isAir()) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cHold an item in your main hand&7!"));
                        return;
                    }
                    VCoreItem vCoreItem = VCorePaper.getInstance().getCustomItemManager().wrap(VCoreItem.class, stack);
                    vCoreItem.sendDebugInformation(player);
                });

        addCommandCallback("debugBlock")
                .withPermission("vcore.debug")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .addCommandPath("addDebugInfo")
                .askFor("SavingTechnique", VCommandCallback.CommandAskType.STRING, "&cTechnique unknown", "LocationBased", "BlockBased", "ALL")
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    String technique = commandParameters.getObject(0, String.class);

                    if (!technique.equalsIgnoreCase("LocationBased") && !technique.equalsIgnoreCase("BlockBased") && !technique.equalsIgnoreCase("ALL")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cTechnique unknown"));
                        return;
                    }


                    RayTraceResult rayTraceResult = player.rayTraceBlocks(7);
                    if (rayTraceResult == null) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue einen Block an&7!"));
                        return;
                    }
                    Block hitBlock = rayTraceResult.getHitBlock();
                    if (hitBlock == null) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue einen Block an&7!"));
                        return;
                    }

                    if (technique.equalsIgnoreCase("BlockBased") || technique.equalsIgnoreCase("ALL")) {
                        VBlock.BlockBased vBlock = VCorePaper.getInstance().getCustomBlockDataManager().wrap(VBlock.BlockBased.class, hitBlock);
                        vBlock.storeCustomData(BlockDebugData.class, System.currentTimeMillis(), null);
                    }
                    if (technique.equalsIgnoreCase("LocationBased") || technique.equalsIgnoreCase("ALL")) {
                        VBlock.LocationBased vBlock = VCorePaper.getInstance().getCustomLocationDataManager().wrap(VBlock.LocationBased.class, hitBlock.getLocation());
                        vBlock.storeCustomData(BlockDebugData.class, System.currentTimeMillis(), null);
                    }
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDebug Info gespeichert&7!"));
                });
        addCommandCallback("debugBlock")
                .withPermission("vcore.debug")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .askFor("SavingTechnique", VCommandCallback.CommandAskType.STRING, "&cTechnique unknown", "LocationBased", "BlockBased", "ALL")
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    String technique = commandParameters.getObject(0, String.class);

                    if (!technique.equalsIgnoreCase("LocationBased") && !technique.equalsIgnoreCase("BlockBased") && !technique.equalsIgnoreCase("ALL")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cTechnique unknown"));
                        return;
                    }

                    RayTraceResult rayTraceResult = player.rayTraceBlocks(7);
                    if (rayTraceResult == null) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue einen Block an&7!"));
                        return;
                    }
                    Block hitBlock = rayTraceResult.getHitBlock();
                    if (hitBlock == null) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue einen Block an&7!"));
                        return;
                    }

                    vCorePlugin.sync(() -> {
                        if (technique.equalsIgnoreCase("BlockBased") || technique.equalsIgnoreCase("ALL")) {
                            VBlock.BlockBased vBlock = VCorePaper.getInstance().getCustomBlockDataManager().wrap(VBlock.BlockBased.class, hitBlock);
                            vBlock.sendDebugInformation(player);
                        }
                        if (technique.equalsIgnoreCase("LocationBased") || technique.equalsIgnoreCase("ALL")) {
                            VBlock.LocationBased vBlock = VCorePaper.getInstance().getCustomLocationDataManager().wrap(VBlock.LocationBased.class, hitBlock.getLocation());
                            vBlock.sendDebugInformation(player);
                        }
                    });
                });

        addCommandCallback("debugEntity")
                .withPermission("vcore.debug")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    vCorePlugin.sync(() -> {
                        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 7, entity -> !entity.getType().equals(EntityType.PLAYER));
                        if (rayTraceResult == null) {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue ein Entity an&7!"));
                            return;
                        }
                        Entity hitEntity = rayTraceResult.getHitEntity();
                        if (hitEntity == null) {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue ein Entity an&7!"));
                            return;
                        }
                        VCoreEntity vCoreEntity = VCorePaper.getInstance().getCustomEntityManager().wrap(VCoreEntity.class, hitEntity);
                        vCoreEntity.sendDebugInformation(player);
                    });
                });
        addCommandCallback("debugChunk")
                .withPermission("vcore.debug")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    Chunk chunk = player.getChunk();
                    WorldChunk worldChunk = new WorldChunk(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
                    player.sendMessage("");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDebugging World Chunk"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bWorldChunk&7: &e" + worldChunk));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bWorldRegion&7: &e" + worldChunk.getRegion()));
                    player.sendMessage("");
                });

        addCommandCallback("testItem")
                .withPermission("vcore.debug")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;

                    VCoreItem vCoreItem = VCorePaper.getInstance().getCustomItemManager()
                            .createItemBuilder(Material.GOLD_INGOT)
                            .displayName(ChatColor.translateAlternateColorCodes('&', "&eTest Debug Item"))
                            .lore("", "&eZeile 1", "&fZeile 2")
                            .buildItem();
                    vCoreItem.toNBTHolder().getPersistentDataContainer().setObject("debugNBT", true);
                    player.getInventory().addItem(vCoreItem.getDataHolder());
                });
    }
}
