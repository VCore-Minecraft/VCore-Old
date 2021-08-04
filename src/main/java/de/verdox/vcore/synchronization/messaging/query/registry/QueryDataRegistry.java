/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.query.registry;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 04.08.2021 21:27
 */
public final class QueryDataRegistry {

    private final Map<Integer, Class<? extends QueryData>> registeredData = new ConcurrentHashMap<>();

    public final void registerQueryData(int id, Class<? extends QueryData> type){
        if(registeredData.containsKey(id))
            throw new IllegalStateException("There is already a type registered with id: "+id);
    }

    public QueryData getQueryData(int id){
        if(!registeredData.containsKey(id))
            return null;
        try {
            return registeredData.get(id).getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("QueryData needs at least an empty Constructor");
        }
    }

    public int getId(QueryData queryData){
        for (Integer integer : registeredData.keySet()) {
            if(queryData.getClass().equals(registeredData.get(integer)))
                return integer;
        }
        throw new IllegalStateException("QueryData "+queryData.getClass()+" has not been registered yet!");
    }

    public Object[] prepareQueryData(QueryData queryData){
        int id = getId(queryData);

        Object[] queryDataArray = queryData.write();
        int newSize = queryDataArray.length+2;
        Object[] preparedData = new Object[newSize];
        // First Object always is the identifier
        preparedData[0] = "VCoreQueryData";
        preparedData[1] = id;
        for(int i = 0; i < queryDataArray.length; i++){
            preparedData[i+2] = queryDataArray[i];
        }
        return preparedData;
    }

    public QueryData readQueryData(Object[] data){
        if(data.length < 2)
            return null;
        if(!(data[0] instanceof String || data[0].equals("VCoreQueryData")))
            return null;
        if(!(data[1] instanceof Integer))
            return null;
        QueryData queryData = getQueryData((Integer) data[1]);
        if(queryData == null)
            return null;
        Object[] queryDataArray = Arrays.copyOfRange(data,2,data.length);
        queryData.read(queryDataArray);
        return queryData;
    }
}
