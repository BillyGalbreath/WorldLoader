package net.pl3x.bukkit.worldloader.command;

import net.pl3x.bukkit.worldloader.configuration.Config;
import net.pl3x.bukkit.worldloader.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CmdRTP implements TabExecutor {
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

        World.Environment env = player.getWorld().getEnvironment();
        if (env == World.Environment.NETHER || env == World.Environment.THE_END) {
            Lang.send(sender, Lang.RTP_WORLD_DISABLED);
            return true;
        }

        World world = label.equals("wild") || label.equals("wilderness") ? Bukkit.getWorld("wilderness") : player.getWorld();
        if (world == null) {
            Lang.send(sender, Lang.RTP_ERROR);
            return true;
        }

        Location c = world.getWorldBorder().getCenter();
        int r = (int) world.getWorldBorder().getSize() / 2;

        int minX = (int) c.getX() - r + 1;
        int maxX = (int) c.getX() + r;
        int minZ = (int) c.getZ() - r + 1;
        int maxZ = (int) c.getZ() + r;

        int x = ThreadLocalRandom.current().nextInt(maxX - minX) + minX;
        int z = ThreadLocalRandom.current().nextInt(maxZ - minZ) + minZ;

        player.teleportAsync(new Location(world, x + 0.5D, 300 + 0.5D, z + 0.5D)).thenAccept(success -> {
            if (success) {
                player.setFallDistance(-1024F); // do not take fall damage
                if (Bukkit.getPluginManager().isPluginEnabled("CmdCD")) {
                    net.pl3x.bukkit.cmdcd.CmdCD.addCooldown(command, player.getUniqueId(), Config.RTP_COOLDOWN);
                }
                Lang.send(player, Lang.RTP_SUCCESS);
            } else {
                Lang.send(player, Lang.RTP_ERROR);
            }
        });

        return true;
    }
}
