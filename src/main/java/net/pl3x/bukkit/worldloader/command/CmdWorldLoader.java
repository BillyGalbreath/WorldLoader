package net.pl3x.bukkit.worldloader.command;

import net.pl3x.bukkit.worldloader.WorldLoader;
import net.pl3x.bukkit.worldloader.configuration.Config;
import net.pl3x.bukkit.worldloader.configuration.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;

public class CmdWorldLoader implements TabExecutor {
    private final WorldLoader plugin;

    public CmdWorldLoader(WorldLoader plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "reload".startsWith(args[0].toLowerCase()) && sender.hasPermission("command.worldloader")) {
            return Collections.singletonList("reload");
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("command.worldloader")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        String response = "&d" + plugin.getName() + " v" + plugin.getDescription().getVersion();

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            Config.reload();
            Lang.reload();

            response += " reloaded";
        }

        Lang.send(sender, response);
        return true;
    }
}
