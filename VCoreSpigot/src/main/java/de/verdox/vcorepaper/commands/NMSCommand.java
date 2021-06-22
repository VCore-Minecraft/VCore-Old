/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.commands;

import de.verdox.vcore.command.VCoreCommand;
import de.verdox.vcore.command.callback.CommandCallback;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.VCorePaper;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            return List.of("");
        });
        addCommandSuggestions(2, (commandSender, args) -> {
            if(args[0].equalsIgnoreCase("world") && args[1].equalsIgnoreCase("sendFakeBiome"))
                return Arrays.stream(Biome.values()).map(Enum::name).collect(Collectors.toList());
            else if(args[0].equalsIgnoreCase("world") && args[1].equalsIgnoreCase("sendFakeDimension"))
                return List.of(World.Environment.NORMAL.name(), World.Environment.NETHER.name(), World.Environment.THE_END.name());
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
                                    if(size < 0){
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cPlease provide a valid size&7!"));
                                        return false;
                                    }
                                }
                                catch (NumberFormatException e){
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cPlease provide a valid size&7!"));
                                    return false;
                                }
                            }
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
