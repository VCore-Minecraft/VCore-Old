package de.verdox.vcorepaper.custom.gui;

import de.verdox.vcorepaper.VCorePaper;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public class GUITemplate {

    public static void createConfirmationGUI(VCoreGUI<?> vCoreGUI, Runnable onConfirm){
        new AnvilGUI.Builder()
                .plugin(vCoreGUI.getPlugin())
                .title(ChatColor.translateAlternateColorCodes('&',"&cBestätige mit Ja"))
                .itemLeft(VCorePaper.getInstance().getCustomItemManager().getItemPreset().blackGUIBorder().getDataHolder())
                .onLeftInputClick(player -> vCoreGUI.openInventory())
                .onComplete((player, text) -> {
                    if(text.equalsIgnoreCase("Ja"))
                        onConfirm.run();
                    return AnvilGUI.Response.close();
                })
                .onClose(player -> vCoreGUI.openInventory())
                .open(vCoreGUI.getPlayer());
    }

    public static void createAnvilInputGUI(VCoreGUI<?> vCoreGUI, String title, BiFunction<Player, String, AnvilGUI.Response> completeFunction){
        new AnvilGUI.Builder()
                .plugin(vCoreGUI.getPlugin())
                .title(ChatColor.translateAlternateColorCodes('&',title))
                .itemLeft(VCorePaper.getInstance().getCustomItemManager().getItemPreset().redBackButton().getDataHolder())
                .onLeftInputClick(player -> vCoreGUI.openInventory())
                .onComplete(completeFunction)
                .onClose(player -> vCoreGUI.openInventory())
                .open(vCoreGUI.getPlayer());
    }

}
