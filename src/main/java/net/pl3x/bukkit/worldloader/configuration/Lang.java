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

    public static String DELAYED_COUNTDOWN = "&aPlease wait... &e{seconds} &aremaining...";

    public static String TELEPORTING = "&dTeleporting...";

    public static String RTP_SUCCESS = "&dTeleported to random location";
    public static String RTP_WORLD_DISABLED = "&4You cannot /rtp in this world";
    public static String RTP_ERROR = "&cThere was a problem trying to find a safe location";

    public static String SET_SPAWN_SUCCESS = "&dSpawn has been set for &7{world}";
    public static String SET_SPAWN_ERROR = "&cThere was a problem trying to set spawn";

    public static String SPAWN_DELAYED_PLEASE_WAIT = "&dYou will be teleported momentarily...";
    public static String SPAWN_SUCCESS = "&dTeleported to spawn";
    public static String SPAWN_ERROR = "&cThere was a problem trying to teleport to spawn";

    public static String WORLD_NOT_FOUND = "&4World not found";
    public static String WORLD_SUCCESS = "&dTeleported to &7{world}";
    public static String WORLD_ERROR = "&cThere was a problem trying to teleport to &7{world}";

    private static void init() {
        COMMAND_NO_PERMISSION = getString("command-no-permission", COMMAND_NO_PERMISSION);
        PLAYER_COMMAND = getString("player-command", PLAYER_COMMAND);

        WORLD_NO_PERMISSION = getString("world-no-permission", WORLD_NO_PERMISSION);

        DELAYED_COUNTDOWN = getString("delayed-countdown", DELAYED_COUNTDOWN);

        TELEPORTING = getString("teleporting", TELEPORTING);

        RTP_SUCCESS = getString("rtp-success", RTP_SUCCESS);
        RTP_WORLD_DISABLED = getString("rtp-world-disabled", RTP_WORLD_DISABLED);
        RTP_ERROR = getString("rtp-error", RTP_ERROR);

        SET_SPAWN_SUCCESS = getString("set-spawn-success", SET_SPAWN_SUCCESS);
        SET_SPAWN_ERROR = getString("set-spawn-error", SET_SPAWN_ERROR);

        SPAWN_DELAYED_PLEASE_WAIT = getString("spawn-delayed-please-wait", SPAWN_DELAYED_PLEASE_WAIT);
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

    public static void sendActionBar(Player player, String message) {
        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', message));
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
