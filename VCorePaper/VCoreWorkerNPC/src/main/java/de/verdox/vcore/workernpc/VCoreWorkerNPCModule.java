package de.verdox.vcore.workernpc;


import de.verdox.vcore.nbt.VCoreNBTModule;
import de.verdox.vcore.nbt.entities.CustomEntityManager;
import de.verdox.vcore.nms.VCoreNMSModule;
import de.verdox.vcore.workernpc.listener.WorkerNPCListener;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.module.VCorePaperModule;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 01.02.2022 20:04
 */
public class VCoreWorkerNPCModule extends VCorePaperModule {

    private CustomEntityManager customEntityManager;
    private VCoreNMSModule vCoreNMSModule;
    private ProfessionRegistry professionRegistry;
    private WorkerNPCListener workerNPCListener;

    @Override
    public void enableModule(VCorePaper coreInstance) {
        this.customEntityManager = coreInstance.getModuleLoader().getModule(VCoreNBTModule.class).getCustomEntityManager();
        this.vCoreNMSModule = coreInstance.getModuleLoader().getModule(VCoreNMSModule.class);
        this.professionRegistry = new ProfessionRegistry(customEntityManager);
        this.workerNPCListener = new WorkerNPCListener(coreInstance, this);
    }

    @Override
    public void disableModule() {

    }

    public WorkerNPC spawnNPC(@NotNull Location location) {
        Villager villager = location.getWorld().spawn(location, Villager.class, CreatureSpawnEvent.SpawnReason.CUSTOM);
        WorkerNPC workerNPC = getCustomEntityManager().wrap(WorkerNPC.class, villager);
        workerNPC.initialize();
        workerNPC.setModule(this);
        return workerNPC;
    }

    public WorkerNPC toWorkerNPC(@NotNull LivingEntity livingEntity) {
        WorkerNPC workerNPC = getCustomEntityManager().wrap(WorkerNPC.class, livingEntity);
        workerNPC.setModule(this);
        return workerNPC;
    }

    public boolean isWorkerNPC(@NotNull LivingEntity livingEntity){
        WorkerNPC workerNPC = getCustomEntityManager().wrap(WorkerNPC.class, livingEntity);
        workerNPC.setModule(this);
        return workerNPC.verify();
    }

    public CustomEntityManager getCustomEntityManager() {
        return customEntityManager;
    }

    public ProfessionRegistry getProfessionRegistry() {
        return professionRegistry;
    }

    public VCoreNMSModule getVCoreNMSModule() {
        return vCoreNMSModule;
    }
}
