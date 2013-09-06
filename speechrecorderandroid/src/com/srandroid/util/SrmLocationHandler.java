/**
 * 
 */
package com.srandroid.util;

import java.io.IOException;
import java.util.Locale;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * not finished
 *
 */
public class SrmLocationHandler 
{
	private static final String LOGTAG = SrmLocationHandler.class.getName();
	
	private static String gps_data;

	private static Location lastKnownLocation;
	
	private static final int TEN_MINUTES = 1000 * 60 * 10;

	private static LocationManager locationManager;
	
	private static LocationListener locationListener;
	
	public static String cityName;
	
	public static String longitude;
	
	public static String latitude;
	
	/**
	 * 
	 */
	public SrmLocationHandler() {
		// TODO Auto-generated constructor stub
	}
	
	public static void getGPSInfo(Context context) 
	{
		//Utils.ConstantVars.GPS_INFO = 
		Log.w(LOGTAG, "getGPSInfo() will get GPS info");

		locationManager = 
			(LocationManager) context.getSystemService(context.LOCATION_SERVICE);
		locationListener = new SrmLocationListener(context);
		
		// start listening for updates
		locationManager.requestLocationUpdates(
			LocationManager.NETWORK_PROVIDER, // type of location provider, GPS_PROVIDER
			1000, // minimum time interval between notifications, 0 as frequently as possible
			1000, // minimum change in distance between notifications, 0 as frequently as possible
			locationListener);
		
		lastKnownLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
		
	}
	
	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) 
	{
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TEN_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TEN_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) 
	{
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
	
	
	/**
	 * 
	 * Listener class to get coordinates 
	 * 
	 */
	private static class SrmLocationListener implements LocationListener 
	{
		private Context context = null;

		public SrmLocationListener(Context context)
		{
			this.context = context;
		}

		// called when a new location is found
	    @Override
	    public void onLocationChanged(Location loc) 
	    {
	    	Log.w(LOGTAG + "$SrmLocationListener", 
				  "onLocationChanged(): Location changed: latitude: " + loc.getLatitude() 
				  + " longitude: " + loc.getLongitude());

	        longitude = "Longitude: " + loc.getLongitude();
	        Log.w(LOGTAG + "$SrmLocationListener", 
	        		"onLocationChanged(): get new longitude=" + longitude);

	        latitude = "Latitude: " + loc.getLatitude();
	        Log.w(LOGTAG + "$SrmLocationListener", 
	        		"onLocationChanged(): get new latitude=" + latitude);
	        
	        /*-------to get City-Name from coordinates -------- */
	        Geocoder gcd = new Geocoder(context, Locale.getDefault());
	        try 
	        {
	        	cityName = gcd.getFromLocation(loc.getLatitude(),
											   loc.getLongitude(), 1).get(0).getLocality();
	        } 
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
	        String s = "longitude=" + longitude 
				+ ", latitude=" + latitude + ", Current City is: " + cityName;

	        gps_data = s;

	        Log.w(LOGTAG + "$SrmLocationListener", 
				  "onLocationChanged(): new location is:" + s);
	        
	        // close listener
			locationManager.removeUpdates(locationListener);
	    }

	    @Override
	    public void onProviderDisabled(String provider) 
	    {
	    	gps_data = "device is disabled";
	    	// need an Intent to open location settings
	    }

	    @Override
	    public void onProviderEnabled(String provider) 
	    {

	    }

	    @Override
	    public void onStatusChanged(String provider, int status, Bundle extras) 
	    {
			
	    }


	}
}
