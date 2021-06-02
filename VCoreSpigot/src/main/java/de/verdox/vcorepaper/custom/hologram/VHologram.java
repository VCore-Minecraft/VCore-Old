package de.verdox.vcorepaper.custom.hologram;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import de.verdox.vcore.plugin.bukkit.BukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import java.util.function.Consumer;

public class VHologram implements HologramInterface {
    private final BukkitPlugin bukkitPlugin;
    private final HologramContent hologramContent;

    private Location location;
    private boolean global;
    private Hologram hologram;
    private Consumer<HologramContent> consumer;
    private BukkitTask hologramUpdater;
    private int lineCounter = 0;
    private long lifeTimeInTicks = -1;
    private long updateInterval = -1;
    private boolean updating = false;

    public VHologram(BukkitPlugin bukkitPlugin, Location location, boolean global){
        this.bukkitPlugin = bukkitPlugin;
        this.location = location;
        this.global = global;
        this.hologram = HologramsAPI.createHologram(bukkitPlugin,location);
        if(!global)
            hologram.getVisibilityManager().setVisibleByDefault(false);
        hologramContent = new HologramContent(this);
    }

    @Override
    public HologramInterface withUpdater(Consumer<HologramContent> consumer, long intervalInTicks){
        this.consumer = consumer;
        this.updateInterval = intervalInTicks;
        return this;
    }

    /**
     * Changes the lifetime of the hologram
     * @param lifetime If lifetime is < 0 hologram will have no lifetime but exist until the server shuts down
     * @return
     */

    @Override
    public HologramInterface withLifetime(long lifetime) {
        this.lifeTimeInTicks = lifetime;
        return this;
    }

    @Override
    public void spawnHologram() {
        if(!Bukkit.isPrimaryThread())
            throw new IllegalStateException("Holograms may only be generated with Bukkit MainThread");

        hologram = HologramsAPI.createHologram(bukkitPlugin,location);
        if(!global)
            hologramContent.getVisiblePlayers().forEach(player -> hologram.getVisibilityManager().showTo(player));
        Bukkit.getScheduler().runTaskAsynchronously(bukkitPlugin, this::updateHologram);

        if(consumer == null || updateInterval <= 0)
            return;
        this.hologramUpdater = Bukkit.getScheduler().runTaskTimerAsynchronously(bukkitPlugin,() -> {
            if(isDeleted())
                hologramUpdater.cancel();
            hologramContent.clear();
            consumer.accept(hologramContent);
            updateHologram();
        },0,updateInterval);
        Bukkit.getScheduler().runTaskLater(bukkitPlugin, this::delete,lifeTimeInTicks);
    }

    private void updateHologram(){
        if(hologram == null)
            return;
        if(hologram.isDeleted())
            return;
        if(hologramContent == null)
            return;
        if(hologramContent.size() == 0)
            return;
        hologramContent.getHologramLines().forEach((integer, line) -> {
            if(line == null)
                return;
            if(integer >= hologram.size()){
                insertLineToHologram(line);
                return;
            }
            HologramLine hologramLine = hologram.getLine(integer);
            if(hologramLine instanceof ItemLine && line instanceof HologramContent.ItemHologramLine){
                ItemLine itemLine = (ItemLine) hologramLine;
                ItemStack stackInHologram = itemLine.getItemStack();
                // If the ItemStack in the Hologram is already set don't change line
                if(stackInHologram.equals(((HologramContent.ItemHologramLine) line).getStack()))
                    return;
            }
            else if(hologramLine instanceof TextLine && line instanceof HologramContent.TextHologramLine){
                TextLine textLine = (TextLine) hologramLine;
                String text = textLine.getText();
                if(text.equals(((HologramContent.TextHologramLine) line).getText()))
                    return;
            }
            insertLineToHologram(line);
        });
    }

    private void insertLineToHologram(HologramContent.HologramLine hologramLine){
        if(hologram == null)
            return;
        while(updating){}
        Bukkit.getScheduler().runTask(bukkitPlugin,() -> {
            updating = true;
            if(hologramLine.getRow() < hologram.size())
                hologram.removeLine(hologramLine.getRow());
            if(hologramLine instanceof HologramContent.ItemHologramLine)
                hologram.insertItemLine(hologramLine.getRow(), ((HologramContent.ItemHologramLine) hologramLine).getStack());
            else if(hologramLine instanceof HologramContent.TextHologramLine)
                hologram.insertTextLine(hologramLine.getRow(), ((HologramContent.TextHologramLine) hologramLine).getText());
            updating = false;
        });
    }

    @Override
    public HologramInterface addTextLine(String line) {
        hologramContent.setTextLine(lineCounter++,line);
        return this;
    }

    @Override
    public HologramInterface addItemLine(ItemStack stack) {
        hologramContent.setItemLine(lineCounter++,stack);
        return this;
    }

    @Override
    public HologramInterface clearLines() {
        hologramContent.clear();
        return this;
    }

    @Override
    public int size() {
        return hologramContent.size();
    }

    @Override
    public void delete() {
        if(hologram == null)
            return;
        if(hologram.isDeleted())
            return;
        hologram.delete();
    }

    @Override
    public boolean isDeleted() {
        if(hologram == null)
            return false;
        return hologram.isDeleted();
    }

    @Override
    public HologramInterface showTo(Player player) {
        hologramContent.showToPlayer(player);
        return this;
    }

    @Override
    public HologramInterface hideFrom(Player player) {
        hologramContent.hideFromPlayer(player);
        return this;
    }

    @Override
    public Location getLocation() {
        return hologram.getLocation();
    }
}
