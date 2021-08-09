package de.verdox.vcorepaper.commands;

import de.verdox.vcore.plugin.command.VCommandCallback;
import de.verdox.vcore.plugin.command.VCoreCommand;
import de.verdox.vcore.plugin.command.callback.CommandCallback;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.blocks.VBlock;
import de.verdox.vcorepaper.custom.blocks.debug.BlockDebugData;
import de.verdox.vcorepaper.custom.blocks.enums.VBlockEventPermission;
import de.verdox.vcorepaper.custom.entities.VCoreEntity;
import de.verdox.vcorepaper.custom.items.VCoreItem;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdminCommands extends VCoreCommand.VCoreBukkitCommand {
    private VCorePaper vCorePaper;

    public AdminCommands(VCorePaper vCorePlugin, String commandName) {
        super(vCorePlugin, commandName);
        this.vCorePaper = vCorePlugin;

        addCommandCallback("debugNetworkInfo")
                .withPermission("vcore.debug")
                .setExecutor(VCommandCallback.CommandExecutorType.CONSOLE)
                .commandCallback((commandSender, commandParameters) -> {
                    vCorePaper.async(() -> {
                        vCorePaper.consoleMessage("",false);
                        vCorePaper.consoleMessage("&eDebugging Network",false);
                        vCorePaper.consoleMessage("",false);


                        vCorePaper.consoleMessage("&eProxy Server",false);
                        vCorePaper.consoleMessage("",false);
                        vCorePaper.getNetworkManager().getProxyPlayers().forEach((serverName, vCorePlayers) -> {
                            vCorePaper.consoleMessage("&8[&a"+serverName+"&8]",1,false);
                            vCorePlayers.forEach(vCorePlayer -> {
                                vCorePaper.consoleMessage("&a"+vCorePlayer.getDisplayName(),2,false);
                            });
                        });

                        vCorePaper.consoleMessage("&eGame Server",false);
                        vCorePaper.consoleMessage("",false);
                        vCorePaper.getNetworkManager().getGameServerPlayers().forEach((serverName, vCorePlayers) -> {
                            vCorePaper.consoleMessage("&8[&a"+serverName+"&8]",1,false);
                            vCorePlayers.forEach(vCorePlayer -> {
                                vCorePaper.consoleMessage("&a"+vCorePlayer.getDisplayName(),2,false);
                            });
                        });
                    });
                });

        addCommandCallback("listPlugins").withPermission("vcore.debug")
                .commandCallback((commandSender, commandParameters) -> {
                    for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                        if(!(plugin instanceof VCorePlugin.Minecraft))
                            continue;
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&a"+plugin.getName()));
                    }
                });
        addCommandCallback("debugMode")
                .withPermission("vcore.debug")
                .askFor("pluginName", VCommandCallback.CommandAskType.STRING, "&cVCore-Plugin not found",
                        Arrays.stream(Bukkit.getPluginManager().getPlugins())
                                .filter(plugin -> plugin instanceof VCorePlugin.Minecraft).map(Plugin::getName).toArray(String[]::new))
                .askFor("boolean", VCommandCallback.CommandAskType.BOOLEAN,"&cWrong Input")
                .commandCallback((commandSender, commandParameters) -> {
                    String pluginName = commandParameters.getObject(0,String.class);
                    boolean debug = commandParameters.getObject(1,Boolean.class);
                    Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
                    if(!(plugin instanceof VCorePlugin.Minecraft)) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cVCore-Plugin not found"));
                        return;
                    }
                    VCorePlugin.Minecraft foundVCorePlugin = (VCorePlugin.Minecraft) plugin;
                    foundVCorePlugin.setDebugMode(debug);
                    if(debug)
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b"+pluginName+" &edebugMode&7: &a"+true));
                    else
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b"+pluginName+" &edebugMode&7: &c"+false));
                });
        addCommandCallback("debugBlock")
                .addCommandPath("addDebugInfo")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    RayTraceResult rayTraceResult = player.rayTraceBlocks(7);
                    if(rayTraceResult == null) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue einen Block an&7!"));
                        return;
                    }
                    Block hitBlock = rayTraceResult.getHitBlock();
                    if(hitBlock == null) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue einen Block an&7!"));
                        return;
                    }
                    VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().wrap(VBlock.class, hitBlock.getLocation());
                    vBlock.storeCustomData(BlockDebugData.class,System.currentTimeMillis(),null);
                });
        addCommandCallback("debugBlock")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    RayTraceResult rayTraceResult = player.rayTraceBlocks(7);
                    if(rayTraceResult == null) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue einen Block an&7!"));
                        return;
                    }
                    Block hitBlock = rayTraceResult.getHitBlock();
                    if(hitBlock == null) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue einen Block an&7!"));
                        return;
                    }
                    VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().wrap(VBlock.class, hitBlock.getLocation());
                    commandSender.sendMessage("");
                    vBlock.getNBTCompound().getKeys().forEach(s -> {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7>> &e"+s+"&7: "+vBlock.getNBTCompound().getObject(s,Object.class).toString()));
                    });
                });
        addCommandCallback("debugEntity")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    vCorePlugin.sync(() -> {
                        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation().clone().add(0,1,0),player.getLocation().getDirection(),7);
                        if(rayTraceResult == null) {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue ein Entity an&7!"));
                            return;
                        }
                        Entity hitEntity = rayTraceResult.getHitEntity();
                        if(hitEntity == null) {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBitte schaue ein Entity an&7!"));
                            return;
                        }
                        VCoreEntity vCoreEntity = VCorePaper.getInstance().getCustomEntityManager().wrap(VCoreEntity.class,hitEntity);
                        commandSender.sendMessage("");
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eDebugging &7: "+hitEntity));
                        vCoreEntity.getNBTCompound().getKeys().forEach(s -> {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7>> &e"+s+"&7: "+vCoreEntity.getNBTCompound().getObject(s,Object.class)));
                        });
                    });
                });
    }
}
