package ro.coderdojo.ac;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public final class EventsListener implements Listener {

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

    }
}
