package co.bantamstudio.attabase;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.ActionBarSherlock.OnCreateOptionsMenuListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.ViewAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class ActivityBaseList extends SherlockActivity {
	ActionBarSherlock mSherlock = ActionBarSherlock.wrap(this);
	//private Set<Cursor> cursors;
	private SimpleCursorAdapter mAdapter;
	private LocationDbHelper mDbHelper;
	// http://www.qubi.us/2011/09/easy-viewanimator-transition-slide.html
	private ViewAnimator va;
	private VIEW_TYPE mCurrentView; 
	
	public static enum VIEW_TYPE {VIEW_SERVICES, VIEW_BASES, VIEW_BASE, VIEW_LOCATION};
	public static enum SERVICE {AIR_FORCE, ARMY, NAVY, MARINES, DEFAULT};
	//private int mCurrentService;
	//private int mCurrentBase;
	private Service mCurrentService;
	private Base mCurrentBase;
	
	// Animations
	private Animation leftToMiddle = AttaBaseContract.horizontalAnimation(-1.0f, AttaBaseContract.RIGHT);
	private Animation middleToRight = AttaBaseContract.horizontalAnimation(0.0f, AttaBaseContract.RIGHT);
	private Animation rightToMiddle = AttaBaseContract.horizontalAnimation(+1.0f, AttaBaseContract.LEFT);
	private Animation middleToLeft = AttaBaseContract.horizontalAnimation(0.0f, AttaBaseContract.LEFT);
	private Location mCurrentLocation;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);
        va = (ViewAnimator)findViewById(R.id.listAnimator);
        va.setInAnimation(rightToMiddle);
        va.setOutAnimation(middleToLeft);
        
        //loadServices(findViewById(R.id.button1));
        mDbHelper = new LocationDbHelper(this.getApplicationContext());
        
        // Load set preferences
        //SharedPreferences prefs = getSharedPreferences(AttaBaseContract.APP_STRING, Context.MODE_PRIVATE);
        //mCurrentBase = prefs.getInt(AttaBaseContract.PREFS_HOME_BASE_INT, AttaBaseContract.NO_BASE);
        //mCurrentService = prefs.getInt(AttaBaseContract.PREFS_HOME_SERVICE_INT, AttaBaseContract.NO_BASE);
    }

    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        
        if (va.getChildCount() == 0){
	        //if (mCurrentBase > 0){
	        //	transitionToNewView(VIEW_TYPE.VIEW_BASE, mCurrentBase);
	        //}
	        //else if (mCurrentService > 0){
	        //	transitionToNewView(VIEW_TYPE.VIEW_BASES, mCurrentService);
	        //}
	        //else {
	        	transitionToNewView(VIEW_TYPE.VIEW_SERVICES, AttaBaseContract.NO_BASE);
	        //}  
        }
    }
    
//    @Override
//    public boolean onCreateOptionsMenu(android.view.Menu menu) {
//        return mSherlock.dispatchCreateOptionsMenu(menu);
//    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;

        menu.add("Save")
            .setIcon(R.drawable.ic_input_get)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add("Search")
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menu.add("Refresh")
            .setIcon(R.drawable.ic_dialog_map)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
//        MenuItem miExample1 = menu.add("Example1");
//        miExample1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//         
//        MenuItem miDismiss = menu.add("Dismiss");
//        miDismiss.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//         
//        miDismiss.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//            public boolean onMenuItemClick(MenuItem item) {
// 
//                finish();
// 
//                return true;
//            }
//     
//        });
//         
//        return true;
    }
    
    @Override
    protected void onDestroy(){
    	if (mDbHelper!=null){
    		mDbHelper.close();
    	}
    	
    	super.onDestroy();
    }
    
    @Override
    public void onBackPressed() {
       Log.d("CDA", "onBackPressed Called");
       if (va.getChildCount() > 1){
    	   int child = va.getDisplayedChild();
           va.setInAnimation(leftToMiddle);
           va.setOutAnimation(middleToRight);
    	   va.showPrevious();
    	   va.removeViewAt(child);
           va.setInAnimation(rightToMiddle);
           va.setOutAnimation(middleToLeft);
    	   mCurrentView = getPreviousViewType(mCurrentView);
       }      
       else
    	   super.onBackPressed();
    }
    
    private LinearLayout populateBaseListView(Service service, Base base) {
        // LOAD LAYOUT XML    
        LinearLayout ll = (LinearLayout) View.inflate(this, R.layout.base_list_view, null);
        
        // INITIALIZE THE HEADER
        LinearLayout headerLl = (LinearLayout) ll.findViewById(R.id.baseInformation);
        Header header;
		try {
			header = new Header(mCurrentView, headerLl, service, base);
		} catch (Exception e) {
			header = null;
			Log.d("Exception", e.getMessage());
		}
        if (header == null){
        	headerLl.setVisibility(LinearLayout.GONE);
        }
        else {
        	headerLl.setVisibility(LinearLayout.VISIBLE);
        }
        
        
        // INITIALIZE THE LIST
        ListView locations = (ListView) ll.findViewById(R.id.baseListView1);
		locations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				transitionToNewView(getNextViewType(mCurrentView), (int)arg3);
			}
		});
        // POPULATE THE LIST WITH LOCATIONS
        locations.setAdapter(mAdapter);

        return ll;
    }
    
    private LinearLayout populateBaseLocationView(Location loc) {

        // LOAD LAYOUT XML    
        LinearLayout ll = (LinearLayout) View.inflate(this, R.layout.base_location_view, null);
        
        TextView locationNameText = (TextView) ll.findViewById(R.id.locationName);
        locationNameText.setText(loc.getLocationName());
        TextView locationPhoneText = (TextView) ll.findViewById(R.id.locationPhone);
        locationPhoneText.setText(loc.getLocationPhone1());
        TextView locationCityText = (TextView) ll.findViewById(R.id.locationCity);
        locationCityText.setText(loc.getLocationCity());
        TextView locationCountryText = (TextView) ll.findViewById(R.id.locationCountry);
        locationCountryText.setText(loc.getLocationCountry());
        
        return ll;
    }
    
	private void transitionToNewView(VIEW_TYPE vt, int index){
    	LinearLayout ll;
    	mCurrentView = vt;
    	
    	switch (vt) {
    	case VIEW_SERVICES:
    		mCurrentService = null;
    		mCurrentBase = null;
    		setCursorAdapterServicesAll();
        	ll = populateBaseListView(mCurrentService, mCurrentBase);
        	va.addView(ll);
        	va.showNext();
        	break;
    	case VIEW_BASES:
    		try {
				mCurrentService = new Service(this, mDbHelper, index);
			} catch (Exception e1) {
				Log.d("Exception", e1.getMessage());
			};
    		mCurrentBase = null;
    		setCursorAdapterService(index);
        	ll = populateBaseListView(mCurrentService, mCurrentBase);
        	va.addView(ll);
        	va.showNext();
        	break;
    	case VIEW_BASE:
    		try {
				mCurrentBase = new Base(mDbHelper,mCurrentService,index);
			} catch (Exception e) {
				Log.d("Exception", e.getMessage());
			}
    		setCursorAdapterBaseLocationsAll(index);
    		ll = populateBaseListView(mCurrentService, mCurrentBase);
        	va.addView(ll);
        	va.showNext();
        	break;
    	case VIEW_LOCATION:
    		//setCursorAdapterBaseLocation(loc);
    		try {
				mCurrentLocation = new Location(mDbHelper,index,mCurrentService, mCurrentBase);
			} catch (Exception e) {
				Log.d("Exception", e.getMessage());
			}
    		ll = populateBaseLocationView(mCurrentLocation);
    		va.addView(ll);
        	va.showNext();
        	break;
    	default:
    	
    	}    	
    }
    
	private VIEW_TYPE getNextViewType(VIEW_TYPE vt){
    	switch(vt){
    	case VIEW_SERVICES:
    		return VIEW_TYPE.VIEW_BASES;
    	case VIEW_BASES:
    		return VIEW_TYPE.VIEW_BASE;
    	case VIEW_BASE:
    		return VIEW_TYPE.VIEW_LOCATION;
    	case VIEW_LOCATION: 
    		return VIEW_TYPE.VIEW_LOCATION;
    	default:
    		return VIEW_TYPE.VIEW_SERVICES;
    	}
    }
    
    private VIEW_TYPE getPreviousViewType(VIEW_TYPE vt){
    	switch(vt){
    	case VIEW_SERVICES:
    		return VIEW_TYPE.VIEW_SERVICES;
    	case VIEW_BASES:
    		return VIEW_TYPE.VIEW_SERVICES;
    	case VIEW_BASE:
    		return VIEW_TYPE.VIEW_BASES;
    	case VIEW_LOCATION: 
    		return VIEW_TYPE.VIEW_BASE;
    	default:
    		return VIEW_TYPE.VIEW_BASE;
    	}
    }
    
    @SuppressWarnings("deprecation")
    private void setCursorAdapterBaseLocationsAll(int base){
    	Cursor baseLocations = mDbHelper.getBaseLocations(base);
    	startManagingCursor(baseLocations);
        String[] columns = new String[] {AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME, AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME};
        int to[] = new int[] {R.id.name_entry, R.id.name_entry_sub};
        mAdapter = new SimpleCursorAdapter(this, R.layout.base_list_item, baseLocations, columns, to);
    }
    
    @SuppressWarnings("deprecation")
    private void setCursorAdapterServicesAll(){
    	Cursor services = mDbHelper.getAllServices();
    	startManagingCursor(services);
        String[] columns = new String[] {AttaBaseContract.ServiceSchema.COLUMN_NAME_SERVICE_NAME};
        int to[] = new int[] {R.id.name_entry};
        mAdapter = new SimpleCursorAdapter(this, R.layout.base_list_item, services, columns, to);
    }
    
    @SuppressWarnings("deprecation")
    private void setCursorAdapterService(int service){
    	Cursor bases = mDbHelper.getAllBases(service);
    	startManagingCursor(bases);
        String[] columns = new String[] {AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME};
        int to[] = new int[] {R.id.name_entry};
        mAdapter = new SimpleCursorAdapter(this, R.layout.base_list_item, bases, columns, to);
    }
//    @SuppressWarnings("deprecation")
//    private void setCursorAdapterBaseLocation(int loc){
//    	Cursor location = mDbHelper.getBaseLocation(loc);
//    	startManagingCursor(location);
//        String[] columns = new String[] {AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME, AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME};
//        int to[] = new int[] {R.id.name_entry, R.id.name_entry_sub};
//        mAdapter = new SimpleCursorAdapter(this, R.layout.activity_base_list_entry, location, columns, to);
//    }
    
    @SuppressWarnings("deprecation")
    private void setCursorAdapterBaseLocationTypes(int base){
    	Cursor baseLocationTypes = mDbHelper.getBaseLocationTypes(base);
    	startManagingCursor(baseLocationTypes);
        String[] columns = new String[] {AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME};
        int to[] = new int[] {R.id.name_entry};
        mAdapter = new SimpleCursorAdapter(this, R.layout.base_list_item, baseLocationTypes, columns, to);
    }
}
