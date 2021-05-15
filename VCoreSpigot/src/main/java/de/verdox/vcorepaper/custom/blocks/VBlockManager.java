package de.verdox.vcorepaper.custom.blocks;

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
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class VBlockManager extends CustomDataManager<BlockState, VBlockCustomData<?>,VBlock> {

    private final ConcurrentHashMap<SplitChunkKey, Set<BlockPersistentData>> cache = new ConcurrentHashMap<>();
    private final Set<ChunkKey> cachedChunkKeys = new HashSet<>();
    private final VBlockFileStorage vBlockFileStorage = new VBlockFileStorage(this);

    public VBlockManager(VCorePaper vCorePaper) {
        super(vCorePaper);
    }

    public void VBlockCallback(BlockState blockState, Consumer<VBlock> consumer){
        Bukkit.getScheduler().runTaskAsynchronously(getVCorePaper(),() -> {
            BlockPersistentData blockPersistentData = getOrCreateBlockPersistentData(blockState);
            if(blockPersistentData == null)
                throw new NullPointerException("BlockPersistentData could not be created!");
            VBlock vBlock = new VBlock(blockState,this,blockPersistentData);
            consumer.accept(vBlock);
        });
    }

    public VBlockFileStorage getVBlockFileStorage() {
        return vBlockFileStorage;
    }

    public BlockPersistentData getBlockPersistentData(BlockState blockState){
        SplitChunkKey splitChunkKey = new SplitChunkKey(blockState.getChunk(),blockState.getLocation().getBlockY());
        cachedChunkKeys.add(new ChunkKey(blockState.getChunk()));

        if(!(cache.containsKey(splitChunkKey)))
            return null;

        return cache.get(splitChunkKey).parallelStream().filter(blockPersistentData -> blockPersistentData.getLocationKey().equals(new LocationKey(blockState.getLocation()))).findAny().orElse(null);
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

    public synchronized BlockPersistentData getOrCreateBlockPersistentData(BlockState blockState){
        SplitChunkKey splitChunkKey = new SplitChunkKey(blockState.getChunk(),blockState.getLocation().getBlockY());

        BlockPersistentData blockPersistentData = getBlockPersistentData(blockState);
        if(blockPersistentData != null)
            return blockPersistentData;

        JSONObject jsonObject = vBlockFileStorage.getBlockStateJsonObject(blockState);
        //TODO: Wenn noch kein JSONObjekt abgespeichert wurde, wird jetzt eins erstellt
        if(jsonObject == null)
            jsonObject = new JSONObject();

        Set<BlockPersistentData> dataSet = cache.put(splitChunkKey,new HashSet<>());
        blockPersistentData = new BlockPersistentData(new LocationKey(blockState.getLocation()),jsonObject,blockState.getLocation());

        cache.get(splitChunkKey).add(blockPersistentData);
        return blockPersistentData;
    }

    public synchronized BlockPersistentData removeAndSaveBlockPersistentData(BlockState blockState){
        try {
            BlockPersistentData blockPersistentData = removeBlockPersistentData(blockState);
            if(blockPersistentData == null)
                return null;
            File jsonFile = vBlockFileStorage.getBlockStateJsonFile(blockState);
            if(jsonFile == null)
                return null;

            blockPersistentData.getJsonObject().writeJSONString(new FileWriter(jsonFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized BlockPersistentData saveBlockPersistentData(BlockState blockState){
        try {
            BlockPersistentData blockPersistentData = getBlockPersistentData(blockState);
            if (blockPersistentData == null)
                return null;
            File jsonFile = vBlockFileStorage.getBlockStateJsonFile(blockState);
            if (jsonFile == null)
                return null;
            if(!jsonFile.exists())
                jsonFile.createNewFile();
            blockPersistentData.getJsonObject().writeJSONString(new FileWriter(jsonFile));
            return blockPersistentData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BlockPersistentData removeBlockPersistentData(BlockState blockState){
        BlockPersistentData blockPersistentData = getBlockPersistentData(blockState);
        if(blockPersistentData == null)
            return null;

        SplitChunkKey splitChunkKey = new SplitChunkKey(blockState.getChunk(),blockState.getLocation().getBlockY());

        if(!(cache.containsKey(splitChunkKey)))
            return null;
        cache.get(splitChunkKey).remove(blockPersistentData);

        if(cache.get(splitChunkKey).isEmpty())
            cachedChunkKeys.remove(new ChunkKey(blockState.getChunk()));

        return blockPersistentData;
    }

    public boolean isCached(Chunk chunk){
        return cachedChunkKeys.contains(new ChunkKey(chunk));
    }

    @Override
    public VBlock wrap(Class<? extends VBlock> type, BlockState inputObject) {
        return null;
    }

    @Override
    protected VBlockCustomData<?> instantiateCustomData(Class<? extends VBlockCustomData<?>> dataClass) {
        return null;
    }
}
