/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.bossbar;

import de.verdox.vcorepaper.VCorePaper;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 16.06.2021 02:23
 */
public class VBossBar {
    private final BossBar bossBar;
    private final Map<UUID, BukkitTask> hideTasks = new ConcurrentHashMap<>();
    public VBossBar(String title, BarColor barColor, BarStyle barStyle, BarFlag... barFlags){
        this.bossBar = Bukkit.createBossBar(title, barColor, barStyle, barFlags);
    }

    public void setProgress(float progress){
        bossBar.setProgress(progress);
    }

    public void showToPlayer(Player player){
        stopHideTask(player.getUniqueId());
        bossBar.addPlayer(player);
    }

    public void hideFromPlayer(Player player){
        stopHideTask(player.getUniqueId());
        bossBar.removePlayer(player);
    }

    public void showForTime(Player player, long timeInTicks){
        bossBar.addPlayer(player);
        startHideTask(player.getUniqueId(), timeInTicks);
    }

    private void startHideTask(UUID uuid, long whenToHideInTicks){
        hideTasks.put(uuid, Bukkit.getScheduler().runTaskLater(VCorePaper.getInstance(), () -> {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null)
                return;
            hideFromPlayer(player);
        }, whenToHideInTicks));
    }

    public void clear(){
        bossBar.getPlayers().clear();
        bossBar.setVisible(false);
        hideTasks.forEach((uuid, bukkitTask) -> {
            if(!bukkitTask.isCancelled())
                bukkitTask.cancel();
        });
        hideTasks.clear();
    }

    private void stopHideTask(UUID uuid){
        if(!hideTasks.containsKey(uuid))
            return;
        BukkitTask task = hideTasks.get(uuid);
        if(!task.isCancelled())
            task.cancel();
        hideTasks.remove(uuid);
    }
}
