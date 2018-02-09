package ro.coderdojo.ac;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class EventsListener implements Listener {

    HashMap<String, String> team = new HashMap();
    JavaPlugin plugin;

    public EventsListener(JavaPlugin plugin) {
        this.plugin = plugin;
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

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                player.setResourcePack("http://app.narvi.ro/ac-resourcePack.zip");
            }
        }, 1);
        

        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(new Location(event.getPlayer().getWorld(), -1598.383, 64.00000, -220.431, -89.8f, 6.9f));

    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) throws Exception {
        System.out.println("Client resource action: " + event.getStatus());
        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED) {
            event.getPlayer().kickPlayer("you must accept the resourcePack");
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (event.getClickedBlock() == null) {
            return;
        }
        Material material = event.getClickedBlock().getState().getType();
        Location location = event.getClickedBlock().getState().getLocation();
        if (action == Action.RIGHT_CLICK_BLOCK && material == Material.WOOD_BUTTON) {
            System.out.println("Click: " + location);
            if (location.getBlockX() == -1594.0 && location.getBlockY() == 65.0 && location.getBlockZ() == -224.0) {
                player.teleport(new Location(player.getWorld(), -1498.590, 81, -239.329));
                team.put(player.getName(), "Assasin");
                player.sendMessage("you are assasin");
            }

            if (location.getBlockX() == -1594.0 && location.getBlockY() == 65.0 && location.getBlockZ() == -218.0) {
                player.teleport(new Location(player.getWorld(), -1312.587, 69, -431.370));
                team.put(player.getName(), "Templier");
                player.sendMessage("you are templier");
            }

        }

    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (team.get(event.getPlayer().getName()).equals("Assasin")) {
            event.setRespawnLocation(new Location(event.getPlayer().getWorld(), -1498.590, 81, -239.329));
        }
        if (team.get(event.getPlayer().getName()).equals("Templier")) {
            event.setRespawnLocation(new Location(event.getPlayer().getWorld(), -1312.587, 69, -431.370));
        }

    }

}
