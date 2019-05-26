package net.pl3x.bukkit.worldloader.command;

import net.pl3x.bukkit.worldloader.configuration.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CmdSetSpawn implements TabExecutor {
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

        boolean success = player.getWorld().setSpawnLocation(player.getLocation());

        Lang.send(sender, (success ? Lang.SET_SPAWN_SUCCESS : Lang.SET_SPAWN_ERROR)
                .replace("{world}", player.getWorld().getName()));
        return true;
    }
}
