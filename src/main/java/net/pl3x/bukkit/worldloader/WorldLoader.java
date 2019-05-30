package net.pl3x.bukkit.worldloader;

import net.pl3x.bukkit.worldloader.command.CmdSetSpawn;
import net.pl3x.bukkit.worldloader.command.CmdSpawn;
import net.pl3x.bukkit.worldloader.command.CmdWorld;
import net.pl3x.bukkit.worldloader.command.CmdWorldLoader;
import net.pl3x.bukkit.worldloader.configuration.Config;
import net.pl3x.bukkit.worldloader.configuration.Lang;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldLoader extends JavaPlugin {
    private static WorldLoader instance;

    public WorldLoader() {
        instance = this;
    }

    public void onEnable() {
        Config.reload();
        Lang.reload();

        getCommand("setspawn").setExecutor(new CmdSetSpawn());
        getCommand("spawn").setExecutor(new CmdSpawn());
        getCommand("world").setExecutor(new CmdWorld());
        getCommand("worldloader").setExecutor(new CmdWorldLoader(this));
    }

    public static WorldLoader getInstance() {
        return instance;
    }
}
