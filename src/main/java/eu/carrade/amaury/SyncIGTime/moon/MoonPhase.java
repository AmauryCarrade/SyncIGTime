package eu.carrade.amaury.SyncIGTime.moon;


import org.bukkit.Bukkit;

public enum MoonPhase {

	NEW(4 * 24000l),
	WAXING_CRESCENT(5 * 24000l),
	FIRST_QUARTER(6 * 24000l),
	WAXING_GIBBOUS(7 * 24000l),
	FULL(0l),
	WANING_GIBBOUS(24000l),
	THIRD_QUARTER(2 * 24000l),
	WANING_CRESCENT(3 * 24000l);



	private long ticksToGetThisPhase;

	MoonPhase(long ticksToGetThisPhase) {
		this.ticksToGetThisPhase = ticksToGetThisPhase;
	}

	/**
	 * Returns the ticks to add to a time mod 24000 to get this moon phase.
	 *
	 * @return The ticks to add.
	 */
	public long getTicksToGetThisPhase() {
		return ticksToGetThisPhase;
	}


	/**
	 * Get a phase from it's number as returned by Angus McIntyre's algorythm.
	 *
	 * @param number The phase's number. 0-7, or 8 two days per year.
	 *
	 * @return The moon phase.
	 */
	public static MoonPhase fromPhaseNumber(int number) {
		if(number >= 0 && number <= 7) return MoonPhase.values()[number];
		else                           return MoonPhase.NEW; // If number = 8. TODO is this correct?
	}
}
