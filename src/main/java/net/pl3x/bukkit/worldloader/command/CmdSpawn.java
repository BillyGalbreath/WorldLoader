package net.pl3x.bukkit.worldloader.command;

import net.pl3x.bukkit.worldloader.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CmdSpawn implements TabExecutor {
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
        if (!player.hasPermission("command.spawn")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        Location spawn = Bukkit.getWorlds().get(0).getSpawnLocation();

        Lang.send(sender, Lang.TELEPORTING);

        player.teleportAsync(spawn).thenAccept(success ->
                Lang.send(sender, success ? Lang.SPAWN_SUCCESS : Lang.SPAWN_ERROR));
        return true;
    }
}
