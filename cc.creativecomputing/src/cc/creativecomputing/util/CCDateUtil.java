package cc.creativecomputing.util;

import java.util.Calendar;

public class CCDateUtil{

	// ////////////////////////////////////////////////////////////

	// getting the time



	/** Seconds position of the current time. */
	static public int second(){
		return Calendar.getInstance().get(Calendar.SECOND);
	}

	/** Minutes position of the current time. */
	static public int minute(){
		return Calendar.getInstance().get(Calendar.MINUTE);
	}

	/**
	 * Hour position of the current time in international format (0-23).
	 * <P>
	 * To convert this value to American time: <BR>
	 * 
	 * <PRE>
	 * 
	 * int yankeeHour = (hour() % 12); if (yankeeHour == 0) yankeeHour = 12;
	 * 
	 * </PRE>
	 */
	static public int hour(){
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * Get the current day of the month (1 through 31).
	 * <P>
	 * If you're looking for the day of the week (M-F or whatever) or day of the
	 * year (1..365) then use java's Calendar.get()
	 */
	static public int day(){
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Get the current month in range 1 through 12.
	 */
	static public int month(){
		// months are number 0..11 so change to colloquial 1..12
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	/**
	 * Get the current year.
	 */
	static public int year(){
		return Calendar.getInstance().get(Calendar.YEAR);
	}

}
