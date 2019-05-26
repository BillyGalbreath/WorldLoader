package net.pl3x.bukkit.worldloader.command;

import net.pl3x.bukkit.worldloader.configuration.Config;
import net.pl3x.bukkit.worldloader.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CmdWorld implements TabExecutor {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender.hasPermission("command.world")) {
            String arg = args[0].toLowerCase();
            return Bukkit.getWorlds().stream()
                    .filter(world -> world.getName().toLowerCase().startsWith(arg))
                    .map(World::getName)
                    .filter(name -> sender.hasPermission("command.world." + name))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.send(sender, "&cPlayer only command");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("command.world")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            return false; // show command usage
        }

        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            Lang.send(sender, Lang.WORLD_NOT_FOUND);
            return true;
        }

        if (Config.PER_WORLD_PERMISSIONS && !player.hasPermission("command.world." + world.getName().toLowerCase())) {
            Lang.send(sender, Lang.WORLD_NO_PERMISSION
                    .replace("{world}", world.getName()));
            return true;
        }

        Lang.send(sender, Lang.TELEPORTING);

        player.teleportAsync(world.getSpawnLocation()).thenAccept(success ->
                Lang.send(sender, (success ? Lang.WORLD_SUCCESS : Lang.WORLD_ERROR)
                        .replace("{world}", world.getName())));
        return true;
    }
}
