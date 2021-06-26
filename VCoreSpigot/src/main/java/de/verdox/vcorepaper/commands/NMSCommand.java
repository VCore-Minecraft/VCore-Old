/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.commands;

import de.verdox.vcore.plugin.command.VCoreCommand;
import de.verdox.vcore.plugin.command.callback.CommandCallback;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.VCorePaper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 15:15
 */
public class NMSCommand extends VCoreCommand.VCoreBukkitCommand{
    public NMSCommand(VCorePlugin.Minecraft vCorePlugin, String commandName) {
        super(vCorePlugin, commandName);
        addCommandSuggestions(0, (commandSender, args) -> List.of("entity","world","server"));
        addCommandSuggestions(1,(commandSender, args) -> {
            if(args[0].equalsIgnoreCase("world"))
                return List.of("sendChunks","resetView","sendFakeBiome","sendFakeDimension","sendFakeBorder");
            if(args[0].equalsIgnoreCase("entity"))
                return List.of("fakeNonLiving","sendFakeHologram","sendFakeItem");
            return List.of("");
        });
        addCommandSuggestions(2, (commandSender, args) -> {
            if(args[0].equalsIgnoreCase("world") && args[1].equalsIgnoreCase("sendFakeBiome"))
                return Arrays.stream(Biome.values()).map(Enum::name).collect(Collectors.toList());
            else if(args[0].equalsIgnoreCase("world") && args[1].equalsIgnoreCase("sendFakeDimension"))
                return List.of(World.Environment.NORMAL.name(), World.Environment.NETHER.name(), World.Environment.THE_END.name());
            else if(args[0].equalsIgnoreCase("entity") && args[1].equalsIgnoreCase("fakeNonLiving"))
                return List.of(EntityType.ARMOR_STAND.name());
            return null;
        });
    }

    @Override
    protected CommandCallback<CommandSender> commandCallback() {
        return (sender, args) -> {
            if(!(sender instanceof Player))
                return false;
            Player player = (Player) sender;

            if(args.length >= 1){

                if(args[0].equalsIgnoreCase("world")){
                    if(args.length >= 2){
                        if(args[1].equalsIgnoreCase("sendChunks")){
                            VCorePaper.getInstance().getNmsManager().getNmsWorldHandler().refreshChunks(player);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eRefreshing Chunks&7!"));
                            return true;
                        }
                        else if(args[1].equalsIgnoreCase("resetView")){
                            VCorePaper.getInstance().getNmsManager().getNmsWorldHandler().resetView(player);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eResetting View&7!"));
                            return true;
                        }
                        else if(args[1].equalsIgnoreCase("sendFakeBiome")){
                            if(args.length == 3){
                                try{
                                    Biome biome = Biome.valueOf(args[2]);
                                    VCorePaper.getInstance().getNmsManager().getNmsWorldHandler().sendFakeBiome(player,player.getChunk(),biome);
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eSending Biome&7: &b"+biome+"&7!"));
                                    return true;
                                }
                                catch (IllegalArgumentException e){
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cBiome does not exist&7!"));
                                    return false;
                                }
                            }
                        }
                        else if(args[1].equalsIgnoreCase("sendFakeDimension")){
                            if(args.length == 3){
                                try{
                                    World.Environment environment = World.Environment.valueOf(args[2]);
                                    VCorePaper.getInstance().getNmsManager().getNmsWorldHandler().sendFakeDimension(player,environment);
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eSending Dimension&7: &b"+environment+"&7!"));
                                    return true;
                                }
                                catch (IllegalArgumentException e){
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cEnvironment does not exist&7!"));
                                    return false;
                                }
                            }
                        }
                        else if(args[1].equalsIgnoreCase("sendFakeBorder")){
                            if(args.length == 3){
                                try{
                                    double size = Double.parseDouble(args[2]);
                                    VCorePaper.getInstance().getNmsManager().getNmsWorldHandler().sendFakeWorldBorder(player, player.getLocation(), size);
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eSending World Border with size&7: &b"+size+"&7!"));
                                    if(size < 0){
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cPlease provide a valid size&7!"));
                                        return false;
                                    }
                                    return true;
                                }
                                catch (NumberFormatException e){
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cPlease provide a valid size&7!"));
                                    return false;
                                }
                            }
                        }
                    }
                }
                else if(args[0].equalsIgnoreCase("entity")){
                    if(args.length >= 2){
                        if(args[1].equalsIgnoreCase("fakeNonLiving")){
                            if(args.length == 3){
                                RayTraceResult rayTraceResult = player.rayTraceBlocks(10);
                                if(rayTraceResult == null){
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cLook at a block&7!"));
                                    return false;
                                }
                                Vector hitPosition = rayTraceResult.getHitPosition();
                                try{
                                    EntityType entityType = EntityType.valueOf(args[2]);
                                    VCorePaper.getInstance().getNmsManager().getNMSEntityHandler().sendFakeNonLivingEntity(entityType, new Location(player.getWorld(),hitPosition.getX(),hitPosition.getY(),hitPosition.getZ()),List.of(player));
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eSende Fake Entity&7: &b"+entityType+"&7!"));
                                    return true;
                                }
                                catch (IllegalArgumentException e){
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cUnknown EntityType &b"+args[2]+"&7!"));
                                    return false;
                                }
                            }
                        }
                        else if(args[1].equalsIgnoreCase("sendFakeHologram")){
                            RayTraceResult rayTraceResult = player.rayTraceBlocks(10);
                            if(rayTraceResult == null){
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cLook at a block&7!"));
                                return false;
                            }
                            Vector hitPosition = rayTraceResult.getHitPosition();
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eSending Fake Hologram&7!"));
                            VCorePaper.getInstance().getNmsManager().getNMSEntityHandler().sendArmorStandWithName(ChatColor.translateAlternateColorCodes('&',"&aHallo"),new Location(player.getWorld(),hitPosition.getX(),hitPosition.getY(),hitPosition.getZ()),List.of(player));
                            return true;
                        }
                        else if(args[1].equalsIgnoreCase("sendFakeItem")){
                            RayTraceResult rayTraceResult = player.rayTraceBlocks(10);
                            if(rayTraceResult == null){
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cLook at a block&7!"));
                                return false;
                            }

                            Vector hitPosition = rayTraceResult.getHitPosition();
                            ItemStack itemInHand = player.getInventory().getItemInMainHand();
                            if(itemInHand.getType().isAir()){
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cHold an item in your main hand&7!"));
                                return false;
                            }
                            VCorePaper.getInstance().getNmsManager().getNMSEntityHandler().sendFakeItem(itemInHand,new Location(player.getWorld(),hitPosition.getX(),hitPosition.getY()+1,hitPosition.getZ()), List.of(player));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eSending Fake Item&7!"));
                            return true;
                        }
                    }
                }
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&enms &bworld &asendChunks&7!"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&enms &bworld &asendFakeBiome &8<&eBiome&8>&7!"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&enms &bworld &asendFakeDimension &8<&eDimension&8>&7!"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&enms &bworld &asendFakeBorder &8<&esize&8>&7!"));
            return false;
        };
    }
}
