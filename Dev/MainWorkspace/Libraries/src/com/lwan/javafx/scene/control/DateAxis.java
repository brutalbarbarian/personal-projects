package com.lwan.javafx.scene.control;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import com.lwan.util.DateUtil;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import javafx.scene.chart.Axis;

public class DateAxis extends Axis<Date> {
	private static int GAP = 60;
	
	private Locale locale;
	
	private class DateRange {
		Date min;
		Date max;
		int tick;
		double scale;	// (value - min) * scale = position along
//		int miniTicks;
		
//		boolean isMiniTick(Date d) {
//			Calendar c = getCalendar(d);
//			return !DateUtil.floor(c, tick).equals(c); 
//		}
	}
	
	protected DateRange calculateRange(double length) {
		int base = tickInterval().get();
		length = Math.abs(length);
		
		int maxTicks = (int)Math.round(length / GAP);
		
		Calendar min = getCalendar(minDate().getValue());
		Calendar max = getCalendar(maxDate().getValue());
		
		Calendar tmp = null;
		
		do {
			if (tmp != null) {
				base = DateUtil.getAboveMode(base);
			}
			tmp = (Calendar)min.clone();
			tmp.add(base, maxTicks);
			
		} while (tmp.before(max));
		
//		int miniTicks = 0;
//		
//		while (true) {
//			tmp = (Calendar)min.clone();
//			tmp.add(base, maxTicks / ((miniTicks + 1) * 2));
//			if (tmp.before(max)) {
//				break;
//			} else {
//				miniTicks ++;
//			}
//		}
		
		min = DateUtil.floor(min, base);
		max = DateUtil.ceil(max, base);
		if (min.equals(max)) {
			max.add(base, 1);
		}
		
		DateRange result = new DateRange();
		result.tick = base;
		result.min = min.getTime();
		result.max = max.getTime();		
		result.scale = length / (max.getTimeInMillis() - min.getTimeInMillis());
//		result.miniTicks = miniTicks;
		
		return result;
	}
	
	private DateRange currentRange;
	
	private IntegerProperty tickInterval;
	
	/**
	 * See Calendar modes 
	 * 
	 * @return
	 */
	public IntegerProperty tickInterval() {
		return tickInterval;
	}
	
	private Property<Date> minDate, maxDate;
	public Property<Date> minDate() {
		return minDate;
	}	
	public Property<Date> maxDate() {
		return maxDate;
	}
	
	protected Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance(locale);
		cal.setTime(date);
		return cal;
	}
	
	// TickInterval is really the baseline date unit...
	public DateAxis(int tickInterval, Date minDate, Date maxDate) {
		this(tickInterval, minDate, maxDate, Locale.getDefault());
	}
	
	public DateAxis(int tickInterval, Date minDate, Date maxDate, Locale locale) {
		this.tickInterval = new SimpleIntegerProperty(this, "TickMargin", tickInterval);
		this.minDate = new SimpleObjectProperty<Date>(this, "MinDate", minDate);
		this.maxDate = new SimpleObjectProperty<Date>(this, "MaxDate", maxDate);
		this.locale = locale;
		setTickLabelRotation(45);
		setTickLabelGap(GAP);
	}
	
	@Override
	protected Object autoRange(double length) {
		currentRange = calculateRange(length);
		return currentRange;
	}

	@Override
	protected void setRange(Object range, boolean animate) {
		// uhhh... ignore?
		if (range != currentRange) {
			currentRange = (DateRange) range;
		}
	}	

	@Override
	protected DateRange getRange() {
		return currentRange;
	}

	@Override
	public double getZeroPosition() {
		// there is no such thing as a zero position for date
		return Double.NaN;
	}
//
//	protected double getDisplayLength() {
//		Side side = getSide();
//		if (Side.TOP.equals(side) || Side.BOTTOM.equals(side)) {
//			return getWidth();
//		} else {
//			return getHeight();
//		}
//	}
	
	@Override
	public double getDisplayPosition(Date value) {
		if (value.before(currentRange.min) || value.after(currentRange.max)) {
			// out of range...
			return Double.NaN;
		}		
		
		long range = value.getTime() - getCalendar(currentRange.min).getTimeInMillis();
		return range * currentRange.scale;
	}

	@Override
	public Date getValueForDisplay(double displayPosition) {
		long date = Math.round(displayPosition / currentRange.scale) + 
				getCalendar(currentRange.min).getTimeInMillis();
		
		return new Date(date);
	}

	@Override
	public boolean isValueOnAxis(Date value) {
		return value.compareTo(currentRange.min) >= 0 &&
				value.compareTo(currentRange.max) <= 0;
	}

	@Override
	public double toNumericValue(Date value) {
		return value.getTime();
	}

	@Override
	public Date toRealValue(double value) {
		Number n = value;
		return new Date(n.longValue());
	}

	@Override
	protected List<Date> calculateTickValues(double length, Object range) {
		Calendar cal = getCalendar(currentRange.min);
		Calendar maxCal = getCalendar(currentRange.max);
		
		List<Date> ticks = new Vector<>();
		while (cal.before(maxCal)) {
			ticks.add(cal.getTime());
			
//			long pre = cal.getTimeInMillis();
			cal.add(currentRange.tick, 1);
						
//			long rng = cal.getTimeInMillis() - pre;
//			for (int i = 1; i <= currentRange.miniTicks; i++) {
//				ticks.add(new Date(pre + (rng * i / (currentRange.miniTicks + 1))));
//			}
		}
		ticks.add(currentRange.max);
				
		return ticks;
	}

	@Override
	protected String getTickMarkLabel(Date value) {
		int tick = currentRange.tick;
//		if (currentRange.isMiniTick(value)) {
//			tick = DateUtil.getBelowMode(tick);
//		}
//		
		Calendar cal = getCalendar(value);
		
		String result = "";
		switch(tick) {
		case Calendar.DATE:
		case Calendar.WEEK_OF_MONTH:
			result = result + cal.get(Calendar.DATE) + " ";
		case Calendar.MONTH:
			result = result + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, locale) + " ";
		case Calendar.YEAR:
			result = result + cal.get(Calendar.YEAR);		
		}
		return result;
	}

}
