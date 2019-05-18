package net.pl3x.bukkit.worldloader;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldLoader extends JavaPlugin {
    public void onEnable() {
        saveDefaultConfig();

        for (String name : getConfig().getKeys(false)) {
            ConfigurationSection section = getConfig().getConfigurationSection(name);
            if (section != null) {
                getLogger().info("Preparing level \"" + name + "\"");

                World world = new WorldCreator(name)
                        .type(WorldType.valueOf(section.getString("type")))
                        .environment(World.Environment.valueOf(section.getString("environment")))
                        .seed(section.getLong("seed", ThreadLocalRandom.current().nextLong()))
                        .generateStructures(section.getBoolean("generate-structures"))
                        .createWorld();

                if (world == null) {
                    getLogger().warning("Could not load level \"" + name + "\"");
                    continue;
                }

                world.setPVP(section.getBoolean("pvp"));
                world.setDifficulty(Difficulty.valueOf(section.getString("difficulty")));
                world.setSpawnFlags(section.getBoolean("spawn-monsters"), section.getBoolean("spawn-animals"));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Stream.of("list", "tp")
                    .filter(arg -> arg.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("tp")) {
            return getServer().getWorlds().stream()
                    .map(world -> world.getName().toLowerCase())
                    .filter(name -> name.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("list")) {
                send(sender, "&dWorlds&7:\n&7-&d " + getServer().getWorlds().stream().map(World::getName).collect(Collectors.joining("\n&7-&d ")));
                return true;
            }

            if (args[0].equalsIgnoreCase("tp")) {
                if (!(sender instanceof Player)) {
                    send(sender, "&cPlayer only command");
                    return true;
                }
                if (args.length == 1) {
                    send(sender, "&cSpecify world to teleport to");
                    return true;
                }
                World world = getServer().getWorld(args[1]);
                if (world == null) {
                    send(sender, "&cWorld not found");
                    return true;
                }
                send(sender, "&dTeleporting...");
                ((Player) sender).teleportAsync(world.getSpawnLocation()).thenAccept(success -> {
                    if (success) {
                        send(sender, "&dFinished teleporting.");
                    } else {
                        send(sender, "&cTeleport failed.");
                    }
                });
                return true;
            }
        }
        return false;
    }

    private void send(CommandSender sender, String message) {
        for (String part : message.split("\n")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', part));
        }
    }
}
