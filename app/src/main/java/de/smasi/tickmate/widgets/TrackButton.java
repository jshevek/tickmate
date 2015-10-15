package de.smasi.tickmate.widgets;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import de.smasi.tickmate.models.Track;
import de.smasi.tickmate.views.ShowTrackActivity;


public class TrackButton extends ImageButton implements OnClickListener, View.OnLongClickListener {
	Track track;
    Context mContext;
	
	public TrackButton(Context context, Track track) {
		super(context);
		this.track = track;
		this.setOnClickListener(this);
        this.setOnLongClickListener(this);
		this.setImageResource(track.getIconId(context));
        mContext = context;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getContext(), ShowTrackActivity.class);
		intent.putExtra("track_id", track.getId());
		getContext().startActivity(intent);
	}

    @Override
    // TODO What should this do if no launch action has been defined? A simple toast? Launch config? 
    // Call onClick?
    public boolean onLongClick(View v) {
        LauncherAction la = track.getLauncherAction();
           if  (la.isActionDefined()) {
               LauncherAction.launchComponent(mContext, la.getComponent());
               return true;
           } else {
               // Launch settings to setup launch activity
               Toast.makeText(v.getContext(), "Launch actions may be defined in the track settings.", Toast.LENGTH_LONG).show();
               onClick(v);
//               Intent intent = new Intent(v.getContext(), TrackPreferenceActivity.class);
//               intent.putExtra("track_id", track.getId());
//               v.getContext().startActivity(intent);
               return true;
           }
    }
}
