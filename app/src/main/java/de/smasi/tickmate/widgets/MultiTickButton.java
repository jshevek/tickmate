package de.smasi.tickmate.widgets;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

import de.smasi.tickmate.R;
import de.smasi.tickmate.database.TracksDataSource;
import de.smasi.tickmate.models.Track;

public class MultiTickButton extends Button implements OnClickListener, OnLongClickListener {
	Track track;
	TracksDataSource ds;
	Calendar date;
	int count;

	public MultiTickButton(Context context, Track track, Calendar date, TracksDataSource ds) {
		super(context);
		this.setOnClickListener(this);
		this.setOnLongClickListener(this);
		this.track = track;
		this.date = date;
		int size = 32;
		this.setWidth(size);
		this.setMinWidth(size);
		this.setMaxWidth(size);
		this.setHeight(size);
		this.setMinHeight(size);
		this.setPadding(0, 0, 0, 0);
		this.ds = ds;
		setTickCount(ds.getTickCountForDay(track, date));
	}

	Track getTrack () {
		return track;
	}

	Calendar getDate () {
		return date;
	}

	public void setTickCount(int count) {
		this.count = count;
		updateText();
	}
	
	private void updateStatus() {
		TracksDataSource ds = new TracksDataSource(this.getContext());
		count = ds.getTicksForDay(this.getTrack(), this.getDate()).size();
		updateText();
	}
	
	private void updateText() {		
		if (count > 0) {
			this.setBackgroundResource(R.drawable.counter_positive);
			this.setText(Integer.toString(count));
		} else {
			this.setBackgroundResource(R.drawable.counter_neutral);
			this.setText("");
		}
	}
	
	@Override
	public void onClick(View v) {
		if (!ButtonHelpers.isCheckChangePermitted(getContext(), date)) {
			Toast.makeText(getContext(), R.string.notify_user_ticking_disabled, Toast.LENGTH_LONG).show();
			return;
		}
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);

		if (c.get(Calendar.DAY_OF_MONTH) == this.date.get(Calendar.DAY_OF_MONTH)) {
			ds.setTick(this.getTrack(), c, false);
		} else {
			ds.setTick(this.getTrack(), this.date, false);
		}
		
		updateStatus();
	}
	
	@Override
	public boolean onLongClick(View v) {
		if (!ButtonHelpers.isCheckChangePermitted(getContext(), date)) {
			Toast.makeText(getContext(), R.string.notify_user_ticking_disabled, Toast.LENGTH_LONG).show();
			return false;
		}

		TracksDataSource ds = new TracksDataSource(this.getContext());
		boolean success = ds.removeLastTickOfDay(this.getTrack(), this.getDate());
		
		if (success) {
			updateStatus();
			Toast.makeText(this.getContext(), R.string.tick_deleted, Toast.LENGTH_SHORT).show();
		}
		
		return true;
	}
}
