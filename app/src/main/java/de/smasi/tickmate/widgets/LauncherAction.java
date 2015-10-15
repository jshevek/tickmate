package de.smasi.tickmate.widgets;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by js on 6/26/15.
 * This class deals with assigning, managing, and executing launcher actions which can be
 * associated with a track.  Each track has a launcher action, though the default is
 * "No action defined" and many users may leave all of their tracks with this kind of non-action.
 * The launcher action currently supports "Launching a user-defined app", but in the future may
 * support "opening any user specified media or other file"
 */

public class LauncherAction {
    public final static String sPreferenceKeyLauncherActionDetail = "launcher_action_detail";
    private ComponentName mComponent;

    public LauncherAction() {

    }

    public LauncherAction(ComponentName cn) {
        mComponent = cn;
    }

    public void setComponent(ComponentName component) {
        mComponent = component;
    }

    public ComponentName getComponent() {
        return mComponent;
    }

    public boolean isActionDefined() {
        return mComponent != null;
    }

    public String flattenToString() {
        if (mComponent == null) {
            return "";
        } else {
            return mComponent.flattenToString();
        }
    }

    public static LauncherAction unflattenFromString(String s) {
        if (s == "") {
            return new LauncherAction();
        } else {
            return new LauncherAction(ComponentName.unflattenFromString(s));
        }
    }

    public static void launchComponent(Context context, ComponentName launcherComponent) {
        Intent launchIntent = new Intent();
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        launchIntent.setAction(Intent.ACTION_MAIN);
        launchIntent.setComponent(launcherComponent);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launchIntent);
    }

}

