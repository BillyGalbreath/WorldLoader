package net.pl3x.bukkit.worldloader.command;

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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class CmdRTP implements TabExecutor {
    private static final Set<UUID> searching = new HashSet<>();

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

        CompletableFuture.supplyAsync(() -> {
            ThreadLocalRandom rand = ThreadLocalRandom.current();

            Location center = world.getWorldBorder().getCenter();
            int maxRadius = (int) world.getWorldBorder().getSize() / 2;

            int minX = (int) center.getX() - maxRadius + 1;
            int maxX = (int) center.getX() + maxRadius;
            int minZ = (int) center.getZ() - maxRadius + 1;
            int maxZ = (int) center.getZ() + maxRadius;

            int maxTries = 100;
            while (maxTries-- > 0) {
                int x = rand.nextInt(maxX - minX) + minX;
                int z = rand.nextInt(maxZ - minZ) + minZ;

                int y = world.getEnvironment() == World.Environment.NETHER ? rand.nextInt(75) + 25 : world.getHighestBlockYAt(x, z);

                Location location = new Location(world, x, y, z);

                Block feet = location.getBlock();
                if (feet.getType() == Material.AIR) {
                    Block head = feet.getRelative(BlockFace.UP);
                    if (head.getType() == Material.AIR) {
                        Block ground = feet.getRelative(BlockFace.DOWN);
                        if (ground.getType().isOccluding()) {
                            return location;
                        }
                    }
                }
            }
            return null;
        }).thenAccept(location -> {
            searching.remove(player.getUniqueId());
            if (player.isOnline()) {
                if (location == null) {
                    Lang.send(player, Lang.RTP_ERROR);
                    return;
                }
                player.teleportAsync(location).thenAccept(success -> {
                    if (success) {
                        if (Bukkit.getPluginManager().isPluginEnabled("CmdCD")) {
                            net.pl3x.bukkit.cmdcd.CmdCD.addCooldown(command, player.getUniqueId(), Config.RTP_COOLDOWN);
                        }
                        Lang.send(player, Lang.RTP_SUCCESS);
                    } else {
                        Lang.send(player, Lang.RTP_ERROR);
                    }
                });
            }
        });

        return true;
    }
}
