package de.verdox.vcore.files.config.serialization;

import java.util.Map;

public interface VCoreDeserializer <S extends VCoreSerializable> {
    S deSerialize(Map<String, Object> data);
}
