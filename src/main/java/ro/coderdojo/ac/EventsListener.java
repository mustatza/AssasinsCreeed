package ro.coderdojo.ac;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EntityHuman;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import com.mojang.authlib.GameProfile;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public final class EventsListener implements Listener {

    HashMap<String, String> team = new HashMap();
    JavaPlugin plugin;
    boolean beginFight = false;

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
    public void onWallProximity(PlayerMoveEvent event) throws Exception {
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
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 18000, 1));
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 18000, 1));
    }

    public void setName(Player player, String name) {
        try {
            EntityPlayer entityP = ((CraftPlayer) player).getHandle();
            Field gField = EntityHuman.class.getDeclaredField("g");
            gField.setAccessible(true);
            gField.set(entityP, new GameProfile(player.getUniqueId(), name));

            for (Player players : Bukkit.getOnlinePlayers()) {
                players.hidePlayer(player);
                players.showPlayer(player);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void playerJoined(PlayerJoinEvent event) throws Exception {
        Player player = event.getPlayer();
        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        healthAttribute.setBaseValue(20.00);

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
    public void onFoodChange(FoodLevelChangeEvent event
    ) {
        event.setCancelled(true);
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event
    ) {
        Player player = event.getPlayer();
        checkPressedButton(event, player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event
    ) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Player) {
            Player other = (Player) event.getRightClicked();
            List<Block> blocks = other.getLineOfSight(null, 10);
            boolean vazut = false;
            for (Block block : blocks) {
                if (block.getLocation().getBlockX() == player.getLocation().getBlockX()
                        && block.getLocation().getBlockY() == player.getLocation().getBlockY()
                        && block.getLocation().getBlockZ() == player.getLocation().getBlockZ()) {
                    vazut = true;
                }
            }
            if (vazut) {
                player.sendMessage(Math.random() + " ma vede");
                other.sendMessage(Math.random() + " te vad");
            } else {
                player.sendMessage(Math.random() + " NUU  ma vede");
                other.sendMessage(Math.random() + " NUU te vad");
            }
            if ((team.get(player.getName()).equals("Assasin") && team.get(other.getName()).equals("Templier"))
                    || (team.get(player.getName()).equals("Templier") && team.get(other.getName()).equals("Assasin"))) {
                other.setHealth(0);
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

    @EventHandler
    public void onDemage(EntityDamageEvent event
    ) {
        if (event.getEntityType() == EntityType.PLAYER && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFight(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        if (event.getDamager().getType() != EntityType.PLAYER) {
            return;
        }
        Player damaged = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        if (!beginFight) {
            damager.sendMessage("you began a fight with " + damaged.getName());
            damaged.sendMessage(damager.getName() + " began a fight with you ");
            beginFight = true;
        }

        String damagedName = "" + ChatColor.RED;
        boolean changed = false;
        for (int a = 1; a <= 10; a++) {
            if (a > damaged.getHealth() / 2 && !changed) {
                damagedName = damagedName + ChatColor.WHITE;
                changed = true;
            }
            damagedName = damagedName + "♥";
        }
        setName(damaged, damagedName);
    }

    private void checkPressedButton(PlayerInteractEvent event, Player player) {
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
    public void onHealthUp(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        String damagedName = "" + ChatColor.RED;
        boolean changed = false;
        for (int a = 1; a <= 10; a++) {
            if (a > player.getHealth() / 2 && !changed) {
                damagedName = damagedName + ChatColor.WHITE;
                changed = true;
            }
            damagedName = damagedName + "♥";
        }
        setName(player, damagedName);
    }
}
