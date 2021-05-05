package de.verdox.vcorepaper.custom;

import de.verdox.vcorepaper.VCorePaper;

import java.util.HashSet;
import java.util.Set;

public abstract class CustomDataManager<S, R extends CustomDataHolder<S,?>,T extends CustomData<S,?,?,?>> {

    private final VCorePaper vCorePaper;
    protected final Set<Class<? extends T>> customDataCache;

    public CustomDataManager(VCorePaper vCorePaper){
        this.vCorePaper = vCorePaper;
        customDataCache = new HashSet<>();
    }

    public abstract R wrap(Class<? extends R> type, S inputObject);

    public final boolean registerData(Class<? extends T> customDataClass){
        return customDataCache.add(customDataClass);
    }

    public Set<Class<? extends T>> getCustomDataStorage() {
        return customDataCache;
    }

    public VCorePaper getvCorePaper() {
        return vCorePaper;
    }
}
