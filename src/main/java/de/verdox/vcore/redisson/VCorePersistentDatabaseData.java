package de.verdox.vcore.redisson;

import java.util.Map;

public interface VCorePersistentDatabaseData {
    void restoreFromDataBase (Map<String, Object> dataFromDatabase);
}
