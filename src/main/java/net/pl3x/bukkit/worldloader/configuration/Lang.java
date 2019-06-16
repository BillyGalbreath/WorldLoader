package net.pl3x.bukkit.worldloader.configuration;

import com.google.common.base.Throwables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Lang {
    public static String COMMAND_NO_PERMISSION = "&4You do not have permission for that command!";
    public static String PLAYER_COMMAND = "&4This command is only available to players!";

    public static String WORLD_NO_PERMISSION = "&4You do not have permission to warp to {world}!";

    public static String TELEPORTING = "&dTeleporting...";

    public static String RTP_ALREADY_IN_PROGRESS = "&cRTP already in progress, please wait...";
    public static String RTP_SEARCHING = "&dSearching for random location...";
    public static String RTP_SEARCHING_ACTION = "&dSearching For Safe Location... &7{current}&e/&7{max}";
    public static String RTP_SUCCESS = "&dTeleported to random location";
    public static String RTP_SUCCESS_ACTION = "&aFound A Safe Location. Teleporting in {seconds}s...";
    public static String RTP_ERROR = "&cThere was a problem trying to find a safe location";
    public static String RTP_ERROR_ACTION = "&cCould Not Find A Safe Location";

    public static String SET_SPAWN_SUCCESS = "&dSpawn has been set for &7{world}";
    public static String SET_SPAWN_ERROR = "&cThere was a problem trying to set spawn";

    public static String SPAWN_SUCCESS = "&dTeleported to spawn";
    public static String SPAWN_ERROR = "&cThere was a problem trying to teleport to spawn";

    public static String WORLD_NOT_FOUND = "&4World not found";
    public static String WORLD_SUCCESS = "&dTeleported to &7{world}";
    public static String WORLD_ERROR = "&cThere was a problem trying to teleport to &7{world}";

    private static void init() {
        COMMAND_NO_PERMISSION = getString("command-no-permission", COMMAND_NO_PERMISSION);
        PLAYER_COMMAND = getString("player-command", PLAYER_COMMAND);

        WORLD_NO_PERMISSION = getString("world-no-permission", WORLD_NO_PERMISSION);

        TELEPORTING = getString("teleporting", TELEPORTING);

        RTP_ALREADY_IN_PROGRESS = getString("rpt-already-in-progress", RTP_ALREADY_IN_PROGRESS);
        RTP_SEARCHING = getString("rtp-searching", RTP_SEARCHING);
        RTP_SUCCESS = getString("rtp-success", RTP_SUCCESS);
        RTP_ERROR = getString("rtp-error", RTP_ERROR);

        SET_SPAWN_SUCCESS = getString("set-spawn-success", SET_SPAWN_SUCCESS);
        SET_SPAWN_ERROR = getString("set-spawn-error", SET_SPAWN_ERROR);

        SPAWN_SUCCESS = getString("spawn-success", SPAWN_SUCCESS);
        SPAWN_ERROR = getString("spawn-error", SPAWN_ERROR);

        WORLD_NOT_FOUND = getString("world-not-found", WORLD_NOT_FOUND);
        WORLD_SUCCESS = getString("world-success", WORLD_SUCCESS);
        WORLD_ERROR = getString("world-error", WORLD_ERROR);
    }

    // ############################  DO NOT EDIT BELOW THIS LINE  ############################

    /**
     * Reload the language file
     */
    public static void reload(Plugin plugin) {
        File configFile = new File(plugin.getDataFolder(), Config.LANGUAGE_FILE);
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load " + Config.LANGUAGE_FILE + ", please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header("This is the main language file for " + plugin);
        config.options().copyDefaults(true);

        Lang.init();

        try {
            config.save(configFile);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + configFile, ex);
        }
    }

    private static YamlConfiguration config;

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    /**
     * Sends a message to a recipient
     *
     * @param recipient Recipient of message
     * @param message   Message to send
     */
    public static void send(CommandSender recipient, String message) {
        if (recipient != null) {
            for (String part : colorize(message).split("\n")) {
                recipient.sendMessage(part);
            }
        }
    }

    /**
     * Broadcast a message to server
     *
     * @param message Message to broadcast
     */
    public static void broadcast(String message) {
        for (String part : colorize(message).split("\n")) {
            Bukkit.getOnlinePlayers().forEach(recipient -> recipient.sendMessage(part));
            Bukkit.getConsoleSender().sendMessage(part);
        }
    }

    /**
     * Sends a message to a recipient's actionbar
     *
     * @param recipient Recipient of message
     * @param message   Message to send
     */
    public static void sendActionBar(Player recipient, String message) {
        if (recipient != null) {
            recipient.sendActionBar(colorize(message));
        }
    }

    /**
     * Colorize a String
     *
     * @param str String to colorize
     * @return Colorized String
     */
    public static String colorize(String str) {
        if (str == null) {
            return "";
        }
        str = ChatColor.translateAlternateColorCodes('&', str);
        if (ChatColor.stripColor(str).isEmpty()) {
            return "";
        }
        return str;
    }
}
