/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.instructions;

import com.google.common.eventbus.Subscribe;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.messaging.event.MessageEvent;
import de.verdox.vcore.synchronization.messaging.instructions.annotations.InstructionInfo;
import de.verdox.vcore.synchronization.messaging.messages.Message;
import de.verdox.vcore.synchronization.messaging.messages.MessageWrapper;
import org.checkerframework.checker.index.qual.NonNegative;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 21:42
 */
public class InstructionService {
    private final VCorePlugin<?, ?> plugin;
    private final Map<Integer, Class<? extends MessagingInstruction>> instructionTypes = new ConcurrentHashMap<>();
    private final Map<UUID, MessagingInstruction> pendingInstructions = new ConcurrentHashMap<>();

    public InstructionService(VCorePlugin<?, ?> plugin){
        this.plugin = plugin;
        this.plugin.getServices().eventBus.register(this);

        plugin.getServices().getVCoreScheduler().asyncSchedule(() -> {
            for (UUID uuid : pendingInstructions.keySet()) {
                MessagingInstruction instruction = pendingInstructions.get(uuid);
                if((System.currentTimeMillis() - instruction.getCreationTimeStamp()) >= TimeUnit.SECONDS.toMillis(60))
                    pendingInstructions.remove(uuid);
            }
        },10, TimeUnit.SECONDS);
    }

    private UUID getSessionUUID(){
        return plugin.getServices().getMessagingService().getSessionUUID();
    }

    public void registerInstructionType(@NonNegative int id, Class<? extends MessagingInstruction> type){
        if(instructionTypes.containsKey(id))
            throw new IllegalStateException("Id already registered: "+id);
        instructionTypes.put(id,type);
    }

    public void sendInstruction(@Nonnull MessagingInstruction messagingInstruction){
        InstructionInfo instructionInfo = getInstructionInfo(messagingInstruction.getClass());
        if(messagingInstruction.getData() == null)
            throw new IllegalStateException("You can't send empty instructions");
        int instructionID = getID(messagingInstruction.getClass());
        if(instructionID == -1)
            throw new IllegalStateException("Sending an Instruction that has not been registered: "+messagingInstruction.getClass().getSimpleName());
        messagingInstruction.setPlugin(plugin);
        UUID uuid = messagingInstruction.getUuid();
        Message instructionMessage = plugin.getServices().getMessagingService()
                .constructMessage()
                .withParameters("VCoreInstruction")
                .withData(getSessionUUID(),instructionID,messagingInstruction.getUuid(),messagingInstruction.getParameters(), messagingInstruction.getData())
                .constructMessage();
        if(!messagingInstruction.onSend(messagingInstruction.getData())) {
            plugin.consoleMessage("&cCancelled Instruction &8[&b"+messagingInstruction.getUuid()+"&8]" ,true);
            return;
        }
        plugin.consoleMessage("&eSending Instruction &8[&b"+messagingInstruction.getUuid()+"&8] &7| &eParameters &8[&b"+ Arrays.toString(instructionMessage.getParameters())+"&8] &7| &eInstructionData&8[&e"+Arrays.toString(instructionMessage.dataToSend())+"&8]",true);
        plugin.getServices().getMessagingService().publishMessage(instructionMessage);
        if(instructionInfo.awaitsResponse())
            pendingInstructions.put(uuid,messagingInstruction);
    }

    private void sendResponse(int instructionID, UUID instructionUUID, String[] arguments, Object[] instructionData, Object[] responseData){
        Message response = plugin.getServices().getMessagingService().constructMessage()
                .withParameters("VCoreInstructionResponse")
                .withData(getSessionUUID(),instructionID, instructionUUID, arguments, instructionData, responseData).constructMessage();
        plugin.consoleMessage("&eSending Instruction Response &8[&b"+instructionUUID+"&8] &7| &eParameters &8[&b"+ Arrays.toString(response.getParameters())+"&8] &7| &eInstructionData&8[&e"+Arrays.toString(response.dataToSend())+"&8]",true);
        plugin.getServices().getMessagingService().publishMessage(response);
    }

    private int getID(Class<? extends MessagingInstruction> type){
        for (Integer integer : instructionTypes.keySet()) {
            Class<? extends MessagingInstruction> foundType = instructionTypes.get(integer);
            if(type.equals(foundType))
                return integer;
        }
        return -1;
    }

    private MessagingInstruction instantiateInstruction(Class<? extends MessagingInstruction> type, UUID instructionUUID){
        try {
            MessagingInstruction instruction = type.getConstructor(UUID.class).newInstance(instructionUUID);
            instruction.setPlugin(plugin);
            return instruction;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(type.getSimpleName()+" needs a constructor (UUID)");
        }
    }

    //TODO: Satt superclass maybe iwann anders lösen
    private InstructionInfo getInstructionInfo(@Nonnull Class<? extends MessagingInstruction> type){
        InstructionInfo instructionInfo = type.getSuperclass().getAnnotation(InstructionInfo.class);
        if(instructionInfo == null)
            throw new IllegalStateException("Class "+type.getName()+" is missing InstructionInfo Annotation");
        return instructionInfo;
    }

    @Subscribe
    public void onMessage(MessageEvent messageEvent){
        MessageWrapper messageWrapper = new MessageWrapper(messageEvent.getMessage());

        if(messageWrapper.parameterContains("VCoreInstruction") || messageWrapper.parameterContains("VCoreInstructionResponse")){
            UUID senderUUID = messageEvent.getMessage().getData(0,UUID.class);
            int instructionID = messageEvent.getMessage().getData(1,Integer.class);
            UUID instructionUUID = messageEvent.getMessage().getData(2,UUID.class);
            String[] arguments = messageEvent.getMessage().getData(3,String[].class);
            Object[] instructionData = messageEvent.getMessage().getData(4,Object[].class);

            // Do not answer your own Instructions
            if(messageWrapper.parameterContains("VCoreInstruction") && senderUUID.equals(getSessionUUID()))
                return;

            Class<? extends MessagingInstruction> type = instructionTypes.get(instructionID);
            if(messageWrapper.parameterContains("VCoreInstruction")){
                InstructionInfo instructionInfo = getInstructionInfo(type);
                MessagingInstruction responseInstruction = instantiateInstruction(type,instructionUUID);
                plugin.consoleMessage("&eReceived Instruction &8[&b"+instructionUUID+"&8] &7| &eParameters &8[&b"+ Arrays.toString(arguments)+"&8] &7| &eInstructionData&8[&e"+Arrays.toString(instructionData)+"&8]",true);
                if(!(responseInstruction instanceof InstructionResponder))
                    return;
                InstructionResponder instructionResponder = (InstructionResponder) responseInstruction;
                Object[] responseData = instructionResponder.respondToInstruction(instructionData);
                if(responseData == null || responseData.length == 0)
                    return;
                if(instructionInfo.awaitsResponse())
                    sendResponse(instructionID,instructionUUID,arguments,instructionData,responseData);
            }
            else if(messageWrapper.parameterContains("VCoreInstructionResponse")){
                Object[] responseData = messageEvent.getMessage().getData(5,Object[].class);
                plugin.consoleMessage("&eReceived Instruction Response &8[&b"+instructionUUID+"&8] &7| &eParameters &8[&b"+ Arrays.toString(arguments)+"&8] &7| &eInstructionData&8[&e"+Arrays.toString(instructionData)+"&8]",true);
                if(!pendingInstructions.containsKey(instructionUUID))
                    return;
                MessagingInstruction messagingInstruction = pendingInstructions.get(instructionUUID);
                if(!(messagingInstruction instanceof ResponseProcessor<?>))
                    return;
                ResponseProcessor<?> responseProcessor = (ResponseProcessor<?>) messagingInstruction;
                responseProcessor.onResponse((CompletableFuture) responseProcessor.getFuture(),instructionData,responseData);
                pendingInstructions.remove(instructionUUID);
            }
        }
    }
}
