/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.instructions;

import java.util.concurrent.CompletableFuture;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 21:37
 */
public interface ResponseProcessor<T> {
    void onResponse(CompletableFuture<T> future, Object[] queryData, Object[] responseData);
    CompletableFuture<T> getFuture();
}
