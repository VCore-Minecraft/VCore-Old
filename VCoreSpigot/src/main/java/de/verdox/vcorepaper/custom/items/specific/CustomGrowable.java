package de.verdox.vcorepaper.custom.items.specific;

import de.verdox.vcore.data.session.PlayerSession;
import de.verdox.vcorepaper.custom.items.VCoreItem;
import org.bukkit.Location;

public interface CustomGrowable {
    void onHarvestCrop(PlayerSession harvestingPlayer, VCoreItem vCoreItem, Location harvestLocation);
    void onPlantCrop(PlayerSession plantingPlayer, VCoreItem vCoreItem, Location plantLocation);
}
