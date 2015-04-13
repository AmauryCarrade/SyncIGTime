package eu.carrade.amaury.SyncIGTime;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Calendar;
import java.util.TimeZone;


public class UpdateTimeTask implements Runnable {

	private final double secondsIn24Hours = 86400.0d;
	private final double ticksPerMinecraftDay = 24000.0d;

	private Calendar thisDay6AM;

	private TimeZone timezone = TimeZone.getTimeZone("Europe/Paris");


	public UpdateTimeTask() {
		Calendar now = Calendar.getInstance(timezone);

		thisDay6AM = Calendar.getInstance(timezone);
		thisDay6AM.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 6, 0, 0);
	}


	@Override
	public void run() {
		Calendar now = Calendar.getInstance(timezone);

		double secondsSince6AMThisDay = (now.getTimeInMillis() - thisDay6AM.getTimeInMillis()) / 1000.0;
		long inGameTime = (long) ((secondsSince6AMThisDay / secondsIn24Hours) * ticksPerMinecraftDay);

		synchronized (Bukkit.getServer().getWorlds()) {
			for(World world : Bukkit.getServer().getWorlds()) {
				synchronized (world) {
					if (world.getEnvironment() == World.Environment.NORMAL) {
						world.setTime(inGameTime);
					}
				}
			}
		}
	}
}
