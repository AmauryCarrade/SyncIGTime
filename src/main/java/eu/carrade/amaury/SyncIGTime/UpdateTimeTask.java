package eu.carrade.amaury.SyncIGTime;

import eu.carrade.amaury.SyncIGTime.moon.MoonPhase;
import eu.carrade.amaury.SyncIGTime.moon.MoonPhaseCalculator;
import org.bukkit.World;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;


public class UpdateTimeTask implements Runnable {

	private final double secondsIn24Hours = 86400d;
	private final double ticksPerMinecraftDay = 24000d;

	private Calendar thisDay6AM;
	private TimeZone timezone;

	private MoonPhase currentPhase;
	private int currentDayOfYear;

	private boolean syncMoonPhase;
	private Set<World> worlds = new HashSet<>();


	public UpdateTimeTask(Set<World> worlds, boolean syncMoonPhase, String timezone) {

		this.worlds.addAll(worlds);
		this.syncMoonPhase = syncMoonPhase;

		this.timezone = TimeZone.getTimeZone(timezone);

		Calendar now = Calendar.getInstance(this.timezone);

		thisDay6AM = Calendar.getInstance(this.timezone);
		thisDay6AM.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 6, 0, 0);

		if(syncMoonPhase) {
			currentPhase = MoonPhaseCalculator.moonPhase(now);
			currentDayOfYear = now.get(Calendar.DAY_OF_YEAR);
		}
	}


	@Override
	public void run() {
		Calendar now = Calendar.getInstance(timezone);

		// New day... new phase?
		if(syncMoonPhase && now.get(Calendar.DAY_OF_YEAR) != currentDayOfYear) {
			currentPhase = MoonPhaseCalculator.moonPhase(now);
			currentDayOfYear = now.get(Calendar.DAY_OF_YEAR);
		}

		double secondsSince6AMThisDay = (now.getTimeInMillis() - thisDay6AM.getTimeInMillis()) / 1000.0;

		// Hour-only time
		long inGameTime = (long) (((secondsSince6AMThisDay / secondsIn24Hours) * ticksPerMinecraftDay) % ticksPerMinecraftDay);
		if(inGameTime < 0) inGameTime += (long) ticksPerMinecraftDay;

		// Moon phase
		if(syncMoonPhase) {
			inGameTime += currentPhase.getTicksToGetThisPhase();
		}


		for(World world : worlds) {
			synchronized (world) {
				world.setFullTime(inGameTime);
			}
		}
	}
}
