package com.mkarshnas6.karenstudio.worldskill.geofence

import android.content.Context

class GeofenceManager(private val context: Context) {

    companion object {
        private const val TAG = "GeofenceManager"

        // every geofence have id
        const val GEOFENCE_ID_HOME = "home_geofence"
        const val GEOFENCE_ID_WORK = "work_geofence"
    }

    // get this client of google play service
//    private val geofenceClient : GeofencingClient

}