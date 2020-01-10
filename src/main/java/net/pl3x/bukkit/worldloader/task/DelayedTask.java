package net.pl3x.bukkit.worldloader.task;

import net.pl3x.bukkit.worldloader.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.function.Consumer;

public class DelayedTask extends BukkitRunnable {
    private final UUID uuid;
    private final int seconds;
    private final Consumer<Player> consumer;

    private int count = -1;

    public DelayedTask(UUID uuid, int seconds, Consumer<Player> consumer) {
        this.uuid = uuid;
        this.seconds = seconds;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            cancel();
            return;
        }

        if (++count < seconds) {
            Lang.sendActionBar(player, Lang.DELAYED_COUNTDOWN
                    .replace("{seconds}", Integer.toString(seconds - count)));
            return;
        }

        Lang.sendActionBar(player, "&f ");
        consumer.accept(player);
        cancel();
    }
}
