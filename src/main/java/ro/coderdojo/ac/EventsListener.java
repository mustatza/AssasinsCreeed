package ro.coderdojo.ac;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;


public final class EventsListener implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
	event.setCancelled(true);
	}
        @EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
	event.setCancelled(true);
	}
         @EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
	event.setCancelled(true);
	}
}
