package net.pl3x.bukkit.worldloader.command;

import net.pl3x.bukkit.worldloader.WorldLoader;
import net.pl3x.bukkit.worldloader.configuration.Config;
import net.pl3x.bukkit.worldloader.configuration.Data;
import net.pl3x.bukkit.worldloader.configuration.Lang;
import net.pl3x.bukkit.worldloader.task.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CmdSpawn implements TabExecutor {
    private final WorldLoader plugin;

    private Map<UUID, DelayedTask> tasks = new HashMap<>();

    public CmdSpawn(WorldLoader plugin) {
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

        if (!sender.hasPermission("command.spawn")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        DelayedTask oldTask = tasks.remove(uuid);
        if (oldTask != null) {
            oldTask.cancel();
        }

        if (Config.SPAWN_TELEPORT_DELAY <= 0 || player.hasPermission("command.spawn.bypass.delay")) {
            teleportToSpawn(player);
            return true;
        }

        DelayedTask delayedTask = new DelayedTask(uuid, Config.SPAWN_TELEPORT_DELAY, this::teleportToSpawn);
        tasks.put(uuid, delayedTask);
        delayedTask.runTaskTimer(plugin, 0, 20);
        Lang.send(sender, Lang.SPAWN_DELAYED_PLEASE_WAIT);

        return true;
    }

    private void teleportToSpawn(Player player) {
        Lang.send(player, Lang.TELEPORTING);
        World world = Config.SPAWN_USES_CURRENT_WORLD ? player.getWorld() : Bukkit.getWorlds().get(0);
        player.teleportAsync(Data.getSpawn(world)).thenAccept(success ->
                Lang.send(player, success ? Lang.SPAWN_SUCCESS : Lang.SPAWN_ERROR));
    }
}
