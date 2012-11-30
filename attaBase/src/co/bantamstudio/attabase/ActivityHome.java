package co.bantamstudio.attabase;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityHome extends SherlockActivity {
	
	public final static String EXTRA_MESSAGE = "co.bantamstudio.attaBase.MESSAGE";
	private Base mCurrentBase;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setTheme(R.style.Theme_Sherlock_Light);
    	super.onCreate(savedInstanceState);
        
        // INITIALIZE GA TRACKER
        AttaBaseContract.gaTracker.startNewSession(AttaBaseContract.gaID, 60, getApplicationContext());
    }
    
    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        
        // CHECK TO SEE IF USER HAS SET DEFAULT BASE / SERVICE
        SharedPreferences prefs = getSharedPreferences(AttaBaseContract.APP_STRING, Context.MODE_PRIVATE);
        boolean hasImported = prefs.getBoolean(AttaBaseContract.PREFS_IMPORTED_BOOL, false);
        long base = prefs.getLong(AttaBaseContract.PREFS_HOME_BASE_INT, AttaBaseContract.NO_BASE);
        long service = prefs.getLong(AttaBaseContract.PREFS_HOME_SERVICE_INT, AttaBaseContract.NO_SERVICE);
        
        // IF INITIAL IMPORT HASN'T OCCURED, IMPORT CSV FILE
        if (!hasImported){
        	AttaBaseContract.gaTracker.trackPageView("Import");
        	Intent intent = new Intent(this.getApplicationContext(), ActivityImportCSV.class);
        	startActivity(intent);
        }
        else if(base == AttaBaseContract.NO_BASE || service == AttaBaseContract.NO_SERVICE){
        	AttaBaseContract.gaTracker.trackPageView("Wizard");
        	Intent intent = new Intent(this.getApplicationContext(), ActivityWizard.class);
        	startActivity(intent);
        }        
        
        // TRY TO BUILD A BASE OBJECT WITH USER SETTINGS
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM |
                ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE |
                ActionBar.DISPLAY_USE_LOGO);
   	
    	
    	// CREATE DONATE MENU
    	MenuItem donateMenu = menu.add("Donate");
    	donateMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    	donateMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
		    	Uri websiteUri = Uri.parse(AttaBaseContract.PAYPAL_DONATE);
		    	Intent intent = new Intent(Intent.ACTION_VIEW, websiteUri);
		    	AttaBaseContract.gaTracker.trackPageView("Donate");
		    	startActivity(intent);
				return true;
			}
		});
    	
    	// CREATE FEEDBACK MENU
    	MenuItem feedBackMenu = menu.add("Feedback");
    	feedBackMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    	feedBackMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
		    	Uri websiteUri = Uri.parse(AttaBaseContract.FEEDBACK_LINK);
		    	Intent intent = new Intent(Intent.ACTION_VIEW, websiteUri);
		    	AttaBaseContract.gaTracker.trackPageView("Feedback");
		    	startActivity(intent);
				return true;
			}
		});
    	return super.onCreateOptionsMenu(menu);
    }
    
    private void populateHomeScreen(){
        // LOAD LAYOUT XML    
        LinearLayout ll = (LinearLayout) findViewById(R.id.addressBlockSmall);
        if (mCurrentBase != null && mCurrentBase.hasAddress() && mCurrentBase.getLocation()!=null){
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
        // IF BASE DOESN'T HAVE ADDRESS, JUST SHOW LINK TO BASE VIEW
        else if (mCurrentBase != null){
        	((TextView)ll.findViewById(R.id.baseName)).setText(mCurrentBase.getBaseString());
        	((LinearLayout)ll.findViewById(R.id.addressGroup)).setVisibility(LinearLayout.GONE);
        	((LinearLayout)ll.findViewById(R.id.phoneGroup)).setVisibility(LinearLayout.GONE);
        	((LinearLayout)ll.findViewById(R.id.websiteGroup)).setVisibility(LinearLayout.GONE);
        }
        // IF BASE IS NULL, DON'T SHOW ANYTHING BUT THE BROWSE
        else {
        	((LinearLayout)ll.findViewById(R.id.addressBlockSmall)).setVisibility(LinearLayout.GONE);
        }
    }
    
    // BUTTON AT BOTTOM OF HOME SCREEN TO BROWSE ALL LOCATIONS
    public void buttonBrowse(View view){
    	// GA
    	AttaBaseContract.gaTracker.trackPageView("Browse All");
    	
    	Intent intent = new Intent(getBaseContext(), ActivityBaseList.class);
    	startActivity(intent);
    }
    // VIEW HOME BASE
    public void goToBase(View view){
    	// GA
    	AttaBaseContract.gaTracker.trackPageView("Home Base View");
    	
    	Intent intent = new Intent(getBaseContext(), ActivityBaseList.class);
    	intent.putExtra(AttaBaseContract.BASE_LIST_STATE, AttaBaseContract.BASE_LIST_BASE);
    	intent.putExtra(AttaBaseContract.BASE_LIST_SERVICE_INDEX, mCurrentBase.getService().getServiceIndex());
    	intent.putExtra(AttaBaseContract.BASE_LIST_BASE_INDEX, mCurrentBase.getBaseIndex());
    	startActivity(intent);
    }
    public void goToWebsite(View view){
    	Uri websiteUri = Uri.parse(mCurrentBase.getLocation().getWebsite1());
    	// GA
    	AttaBaseContract.gaTracker.trackEvent("External URL", "Home", mCurrentBase.getLocation().getWebsite1(), (int) mCurrentBase.getBaseIndex());
    	
    	Intent intent = new Intent(Intent.ACTION_VIEW, websiteUri);
    	startActivity(intent);
    }
    public void goToDialer(View view){
    	Uri phoneUri = Uri.parse("tel:"+mCurrentBase.getLocation().getLocationPhone1());
    	// GA
    	AttaBaseContract.gaTracker.trackEvent("Dialer", "Home", mCurrentBase.getLocation().getLocationPhone1(), (int) mCurrentBase.getBaseIndex());
    	
    	Intent intent = new Intent(Intent.ACTION_VIEW, phoneUri);
    	startActivity(intent);
    }
	public void goToMap(View view){
		try {
	    	String address = 	mCurrentBase.getLocation().getLocationAddress1() +
	    						(mCurrentBase.getLocation().getLocationAddress2().equalsIgnoreCase("")?"":(", "+mCurrentBase.getLocation().getLocationAddress2())) +
	    						(mCurrentBase.getLocation().getLocationAddress3().equalsIgnoreCase("")?"":(", "+mCurrentBase.getLocation().getLocationAddress3())) +
	    						(mCurrentBase.getLocation().getLocationAddress4().equalsIgnoreCase("")?"":(", "+mCurrentBase.getLocation().getLocationAddress4())) +
	    						(mCurrentBase.getLocation().getLocationCity().equalsIgnoreCase("")?"":(", "+mCurrentBase.getLocation().getLocationCity())) +
	    						(mCurrentBase.getLocation().getLocationState().equalsIgnoreCase("")?"":(", "+mCurrentBase.getLocation().getLocationState())) +
	    						(mCurrentBase.getLocation().getLocationZip().equalsIgnoreCase("")?"":(" "+mCurrentBase.getLocation().getLocationZip())) +
	    						(mCurrentBase.getLocation().getLocationCountry().equalsIgnoreCase("")?"":(", "+mCurrentBase.getLocation().getLocationCountry()));
	    	String addressLabel = mCurrentBase.getBaseString();
	    	
	    	Uri locationUri = Uri.parse("geo:0,0?q="+address+"("+addressLabel+")");
	    	// GA
	    	AttaBaseContract.gaTracker.trackEvent("Map", "Home", addressLabel + " " + address, (int) mCurrentBase.getBaseIndex());
			
	    	Intent intent = new Intent(Intent.ACTION_VIEW, locationUri);
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.d(AttaBaseContract.APP_STRING, e.getMessage());
		}
    }
}
