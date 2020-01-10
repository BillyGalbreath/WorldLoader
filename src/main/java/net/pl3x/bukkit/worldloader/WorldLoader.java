package net.pl3x.bukkit.worldloader;

import net.pl3x.bukkit.worldloader.command.CmdRTP;
import net.pl3x.bukkit.worldloader.command.CmdSetSpawn;
import net.pl3x.bukkit.worldloader.command.CmdSpawn;
import net.pl3x.bukkit.worldloader.command.CmdWorld;
import net.pl3x.bukkit.worldloader.command.CmdWorldLoader;
import net.pl3x.bukkit.worldloader.configuration.Config;
import net.pl3x.bukkit.worldloader.configuration.Data;
import net.pl3x.bukkit.worldloader.configuration.Lang;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldLoader extends JavaPlugin {
    public void onEnable() {
        Config.reload(this);
        Lang.reload(this);
        Data.reload(this);

        getCommand("rtp").setExecutor(new CmdRTP());
        getCommand("setspawn").setExecutor(new CmdSetSpawn(this));
        getCommand("spawn").setExecutor(new CmdSpawn(this));
        getCommand("world").setExecutor(new CmdWorld());
        getCommand("worldloader").setExecutor(new CmdWorldLoader(this));
    }
}
