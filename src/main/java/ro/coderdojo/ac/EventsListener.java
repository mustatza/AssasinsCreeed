package ro.coderdojo.ac;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class EventsListener implements Listener {

    public EventsListener() {
        World world = Bukkit.getServer().getWorld("world");
        for (Entity e : world.getEntities()) {
            if (e.getType() != EntityType.VILLAGER && e.getType() != EntityType.PARROT) {
                e.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity().getType() != EntityType.VILLAGER && event.getEntity().getType() != EntityType.PARROT) {
            event.setCancelled(true);
            event.getEntity().remove();
        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (CoderDojoCommand.isEnabled) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        event.setCancelled(true);
    }

    BlockState restoreblock;

    @EventHandler
    public void onWallProximity(PlayerMoveEvent event) {
        Block atPlayersFeet = event.getTo().getBlock();
        if (restoreblock != null) {
            System.out.println((restoreblock.getLocation() != atPlayersFeet.getLocation()) + " - " + restoreblock.getLocation() + "  " + atPlayersFeet.getLocation());
            if (restoreblock.getLocation().getBlockX() != atPlayersFeet.getLocation().getBlockX()
                    || restoreblock.getLocation().getBlockY() != atPlayersFeet.getLocation().getBlockY()
                    || restoreblock.getLocation().getBlockZ() != atPlayersFeet.getLocation().getBlockZ()) {
                restoreblock.update(true);
                restoreblock = null;
            }
        }
        if (atPlayersFeet.getRelative(BlockFace.NORTH).getType() != Material.AIR
                || atPlayersFeet.getRelative(BlockFace.SOUTH).getType() != Material.AIR
                || atPlayersFeet.getRelative(BlockFace.EAST).getType() != Material.AIR
                || atPlayersFeet.getRelative(BlockFace.WEST).getType() != Material.AIR) {
            if (atPlayersFeet.getType() == Material.AIR) {
                restoreblock = atPlayersFeet.getState();
                event.getPlayer().sendBlockChange(atPlayersFeet.getLocation(), Material.VINE, (byte) 0);

            }
        }
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 18000, 2));
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 18000, 1));
    }

}
