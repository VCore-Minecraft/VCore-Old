package de.verdox.vcorepaper.commands;

import de.verdox.vcore.plugin.command.VCommandCallback;
import de.verdox.vcore.plugin.command.VCoreCommand;
import de.verdox.vcore.plugin.command.callback.CommandCallback;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.api.querytypes.ServerLocation;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.blocks.VBlock;
import de.verdox.vcorepaper.custom.blocks.enums.VBlockEventPermission;
import de.verdox.vcorepaper.custom.items.VCoreItem;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
        addCommandCallback("debugMode").withPermission("vcore.debug")
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


        addCommandSuggestions(0,(commandSender, args) -> List.of("listPlugins","plugin","block","item","entity","chunk"));
        addCommandSuggestions(1,(commandSender, args) -> {
            if(args[0].equalsIgnoreCase("block"))
                return List.of("allow","deny","isCached");
            if(args[0].equalsIgnoreCase("item"))
                return List.of("setDisplayName");
            return null;
        });
        addCommandSuggestions(2,(commandSender, args) -> {
            if(args[0].equalsIgnoreCase("block"))
                if(args[1].equalsIgnoreCase("allow") || args[1].equalsIgnoreCase("deny"))
                    return Arrays.stream(VBlockEventPermission.values()).map(Enum::name).collect(Collectors.toList());
                return null;
        });
    }

    @Override
    protected CommandCallback<CommandSender> commandCallback() {
        return (sender, args) -> {
            if(!sender.hasPermission("vcore.debug"))
                return false;

            if(args.length >= 1){
                if(args[0].equalsIgnoreCase("listPlugins")){
                    VCorePaper.getInstance().consoleMessage("&eDebuggable Plugins&7:",false);
                    for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                        if(!(plugin instanceof VCorePlugin.Minecraft))
                            continue;
                        VCorePaper.getInstance().consoleMessage("&a"+plugin.getName(),1,false);
                    }
                    return true;
                }
                else if(args[0].equalsIgnoreCase("plugin")){
                    String pluginName = args[1];
                    Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
                    if(plugin == null){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cPlugin konnte nicht gefunden werden&7!"));
                        return false;
                    }

                    if(!(plugin instanceof VCorePlugin.Minecraft)){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cDas Plugin ist kein VCorePlugin&7!"));
                        return false;
                    }

                    VCorePlugin.Minecraft vCorePlugin = (VCorePlugin.Minecraft) plugin;

                    if(args[2].equalsIgnoreCase("player")){
                        Player player = Bukkit.getPlayer(args[3]);
                        if(player == null){
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cPlayer not found&7: &b"+args[3]));
                            return false;
                        }

                        //TODO: NEU MACHEN
                        //vCorePlugin.getSessionManager().getSession(player.getUniqueId()).debugToConsole();
                        return true;
                    }
                    else if(args[2].equalsIgnoreCase("subsystems")){
                        //TODO: NEU MACHEN
                        //vCorePlugin.getServerDataManager().getAllSessions().forEach(SSession::debugToConsole);
                        return true;
                    }
                }

                if(!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cOnly players can execute this command&7: &b"+args[3]));
                    return false;
                }
                Player player = (Player) sender;

                if(args[0].equalsIgnoreCase("block")){

                    RayTraceResult rayTraceResult = player.rayTraceBlocks(20);
                    if(rayTraceResult == null) {
                        VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.CHAT, "&cLook at a block&7!");
                        return false;
                    }
                    if(rayTraceResult.getHitBlock() == null) {
                        VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.CHAT, "&cLook at a block&7!");
                        return false;
                    }
                    Block block = rayTraceResult.getHitBlock();
                    if(args.length == 1){
                        VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"");
                        VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&6Block Debug&7");
                        VCorePaper.getInstance().getCustomBlockManager().VBlockCallback(block.getLocation(),vBlock -> {
                            vBlock.getCustomDataKeys().forEach(key -> {
                                VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"  &7>> &6"+key+"&7: &b"+vBlock.getNBTCompound().getObject(key,Object.class));
                            });
                            VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"");
                        });
                        return true;
                    }
                    else if(args.length == 2){
                        if(args[1].equalsIgnoreCase("isCached")){
                            VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().getVBlock(block.getLocation());
                            boolean cached = vBlock != null;
                            VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&eCached&7: &b"+cached+"&7!");
                            return true;
                        }
                    }
                    else if(args.length == 3){
                        if(args[1].equalsIgnoreCase("allow") || args[1].equalsIgnoreCase("deny")){
                            String name = args[2];
                            try{
                                VBlockEventPermission vBlockEventPermission = VBlockEventPermission.valueOf(name);
                                VCorePaper.getInstance().getCustomBlockManager().VBlockCallback(block.getLocation(),vBlock -> {
                                    boolean allow = args[1].equalsIgnoreCase("allow");
                                    vBlock.allowBlockPermission(vBlockEventPermission,allow);
                                });
                                VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&eRule &6"+vBlockEventPermission.name()+" &e applied to Block");
                                return true;
                            }
                            catch (Exception e){
                                VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&cPermission konnte nicht gefunden werden&7!");
                                return false;
                            }
                        }
                    }
                }
                else if(args[0].equalsIgnoreCase("item")){
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    if(args.length == 1){
                        if(itemInHand.getType().equals(Material.AIR)){
                            VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.CHAT, "&cHold an item in your main hand&7!");
                            return false;
                        }
                        VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"");
                        VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&6Item Debug&7");
                        VCoreItem vCoreItem = VCorePaper.getInstance().getCustomItemManager().wrap(VCoreItem.class,itemInHand);
                        vCoreItem.getCustomDataKeys().forEach(key -> {
                            VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"  &7>> &6"+key+"&7: &b"+vCoreItem.getNBTCompound().getObject(key,Object.class));
                        });
                        VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"");
                        return true;
                    }
                }
                else if(args[0].equalsIgnoreCase("entity")){
                    VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&cNot implemented yet&7!");
                    return false;
                }
                else if(args[0].equalsIgnoreCase("chunk")){
                    Chunk chunk = player.getChunk();
                    VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"");
                    VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&6Chunk Debug&7");
                    VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&7> &eCached Chunks&7: &b"+VCorePaper.getInstance().getCustomBlockManager().getCachedChunkCount());
                    VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&7> &eIs Chunk cached&7: &b"+VCorePaper.getInstance().getCustomBlockManager().isCached(chunk));
                    return true;
                }
            }

            if(sender instanceof Player) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/&edebug block"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/&edebug item"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/&edebug entity"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/&edebug chunk"));
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&edebug help"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&edebug listPlugins"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&edebug plugin &8<&bpluginName&8> &eplayer &8<&bplayerName&8>"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&edebug plugin &8<&bpluginName&8> &esubSystems"));
            return false;
        };
    }
}
