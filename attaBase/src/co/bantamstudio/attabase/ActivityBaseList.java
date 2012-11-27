package co.bantamstudio.attabase;

import java.io.IOException;
import java.util.List;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.ActionBarSherlock.OnCreateOptionsMenuListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class ActivityBaseList extends SherlockActivity {
	ActionBarSherlock mSherlock = ActionBarSherlock.wrap(this);
	//private Set<Cursor> cursors;
	//private SimpleCursorAdapter mAdapter;
	//private LocationDbHelper mDbHelper;
	// http://www.qubi.us/2011/09/easy-viewanimator-transition-slide.html
	private ViewAnimator va;
	
	
	public static enum VIEW_TYPE {VIEW_SERVICES, VIEW_BASES, VIEW_BASE, VIEW_LOCATION};
	public static enum SERVICE {AIR_FORCE, ARMY, NAVY, MARINES, DEFENSE_LOGISTICS, STATE_PROGRAMS, DEFAULT};

	private Service mCurrentService;
	private Base mCurrentBase;
	private Location mCurrentLocation;
	private VIEW_TYPE mCurrentView; 
	private long mCurrentIndex;
	
	// Animations
	private Animation leftToMiddle = AttaBaseContract.horizontalAnimation(-1.0f, AttaBaseContract.RIGHT);
	private Animation middleToRight = AttaBaseContract.horizontalAnimation(0.0f, AttaBaseContract.RIGHT);
	private Animation rightToMiddle = AttaBaseContract.horizontalAnimation(+1.0f, AttaBaseContract.LEFT);
	private Animation middleToLeft = AttaBaseContract.horizontalAnimation(0.0f, AttaBaseContract.LEFT);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);
        InitializeUI();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_base_list);

        InitializeUI();
    }
    
    private void InitializeUI(){
    	if (va == null){
	        va = (ViewAnimator)findViewById(R.id.listAnimator);
	        va.setInAnimation(rightToMiddle);
	        va.setOutAnimation(middleToLeft);
    	}
        
        // Get message from intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(AttaBaseContract.BASE_LIST_STATE);
        try {
        	if (message == null){
	        	mCurrentView = VIEW_TYPE.VIEW_SERVICES;
	        	mCurrentIndex = AttaBaseContract.NO_BASE;
        	}
        	else if (message.equalsIgnoreCase(AttaBaseContract.BASE_LIST_LOCATION)){
	        	mCurrentView = VIEW_TYPE.VIEW_LOCATION;
	        	mCurrentService = new Service(this, intent.getLongExtra(AttaBaseContract.BASE_LIST_SERVICE_INDEX, AttaBaseContract.NO_SERVICE));
	        	mCurrentBase = new Base(this, mCurrentService, intent.getLongExtra(AttaBaseContract.BASE_LIST_BASE_INDEX, AttaBaseContract.NO_BASE));
	        	mCurrentIndex = intent.getLongExtra(AttaBaseContract.BASE_LIST_LOCATION_INDEX, AttaBaseContract.NO_BASE);        	
	        }else if (message.equalsIgnoreCase(AttaBaseContract.BASE_LIST_BASE)) {
	        	mCurrentView = VIEW_TYPE.VIEW_BASE;
	        	mCurrentService = new Service(this, intent.getLongExtra(AttaBaseContract.BASE_LIST_SERVICE_INDEX, AttaBaseContract.NO_SERVICE));
	        	mCurrentIndex = intent.getLongExtra(AttaBaseContract.BASE_LIST_BASE_INDEX, AttaBaseContract.NO_BASE);
	        }else {
	        	mCurrentView = VIEW_TYPE.VIEW_SERVICES;
	        	mCurrentIndex = AttaBaseContract.NO_BASE;
	        }
		} catch (Exception e) {
			Log.d(AttaBaseContract.APP_STRING, e.getMessage());
			mCurrentView = VIEW_TYPE.VIEW_SERVICES;
        	mCurrentIndex = AttaBaseContract.NO_BASE;
		}
    }

    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        
        if (va.getChildCount() == 0){
        	transitionToNewView(mCurrentView, mCurrentIndex);
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
    
    @SuppressWarnings("deprecation")
	private LinearLayout populateBaseListView(Service service, Base base) {
    	Cursor cursor;
    	SimpleCursorAdapter adapter;
    	String[] columns;
    	int to[];
    	
    	switch (mCurrentView) {
        case VIEW_BASES:
        	Uri serviceUri = Uri.withAppendedPath(AttaBaseProvider.CONTENT_URI_SERVICE, String.valueOf(service.getServiceIndex()));
        	serviceUri = Uri.withAppendedPath(serviceUri, "base");
        	cursor = managedQuery(serviceUri, null, null, null, null);
            columns = new String[] {AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME, AttaBaseContract.LocationSchema.COLUMN_NAME_NICE_LOCATION};
            to = new int[] {R.id.name_entry, R.id.name_entry_sub};
            adapter = new SimpleCursorAdapter(this, R.layout.base_list_item, cursor, columns, to);
        	break;
        case VIEW_SERVICES:
    		cursor = managedQuery(AttaBaseProvider.CONTENT_URI_SERVICE, null, null, null, null);
            columns = new String[] {AttaBaseContract.ServiceSchema.COLUMN_NAME_SERVICE_NAME};
            to = new int[] {R.id.name_entry};
            adapter = new SimpleCursorAdapter(this, R.layout.base_list_item, cursor, columns, to);
        	break;
        case VIEW_BASE:
        	Uri baseLocationsUri = Uri.withAppendedPath(AttaBaseProvider.CONTENT_URI_BASE, String.valueOf(base.getBaseIndex()));
        	cursor = managedQuery(baseLocationsUri, null, null, null, null);
        	columns = new String[] {AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME, AttaBaseContract.LocationSchema.COLUMN_NAME_NICE_LOCATION};
            to = new int[] {R.id.name_entry, R.id.name_entry_sub};
            adapter = new SimpleCursorAdapter(this, R.layout.base_list_item, cursor, columns, to);
            break;
		default:
			cursor = null;
			adapter = null;
			break;
		}
    	
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
        if (adapter != null){
	        ListView locations = (ListView) ll.findViewById(R.id.baseListView1);
			locations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					//Toast.makeText(getApplicationContext(),
					//		((TextView)((arg1).findViewById(R.id.name_entry))).getText(), Toast.LENGTH_SHORT).show();
					transitionToNewView(getNextViewType(mCurrentView), arg3);
				}
			});
	        // POPULATE THE LIST WITH LOCATIONS
	        locations.setAdapter(adapter);
        }
        
        // SET UP FILTER
//        mAdapter.setFilterQueryProvider(new FilterQueryProvider(){
//
//			@SuppressWarnings("deprecation")
//			public Cursor runQuery(CharSequence constraint) {
//				Cursor c;
//				switch (mCurrentView) {
//				case VIEW_BASE:
//					c = mDbHelper.getBaseLocations(mCurrentBase.getBaseIndes(), constraint);
//					break;
//				case VIEW_BASES:
//					c = mDbHelper.getAllBases(mCurrentService.getServiceIndex());
//					break;
//				case VIEW_SERVICES:
//					c = mDbHelper.getAllServices();
//					break;
//				default:
//					return null;
//				}
//				startManagingCursor(c);
//				return c;
//			}
//        	
//        });
        //locations.setTextFilterEnabled(true);
        
//        EditText et = (EditText) ll.findViewById(R.id.filterText);
//        
//        et.addTextChangedListener(new TextWatcher(){
//
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//	        		//mAdapter.getFilter().filter(s.toString());
//	        		//mAdapter.runQueryOnBackgroundThread(s.toString());
//			}
//
//        });
        
        return ll;
    }
    
    private LinearLayout populateBaseLocationView(Location loc) {

        // LOAD LAYOUT XML    
        LinearLayout ll = (LinearLayout) View.inflate(this, R.layout.base_location_view, null);
        ((TextView) ll.findViewById(R.id.baseName)).setText(loc.getLocationName());
        ((TextView) ll.findViewById(R.id.address1)).setText(loc.getLocationAddress1());
        ((TextView) ll.findViewById(R.id.address2)).setText(loc.getLocationAddress2());
        ((TextView) ll.findViewById(R.id.address3)).setText(loc.getLocationAddress3());
        ((TextView) ll.findViewById(R.id.address4)).setText(loc.getLocationAddress4());
        ((TextView) ll.findViewById(R.id.phone1)).setText(loc.getLocationPhone1());
        ((TextView) ll.findViewById(R.id.phone2)).setText(loc.getLocationPhone2());
        ((TextView) ll.findViewById(R.id.phone3)).setText(loc.getLocationPhone3());
        ((TextView) ll.findViewById(R.id.fax)).setText(loc.getLocationPhoneFax());
        ((TextView) ll.findViewById(R.id.dsn)).setText(loc.getDsn());
        ((TextView) ll.findViewById(R.id.dsn_fax)).setText(loc.getDsnFax());
        ((TextView) ll.findViewById(R.id.city)).setText(loc.getLocationCity());
        ((TextView) ll.findViewById(R.id.state)).setText(loc.getLocationState());
        ((TextView) ll.findViewById(R.id.country)).setText(loc.getLocationCountry());
        ((TextView) ll.findViewById(R.id.website1)).setText(loc.getWebsite1());
        ((TextView) ll.findViewById(R.id.website2)).setText(loc.getWebsite2());
        ((TextView) ll.findViewById(R.id.website3)).setText(loc.getWebsite3());
        ((TextView) ll.findViewById(R.id.zip)).setText(loc.getLocationZip());
        
        // Website Group Visibility
    	if (loc.getWebsite1().equalsIgnoreCase("")){
    		((TextView)ll.findViewById(R.id.website1)).setVisibility(TextView.GONE);
    		((LinearLayout)ll.findViewById(R.id.websiteGroup)).setVisibility(LinearLayout.GONE);
    	}
    	if (loc.getWebsite2().equalsIgnoreCase(""))
    		((TextView)ll.findViewById(R.id.website2)).setVisibility(TextView.GONE);
    	if (loc.getWebsite3().equalsIgnoreCase(""))
    		((TextView)ll.findViewById(R.id.website3)).setVisibility(TextView.GONE);
    	
    	// Phone Group Visibility
    	if (loc.getLocationPhone1().equalsIgnoreCase(""))
    		((TextView)ll.findViewById(R.id.phone1)).setVisibility(TextView.GONE);
    	if (loc.getLocationPhone2().equalsIgnoreCase(""))
    		((TextView)ll.findViewById(R.id.phone2)).setVisibility(TextView.GONE);
    	if (loc.getLocationPhone3().equalsIgnoreCase(""))
    		((TextView)ll.findViewById(R.id.phone3)).setVisibility(TextView.GONE);
    	if (loc.getLocationPhoneFax().equalsIgnoreCase(""))
    		((LinearLayout)ll.findViewById(R.id.faxGroup)).setVisibility(LinearLayout.GONE);
    	if (loc.getDsn().equalsIgnoreCase(""))
    		((LinearLayout)ll.findViewById(R.id.dsnGroup)).setVisibility(LinearLayout.GONE);
    	if (loc.getDsnFax().equalsIgnoreCase(""))
    		((LinearLayout)ll.findViewById(R.id.dsnFaxGroup)).setVisibility(LinearLayout.GONE);
    	if (loc.getLocationPhone1().equalsIgnoreCase("") && loc.getLocationPhone2().equalsIgnoreCase("") &&
    			loc.getLocationPhone3().equalsIgnoreCase("") && loc.getLocationPhoneFax().equalsIgnoreCase("") &&
    			loc.getDsn().equalsIgnoreCase("") && loc.getDsnFax().equalsIgnoreCase(""))
    		((LinearLayout)ll.findViewById(R.id.phoneGroup)).setVisibility(LinearLayout.GONE);
    	
    	// Address Fields Visibility    	
    	if (loc.getLocationAddress1().equalsIgnoreCase(""))
    		((TextView)ll.findViewById(R.id.address1)).setVisibility(LinearLayout.GONE);
    	if (loc.getLocationAddress2().equalsIgnoreCase(""))
    		((TextView)ll.findViewById(R.id.address2)).setVisibility(LinearLayout.GONE);
    	if (loc.getLocationAddress3().equalsIgnoreCase(""))
    		((TextView)ll.findViewById(R.id.address3)).setVisibility(LinearLayout.GONE);
    	if (loc.getLocationAddress4().equalsIgnoreCase(""))
    		((TextView)ll.findViewById(R.id.address4)).setVisibility(LinearLayout.GONE);
    	if (loc.getLocationCity().equalsIgnoreCase(""))
    		((TextView)ll.findViewById(R.id.city)).setVisibility(LinearLayout.GONE);
    	if (loc.getLocationState().equalsIgnoreCase(""))
    		((TextView)ll.findViewById(R.id.state)).setVisibility(LinearLayout.GONE);
    	if (loc.getLocationZip().equalsIgnoreCase(""))
    		((TextView)ll.findViewById(R.id.zip)).setVisibility(LinearLayout.GONE);
    	if (loc.getLocationCountry().equalsIgnoreCase(""))
    		((TextView)ll.findViewById(R.id.country)).setVisibility(LinearLayout.GONE);
    	
    	// Make phone1-3 clickable
    	((TextView)ll.findViewById(R.id.phone1)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goToDialer(v);
			}
		});
    	((TextView)ll.findViewById(R.id.phone2)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goToDialer(v);
			}
		});
    	((TextView)ll.findViewById(R.id.phone3)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goToDialer(v);
			}
		});
    	
    	// Make website1-3 clickable
    	((TextView)ll.findViewById(R.id.website1)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goToWebsite(v);
			}
		});
    	((TextView)ll.findViewById(R.id.website2)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goToWebsite(v);
			}
		});
    	((TextView)ll.findViewById(R.id.website3)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goToWebsite(v);
			}
		});
    	
    	// Make address clickable
    	((LinearLayout)ll.findViewById(R.id.addressGroup)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goToMap(v, mCurrentLocation);
			}
		});
    	
        return ll;
    }
    
	private void transitionToNewView(VIEW_TYPE vt, long index){
    	LinearLayout ll;
    	mCurrentView = vt;
    	
    	switch (vt) {
    	case VIEW_SERVICES:
    		mCurrentService = null;
    		mCurrentBase = null;
        	ll = populateBaseListView(mCurrentService, mCurrentBase);
        	va.addView(ll);
        	va.showNext();
        	break;
    	case VIEW_BASES:
    		try {
				mCurrentService = new Service(this, index);
			} catch (Exception e1) {
				Log.d("Exception", e1.getMessage());
			};
    		mCurrentBase = null;
        	ll = populateBaseListView(mCurrentService, mCurrentBase);
        	va.addView(ll);
        	va.showNext();
        	break;
    	case VIEW_BASE:
    		try {
				mCurrentBase = new Base(this, mCurrentService,index);
			} catch (Exception e) {
				Log.d("Exception", e.getMessage());
			}
    		ll = populateBaseListView(mCurrentService, mCurrentBase);
        	va.addView(ll);
        	va.showNext();
        	break;
    	case VIEW_LOCATION:
    		try {
				mCurrentLocation = new Location(this, index, mCurrentService, mCurrentBase);
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
    
    public void goToDialer(View view){
    	String number = (String) ((TextView)view).getText();
    	Uri phoneUri = Uri.parse("tel:"+number);
    	Intent intent = new Intent(Intent.ACTION_VIEW, phoneUri);
    	startActivity(intent);
    }
    public void goToWebsite(View view){
    	String website = (String) ((TextView)view).getText();
    	Uri websiteUri = Uri.parse(website);
    	Intent intent = new Intent(Intent.ACTION_VIEW, websiteUri);
    	startActivity(intent);
    }
    @SuppressLint("NewApi")
	public void goToMap(View view, Location loc){
    	String address = 	loc.getLocationAddress1() +
    						(loc.getLocationAddress2().equalsIgnoreCase("")?"":(", "+loc.getLocationAddress2())) +
    						(loc.getLocationAddress3().equalsIgnoreCase("")?"":(", "+loc.getLocationAddress3())) +
    						(loc.getLocationAddress4().equalsIgnoreCase("")?"":(", "+loc.getLocationAddress4())) +
    						(loc.getLocationCity().equalsIgnoreCase("")?"":(", "+loc.getLocationCity())) +
    						(loc.getLocationState().equalsIgnoreCase("")?"":(", "+loc.getLocationState())) +
    						(loc.getLocationZip().equalsIgnoreCase("")?"":(" "+loc.getLocationZip())) +
    						(loc.getLocationCountry().equalsIgnoreCase("")?"":(", "+loc.getLocationCountry()));
    	String addressLabel = loc.getBase().getBaseString() + " " + loc.getLocationName();
    	
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
