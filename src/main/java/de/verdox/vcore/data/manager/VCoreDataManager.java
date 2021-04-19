package de.verdox.vcore.data.manager;


import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.data.events.RedisDataCreationEvent;
import de.verdox.vcore.data.events.RedisDataRemoveEvent;
import de.verdox.vcore.data.messages.RedisObjectHandlerMessage;
import de.verdox.vcore.data.session.DataSession;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.redisson.RedisManager;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;

import java.util.Set;
import java.util.UUID;

public abstract class VCoreDataManager <S extends VCoreData, R extends VCorePlugin<?,?>> {

    protected final RedisManager<VCorePlugin<?,?>> redisManager;
    protected final R plugin;
    private final RTopic objectHandlerTopic;
    private final MessageListener<RedisObjectHandlerMessage<S>> channelListener;

    public VCoreDataManager(R plugin, boolean useRedisCluster, String[] addressArray, DataConnection.MongoDB mongoDB){
        this.plugin = plugin;
        this.redisManager = new RedisManager<>(plugin,useRedisCluster,addressArray,mongoDB);

        objectHandlerTopic = getRedisManager().getRedissonClient().getTopic("VCoreDataManager:ObjectHandler:"+getClass().getName());

        channelListener = (channel, msg) -> {
            if(msg.getType() == msg.INSERT)
                plugin.getEventBus().post(new RedisDataCreationEvent(msg.getDataType(),msg.getUuid()));

            else if(msg.getType() == msg.DELETE)
                plugin.getEventBus().post(new RedisDataRemoveEvent(msg.getDataType(),msg.getUuid()));
        };
        objectHandlerTopic.addListener(RedisObjectHandlerMessage.class,channelListener);
        plugin.consoleMessage("&eCleanup Task started");
        redisManager.getPlugin().getScheduler().runTaskInterval(this::onCleanupInterval,20L*120,20L*1800);
    }

    public void pushCreation(Class<? extends S> type, S dataObject){
        objectHandlerTopic.publish(new RedisObjectHandlerMessage<S>(type)
                .setDelete()
                .setUUID(dataObject.getUUID())
                .create());
    }

    public abstract S load(Class<? extends S> type, UUID uuid);

    public void pushRemoval(Class<? extends S> type, UUID uuid){
        objectHandlerTopic.publish(new RedisObjectHandlerMessage<S>(type)
                .setDelete()
                .setUUID(uuid)
                .create());
    }

    public RedisManager<VCorePlugin<?, ?>> getRedisManager() {
        return redisManager;
    }

    protected abstract void onCleanupInterval();

    protected abstract DataSession<S> createSession(UUID objectUUID);
    protected abstract boolean exist (UUID objectUUID);
    protected abstract DataSession<S> deleteSession (UUID objectUUID);
    public abstract DataSession<S> getSession(UUID objectUUID);

    public abstract S instantiateVCoreData(Class<? extends S> dataClass, UUID objectUUID);

    public R getPlugin() {
        return plugin;
    }

    public abstract Set<S> getAllData(Class<? extends S> dataClass);
}
