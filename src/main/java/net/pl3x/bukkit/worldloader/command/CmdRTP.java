package net.pl3x.bukkit.worldloader.command;

import net.pl3x.bukkit.worldloader.WorldLoader;
import net.pl3x.bukkit.worldloader.configuration.Config;
import net.pl3x.bukkit.worldloader.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class CmdRTP implements TabExecutor {
    private static final Set<UUID> searching = new HashSet<>();

    private final WorldLoader plugin;

    public CmdRTP(WorldLoader plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.send(sender, Lang.PLAYER_COMMAND);
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("command.rtp")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        if (searching.contains(player.getUniqueId())) {
            Lang.send(sender, Lang.RTP_ALREADY_IN_PROGRESS);
            return true;
        }

        searching.add(player.getUniqueId());
        World world = player.getWorld();

        Lang.send(sender, Lang.RTP_SEARCHING);

        new BukkitRunnable() {
            @Override
            public void run() {
                Location center = world.getWorldBorder().getCenter();
                int maxRadius = (int) world.getWorldBorder().getSize() / 2;

                int minX = (int) center.getX() - maxRadius + 1;
                int maxX = (int) center.getX() + maxRadius;
                int minZ = (int) center.getZ() - maxRadius + 1;
                int maxZ = (int) center.getZ() + maxRadius;

                int maxTries = 10;
                for (int currentTry = 1; currentTry <= maxTries; currentTry++) {
                    if (!player.isOnline()) {
                        return; // cancel
                    }

                    Lang.sendActionBar(player, Lang.RTP_SEARCHING_ACTION
                            .replace("{current}", String.valueOf(currentTry))
                            .replace("{max}", String.valueOf(maxTries)));

                    try {
                        Location location = plugin.getServer().getScheduler().callSyncMethod(plugin, () -> {
                            ThreadLocalRandom rand = ThreadLocalRandom.current();

                            int x = rand.nextInt(maxX - minX) + minX;
                            int z = rand.nextInt(maxZ - minZ) + minZ;

                            int y = world.getEnvironment() == World.Environment.NETHER ? rand.nextInt(75) + 25 : world.getHighestBlockYAt(x, z);

                            Location loc = new Location(world, x, y - 1, z);

                            Block ground = loc.getBlock();
                            if (ground.getType().isOccluding()) {
                                Block feet = ground.getRelative(BlockFace.UP);
                                if (feet.getType() == Material.AIR) {
                                    Block head = feet.getRelative(BlockFace.UP);
                                    if (head.getType() == Material.AIR) {
                                        Block above = head.getRelative(BlockFace.UP);
                                        if (above.getType() == Material.AIR) {
                                            return loc;
                                        }
                                    }
                                }
                            }
                            return null;
                        }).get();
                        if (location != null) {
                            searching.remove(player.getUniqueId());
                            if (player.isOnline()) {
                                Location finalLoc = location.add(0, 1, 0);
                                plugin.getServer().getScheduler().callSyncMethod(plugin, () ->
                                        new BukkitRunnable() {
                                            private int timer = 101;

                                            public void run() {
                                                if (timer < 0) {
                                                    if (player.teleport(finalLoc)) {
                                                        if (Bukkit.getPluginManager().isPluginEnabled("CmdCD")) {
                                                            net.pl3x.bukkit.cmdcd.CmdCD.addCooldown(command, player.getUniqueId(), Config.RTP_COOLDOWN);
                                                        }
                                                        Lang.send(player, Lang.RTP_SUCCESS);
                                                        new BukkitRunnable() {
                                                            public void run() {
                                                                player.teleport(finalLoc); // one more time for good measure
                                                            }
                                                        }.runTaskLater(plugin, 20L);
                                                    } else {
                                                        Lang.send(player, Lang.RTP_ERROR);
                                                    }
                                                    cancel();
                                                    return;
                                                } else if (timer % 20 == 0) {
                                                    Lang.sendActionBar(player, Lang.RTP_SUCCESS_ACTION
                                                            .replace("{seconds}", String.valueOf(timer / 20)));
                                                }
                                                finalLoc.getChunk().load(true);
                                                timer--;
                                            }
                                        }.runTaskTimer(plugin, 0L, 1L)).get();
                            }
                            return; // done
                        }
                    } catch (InterruptedException | ExecutionException ignore) {
                    }
                }
                Lang.sendActionBar(player, Lang.RTP_ERROR_ACTION);
            }
        }.runTaskAsynchronously(plugin);
        return true;
    }
}
