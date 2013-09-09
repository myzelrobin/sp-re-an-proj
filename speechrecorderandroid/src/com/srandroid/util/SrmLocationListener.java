/**
 * 
 */
package com.srandroid.util;

import java.io.IOException;
import java.util.Locale;

import com.srandroid.speechrecorder.R;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * 
 */
public class SrmLocationListener extends Service implements LocationListener 
{
	private static final String LOGTAG = SrmLocationListener.class.getName();
	
	private static Location lastKnownLocation;
	
	private static final int TEN_MINUTES = 1000 * 60 * 10;
	
	private static final int MINUTES_30 = 1000 * 60 * 30;
	
	private static LocationManager locationManager;
	
	// flag for GPS status
	private static boolean isGPSEnabled = false;

	// flag for network status
	private static boolean isNetworkEnabled = false;

	// flag for get location status
	public static boolean canGetLocation = false;
	
	public static String cityName;
	
	public static double longitude;
	
	public static double latitude;

	public static String gps_data;
	
	private Context context = null;
	
	private SharedPreferences shPrefs = null;
	
	public SrmLocationListener(Context context)
	{
		this.context = context;
		
		this.shPrefs = PreferenceManager.getDefaultSharedPreferences(this.context);
	}

	// called when a new location is found
    @Override
    public void onLocationChanged(Location loc) 
    {
    	
    	Log.w(LOGTAG, "onLocationChanged(): Location changed");

		  longitude = loc.getLongitude();
		  Log.w(LOGTAG, "onLocationChanged(): get new longitude=" + longitude);
		
		  latitude = loc.getLatitude();
		  Log.w(LOGTAG, "onLocationChanged(): get new latitude=" + latitude);
	    
        
        /*-------to get City-Name from coordinates -------- */
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        try 
        {
        	cityName = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1)
        				.get(0).getLocality();
        } 
        catch (IOException e) 
        {
        	Log.w(LOGTAG, "onLocationChanged(): find cityName throws error:" + e.getMessage());
        }
        
        String s = "\nlongitude=" + longitude + ", "
			+ "\nlatitude=" + latitude + ", "
        	+ "\nCurrent City is: " + cityName;

        gps_data = s;

        Log.w(LOGTAG, "onLocationChanged(): new location is:" + s);
        
        
        // update gps info into utils and sharedpreference
        Log.w(LOGTAG, "onLocationChanged(): will updata data into utils and shPrefs");
        
        Utils.ConstantVars.GPS_DATA = gps_data;
        Utils.ConstantVars.CITYNAME = cityName;
        
        Utils.updatePreference(shPrefs, 
        		Utils.ConstantVars.KEY_GPS_DATA, 
        		gps_data);
        
        Utils.updatePreference(shPrefs, 
        		Utils.ConstantVars.KEY_CITYNAME, 
        		cityName);
        
    }

    @Override
    public void onProviderDisabled(String provider) 
    {
    	gps_data = "GPS device is disabled";
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
    

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

    
    public void getLocation() 
	{
		//Utils.ConstantVars.GPS_DATA = 
		Log.w(LOGTAG, "getLocation() will get GPS info");
		
		try 
	   	 {
	   	        locationManager = (LocationManager) this.context
	   	                .getSystemService(LOCATION_SERVICE);
	
	   	        // getting GPS status
	   	        isGPSEnabled = locationManager
	   	                .isProviderEnabled(LocationManager.GPS_PROVIDER);
	
	   	        // getting network status
	   	        isNetworkEnabled = locationManager
	   	                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	   	        
	   	        
	   	     if (!isGPSEnabled && !isNetworkEnabled) 
	   	     {
	   	    	Toast.makeText(context, "Device network is not available", Toast.LENGTH_LONG)
	   	    		.show();
	         } 
	   	     else 
	   	     {
	             canGetLocation = true;
	             
	             // First get location from Network Provider
	             if (isNetworkEnabled) 
	             {
	            	 Log.w(LOGTAG, "getLocation(), network is enabled, will retrieve GPS info from network");
	            	 
	            	 locationManager.requestLocationUpdates(
	         				LocationManager.NETWORK_PROVIDER, // type of location provider, GPS_PROVIDER
	         				TEN_MINUTES, // minimum time interval between notifications, 0 as frequently as possible
	         				1000, // minimum change in distance between notifications, 0 as frequently as possible
	         				this);
	         		
	                 if (locationManager != null) 
	                 {
	                	 lastKnownLocation = locationManager
	                             .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	                     if (lastKnownLocation != null) 
	                     {
	                    	 Log.w(LOGTAG, "getLocation(), network is enabled, retrieved location=" 
	                    			 + getlocationString(lastKnownLocation));
	                     }
	                 }
	             }
	             
	             if (isGPSEnabled) // if GPS Enabled get lat/long using GPS Services
	             {
	            	 Log.w(LOGTAG, "getLocation(), gps device is enabled, will get GPS info from gps device");
	            	 
	                 if (lastKnownLocation == null) 
	                 {
	                     locationManager.requestLocationUpdates(
	                             LocationManager.GPS_PROVIDER,
	                             TEN_MINUTES,
	                             1000, 
	                             this);
	                     
	                     if (locationManager != null) 
	                     {
	                    	 lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                    	 //  location = locationManager.getLastKnownLocation("gps");
	                         if (lastKnownLocation != null) 
	                         {
	                        	 Toast.makeText(context, 
		                    			 "Retreiving GPS info from gps device!\nThis takes a while, please wait until it is finished.", 
		                    			 Toast.LENGTH_LONG * 3)
		                    	 	.show();
	                        	 Log.w(LOGTAG, "getLocation(), gps device is enabled, retrieved location=" 
		                    			 + getlocationString(lastKnownLocation));
	                        	 
	                         }
	                     }
	                 }
	             }
	         } 
	   	 }
	   	 catch (Exception e) 
	   	 {
	   		 Log.w(LOGTAG, 
	   				  "getLocation(): start locationManager throws error:" + e.getMessage());
	   	 }
		
	}
	
    private String getlocationString(Location location)
    {
    	Log.w(LOGTAG, "getlocationString() will build gps infos as a string");

		  longitude = location.getLongitude();
		  Log.w(LOGTAG, "getlocationString(): get new longitude=" + longitude);
		
		  latitude = location.getLatitude();
		  Log.w(LOGTAG, "getlocationString(): get new latitude=" + latitude);
	  
	      /*-------to get City-Name from coordinates -------- */
	      Geocoder gcd = new Geocoder(this.context, Locale.getDefault());
	      try 
	      {
	      	cityName = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1)
	      				.get(0).getLocality();
	      	Log.w(LOGTAG, "getlocationString(): get new city name=" + cityName);
	      } 
	      catch (IOException e) 
	      {
	      	Log.w(LOGTAG, "getlocationString(): find city name throws error:" + e.getMessage());
	      }
	      
	      String s = "\nlongitude=" + longitude + ", "
				+ "\nlatitude=" + latitude + ", "
				+ "\nCurrent City is: " + cityName;
	
	      gps_data = s;
	      
	      Log.w(LOGTAG, "getlocationString() build new location string" + s);
	      
	   // update gps info into utils and sharedpreference
	        Log.w(LOGTAG, "getlocationString(): will updata data into utils and shPrefs");
	        
	        Utils.ConstantVars.GPS_DATA = gps_data;
	        Utils.ConstantVars.CITYNAME = cityName;
	        
	        Utils.updatePreference(shPrefs, 
	        		Utils.ConstantVars.KEY_GPS_DATA, 
	        		gps_data);
	        
	        Utils.updatePreference(shPrefs, 
	        		Utils.ConstantVars.KEY_CITYNAME, 
	        		cityName);
	      
	      return gps_data;
    }
    
	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * */
	public void stopUsingGPS()
	{
	    if(locationManager != null)
	    {
	        locationManager.removeUpdates(SrmLocationListener.this);
	    }
	}
    
	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	private boolean isBetterLocation(Location location, Location currentBestLocation) 
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
	    if (isMoreAccurate) 
	    {
	        return true;
	    } 
	    else if (isNewer && !isLessAccurate) 
	    {
	        return true;
	    } 
	    else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) 
	    {
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
	
	
}
