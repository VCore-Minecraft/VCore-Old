package de.verdox.vcorepaper.commands;

import de.verdox.vcore.command.VCoreCommand;
import de.verdox.vcore.command.callback.CommandCallback;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.blocks.VBlockCustomData;
import de.verdox.vcorepaper.custom.items.VCoreItem;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.util.List;

public class AdminCommands extends VCoreCommand.VCoreBukkitCommand {
    public AdminCommands(VCorePlugin.Minecraft vCorePlugin, String commandName) {
        super(vCorePlugin, commandName);
        addCommandSuggestions(0,(commandSender, args) -> List.of("block","item"));
    }

    @Override
    protected CommandCallback<CommandSender> commandCallback() {
        return (sender, args) -> {

            if(!(sender instanceof Player))
                return false;
            Player player = (Player) sender;

            if(!player.hasPermission("vcore.debug"))
                return false;

            if(args.length == 1){
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

                        VCorePaper.getInstance().getVBlockManager().VBlockCallback(block.getState(),vBlock -> {
                            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"");
                            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&6Block Debug&7");
                            System.out.println(vBlock.getCustomDataKeys().size());
                            vBlock.getCustomDataKeys().forEach(key -> {
                                System.out.println(key);
                                VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"  &7>> &6"+key+"&7: &b"+vBlock.getCustomDataManager().getDataType(key).findInDataHolder(vBlock));
                            });
                            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"");
                        });
                        return true;
                    }
                    else if(args[0].equalsIgnoreCase("item")){
                        ItemStack itemInHand = player.getInventory().getItemInMainHand();
                        if(itemInHand.getType().equals(Material.AIR)){
                            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.CHAT, "&cHold an item in your main hand&7!");
                            return false;
                        }
                        VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"");
                        VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&6Item Debug&7");
                        VCoreItem vCoreItem = VCorePaper.getInstance().getCustomItemManager().wrap(VCoreItem.class,itemInHand);
                        vCoreItem.getCustomDataKeys().forEach(key -> {
                            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"  &7>> &6"+key+"&7: &b"+vCoreItem.getCustomDataManager().getDataType(key).findInDataHolder(vCoreItem));
                        });
                        VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"");
                        return true;
                    }
                    else if(args[0].equalsIgnoreCase("entity")){
                        VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player,ChatMessageType.CHAT,"&cNot implemented yet&7!");
                        return false;
                    }
            }

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&edebug block"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&edebug item"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&edebug entity"));

            return false;
        };
    }
}
