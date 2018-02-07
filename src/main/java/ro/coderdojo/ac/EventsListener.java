
package ro.coderdojo.ac;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ro.coderdojo.ac.CoderDojoCommand;



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

    @EventHandler
    public void playerJoined(PlayerJoinEvent event) throws Exception {
        Player player = event.getPlayer();
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(new Location(event.getPlayer().getWorld(), -1598.383, 64.00000, -220.431, -89.8f, 6.9f));

    }

}
