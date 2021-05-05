package de.verdox.vcorepaper.custom.gui;

import de.tr7zw.changeme.nbtapi.NBTItem;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.VCoreScheduler;
import de.verdox.vcore.plugin.bukkit.BukkitPlugin;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class VCoreGUI<T> {

    //TODO: Pagination System hinzufügen?
    //TODO: .pagniation(PaginationItemCreator) // -> .setNextPageItem .setPreviousPageItem (Eventuell bei Content Page integer hinzufügen?)

    private final BukkitPlugin plugin;
    private final Player player;

    private Map<ItemStack ,InventoryContent> itemCache;
    private final Consumer<Player> closeListener;
    private final BiFunction<ItemStack, T, Response> onItemClick;
    private final BiFunction<Player, List<ItemStack>, Response> completeFunction;
    private final boolean updater;
    private final Consumer<ContentBuilder<T>> consumer;
    private boolean open;
    private final boolean preventClose;

    private final VCoreGUIListener listener;

    private final String inventoryTitle;
    private final int size;
    private final InventoryType inventoryType;
    private Inventory inventory;

    private BukkitTask guiUpdater;

    public VCoreGUI(BukkitPlugin plugin, Player player, String inventoryTitle, int size, InventoryType inventoryType, boolean preventClose, Map<ItemStack, InventoryContent> itemCache, Consumer<Player> closeListener, BiFunction<ItemStack, T, Response> onItemClick, BiFunction<Player, List<ItemStack>, Response> completeFunction, boolean updater, Consumer<ContentBuilder<T>> consumer){
        this.completeFunction = completeFunction;
        this.updater = updater;
        this.consumer = consumer;
        this.listener = new VCoreGUIListener();
        this.plugin = plugin;
        this.player = player;
        this.inventoryTitle = inventoryTitle;
        this.size = size;
        this.inventoryType = inventoryType;
        this.preventClose = preventClose;
        this.itemCache = itemCache;
        this.closeListener = closeListener;
        this.onItemClick = onItemClick;

        openInventory();
    }

    public Player getPlayer() {
        return player;
    }

    public void openInventory(){
        Bukkit.getPluginManager().registerEvents(this.listener,this.plugin);
        if(this.size == 0) {
            if(inventoryType == null)
                throw new IllegalArgumentException("Please specify either an InventoryType or an InventorySize");
            this.inventory = Bukkit.createInventory(player,inventoryType,inventoryTitle);
        }
        else {
            this.inventory = Bukkit.createInventory(player,size,inventoryTitle);
        }

        //TODO: Inventory Items setzen

        itemCache.forEach((stack, inventoryContent) -> {
            if(inventory.getSize() <= inventoryContent.slot)
                return;
            if(inventoryContent.slot < 0)
                return;
            inventory.setItem(inventoryContent.slot,stack);
        });

        this.open = true;
        Bukkit.getScheduler().runTask(plugin,() -> player.openInventory(inventory));

        if(updater){
            this.guiUpdater = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,() -> {

                if(inventory.getViewers().size() <= 0)
                    guiUpdater.cancel();

                ContentBuilder<T> contentBuilder = new ContentBuilder<T>();
                consumer.accept(contentBuilder);

                // Removing every item that was changed
                this.itemCache.forEach((stack, inventoryContent) -> {
                    if(contentBuilder.getItemCache().containsKey(stack))
                        return;
                    itemCache.remove(stack);
                    inventory.remove(stack);
                });

                contentBuilder.getItemCache().forEach((stack, inventoryContent) -> {

                    // Items that are the same won't be replaced
                    if(itemCache.containsKey(stack)){
                        if(itemCache.get(stack).equals(inventoryContent))
                            return;
                    }
                    if(inventory.getSize() <= inventoryContent.slot)
                        return;
                    if(inventoryContent.slot < 0)
                        return;
                    itemCache.put(stack,inventoryContent);
                    inventory.setItem(inventoryContent.slot,stack);
                });
            },20L,20L);
        }
    }

    public BukkitPlugin getPlugin() {
        return plugin;
    }

    public void closeInventory(){

        if(open){
            open = false;
            player.closeInventory();
            HandlerList.unregisterAll(this.listener);
            if(this.completeFunction != null)
                this.completeFunction.apply(player, Arrays.asList(inventory.getContents()));
            if(this.closeListener != null)
                closeListener.accept(player);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public static class Builder<T>{
        private Consumer<Player> closeListener;
        private boolean preventClose;
        private Map<ItemStack, InventoryContent> itemCache;
        private BiFunction<ItemStack, T, Response> onItemClick;
        private BiFunction<Player, List<ItemStack>, Response> completeFunction;
        private BukkitPlugin plugin;
        private String title = "VCore GUI";
        private Consumer<ContentBuilder<T>> consumer;
        private int size = 0;
        private InventoryType inventoryType;
        private boolean update;

        public Builder<T> update(){
            this.update = true;
            return this;
        }

        public Builder<T> preventClose(){
            this.preventClose = true;
            return this;
        }

        public Builder<T> size(int size){
            this.size = size;
            return this;
        }

        public Builder<T> onClose(Consumer<Player> closeListener){
            Validate.notNull(closeListener,"closeListener cannot be null");
            this.closeListener = closeListener;
            return this;
        }

        public Builder<T> content(Consumer<ContentBuilder<T>> consumer) {
            Validate.notNull(consumer,"supplier cannot be null");
            ContentBuilder<T> contentBuilder = new ContentBuilder<>();
            consumer.accept(contentBuilder);
            this.itemCache = contentBuilder.getItemCache();
            this.consumer = consumer;
            return this;
        }

        public Builder<T> onItemClick(BiFunction<ItemStack, T, Response> onItemClick){
            Validate.notNull(onItemClick,"biFunction cannot be null");
            this.onItemClick = onItemClick;
            return this;
        }

        public Builder<T> plugin(BukkitPlugin plugin) {
            Validate.notNull(plugin, "Plugin cannot be null");
            this.plugin = plugin;
            return this;
        }

        public Builder<T> title(String title) {
            Validate.notNull(title, "title cannot be null");
            this.title = title;
            return this;
        }

        public Builder<T> type(InventoryType inventoryType) {
            Validate.notNull(inventoryType, "inventoryType cannot be null");
            this.inventoryType = inventoryType;
            return this;
        }

        public Builder<T> completeFunction(BiFunction<Player, List<ItemStack>, Response> completeFunction){
            Validate.notNull(completeFunction, "completeFunction cannot be null");
            this.completeFunction = completeFunction;
            return this;
        }

        public VCoreGUI<T> open(Player player){
            Validate.notNull(this.plugin, "Plugin cannot be null");
            Validate.notNull(player, "Player cannot be null");
            if(update)
                Validate.notNull(consumer, "Content must be set!");
            if(inventoryType == null && size <= 0)
                size = 9;
            return new VCoreGUI<T>(plugin,player,title,size,inventoryType,preventClose,itemCache,closeListener,onItemClick, completeFunction,update, consumer);
        }

    }

    public static class ContentBuilder<T>{
        private Map<ItemStack, InventoryContent> itemCache = new ConcurrentHashMap<>();

        public ContentBuilder addContent(int slot, ItemStack stack, T object){
            Validate.notNull(stack, "Stack cannot be null");
            Validate.notNull(object, "Object cannot be null");
            NBTItem nbtItem = new NBTItem(stack,true);
            nbtItem.setInteger("vcore_gui_slot",slot);
            itemCache.put(stack, new InventoryContent(stack,slot,object));
            return this;
        }

        Map<ItemStack, InventoryContent> getItemCache() {
            return itemCache;
        }
    }

    public static class Response {
        private final static String CLOSE = "close";
        private final static String NOTHING = "nothing";
        private String code;

        private Response(String code){
            this.code = code;
        }

        public static Response close() {return new Response(CLOSE);}

        public static Response nothing() {return new Response(NOTHING);}
    }

    public static class InventoryContent{
        private ItemStack stack;
        private int slot;
        private Object object;

        private InventoryContent(ItemStack stack, int slot, Object object){
            this.stack = stack;
            this.slot = slot;
            this.object = object;
        }

        public static InventoryContent createItem(Object object, ItemStack stack, int slot){
            return new InventoryContent(stack,slot,object);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InventoryContent)) return false;
            InventoryContent content = (InventoryContent) o;
            return slot == content.slot &&
                    stack.equals(content.stack) &&
                    object.equals(content.object);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stack, slot, object);
        }
    }

    private class VCoreGUIListener implements Listener{

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!e.getInventory().equals(VCoreGUI.this.inventory))
                return;
            e.setCancelled(true);
            Player clicker = (Player) e.getWhoClicked();
            ItemStack clickedItem = e.getCurrentItem();
            if(clickedItem == null || clickedItem.getType().equals(Material.AIR))
                return;
            InventoryContent content = VCoreGUI.this.itemCache.get(clickedItem);
            if(content == null)
                return;
            if(VCoreGUI.this.onItemClick == null)
                return;
            Response response = VCoreGUI.this.onItemClick.apply(clickedItem, (T) content.object);
            if(response.code.equals(Response.CLOSE))
                VCoreGUI.this.closeInventory();
        }

        @EventHandler
        public void onInventoryDrag(InventoryDragEvent e){
            if(e.getInventory().equals(VCoreGUI.this.inventory))
                e.setCancelled(true);
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(VCoreGUI.this.open && e.getInventory().equals(VCoreGUI.this.inventory)){
                if(VCoreGUI.this.preventClose)
                    Bukkit.getScheduler().runTask(VCoreGUI.this.getPlugin().getPlugin(), VCoreGUI.this::openInventory);
                else
                    if(VCoreGUI.this.closeListener != null)
                        VCoreGUI.this.closeListener.accept((Player) e.getPlayer());
            }
        }

    }
}
