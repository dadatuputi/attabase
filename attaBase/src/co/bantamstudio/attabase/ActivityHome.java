package co.bantamstudio.attabase;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityHome extends Activity {
	
	public final static String EXTRA_MESSAGE = "co.bantamstudio.attaBase.MESSAGE";
	private Base mCurrentBase;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        
        // Load boolean flag 
        SharedPreferences prefs = getSharedPreferences(AttaBaseContract.APP_STRING, Context.MODE_PRIVATE);
        boolean hasImported = prefs.getBoolean(AttaBaseContract.PREFS_IMPORTED_BOOL, false);
        long base = prefs.getLong(AttaBaseContract.PREFS_HOME_BASE_INT, AttaBaseContract.NO_BASE);
        long service = prefs.getLong(AttaBaseContract.PREFS_HOME_SERVICE_INT, AttaBaseContract.NO_SERVICE);
        
        // If initial import hasn't occurred, start import activity
        if (!hasImported){
        	Intent intent = new Intent(this.getApplicationContext(), ActivityImportCSV.class);
        	startActivity(intent);
        }
        else if(base == AttaBaseContract.NO_BASE || service == AttaBaseContract.NO_SERVICE){
        	Intent intent = new Intent(this.getApplicationContext(), ActivityWizard.class);
        	startActivity(intent);
        }        
        
        try {
			mCurrentBase = new Base(this, new Service(this,service), base);
		} catch (Exception e) {
			mCurrentBase = null;
		}
        setContentView(R.layout.activity_home);
        populateHomeScreen();
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        
        // Activity being restarted from stopped state    
    }
    
    private void populateHomeScreen(){
    	//getBaseAddress
    	
        // LOAD LAYOUT XML    
        LinearLayout ll = (LinearLayout) findViewById(R.id.addressBlockSmall);
        if (mCurrentBase != null && mCurrentBase.getLocation()!=null){
        	((TextView)ll.findViewById(R.id.baseName)).setText(mCurrentBase.getBaseString());
        	((TextView)ll.findViewById(R.id.address1)).setText(mCurrentBase.getLocation().getLocationAddress1());
        	((TextView)ll.findViewById(R.id.address2)).setText(mCurrentBase.getLocation().getLocationAddress2());
        	((TextView)ll.findViewById(R.id.address3)).setText(mCurrentBase.getLocation().getLocationAddress3());
        	((TextView)ll.findViewById(R.id.city)).setText(mCurrentBase.getLocation().getLocationCity());
        	((TextView)ll.findViewById(R.id.state)).setText(mCurrentBase.getLocation().getLocationState());
        	((TextView)ll.findViewById(R.id.zip)).setText(mCurrentBase.getLocation().getLocationZip());
        	((TextView)ll.findViewById(R.id.country)).setText(mCurrentBase.getLocation().getLocationCountry());
        	((TextView)ll.findViewById(R.id.address3)).setText(mCurrentBase.getLocation().getLocationAddress3());
        	((TextView)ll.findViewById(R.id.phone)).setText(mCurrentBase.getLocation().getLocationPhone1());
        	((TextView)ll.findViewById(R.id.website)).setText(mCurrentBase.getLocation().getWebsite1());
        	
        	if (mCurrentBase.getLocation().getWebsite1().equalsIgnoreCase(""))
    			((LinearLayout)ll.findViewById(R.id.websiteGroup)).setVisibility(LinearLayout.GONE);
        	if (mCurrentBase.getLocation().getLocationPhone1().equalsIgnoreCase(""))
        		((LinearLayout)ll.findViewById(R.id.phoneGroup)).setVisibility(LinearLayout.GONE);
        	if (mCurrentBase.getLocation().getLocationAddress1().equalsIgnoreCase(""))
        		((TextView)ll.findViewById(R.id.address1)).setVisibility(LinearLayout.GONE);
        	if (mCurrentBase.getLocation().getLocationAddress2().equalsIgnoreCase(""))
        		((TextView)ll.findViewById(R.id.address2)).setVisibility(LinearLayout.GONE);
        	if (mCurrentBase.getLocation().getLocationAddress3().equalsIgnoreCase(""))
        		((TextView)ll.findViewById(R.id.address3)).setVisibility(LinearLayout.GONE);
        	
        }
    }
    
    public void buttonBrowse(View view){
    	Intent intent = new Intent(getBaseContext(), ActivityBaseList.class);
    	startActivity(intent);
    }
    
    public void goToBase(View view){
    	Intent intent = new Intent(getBaseContext(), ActivityBaseList.class);
    	intent.putExtra(AttaBaseContract.BASE_LIST_STATE, AttaBaseContract.BASE_LIST_BASE);
    	intent.putExtra(AttaBaseContract.BASE_LIST_SERVICE_INDEX, mCurrentBase.getService().getServiceIndex());
    	intent.putExtra(AttaBaseContract.BASE_LIST_BASE_INDEX, mCurrentBase.getBaseIndex());
    	startActivity(intent);
    }
    public void goToWebsite(View view){
    	Uri websiteUri = Uri.parse(mCurrentBase.getLocation().getWebsite1());
    	Intent intent = new Intent(Intent.ACTION_VIEW, websiteUri);
    	startActivity(intent);
    }
    public void goToDialer(View view){
    	Uri phoneUri = Uri.parse("tel:"+mCurrentBase.getLocation().getLocationPhone1());
    	Intent intent = new Intent(Intent.ACTION_VIEW, phoneUri);
    	startActivity(intent);
    }
    @SuppressLint("NewApi")
	public void goToMap(View view){
    	String address = 	mCurrentBase.getLocation().getLocationAddress1() +
    						(mCurrentBase.getLocation().getLocationAddress2().equalsIgnoreCase("")?"":(", "+mCurrentBase.getLocation().getLocationAddress2())) +
    						(mCurrentBase.getLocation().getLocationAddress3().equalsIgnoreCase("")?"":(", "+mCurrentBase.getLocation().getLocationAddress3())) +
    						(mCurrentBase.getLocation().getLocationAddress4().equalsIgnoreCase("")?"":(", "+mCurrentBase.getLocation().getLocationAddress4())) +
    						(mCurrentBase.getLocation().getLocationCity().equalsIgnoreCase("")?"":(", "+mCurrentBase.getLocation().getLocationCity())) +
    						(mCurrentBase.getLocation().getLocationState().equalsIgnoreCase("")?"":(", "+mCurrentBase.getLocation().getLocationState())) +
    						(mCurrentBase.getLocation().getLocationZip().equalsIgnoreCase("")?"":(" "+mCurrentBase.getLocation().getLocationZip())) +
    						(mCurrentBase.getLocation().getLocationCountry().equalsIgnoreCase("")?"":(", "+mCurrentBase.getLocation().getLocationCountry()));
    	String addressLabel = mCurrentBase.getBaseString();
    	
    	Uri locationUri = null;
    	boolean useSimpleLocation = true;
    	
    	if (Build.VERSION.SDK_INT >= 9 && Geocoder.isPresent()){
    		useSimpleLocation = false;
    		Geocoder gc = new Geocoder(this);
    		List<Address> al = null;
			try {
				al = gc.getFromLocationName(address, 1);
	    		double lat = al.get(0).getLatitude();
	    		double lon = al.get(0).getLongitude();
	    		locationUri = Uri.parse("geo:"+lat+","+lon+"?z=1");
			} catch (IOException e) {
				Log.d(AttaBaseContract.APP_STRING, e.getMessage());
				useSimpleLocation = true;
			}
    	}
    	
    	if (useSimpleLocation)    	
    		locationUri = Uri.parse("geo:0,0?q="+address+"("+addressLabel+")");
				
    	if (locationUri != null){
    		Intent intent = new Intent(Intent.ACTION_VIEW, locationUri);
        	startActivity(intent);
    	}
    }
}
