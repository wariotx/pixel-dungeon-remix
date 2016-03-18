package com.nyrds.pixeldungeon.ml;

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nyrds.android.util.Util;

/**
 * Created by mike on 09.03.2016.
 */
public class EventCollector {
	static private Tracker mTracker;
	static private boolean mDisabled = true;

	static public void init(Context context) {
		if (mTracker == null) {
			AnalyticsTrackers.initialize(context);

			mTracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
			mTracker.enableAdvertisingIdCollection(true);
			mDisabled = false;
		}
	}

	static public void disable() {
		mDisabled = true;
	}

	static public void logEvent(String category, String event) {
		if (!mDisabled) {
			mTracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(event).build());
		}
	}

	static public void logEvent(String category, String event, String label) {
		if (!mDisabled) {
			mTracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(event).setLabel(label).build());
		}
	}

	static public void logScene(String scene) {
		if (!mDisabled) {
			mTracker.setScreenName(scene);
			mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		}
	}

	static public void logException(Exception e) {
		if(!mDisabled) {
			mTracker.send(new HitBuilders.ExceptionBuilder().setDescription(Util.toString(e)).build());
			e.printStackTrace();
		}
	}

	static public void logFatalException(Exception e,String desc) {
		if(!mDisabled) {
			mTracker.send(new HitBuilders.ExceptionBuilder().setDescription(desc + " " +Util.toString(e)).setFatal(true).build());
			e.printStackTrace();
		}
	}
}
