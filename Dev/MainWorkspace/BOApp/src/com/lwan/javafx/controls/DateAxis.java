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
	public Date getEffectiveMinDate() {
		Calendar min = getCalendar(minDate().getValue());
		return DateUtil.floor(min, getEffectiveTickInterval()).getTime();
	}
	public Date getEffectiveMaxDate() {
		Calendar max = getCalendar(maxDate().getValue());
		int interval = getEffectiveTickInterval();
		Calendar result = DateUtil.ceil(max, interval);
		
		if (result.equals(getEffectiveMinDate())) {
			// we don't want the min and max to every be equal...
			max.add(interval, 1);
		}
		
		return max.getTime();
	}
	
	public Date getNextTick(Date date) {
		Calendar c = getCalendar(date);
		c.add(getEffectiveTickInterval(), 1);
		return c.getTime();
	}
	
	protected Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance(App.getLocale());
		cal.setTime(date);
		return cal;
	}
	
	
	// TickInterval is really the baseline date unit...
	// Calendar.Date for dates
	// Calendar.Minute ? for time
	public DateAxis(int tickInterval, Date minDate, Date maxDate) {
		setAutoRanging(false);	// we don't ever want autoranging
		this.tickInterval = new SimpleIntegerProperty(this, "TickMargin", tickInterval);
		this.minDate = new SimpleObjectProperty<Date>(this, "MinDate", minDate);
		this.maxDate = new SimpleObjectProperty<Date>(this, "MaxDate", maxDate);
	}
	
	
	
	@Override
	protected Object autoRange(double length) {
		return new Object[]{getEffectiveMinDate(), getEffectiveMaxDate(), getEffectiveTickInterval()};
	}

	@Override
	protected void setRange(Object range, boolean animate) {
		// uhhh... ignore?
	}
	
	protected int getEffectiveTickInterval() {
		int base = tickInterval().get();
		double length = Math.abs(getDisplayLength());
		if (length == 0) {
			length = 600;	// guess?
//			return Calendar.YEAR;	// not initialised yet
		}
		
		int maxTicks = (int)Math.round(length / 20);	// 1 per 20 pixels?
		
		Calendar min = getCalendar(minDate().getValue()); //getEffectiveMinDate());
		Calendar max = getCalendar(maxDate().getValue());//getEffectiveMaxDate());
		
		Calendar tmp = null;// = (Calendar)min.clone();
		
		do {
			if (tmp != null) {
				base = DateUtil.getAboveMode(base);
			}
			tmp = (Calendar)min.clone();
			tmp.add(base, maxTicks);
			
		} while (tmp.before(max));
		
		return base;
	}

	@Override
	protected Object getRange() {
		return new Object[]{
			getEffectiveMinDate(),
			getEffectiveMaxDate(),
			getEffectiveTickInterval()
		};
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
		Object[] range = (Object[])getRange();		
		Date minDate = (Date)range[0];
		Date maxDate = (Date)range[1];
		if (value.before(minDate) || value.after(maxDate)) {
			// out of range...
			return Double.NaN;
		}		
		
		long min = minDate.getTime();
		long max = maxDate.getTime();
		long timeLength = max - min;
		double date = value.getTime();
		
		double length = getDisplayLength();
		return ((date - min) / timeLength) * length;
	}

	@Override
	public Date getValueForDisplay(double displayPosition) {
		Object[] range = (Object[])getRange();		
		Date minDate = (Date)range[0];
		Date maxDate = (Date)range[1];
		
		long min = minDate.getTime();
		long max = maxDate.getTime();
		long timeLength = max - min;
		
		double length = getDisplayLength();
		long date = Math.round(((displayPosition / length) * timeLength) + min);
		
		return new Date(date);
	}

	@Override
	public boolean isValueOnAxis(Date value) {
		return value.compareTo(getEffectiveMinDate()) >= 0 &&
				value.compareTo(getEffectiveMaxDate()) <= 0;
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
		Object[] rng = (Object[])range;
		Date min = (Date)rng[0];
		Date max = (Date)rng[1];
		int interval = (int)rng[2];
		
		
		Calendar cal = getCalendar(min);
		Calendar maxCal = getCalendar(max);
		
		List<Date> ticks = new Vector<>();
		while (cal.before(maxCal)) {
			ticks.add(cal.getTime());	
			cal.add(interval, 1);
		}
		ticks.add(max);
				
		return ticks;
	}

	@Override
	protected String getTickMarkLabel(Date value) {
		Calendar cal = getCalendar(value);
		
		String result = "";
		switch(getEffectiveTickInterval()) {
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
