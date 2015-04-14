package eu.carrade.amaury.SyncIGTime;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;


public class SyncIGTime extends JavaPlugin {

	private BukkitTask updateTimeTask;

	@Override
	public void onEnable() {

		saveDefaultConfig();

		Set<World> worlds = new HashSet<>();
		Boolean syncMoonPhase = getConfig().getBoolean("syncMoonPhases", true);

		for(String worldName : getConfig().getStringList("worlds")) {
			World world = getServer().getWorld(worldName);
			if(world != null && world.getEnvironment() == World.Environment.NORMAL) {
				worlds.add(world);
			} else {
				getLogger().warning("The world " + worldName + " does not exists or isn't an overworld.");
			}
		}


		for(World world : worlds) {
			world.setGameRuleValue("doDaylightCycle", "false");
		}

		updateTimeTask = getServer().getScheduler().runTaskTimerAsynchronously(
				this, new UpdateTimeTask(worlds, syncMoonPhase), 1l, 60l
		);

	}

	@Override
	public void onDisable() {
		if(updateTimeTask != null) {
			updateTimeTask.cancel();
		}
	}
}
