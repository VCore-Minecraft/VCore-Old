package de.verdox.vcorepaper.custom.blocks;

import de.verdox.vcore.concurrent.CatchingRunnable;
import de.verdox.vcore.util.keys.ChunkKey;
import de.verdox.vcore.util.keys.LocationKey;
import de.verdox.vcore.util.keys.SplitChunkKey;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.CustomDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.json.simple.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class VBlockManager extends CustomDataManager<BlockState, VBlockCustomData<?>,VBlock> {

    private final ConcurrentHashMap<SplitChunkKey, Set<BlockPersistentData>> cache = new ConcurrentHashMap<>();
    private final Set<ChunkKey> cachedChunkKeys = new HashSet<>();
    private final VBlockFileStorage vBlockFileStorage = new VBlockFileStorage(this);
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public VBlockManager(VCorePaper vCorePaper) {
        super(vCorePaper);
        executor.scheduleAtFixedRate(new CatchingRunnable(() -> {
            long timeStamp = System.currentTimeMillis();
            doTick();
            VCorePaper.getInstance().consoleMessage("&eVBlockManagerTick took&7: &b"+(System.currentTimeMillis()-timeStamp)+"ms&8[&6"+getCacheSize()+"&8]",true);
        }),0,5, TimeUnit.SECONDS);
    }

    private void doTick(){
        for (SplitChunkKey splitChunkKey : cache.keySet()) {
            Set<BlockPersistentData> blockPersistentDataSet = cache.get(splitChunkKey);
            for (BlockPersistentData blockPersistentData : blockPersistentDataSet) {
                if(blockPersistentData == null)
                    continue;
                for (Consumer<VBlock> tickCallback : blockPersistentData.getTickCallbacks()) {
                    tickCallback.accept(blockPersistentData.toVBlock());
                }
            }
            blockPersistentDataSet.removeIf(BlockPersistentData::isEmpty);
        }
    }

    public int getCacheSize(){
        AtomicInteger counter = new AtomicInteger(0);
        cache.forEach((splitChunkKey, blockPersistentData) -> counter.addAndGet(blockPersistentData.size()));
        return counter.get();
    }

    public void VBlockCallback(BlockState blockState, Consumer<VBlock> consumer, boolean createIfNotExist){
        Bukkit.getScheduler().runTaskAsynchronously(getVCorePaper(),() -> {
            BlockPersistentData persistentData;
            if(createIfNotExist)
                persistentData = getOrCreateBlockPersistentData(blockState);
            else
                persistentData = getOrLoadBlockPersistentData(blockState);
            if(persistentData == null)
                return;
            VBlock vBlock = new VBlock(blockState, this, persistentData);
            consumer.accept(vBlock);
        });
    }

    /**
     * If VBlock was not loaded before it will return null;
     * @param blockState
     * @return Returns a vblock that may already be in cache. If not function will return null;
     */
    public VBlock getCachedVBlock(BlockState blockState){
        BlockPersistentData blockPersistentData = getBlockPersistentData(blockState);
        if(blockPersistentData == null)
            return null;
        return new VBlock(blockState, this, blockPersistentData);
    }

    public VBlock getVBlock(BlockState blockState){
        if(Bukkit.isPrimaryThread())
            getVCorePaper().consoleMessage("&4Loading VBlock with main thread&7!",false);
        BlockPersistentData blockPersistentData = getOrLoadBlockPersistentData(blockState);
        if(blockPersistentData == null)
            return null;
        return new VBlock(blockState,this,blockPersistentData);
    }

    public int getCachedChunkSize(){
        return cachedChunkKeys.size();
    }

    public boolean isChunkCached(Chunk chunk){
        return cachedChunkKeys.contains(new ChunkKey(chunk));
    }

    public VBlockFileStorage getVBlockFileStorage() {
        return vBlockFileStorage;
    }

    BlockPersistentData getBlockPersistentData(BlockState blockState){
        SplitChunkKey splitChunkKey = new SplitChunkKey(blockState.getChunk(),blockState.getLocation().getBlockY());
        if(!(cache.containsKey(splitChunkKey))) {
            return null;
        }
        BlockPersistentData blockPersistentData = cache.get(splitChunkKey).parallelStream().filter(foundData -> foundData.getLocationKey().equals(new LocationKey(blockState.getLocation()))).findAny().orElse(null);
        if(blockPersistentData == null)
            return null;
        cachedChunkKeys.add(new ChunkKey(blockState.getChunk()));
        if(!blockPersistentData.validate()) {
            VCorePaper.getInstance().consoleMessage("BlockData was cleared due to wrong blockData stored",true);
            blockPersistentData.getJsonObject().clear();
        }
        return blockPersistentData;
    }

    public Set<BlockPersistentData> getDataOfChunk(Chunk chunk){
        Set<BlockPersistentData> persistentData = new HashSet<>();
        new ChunkKey(chunk).splitChunkKey(chunk.getWorld()).forEach(splitChunkKey -> {
            if(!(cache.containsKey(splitChunkKey)))
                return;
            persistentData.addAll(cache.get(splitChunkKey));
        });
        return persistentData;
    }

    synchronized BlockPersistentData getOrLoadBlockPersistentData(BlockState blockState){
        SplitChunkKey splitChunkKey = new SplitChunkKey(blockState.getChunk(),blockState.getLocation().getBlockY());

        BlockPersistentData blockPersistentData = getBlockPersistentData(blockState);
        if(blockPersistentData != null)
            return blockPersistentData;

        JSONObject jsonObject = vBlockFileStorage.getBlockStateJsonObject(blockState);
        if(jsonObject == null)
            return null;

        if(!cache.containsKey(splitChunkKey))
            cache.put(splitChunkKey,new HashSet<>());
        blockPersistentData = new BlockPersistentData(new LocationKey(blockState.getLocation()),jsonObject,blockState.getLocation());
        blockPersistentData.onDataLoad();

        cache.get(splitChunkKey).add(blockPersistentData);
        return blockPersistentData;
    }


    synchronized BlockPersistentData getOrCreateBlockPersistentData(BlockState blockState){
        SplitChunkKey splitChunkKey = new SplitChunkKey(blockState.getChunk(),blockState.getLocation().getBlockY());

        BlockPersistentData blockPersistentData = getBlockPersistentData(blockState);
        if(blockPersistentData != null)
            return blockPersistentData;

        JSONObject jsonObject = vBlockFileStorage.getOrCreateBlockStateJsonObject(blockState);
        //TODO: Wenn noch kein JSONObjekt abgespeichert wurde, wird jetzt eins erstellt
        if(jsonObject == null)
            jsonObject = new JSONObject();

        if(!cache.containsKey(splitChunkKey))
            cache.put(splitChunkKey,new HashSet<>());
        blockPersistentData = new BlockPersistentData(new LocationKey(blockState.getLocation()),jsonObject,blockState.getLocation());
        blockPersistentData.onDataLoad();

        cache.get(splitChunkKey).add(blockPersistentData);
        return blockPersistentData;
    }

    BlockPersistentData removeAndSaveBlockPersistentData(BlockState blockState){
        BlockPersistentData blockPersistentData = removeBlockPersistentData(blockState);
        if(blockPersistentData == null)
            return null;
        return saveBlockPersistentData(blockPersistentData);
    }

    public BlockPersistentData saveBlockPersistentData(BlockState blockState){
            BlockPersistentData blockPersistentData = getBlockPersistentData(blockState);
            if (blockPersistentData == null)
                return null;
            return saveBlockPersistentData(blockPersistentData);
    }

    private synchronized BlockPersistentData saveBlockPersistentData(BlockPersistentData blockPersistentData){
        File jsonFile = vBlockFileStorage.getOrCreateBlockStateJsonFile(blockPersistentData.getLocation().getBlock().getState());
        if (jsonFile == null)
            throw new NullPointerException("JsonFile is null!");
        if(blockPersistentData.isEmpty() || !blockPersistentData.isSave()) {
            vBlockFileStorage.deleteSaveFile(blockPersistentData.getLocation().getBlock().getState());
            return blockPersistentData;
        }
        vBlockFileStorage.saveJsonObjectToFile(jsonFile,blockPersistentData.getJsonObject());
        return blockPersistentData;
    }

    BlockPersistentData removeBlockPersistentData(BlockState blockState){
        BlockPersistentData blockPersistentData = getBlockPersistentData(blockState);
        VCorePaper.getInstance().consoleMessage("Removing BlockData "+blockPersistentData,false);
        if(blockPersistentData == null)
            return null;
        VCorePaper.getInstance().consoleMessage("1",false);
        SplitChunkKey splitChunkKey = new SplitChunkKey(blockState.getChunk(),blockState.getLocation().getBlockY());

        if(!(cache.containsKey(splitChunkKey)))
            return null;
        VCorePaper.getInstance().consoleMessage("2",false);
        cache.get(splitChunkKey).remove(blockPersistentData);
        VCorePaper.getInstance().consoleMessage("3",false);

        if(cache.get(splitChunkKey).isEmpty()) {
            cache.remove(splitChunkKey);
            cachedChunkKeys.remove(new ChunkKey(blockState.getChunk()));
        }
        VCorePaper.getInstance().consoleMessage("4"+blockPersistentData,false);
        blockPersistentData.onDataUnload();
        return blockPersistentData;
    }

    public boolean isCached(Chunk chunk){
        return cachedChunkKeys.contains(new ChunkKey(chunk));
    }

    @Override
    public VBlock wrap(Class<? extends VBlock> type, BlockState inputObject) {
        if(Bukkit.isPrimaryThread())
            getVCorePaper().consoleMessage("&4Loading / Creating VBlock with main thread&7!",false);
        BlockPersistentData blockPersistentData = getOrCreateBlockPersistentData(inputObject);
        if(blockPersistentData == null)
            throw new NullPointerException("BlockPersistentData could not be created!");
        return new VBlock(inputObject,this,blockPersistentData);
    }

    @Override
    protected VBlockCustomData<?> instantiateCustomData(Class<? extends VBlockCustomData<?>> dataClass) {
        try {
            return dataClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
