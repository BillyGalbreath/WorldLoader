package net.pl3x.bukkit.worldloader.command;

import net.pl3x.bukkit.worldloader.WorldLoader;
import net.pl3x.bukkit.worldloader.configuration.Data;
import net.pl3x.bukkit.worldloader.configuration.Lang;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CmdSetSpawn implements TabExecutor {
    private final WorldLoader plugin;

    public CmdSetSpawn(WorldLoader plugin) {
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
        if (!player.hasPermission("command.setspawn")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        World world = player.getWorld();
        Location spawn = player.getLocation();

        boolean success = world.setSpawnLocation(spawn);

        if (success) {
            Data.setSpawn(plugin, world, spawn);

            Lang.send(sender, Lang.SET_SPAWN_SUCCESS
                    .replace("{world}", world.getName()));
        } else {
            Lang.send(sender, Lang.SET_SPAWN_ERROR
                    .replace("{world}", world.getName()));
        }
        return true;
    }
}
