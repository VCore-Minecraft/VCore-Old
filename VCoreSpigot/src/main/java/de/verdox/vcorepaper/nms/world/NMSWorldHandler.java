/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.nms.NMSHandler;
import de.verdox.vcorepaper.nms.NMSVersion;
import de.verdox.vcorepaper.nms.PacketListener;
import de.verdox.vcorepaper.nms.packetwrapper.v1_16_3.ChunkPacketWrapper;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import reactor.util.annotation.NonNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 16.06.2021 19:20
 */
public interface NMSWorldHandler extends NMSHandler {

    static NMSWorldHandler getRightHandler(NMSVersion nmsVersion){
        if(nmsVersion.equals(NMSVersion.V1_16_5)){
            return new V1_16_R3();
        }
        return null;
    }

    void refreshChunks(Player player);

    /**
     *
     * @param player Player to send Chunk to
     * @param chunk Chunk to send
     */
    void sendChunk(Player player, org.bukkit.Chunk chunk);

    /**
     *
     * @param player Player to send Chunk to
     * @param chunk Chunk to send
     * @param biome Fake Biome to send
     */
    void sendFakeBiome(Player player, org.bukkit.Chunk chunk, org.bukkit.block.Biome biome);

    /**
     *
     * @param player Player to send Chunk to
     * @param environment Fake DimensionType to send
     */
    void sendFakeDimension(Player player, @NonNull org.bukkit.World.Environment environment);


    class V1_16_R3 implements NMSWorldHandler{

        private final PacketListener chunkPacketListener = new PacketListener(ListenerPriority.NORMAL, true, PacketType.Play.Server.MAP_CHUNK) {

            @Override
            public void onSend(PacketEvent event, PacketInstruction packetInstruction) {
                // Send Fake Biome
            }

            @Override
            public void onReceive(PacketEvent event, PacketInstruction packetInstruction) {

            }
        };

        @Override
        public void refreshChunks(Player player) {
            VCoreUtil.getBukkitPlayerUtil().getChunksInServerViewDistance(player).forEach(chunkKey -> {
                org.bukkit.World world = player.getWorld();
                sendChunk(player,chunkKey.getChunkIn(world));
            });
        }

        @Override
        public void sendChunk(@NonNull Player player, @NonNull Chunk chunk) {
            PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), 65535,true);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutMapChunk);
            VCorePaper.getInstance().consoleMessage("Sent Fake Chunk ["+chunk.getX()+"|"+chunk.getZ()+"]", true);
        }

        @Override
        public void sendFakeBiome(@NonNull Player player, @NonNull Chunk chunk, @NonNull Biome biome) {

            PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), 65535,true);

            ChunkPacketWrapper chunkPacketWrapper = new ChunkPacketWrapper(packetPlayOutMapChunk);
            chunkPacketWrapper.setBiome(biome);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutMapChunk);
            VCorePaper.getInstance().consoleMessage("Sent Fake Biome ["+biome+" | "+VCoreUtil.getVanillaUtil().getBiomeID_1_16(biome)+"] " +
                    "["+chunkPacketWrapper.getChunkX()+" | "+chunkPacketWrapper.getChunkZ()+"]", true);
            //TODO: Remove Player Instruction here
        }

        @Override
        public void sendFakeDimension(@NonNull Player player, @NonNull org.bukkit.World.Environment environment) {
            if(!Bukkit.isPrimaryThread()) {
                Bukkit.getScheduler().runTask(VCorePaper.getInstance(), () -> sendFakeDimension(player, environment));
                return;
            }

            CraftChunk chunk;

            Location location = player.getLocation().clone();

            boolean flag = !location.getWorld().getEnvironment().equals(environment);

            CraftPlayer craftPlayer = (CraftPlayer) player;
            CraftServer craftServer = (CraftServer) craftPlayer.getServer();

            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            WorldServer worldServer = entityPlayer.getWorldServer();
            WorldData worldData = worldServer.getWorldData();
            DedicatedServer dedicatedServer = craftServer.getServer();

            DimensionManager dimensionManager = craftPlayer.getHandle().getWorld().getDimensionManager();
            long seed = craftPlayer.getWorld().getSeed();

            ResourceKey<World> world;
            switch (environment){
                case NORMAL: world = World.OVERWORLD;break;
                case NETHER: world = World.THE_NETHER;break;
                case THE_END: world = World.THE_END;break;
                default:throw new IllegalStateException("Unknwon Environment: "+environment);
            }
            if(world == null)
                return;
            entityPlayer.getWorldServer().removePlayer(entityPlayer);

            //entityPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.a, 0.0F));

            worldServer = ((CraftWorld)location.getWorld()).getHandle();
            entityPlayer.forceSetPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            worldServer.getChunkProvider().addTicket(TicketType.POST_TELEPORT, new ChunkCoordIntPair(location.getBlockX() >> 4, location.getBlockZ() >> 4), 1, entityPlayer.getId());
            entityPlayer.forceCheckHighPriority();

            EnumGamemode enumGamemode = EnumGamemode.getById(player.getGameMode().getValue());

            //TODO: BUGG Anfällig bitte richtigen mülll reinmachen
            craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutRespawn(dimensionManager, world, seed, enumGamemode, enumGamemode, false, false, flag));
            craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutViewDistance(worldServer.getChunkProvider().playerChunkMap.getLoadViewDistance()));
            entityPlayer.spawnIn(worldServer);
            entityPlayer.dead = false;
            entityPlayer.playerConnection.teleport(new Location(worldServer.getWorld(), entityPlayer.locX(), entityPlayer.locY(), entityPlayer.locZ(), entityPlayer.yaw, entityPlayer.pitch));
            entityPlayer.setSneaking(false);
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnPosition(worldServer.getSpawn(), worldServer.v()));
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutServerDifficulty(worldServer.getDifficulty(), worldData.isDifficultyLocked()));
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutExperience(entityPlayer.exp, entityPlayer.expTotal, entityPlayer.expLevel));
            dedicatedServer.getPlayerList().updateClient(entityPlayer);
            entityPlayer.updateAbilities();
            entityPlayer.triggerDimensionAdvancements(((CraftWorld) craftPlayer.getWorld()).getHandle());

            worldServer.addPlayerRespawn(entityPlayer);

            //PacketPlayOutRespawn packetPlayOutRespawn = new PacketPlayOutRespawn(dimensionManager, world, seed, EnumGamemode.SURVIVAL, EnumGamemode.ADVENTURE, false, false, true);
            //craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutRespawn);
//
            //Bukkit.getScheduler().runTaskLater(VCorePaper.getInstance(), () -> {
            //    sendChunk(player, chunk);
            //    PacketPlayOutPosition packetPlayOutPosition = new PacketPlayOutPosition(playerPos.getX(), playerPos.getY(), playerPos.getZ(), playerPos.getYaw(), playerPos.getPitch(), new HashSet<>(), 0);
            //    craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutPosition);
//
            //    player.updateInventory();
            //    ((CraftPlayer) player).sendHealthUpdate();
            //}, 20L);


            //craftServer.getServer().getPlayerList().moveToWorld(craftPlayer.getHandle(), craftPlayer.getHandle().getWorldServer(), true, player.getLocation(), false);
        }
    }
}
