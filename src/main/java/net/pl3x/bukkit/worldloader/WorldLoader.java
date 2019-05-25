package net.pl3x.bukkit.worldloader;

import net.pl3x.bukkit.worldloader.command.CmdSetSpawn;
import net.pl3x.bukkit.worldloader.command.CmdSpawn;
import net.pl3x.bukkit.worldloader.command.CmdWorld;
import net.pl3x.bukkit.worldloader.command.CmdWorldLoader;
import net.pl3x.bukkit.worldloader.configuration.Config;
import net.pl3x.bukkit.worldloader.configuration.Lang;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public class WorldLoader extends JavaPlugin {
    private static WorldLoader instance;

    public WorldLoader() {
        instance = this;
    }

    public void onEnable() {
        Config.reload();
        Lang.reload();

        ConfigurationSection worlds = getConfig().getConfigurationSection("worlds");
        if (worlds != null) {
            for (String name : worlds.getKeys(false)) {
                ConfigurationSection section = worlds.getConfigurationSection(name);
                if (section != null) {
                    if (getServer().getWorld(name) != null) {
                        getLogger().info("Preparing level \"" + name + "\"");
                    } else {
                        getLogger().info("Updating level \"" + name + "\"");
                    }

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

        getCommand("setspawn").setExecutor(new CmdSetSpawn());
        getCommand("spawn").setExecutor(new CmdSpawn());
        getCommand("world").setExecutor(new CmdWorld());
        getCommand("worldloader").setExecutor(new CmdWorldLoader(this));
    }

    public static WorldLoader getInstance() {
        return instance;
    }
}
