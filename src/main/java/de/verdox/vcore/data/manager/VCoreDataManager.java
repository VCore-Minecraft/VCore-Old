package de.verdox.vcore.data.manager;

import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.redisson.events.RedisDataCreationEvent;
import de.verdox.vcore.redisson.events.RedisDataRemoveEvent;
import de.verdox.vcore.redisson.messages.RedisObjectHandlerMessage;
import de.verdox.vcore.data.session.DataSession;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.redisson.RedisManager;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class VCoreDataManager <S extends VCoreData, R extends VCorePlugin<?,?>> {

    protected final RedisManager<VCorePlugin<?,?>> redisManager;
    protected final R plugin;
    private final RTopic objectHandlerTopic;
    private final MessageListener<RedisObjectHandlerMessage<S>> channelListener;

    public VCoreDataManager(@Nonnull R plugin, boolean useRedisCluster, @Nonnull String[] addressArray, String redisPassword, DataConnection.MongoDB mongoDB){
        plugin.consoleMessage("&6Starting &b"+this,true);
        this.plugin = plugin;
        this.redisManager = new RedisManager<>(plugin,useRedisCluster,addressArray,redisPassword,mongoDB);

        objectHandlerTopic = getRedisManager().getRedissonClient().getTopic("VCoreDataManager:ObjectHandler:"+getClass().getName());

        channelListener = (channel, msg) -> {
            if(msg.getType() == msg.INSERT)
                plugin.getEventBus().post(new RedisDataCreationEvent(msg.getDataType(),msg.getUuid()));

            else if(msg.getType() == msg.DELETE)
                plugin.getEventBus().post(new RedisDataRemoveEvent(msg.getDataType(),msg.getUuid()));
        };
        objectHandlerTopic.addListener(RedisObjectHandlerMessage.class,channelListener);
        plugin.consoleMessage("&eCleanup Task started",true);
        redisManager.getPlugin().getScheduler().asyncInterval(this::onCleanupInterval,20L*120,20L*1800);
    }

    public void pushCreation(@Nonnull Class<? extends S> type, @Nonnull S dataObject){
        objectHandlerTopic.publish(new RedisObjectHandlerMessage<S>(type)
                .setDelete()
                .setUUID(dataObject.getUUID())
                .create());
    }

    /**
     *
     * @param type The type you want to load
     * @param uuid The uuid ob the object you want to load
     * @param loadingStrategy The LoadingStrategy you prefer
     * @param callback If you choose LoadingStrategy.LOAD_LOCAL_ELSE_LOAD a callback can be executed
     * @param <T>
     * @return
     */
    public abstract <T extends S> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, @Nullable Consumer<T> callback);
    public final <T extends S> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy){
        return load(type, uuid, loadingStrategy, null);
    }

    public void pushRemoval(@Nonnull Class<? extends S> type, @Nonnull UUID uuid){
        objectHandlerTopic.publish(new RedisObjectHandlerMessage<S>(type)
                .setDelete()
                .setUUID(uuid)
                .create());
    }

    public RedisManager<VCorePlugin<?, ?>> getRedisManager() {
        return redisManager;
    }

    protected abstract void onCleanupInterval();

    protected abstract DataSession<S> createSession(@Nonnull UUID objectUUID);
    protected abstract boolean exist (@Nonnull UUID objectUUID);
    protected abstract DataSession<S> deleteSession (@Nonnull UUID objectUUID);
    public abstract DataSession<S> getSession(@Nonnull UUID objectUUID);
    public abstract void saveAllData();

    public abstract S instantiateVCoreData(@Nonnull Class<? extends S> dataClass, UUID objectUUID);

    public R getPlugin() {
        return plugin;
    }

    public abstract <U extends S> Set<U> getAllData(@Nonnull Class<? extends U> dataClass);
}
