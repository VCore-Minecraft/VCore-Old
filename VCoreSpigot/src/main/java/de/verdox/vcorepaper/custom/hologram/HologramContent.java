package de.verdox.vcorepaper.custom.hologram;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HologramContent {

    private final VHologram vHologram;
    private final Map<Integer, HologramLine> hologramLines = new ConcurrentHashMap<>();
    private final Set<Player> visibleTo = new HashSet<>();

    HologramContent(VHologram vHologram){
        this.vHologram = vHologram;
    }

    public void setItemLine(int row, ItemStack stack){
        if(row < 0)
            throw new IllegalArgumentException("row must be a positive number");
        if(stack == null)
            throw new NullPointerException("stack can't be null!");
        hologramLines.put(row,new ItemHologramLine(row,stack));
    }

    void showToPlayer(Player player){
        visibleTo.add(player);
    }

    void hideFromPlayer(Player player){
        visibleTo.remove(player);
    }

    Set<Player> getVisiblePlayers() {
        return visibleTo;
    }

    public void setTextLine(int row, String text){
        if(row < 0)
            throw new IllegalArgumentException("row must be a positive number");
        if(text == null)
            throw new NullPointerException("text can't be null!");
        hologramLines.put(row,new TextHologramLine(row,text));
    }

    public void clear(){
        this.hologramLines.clear();
    }

    Map<Integer, HologramLine> getHologramLines() {
        return hologramLines;
    }

    public int size(){
        return hologramLines.size();
    }

    public abstract static class HologramLine{

        private int row;

        HologramLine(int row){
            this.row = row;
        }
        public int getRow() {
            return row;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof HologramLine)) return false;
            HologramLine that = (HologramLine) o;
            return getRow() == that.getRow();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getRow());
        }
    }

    public static class ItemHologramLine extends HologramLine{

        private ItemStack stack;

        ItemHologramLine(int row, ItemStack stack) {
            super(row);
            this.stack = stack;
        }

        public ItemStack getStack() {
            return stack;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ItemHologramLine)) return false;
            ItemHologramLine that = (ItemHologramLine) o;
            return Objects.equals(getStack(), that.getStack());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getStack());
        }
    }

    public static class TextHologramLine extends HologramLine{

        private String text;

        TextHologramLine(int row, String text) {
            super(row);
            this.text = text;
        }

        public String getText() {
            return ChatColor.translateAlternateColorCodes('&',text);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TextHologramLine)) return false;
            TextHologramLine that = (TextHologramLine) o;
            return Objects.equals(getText(), that.getText());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getText());
        }
    }
}
