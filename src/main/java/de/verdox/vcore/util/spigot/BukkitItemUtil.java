/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.util.spigot;

import de.verdox.vcore.util.VCoreUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 16.06.2021 00:45
 */
public class BukkitItemUtil {

    /**
     * Applys damage to the tool as if it has broken the given material
     * @param stack ItemStack to apply damage on
     * @param material Material the Item would break
     * @return damage that was dealt
     */
    public int applyDamageNaturally(@Nullable ItemStack stack, @Nonnull Material material){
        if(stack == null)
            return 0;
        ItemMeta meta = stack.getItemMeta();
        if(!(meta instanceof Damageable))
            return 0;
        Damageable damageable = (Damageable) meta;
        int damageDealt = 0;
        if(material.getHardness() == 0)
            return damageDealt;
        else if(!isMaterialTool(stack.getType()))
            return 0;
        // Breaking Blocks with swords applies damage value 2
        else if(stack.getType().name().contains("SWORD"))
            damageDealt = 2;
        else if(stack.getType().equals(Material.SHEARS)
                || stack.getType().equals(Material.FISHING_ROD)
                || stack.getType().equals(Material.FLINT_AND_STEEL))
            damageDealt = 0;
        else
            damageDealt = 1;
        int durabilityLevel = meta.getEnchantLevel(Enchantment.DURABILITY);
        if(durabilityLevel > 0){
            // Chance of item to get damage
            float chanceOfDurabilityLoss = 100 / (float) (durabilityLevel+1);
            if(VCoreUtil.getRandomUtil().randomPercentage() > chanceOfDurabilityLoss)
                damageDealt = 0;
        }

        damageable.setDamage((damageable.getDamage()+damageDealt));
        stack.setItemMeta((ItemMeta) damageable);
        if(damageable.getDamage() <= 0)
            stack.setType(Material.AIR);
        return damageDealt;
    }

    /**
     * Checks if a Material is listed as tool in minecraft client
     * @param material Material to be checked
     * @return true if it is a tool
     */
    public boolean isMaterialTool(@Nonnull Material material){
        if(material.name().contains("SWORD"))
            return true;
        else if(material.name().contains("PICKAXE"))
            return true;
        else if(material.name().contains("AXE"))
            return true;
        else if(material.name().contains("SHOVEL"))
            return true;
        else if(material.name().contains("HOE"))
            return true;
        else if(material.equals(Material.SHEARS))
            return true;
        else if(material.equals(Material.FISHING_ROD))
            return true;
        else return material.equals(Material.FLINT_AND_STEEL);
    }

}
