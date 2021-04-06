package de.verdox.vcore.redisson;

import java.util.Map;

public interface VCoreRedisData {
    Map<String, Object> dataForRedis();
    void restoreFromRedis(Map<String,Object> dataFromRedis);
}
