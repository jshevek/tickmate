package de.smasi.tickmate.widgets;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
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
		// TODO following two lines have some redundancy
		setTickCount(ds.getTickCountForDay(track, date));
		updateStatus();
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
		updateImage();
		updateText();
	}

	// This method is introduced to allow control of the tick color; it replaces functionality
	//  previously achieved with an xml selector
	private void updateImage() {
		// Consider  using setImageTintList(new ColorStateList(states, colors));
		//  where the states are [-]stated_checked.  Disadvantage:  limited API levels. Compat library?

		if (count > 0) {
			// TODO consider creating final static drawables (to use as is, or to copy via
			//     getConstantState().newDrawable()) rather than creating on each call
			ColorFilter cf = new LightingColorFilter(0xFFFFFF, 0xAA0000);
			Drawable buttonCenterDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tick_button_center_no_frame_64);
			Drawable buttonBorderDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tick_button_frame_64);
			buttonCenterDrawable.setColorFilter(cf);
			LayerDrawable buttonCombined = new LayerDrawable(new Drawable[]{buttonCenterDrawable, buttonBorderDrawable});

			int sdk = android.os.Build.VERSION.SDK_INT;
			if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				setBackgroundDrawable(buttonCombined);
			} else {
				setBackground(buttonCombined);
			}
		} else {
			this.setBackgroundResource(R.drawable.counter_neutral);
		}
	}

	private void updateText() {
		if (count > 0) {
			this.setText(Integer.toString(count));
		} else {
			this.setText("");
		}
	}
	
	@Override
	public void onClick(View v) {
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
		TracksDataSource ds = new TracksDataSource(this.getContext());
		boolean success = ds.removeLastTickOfDay(this.getTrack(), this.getDate());
		
		if (success) {
			updateStatus();
			Toast.makeText(this.getContext(), R.string.tick_deleted, Toast.LENGTH_SHORT).show();
		}
		
		return true;
	}
}
