package com.lwan.javafx.controls;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.lwan.javafx.app.App;
import com.lwan.util.DateUtil;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import javafx.scene.chart.Axis;

public class DateAxis extends Axis<Date> {
	private class DateRange {
		Date min;
		Date max;
		int tick;
		double scale;	// (value - min) * scale = position along
	}
	
	protected DateRange calculateRange(double length) {
		int base = tickInterval().get();
		length = Math.abs(length);
		
		int maxTicks = (int)Math.round(length / 20);	// no more then 1 tick per 20 pixels
		
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
	
	public Date getNextTick(Date date) {
		Calendar c = getCalendar(date);
		c.add(currentRange.tick, 1);
		
		return c.getTime();
	}
	
	protected Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance(App.getLocale());
		cal.setTime(date);
		return cal;
	}
	
	// TickInterval is really the baseline date unit...
	public DateAxis(int tickInterval, Date minDate, Date maxDate) {
		this.tickInterval = new SimpleIntegerProperty(this, "TickMargin", tickInterval);
		this.minDate = new SimpleObjectProperty<Date>(this, "MinDate", minDate);
		this.maxDate = new SimpleObjectProperty<Date>(this, "MaxDate", maxDate);
		
	}
	
	@Override
	protected Object autoRange(double length) {
		currentRange = calculateRange(length);
		return currentRange;
	}

	@Override
	protected void setRange(Object range, boolean animate) {
		// uhhh... ignore?
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

	protected double getDisplayLength() {
		Side side = getSide();
		if (Side.TOP.equals(side) || Side.BOTTOM.equals(side)) {
			return getWidth();
		} else {
			return getHeight();
		}
	}
	
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
			cal.add(currentRange.tick, 1);
		}
		ticks.add(currentRange.max);
				
		return ticks;
	}

	@Override
	protected String getTickMarkLabel(Date value) {
		Calendar cal = getCalendar(value);
		
		String result = "";
		switch(currentRange.tick) {
		case Calendar.DATE:
			result = result + cal.get(Calendar.DATE) + " ";
		case Calendar.MONTH:
			result = result + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, App.getLocale()) + " ";
		case Calendar.YEAR:
			result = result + cal.get(Calendar.YEAR);		
		}
		return result;
	}

}
