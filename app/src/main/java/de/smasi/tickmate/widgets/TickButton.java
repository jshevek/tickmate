package de.smasi.tickmate.widgets;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

import de.smasi.tickmate.R;
import de.smasi.tickmate.database.TracksDataSource;
import de.smasi.tickmate.models.Track;


public class TickButton extends ToggleButton implements OnCheckedChangeListener {

    private Track mTrack;
    private TracksDataSource mDataSource;
    private Calendar mDate;

    private LayerDrawable mTickedDrawable;
    private Drawable mUnTickedDrawable;

    public TickButton(Context context, Track track, Calendar date, TracksDataSource ds) {
        super(context);

        // Prepare the layers & color filter for the LayerDrawable
        ColorFilter cf = new LightingColorFilter(0xFFFFFF, 0xFF0000);  // TODO use a non-hardcoded value for the color (int add)
        // ^^ TODO Consider whether other color filters, or other multiplier values, would serve us better
        Drawable buttonCenterDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tick_button_center_no_frame_64);
        Drawable buttonBorderDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tick_button_frame_64);
        buttonCenterDrawable.setColorFilter(cf);

        mUnTickedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.off_64);
        mTickedDrawable = new LayerDrawable(new Drawable[] { buttonCenterDrawable , buttonBorderDrawable} );

        mTrack = track;
        mDate = (Calendar)date.clone();
        //this.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 20));

        /////////
        // TODO Look for dynamic color solution which uses xml:
        // I don't know if its possible to do both at the same time: (a) use an xml selector
        // to give us a state-dependant ([un]checked) Drawable, and (b) programmatically control the color
        //  using a ColorFilter.  Therefore I'm no longer using toggle_button.xml  -js
//        this.setBackgroundResource(R.drawable.toggle_button);

        int size = 32;
        this.setWidth(size);
        this.setMinWidth(size);
        this.setMaxWidth(size);
        this.setHeight(size);
        this.setMinHeight(size);
        this.setPadding(0, 0, 0, 0);
        this.setTextOn("");
        this.setTextOff("");
        //this.setAlpha((float) 0.8);

        mDataSource = ds;

        setChecked(ds.isTicked(track, date, false));
        this.setOnCheckedChangeListener(this);

        setBackgroundDrawable(isChecked() ? mTickedDrawable : mUnTickedDrawable);
    }

    public Track getTrack() {
        return mTrack;
    }

    public Calendar getDate () {
        return mDate;
    }

    // Evaluates whether this TickButton should be check-able.
    // This depends on the preferences (have ticks been disable outside of today?)
    //  the current date, and the date of this TickButton
    private boolean isCheckChangePermitted() {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String limitActivePref = sharedPrefs.getString("active-date-key", "ALLOW_ALL");

        Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);

        switch (limitActivePref) {
            case "ALLOW_CURRENT":
                return (mDate.compareTo(today) == 0);
            case "ALLOW_CURRENT_AND_NEXT_DAY":
                Calendar yesterday = (Calendar) today.clone();
                yesterday.add(Calendar.DATE, -1);
                return (mDate.compareTo(yesterday) >= 0);
            case "ALLOW_ALL":
            default:
                return true;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton arg0, boolean ticked) {
        if (! isCheckChangePermitted()) {
            arg0.setChecked(!arg0.isChecked());
            Toast.makeText(getContext(), R.string.notify_user_ticking_disabled, Toast.LENGTH_LONG).show();
            return;
        }
        TickButton tb = (TickButton)arg0;

        TracksDataSource ds = new TracksDataSource(this.getContext());
        if (ticked) {
            ds.setTick(tb.getTrack(), tb.getDate(), true);
            setBackgroundDrawable(mUnTickedDrawable);  // Using getConstantState().newDrawable() caused bug
        } else {
            ds.removeTick(tb.getTrack(), tb.getDate());
            setBackgroundDrawable(mTickedDrawable);
        }
    }
}
