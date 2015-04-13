package eu.carrade.amaury.SyncIGTime;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public class SyncIGTime extends JavaPlugin {

	private BukkitTask updateTimeTask;

	@Override
	public void onEnable() {

		for(World world : getServer().getWorlds()) {
			if(world.getEnvironment() == World.Environment.NORMAL) {
				world.setGameRuleValue("doDaylightCycle", "false");
			}
		}

		updateTimeTask = getServer().getScheduler().runTaskTimerAsynchronously(this, new UpdateTimeTask(), 1l, 60l);

	}

	@Override
	public void onDisable() {
		if(updateTimeTask != null) {
			updateTimeTask.cancel();
		}
	}
}
