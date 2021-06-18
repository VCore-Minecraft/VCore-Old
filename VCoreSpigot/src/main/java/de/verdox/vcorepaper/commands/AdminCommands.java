package de.verdox.vcorepaper.commands;

import de.verdox.vcore.command.VCoreCommand;
import de.verdox.vcore.command.callback.CommandCallback;
import de.verdox.vcore.data.session.SSession;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.blocks.VBlock;
import de.verdox.vcorepaper.custom.blocks.enums.VBlockEventPermission;
import de.verdox.vcorepaper.custom.items.VCoreItem;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdminCommands extends VCoreCommand.VCoreBukkitCommand {
    public AdminCommands(VCorePlugin.Minecraft vCorePlugin, String commandName) {
        super(vCorePlugin, commandName);
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

                        vCorePlugin.getSessionManager().getSession(player.getUniqueId()).debugToConsole();
                        return true;
                    }
                    else if(args[2].equalsIgnoreCase("subsystems")){
                        vCorePlugin.getServerDataManager().getAllSessions().forEach(SSession::debugToConsole);
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
                        VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.CHAT, "&cLook at a block&7!");
                        return false;
                    }
                    if(rayTraceResult.getHitBlock() == null) {
                        VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.CHAT, "&cLook at a block&7!");
                        return false;
                    }
                    Block block = rayTraceResult.getHitBlock();
                    if(args.length == 1){
                        VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"");
                        VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&6Block Debug&7");
                        VCorePaper.getInstance().getCustomBlockManager().VBlockCallback(block.getLocation(),vBlock -> {
                            vBlock.getCustomDataKeys().forEach(key -> {
                                VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"  &7>> &6"+key+"&7: &b"+vBlock.getNBTCompound().getObject(key,Object.class));
                            });
                            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"");
                        });
                        return true;
                    }
                    else if(args.length == 2){
                        if(args[1].equalsIgnoreCase("isCached")){
                            VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().getVBlock(block.getLocation());
                            boolean cached = vBlock != null;
                            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&eCached&7: &b"+cached+"&7!");
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
                                VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&eRule &6"+vBlockEventPermission.name()+" &e applied to Block");
                                return true;
                            }
                            catch (Exception e){
                                VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&cPermission konnte nicht gefunden werden&7!");
                                return false;
                            }
                        }
                    }
                }
                else if(args[0].equalsIgnoreCase("item")){
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    if(args.length == 1){
                        if(itemInHand.getType().equals(Material.AIR)){
                            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.CHAT, "&cHold an item in your main hand&7!");
                            return false;
                        }
                        VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"");
                        VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&6Item Debug&7");
                        VCoreItem vCoreItem = VCorePaper.getInstance().getCustomItemManager().wrap(VCoreItem.class,itemInHand);
                        vCoreItem.getCustomDataKeys().forEach(key -> {
                            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"  &7>> &6"+key+"&7: &b"+vCoreItem.getNBTCompound().getObject(key,Object.class));
                        });
                        VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"");
                        return true;
                    }
                }
                else if(args[0].equalsIgnoreCase("entity")){
                    VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&cNot implemented yet&7!");
                    return false;
                }
                else if(args[0].equalsIgnoreCase("chunk")){
                    Chunk chunk = player.getChunk();
                    VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"");
                    VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&6Chunk Debug&7");
                    VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&7> &eCached Chunks&7: &b"+VCorePaper.getInstance().getCustomBlockManager().getCachedChunkCount());
                    VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&7> &eIs Chunk cached&7: &b"+VCorePaper.getInstance().getCustomBlockManager().isCached(chunk));
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
