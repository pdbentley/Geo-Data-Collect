package hosm.android.osmtracker.service.gps;

import hosm.android.osmtracker.OSMTracker;
import hosm.android.osmtracker.R;
import hosm.android.osmtracker.activity.TrackLogger;
import hosm.android.osmtracker.activity.ViewHistoryMap;
import hosm.android.osmtracker.db.TrackContentProvider.Schema;
import hosm.android.osmtracker.layout.GpsStatusRecord;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class GPSDisplayMapService implements ServiceConnection {

	/**
	 * Reference to TrackLogger activity
	 */
	private ViewHistoryMap activity;
	
	public GPSDisplayMapService(ViewHistoryMap tl) {
		activity = tl;
	}
	
	@Override
	public void onServiceDisconnected(ComponentName name) {
		activity.setEnabledActionButtons(false);
		activity.setGpsLogger(null);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		
		activity.setGpsLogger( ((GPSLogger.GPSLoggerBinder) service).getService());

		// Update record status regarding of current tracking state
		GpsStatusRecord gpsStatusRecord = (GpsStatusRecord) activity.findViewById(R.id.gpsStatus);
		if (gpsStatusRecord != null) {
			gpsStatusRecord.manageRecordingIndicator(activity.getGpsLogger().isTracking());
		}
		
		// If not already tracking, start tracking
		if (!activity.getGpsLogger().isTracking()) {
			activity.setEnabledActionButtons(false);
			Intent intent = new Intent(OSMTracker.INTENT_START_TRACKING);
			intent.putExtra(Schema.COL_TRACK_ID, activity.getCurrentTrackId());
			activity.sendBroadcast(intent);
		}
	}

}