package net.pl3x.bukkit.worldloader.listener;

import net.pl3x.bukkit.worldloader.WorldSettings;
import net.pl3x.bukkit.worldloader.configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            player.teleportAsync(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        WorldSettings settings = Config.getSettings(event.getPlayer().getWorld());
        if (settings == null) {
            return;
        }

        String title = settings.getOnJoinTitle();
        String subtitle = settings.getOnJoinSubtitle();

        if (title != null) {
            title = ChatColor.translateAlternateColorCodes('&', title.trim());
        }
        if (subtitle != null) {
            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle.trim());
        }

        if ((title != null && !title.isEmpty()) || (subtitle != null && !subtitle.isEmpty())) {
            event.getPlayer().sendTitle(title, subtitle, 20, 70, 40);
        }
    }
}
