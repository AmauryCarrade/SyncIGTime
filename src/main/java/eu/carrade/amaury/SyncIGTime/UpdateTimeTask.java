package eu.carrade.amaury.SyncIGTime;

import eu.carrade.amaury.SyncIGTime.moon.MoonPhase;
import eu.carrade.amaury.SyncIGTime.moon.MoonPhaseCalculator;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Calendar;
import java.util.TimeZone;


public class UpdateTimeTask implements Runnable {

	private final double secondsIn24Hours = 86400d;
	private final double ticksPerMinecraftDay = 24000d;

	private Calendar thisDay6AM;
	private TimeZone timezone = TimeZone.getTimeZone("Europe/Paris");

	private MoonPhase currentPhase;
	private int currentDayOfYear;


	public UpdateTimeTask() {
		Calendar now = Calendar.getInstance(timezone);

		thisDay6AM = Calendar.getInstance(timezone);
		thisDay6AM.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 6, 0, 0);

		currentPhase = MoonPhaseCalculator.moonPhase(now);
		currentDayOfYear = now.get(Calendar.DAY_OF_YEAR);
	}


	@Override
	public void run() {
		Calendar now = Calendar.getInstance(timezone);

		// New day... new phase?
		if(now.get(Calendar.DAY_OF_YEAR) != currentDayOfYear) {
			currentPhase = MoonPhaseCalculator.moonPhase(now);
			currentDayOfYear = now.get(Calendar.DAY_OF_YEAR);
		}

		double secondsSince6AMThisDay = (now.getTimeInMillis() - thisDay6AM.getTimeInMillis()) / 1000.0;

		// Hour-only time
		long inGameTime = (long) (((secondsSince6AMThisDay / secondsIn24Hours) * ticksPerMinecraftDay) % ticksPerMinecraftDay);
		if(inGameTime < 0) inGameTime += (long) ticksPerMinecraftDay;

		// Moon phase
		inGameTime += currentPhase.getTicksToGetThisPhase();


		synchronized (Bukkit.getServer().getWorlds()) {
			for(World world : Bukkit.getServer().getWorlds()) {
				synchronized (world) {
					if (world.getEnvironment() == World.Environment.NORMAL) {
						world.setFullTime(inGameTime);
					}
				}
			}
		}
	}
}
