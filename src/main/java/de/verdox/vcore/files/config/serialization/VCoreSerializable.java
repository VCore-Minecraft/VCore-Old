package de.verdox.vcore.files.config.serialization;

import java.util.Map;

public interface VCoreSerializable {
    Map<String, Object> serialize();
}