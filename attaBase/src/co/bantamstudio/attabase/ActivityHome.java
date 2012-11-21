package co.bantamstudio.attabase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityHome extends Activity {
	
	public final static String EXTRA_MESSAGE = "co.bantamstudio.attaBase.MESSAGE";
	private LocationDbHelper mDbHelper;
	private Base mCurrentBase;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mDbHelper = new LocationDbHelper(this.getApplicationContext());
    }
    
    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        
        // Load boolean flag 
        SharedPreferences prefs = getSharedPreferences(AttaBaseContract.APP_STRING, Context.MODE_PRIVATE);
        boolean hasImported = prefs.getBoolean(AttaBaseContract.PREFS_IMPORTED_BOOL, false);
        int base = prefs.getInt(AttaBaseContract.PREFS_HOME_BASE_INT, AttaBaseContract.NO_BASE);
        int service = prefs.getInt(AttaBaseContract.PREFS_HOME_SERVICE_INT, AttaBaseContract.NO_SERVICE);
        
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
			mCurrentBase = new Base(mDbHelper, new Service(this,mDbHelper,service), base);
		} catch (Exception e) {
			mCurrentBase = null;
			Log.d("Exception", e.getMessage());
		}
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
        if (mCurrentBase.getLocation()!=null){
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
        }
    }
    
    public void buttonBrowse(View view){
    	Intent intent = new Intent(getBaseContext(), ActivityBaseList.class);
    	intent.putExtra(AttaBaseContract.BASE_LIST_STATE, AttaBaseContract.BASE_LIST_BASE);
    	startActivity(intent);
    }
}
