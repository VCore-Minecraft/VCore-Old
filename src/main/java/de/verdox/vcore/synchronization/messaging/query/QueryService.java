/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.query;

import com.google.common.eventbus.Subscribe;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.messaging.event.MessageEvent;
import de.verdox.vcore.synchronization.messaging.messages.Message;
import de.verdox.vcore.synchronization.messaging.messages.MessageWrapper;
import de.verdox.vcore.synchronization.messaging.query.registry.QueryDataRegistry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 01.08.2021 17:45
 */
public class QueryService {

    private final VCorePlugin<?, ?> plugin;
    private final Map<UUID,Long> pendingQueries = new ConcurrentHashMap<>();
    private final Set<QueryHandler> queryHandlers = new HashSet<>();
    private final QueryDataRegistry queryDataRegistry;

    public QueryService(VCorePlugin<?,?> plugin){
        this.plugin = plugin;
        this.plugin.getServices().eventBus.register(this);
        this.queryDataRegistry = new QueryDataRegistry();
        plugin.getServices().getVCoreScheduler().asyncInterval(() -> {
            pendingQueries.keySet().iterator().forEachRemaining(uuid -> {
                long queryStart = pendingQueries.get(uuid);
                if((System.currentTimeMillis() - queryStart) >= TimeUnit.SECONDS.toMillis(60))
                    pendingQueries.remove(uuid);
            });
        },20L*5,20L*5);
    }

    public void registerHandler(QueryHandler queryHandler){
        queryHandlers.add(queryHandler);
    }

    public void unregisterHandler(QueryHandler queryHandler){
        queryHandlers.remove(queryHandler);
    }

    public UUID sendQuery(Message message){
        UUID queryUUID = UUID.randomUUID();
        Message query = plugin.getServices().getMessagingService().constructMessage()
                .withParameters("VCoreQuery")
                .withData(message.getParameters(), message.dataToSend(), queryUUID).constructMessage();
        pendingQueries.put(queryUUID,System.currentTimeMillis());

        queryHandlers.forEach(queryHandler -> queryHandler.onQuerySend(queryUUID, message.getParameters(), message.dataToSend()));
        plugin.consoleMessage("&eSending Query &8[&b"+queryUUID+"&8] &7| &eParameters &8[&b"+ Arrays.toString(message.getParameters())+"&8] &7| &eQueryData&8[&e"+Arrays.toString(message.dataToSend())+"&8]",true);
        plugin.getServices().getMessagingService().publishMessage(query);
        return queryUUID;
    }

    private void sendResponse(UUID queryUUID, String[] arguments, Object[] queryData, Object[] responseData){
        Message response = plugin.getServices().getMessagingService().constructMessage()
                .withParameters(plugin.getPluginName()+"QueryResponse")
                .withData(arguments, queryData, responseData, queryUUID).constructMessage();
        plugin.consoleMessage("&eQuery &8[&b"+queryUUID+"&8] &7| &eParameters &8[&b"+ Arrays.toString(response.getParameters())+"&8] &7| &eQueryData&8[&e"+Arrays.toString(response.dataToSend())+"&8]",true);
        plugin.getServices().getMessagingService().publishMessage(response);
    }

    @Subscribe
    public void message(MessageEvent messageEvent){
        MessageWrapper messageWrapper = new MessageWrapper(messageEvent.getMessage());

        if(messageWrapper.parameterContains("VCoreQuery")){
            String[] arguments = messageEvent.getMessage().getData(0,String[].class);
            Object[] queryData = messageEvent.getMessage().getData(1,Object[].class);
            UUID queryUUID = messageEvent.getMessage().getData(2,UUID.class);

            plugin.consoleMessage("&eReceived Query &8[&b"+queryUUID+"&8] &7| &eParameters &8[&b"+ Arrays.toString(arguments)+"&8] &7| &eQueryData&8[&e"+Arrays.toString(queryData)+"&8]",true);
            queryHandlers.forEach(queryHandler -> {
                Object[] response = queryHandler.respondToQuery(queryUUID,arguments,queryData);
                if(response == null)
                    return;
                sendResponse(queryUUID, arguments, queryData, response);
            });
        }
        else if(messageWrapper.parameterContains(plugin.getPluginName()+"QueryResponse")){
            String[] arguments = messageEvent.getMessage().getData(0,String[].class);
            Object[] queryData = messageEvent.getMessage().getData(1,Object[].class);
            Object[] responseData = messageEvent.getMessage().getData(2,Object[].class);
            UUID queryUUID = messageEvent.getMessage().getData(3,UUID.class);
            if(!pendingQueries.containsKey(queryUUID))
                return;
            plugin.consoleMessage("&eReceived Query Response &8[&b"+queryUUID+"&8] &7| &eParameters &8[&b"+ Arrays.toString(arguments)+"&8] &7| &eQueryData&8[&e"+Arrays.toString(queryData)+"&8]",true);
            queryHandlers.forEach(queryHandler -> {
                queryHandler.onResponse(queryUUID, arguments, queryData, responseData);
            });
            pendingQueries.remove(queryUUID);
        }
    }

}
