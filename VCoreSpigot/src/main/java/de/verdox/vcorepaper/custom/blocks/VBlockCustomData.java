package de.verdox.vcorepaper.custom.blocks;

import de.verdox.vcorepaper.custom.CustomData;

import java.util.List;

public abstract class VBlockCustomData<T> extends CustomData<T> {
    @Override
    public List<String> asLabel(String valueAsString) {
        return null;
    }
}
