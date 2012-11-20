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
	
	private static enum VIEW_TYPE {VIEW_SERVICES, VIEW_BASES, VIEW_BASE, VIEW_LOCATION};
	private static enum SERVICE {AIR_FORCE, ARMY, NAVY, MARINES, DEFAULT};

	
	// Animations
	private Animation leftToMiddle = horizontalAnimation(-1.0f, AttaBaseContract.RIGHT);
	private Animation middleToRight = horizontalAnimation(0.0f, AttaBaseContract.RIGHT);
	private Animation rightToMiddle = horizontalAnimation(+1.0f, AttaBaseContract.LEFT);
	private Animation middleToLeft = horizontalAnimation(0.0f, AttaBaseContract.LEFT);
	private int mCurrentService;
	private int mCurrentBase;
	
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
        SharedPreferences prefs = getSharedPreferences(AttaBaseContract.APP_STRING, Context.MODE_PRIVATE);
        mCurrentBase = prefs.getInt(AttaBaseContract.PREFS_HOME_BASE_INT, AttaBaseContract.NO_BASE);
        mCurrentService = prefs.getInt(AttaBaseContract.PREFS_HOME_SERVICE_INT, AttaBaseContract.NO_BASE);
    }

    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        
        if (va.getChildCount() == 0){
	        if (mCurrentBase > 0){
	        	transitionToNewView(VIEW_TYPE.VIEW_BASE, mCurrentBase);
	        }
	        else if (mCurrentService > 0){
	        	transitionToNewView(VIEW_TYPE.VIEW_BASES, mCurrentService);
	        }
	        else {
	        	transitionToNewView(VIEW_TYPE.VIEW_SERVICES, AttaBaseContract.NO_BASE);
	        }  
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
    
    private LinearLayout populateBaseListView(int serviceIndex, int baseIndex) {
        // LOAD LAYOUT XML    
        LinearLayout ll = (LinearLayout) View.inflate(this, R.layout.base_list_view, null);
        
        // INITIALIZE THE HEADER
        LinearLayout headerLl = (LinearLayout) ll.findViewById(R.id.baseInformation);
        Header header;
		try {
			header = new Header(mCurrentView, serviceIndex, baseIndex, headerLl);
		} catch (Exception e) {
			header = null;
			e.printStackTrace();
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
    
    private LinearLayout populateBaseLocationView(int loc) {

    	String locationName, locationPhone, locationCity, locationCountry;
    	
    	// GET LOCATION INFORMATION
        Cursor location = mDbHelper.getLocation(loc);
        if (location.moveToFirst()){
        	locationName = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME));
        	locationPhone = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE1));
        	locationCity = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_CITY));
        	locationCountry = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_COUNTRY));
        }
        else
        	locationName = locationPhone = locationCity = locationCountry = "";
        location.close();

        
    	
        // LOAD LAYOUT XML    
        LinearLayout ll = (LinearLayout) View.inflate(this, R.layout.base_location_view, null);
        
        TextView locationNameText = (TextView) ll.findViewById(R.id.locationName);
        locationNameText.setText(locationName);
        TextView locationPhoneText = (TextView) ll.findViewById(R.id.locationPhone);
        locationPhoneText.setText(locationPhone);
        TextView locationCityText = (TextView) ll.findViewById(R.id.locationCity);
        locationCityText.setText(locationCity);
        TextView locationCountryText = (TextView) ll.findViewById(R.id.locationCountry);
        locationCountryText.setText(locationCountry);
        
        return ll;
    }
    
	private void transitionToNewView(VIEW_TYPE vt, int index){
    	LinearLayout ll;
    	mCurrentView = vt;
    	
    	switch (vt) {
    	case VIEW_SERVICES:
    		mCurrentService = AttaBaseContract.NO_SERVICE;
    		setCursorAdapterServicesAll();
        	ll = populateBaseListView(AttaBaseContract.NO_SERVICE, AttaBaseContract.NO_BASE);
        	va.addView(ll);
        	va.showNext();
        	break;
    	case VIEW_BASES:
    		mCurrentService = index;
    		setCursorAdapterService(mCurrentService);
        	ll = populateBaseListView(mCurrentService, AttaBaseContract.NO_BASE);
        	va.addView(ll);
        	va.showNext();
        	break;
    	case VIEW_BASE:
    		setCursorAdapterBaseLocationsAll(index);
    		ll = populateBaseListView(mCurrentService, index);
        	va.addView(ll);
        	va.showNext();
        	break;
    	case VIEW_LOCATION:
    		//setCursorAdapterBaseLocation(loc);
    		ll = populateBaseLocationView(index);
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
	
	private Animation horizontalAnimation(float startingX, int direction) {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, startingX,
				Animation.RELATIVE_TO_PARENT, startingX + (direction * 1.0f),
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}
	
	private class Header {
		private TextView textView1;
        private TextView textView2;
        private ImageView iv;
		private SERVICE service;
		private int serviceSymbol;
		private Cursor headerInfo;
		private String text1 = "";
		private String text2 = "";
		private int textColor1 = Color.BLACK;
		private int textColor2 = Color.BLACK;
		private int backgroundColor = Color.WHITE;
		private long text1Size = 24;
		private long text2Size = 12;
		
		public Header(VIEW_TYPE viewType, int serviceIndex, int baseIndex, LinearLayout ll) throws Exception {
			setService(serviceIndex);
	        textView1 = (TextView) ll.findViewById(R.id.text1);
	        textView2 = (TextView) ll.findViewById(R.id.text2);
	    	backgroundColor = getColor1(service);
	    	textColor1 = textColor2 = getColor2(service);
	    	iv = (ImageView) ll.findViewById(R.id.serviceIcon);
	    	
	    	switch (viewType) {
			case VIEW_BASE:
				headerInfo = mDbHelper.getBase(baseIndex);
		    	if (headerInfo.moveToFirst()){
		        	text1 = headerInfo.getString(headerInfo.getColumnIndex(AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME));
		        	text2 = headerInfo.getString(headerInfo.getColumnIndex(AttaBaseContract.ServiceSchema.COLUMN_NAME_SERVICE_NAME));
		        }
		    	headerInfo.close();
				break;
			case VIEW_BASES:
				headerInfo = mDbHelper.getService(serviceIndex);
		    	if (headerInfo.moveToFirst()){
		        	text1 = headerInfo.getString(headerInfo.getColumnIndex(AttaBaseContract.ServiceSchema.COLUMN_NAME_SERVICE_NAME));
		        }
		    	headerInfo.close();

		    	textView2.setVisibility(TextView.GONE);
		    	text1Size = 30;
		    	textView1.setGravity(Gravity.CENTER_VERTICAL);
		    	break;
			case VIEW_SERVICES:
			case VIEW_LOCATION:
			default:
				throw new Exception("no header for current view type");
			}
	    	
	        if (this.text1.equals("") && this.text2.equals(""))
	        	throw new Exception("no header for current view type");
	        else{
	        	textView1.setText(this.text1);
	        	textView1.setTextColor(textColor1);
	        	textView1.setTextSize(text1Size);
	        	
	        	if (textView2.getVisibility() != TextView.GONE){
	        		textView2.setText(this.text2);
	        		textView2.setTextColor(textColor2);	    
	        		textView2.setTextSize(text2Size);
	        	}			    
			    ll.setBackgroundColor(backgroundColor);
	        }  
	        
	        iv.setVisibility(ImageView.VISIBLE);
	        iv.setImageResource(serviceSymbol);
	        iv.setBackgroundColor(backgroundColor);
		}
		
		private int getColor1(SERVICE service) {
			switch (service) {
			case AIR_FORCE:
				return getResources().getColor(R.color.af_blue);
			case ARMY:
				return getResources().getColor(R.color.army_green);
			case MARINES:
				return getResources().getColor(R.color.marines_scarlet);
			case NAVY:
				return getResources().getColor(R.color.navy_blue);
			default:
				return Color.WHITE;
			}
		}
		
		private int getColor2(SERVICE service) {
			switch (service) {
			case AIR_FORCE:
				return getResources().getColor(R.color.af_grey);
			case ARMY:
				return getResources().getColor(R.color.army_yellow);
			case MARINES:
				//return getResources().getColor(R.color.marines_scarlet);
				//return Color.WHITE;
			case NAVY:
				return getResources().getColor(R.color.navy_gold);
			default:
				return Color.BLACK;
			}
		}
		
	    private void setService(int index) {
	    	
	    	Cursor service = mDbHelper.getService(index);
	    	int serviceNumber = 0;
	        if (service.moveToFirst()){
	        	serviceNumber = service.getInt(service.getColumnIndex(AttaBaseContract.ServiceSchema._ID));
	        }
	        service.close();
	        
	        switch (serviceNumber) {
			case 1:
				this.service = SERVICE.ARMY;
				serviceSymbol = R.drawable.army_symbol;
				break;
			case 2:
				this.service = SERVICE.MARINES;
				serviceSymbol = R.drawable.marines_symbol;
				break;
			case 3:
				this.service = SERVICE.NAVY;
				serviceSymbol = R.drawable.navy_symbol;
				break;
			case 4:
				this.service = SERVICE.AIR_FORCE;
				serviceSymbol = R.drawable.af_symbol;
				break;
			default:

				break;
			}
		}
	}
}
