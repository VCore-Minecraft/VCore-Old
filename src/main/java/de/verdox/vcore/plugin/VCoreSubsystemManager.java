package de.verdox.vcore.plugin;

import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.data.datatypes.ServerData;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcore.subsystem.exceptions.SubsystemDeactivatedException;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VCoreSubsystemManager<T extends VCorePlugin<?,R>, R extends VCoreSubsystem<T>>{

    private T plugin;
    private final List<R> subSystems = new ArrayList<>();
    private final List<R> activatedSubSystems = new ArrayList<>();

    private Set<Class<? extends VCoreData>> registeredDataClasses;
    private Set<Class<? extends VCoreData>> activeDataClasses;

    private Set<Class<? extends PlayerData>> registeredPlayerDataClasses;
    private Set<Class<? extends PlayerData>> activePlayerDataClasses;

    private Set<Class<? extends ServerData>> registeredServerDataClasses;
    private Set<Class<? extends ServerData>> activeServerDataClasses;

    VCoreSubsystemManager(T plugin){
        this.plugin = plugin;
    }

    void enable(){
        plugin.consoleMessage("&eStarting Subsystem Manager&7...");
        List<R> providedSubsystems = plugin.provideSubsystems();
        if(providedSubsystems != null) {
            subSystems.addAll(providedSubsystems);
            subSystems.stream()
                    .filter(VCoreSubsystem::isActivated)
                    .forEach(r -> {
                        try {
                            r.onSubsystemEnable();
                            activatedSubSystems.add(r);
                            plugin.consoleMessage("&eActivated Subsystem&7: &b" + r);
                        } catch (SubsystemDeactivatedException e) {
                            e.printStackTrace();
                        }
                    });
        }
        findRegisteredDataClasses();
        findActiveDataClasses();
    }

    public List<R> getActivatedSubSystems() {
        return activatedSubSystems;
    }

    public List<R> getSubSystems() {
        return subSystems;
    }

    public VCoreSubsystem<?> findSubsystemByClass(Class<?> subsystemClass){
        return this.subSystems.stream().filter(r -> r.getClass().equals(subsystemClass)).findAny().orElse(null);
    }

    void disable(){
        plugin.consoleMessage("&eStopping Subsystem Manager&7...");
        activatedSubSystems.forEach(r -> {
            try {
                r.onSubsystemDisable();
                plugin.consoleMessage("&eDeactivated Subsystem&7: &b"+r);
            } catch (SubsystemDeactivatedException e) { e.printStackTrace(); }
        });
    }

    void findRegisteredDataClasses(){
        plugin.consoleMessage("&eSearching for DataClass Implementations&7...");
        Reflections reflections = new Reflections(plugin.getClass().getPackage().getName());
        registeredDataClasses = reflections.getSubTypesOf(VCoreData.class);
        registeredServerDataClasses = reflections.getSubTypesOf(ServerData.class);
        registeredPlayerDataClasses = reflections.getSubTypesOf(PlayerData.class);
    }

    void findActiveDataClasses() {
        activeDataClasses = registeredDataClasses.stream()
                .filter(aClass -> {
                    RequiredSubsystemInfo requiredSubsystemInfo = aClass.getAnnotation(RequiredSubsystemInfo.class);
                    return requiredSubsystemInfo != null && isActivated(requiredSubsystemInfo);
                })
                .collect(Collectors.toSet());

        activePlayerDataClasses = registeredPlayerDataClasses
                .stream()
                .filter(aClass -> {
                    RequiredSubsystemInfo requiredSubsystemInfo = aClass.getAnnotation(RequiredSubsystemInfo.class);
                    return requiredSubsystemInfo != null && isActivated(requiredSubsystemInfo);
                })
                .collect(Collectors.toSet());

        activeServerDataClasses = registeredServerDataClasses
                .stream()
                .filter(aClass -> {
                    RequiredSubsystemInfo requiredSubsystemInfo = aClass.getAnnotation(RequiredSubsystemInfo.class);
                    return requiredSubsystemInfo != null && isActivated(requiredSubsystemInfo);
                })
                .collect(Collectors.toSet());
    }

    public boolean isActivated(RequiredSubsystemInfo requiredSubsystemInfo){
        return activatedSubSystems.stream().anyMatch(bukkit -> bukkit.getClass().equals(requiredSubsystemInfo.parentSubSystem()));
    }

    public Set<Class<? extends PlayerData>> getRegisteredPlayerDataClasses() {
        return this.registeredPlayerDataClasses;
    }

    public Set<Class<? extends PlayerData>> getActivePlayerDataClasses() {
        return this.activePlayerDataClasses;
    }

    public Set<Class<? extends ServerData>> getRegisteredServerDataClasses() {
        return this.registeredServerDataClasses;
    }

    public Set<Class<? extends ServerData>> getActiveServerDataClasses() {
        return this.activeServerDataClasses;
    }
}
