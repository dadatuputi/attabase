package co.bantamstudio.attabase;

import co.bantamstudio.attabase.ActivityBaseList.VIEW_TYPE;
import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.ViewAnimator;

public class ActivityWizard extends SherlockActivity {
	ActionBarSherlock mSherlock = ActionBarSherlock.wrap(this);
	//private Set<Cursor> cursors;
	//private SimpleCursorAdapter mAdapter;
	//private LocationDbHelper mDbHelper;
	// http://www.qubi.us/2011/09/easy-viewanimator-transition-slide.html
	private ViewAnimator va;
	
	private static enum STEP {STEP_1, STEP_1_CHOOSE, STEP_2, STEP_2_CHOOSE, STEP_3};
	private STEP mCurrentStep = STEP.STEP_1;
	private long mCurrentService;
	private long mCurrentBase;
	
	// Animations
	private Animation leftToMiddle = AttaBaseContract.horizontalAnimation(-1.0f, AttaBaseContract.RIGHT);
	private Animation middleToRight = AttaBaseContract.horizontalAnimation(0.0f, AttaBaseContract.RIGHT);
	private Animation rightToMiddle = AttaBaseContract.horizontalAnimation(+1.0f, AttaBaseContract.LEFT);
	private Animation middleToLeft = AttaBaseContract.horizontalAnimation(0.0f, AttaBaseContract.LEFT);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);

        va = (ViewAnimator)findViewById(R.id.listAnimator);
        va.setInAnimation(rightToMiddle);
        va.setOutAnimation(middleToLeft);
    }
    
    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        
        if (va.getChildCount() == 0){
        	transitionToNewView(STEP.STEP_1, AttaBaseContract.NO_BASE);
        }
    }
    
    @Override
    protected void onResume() {
    	
        // INITIALIZE ADS
        SharedPreferences settingsPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean ads = settingsPrefs.getBoolean(AttaBaseContract.PREFS_ADS_BOOLEAN, false);
		AdView adView = (AdView)this.findViewById(R.id.adView);
		if (!ads){
			adView.loadAd(new AdRequest());
			adView.setVisibility(View.VISIBLE);
		} else {
			adView.setVisibility(View.INVISIBLE);
		}
    	super.onResume();
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
    	   mCurrentStep = getPreviousStep(mCurrentStep);
       }      
       else
    	   super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
		this.getSherlock().getMenuInflater().inflate(R.menu.regular, menu);
		
    	getSupportActionBar().setDisplayOptions(
    			ActionBar.DISPLAY_HOME_AS_UP |
                ActionBar.DISPLAY_SHOW_CUSTOM |
                ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE |
                ActionBar.DISPLAY_USE_LOGO);

    	return super.onCreateOptionsMenu(menu);
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.menu_feedback:
			Uri websiteUri = Uri.parse(AttaBaseContract.FEEDBACK_LINK);
			Intent intent = new Intent(Intent.ACTION_VIEW, websiteUri);
			startActivity(intent);
			return true;
		case R.id.menu_donate:
	    	Uri donateUri = Uri.parse(AttaBaseContract.PAYPAL_DONATE);
	    	Intent donateIntent = new Intent(Intent.ACTION_VIEW, donateUri);
	    	AttaBaseContract.gaTracker.trackPageView("Donate");
	    	startActivity(donateIntent);
			return true;
		case R.id.menu_settings:
			Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
    
    
    
    
    
    // GET THE NEXT STEP / STATE
	private STEP getNextStep(STEP step) {
		switch (step) {
		case STEP_1:
			return STEP.STEP_1_CHOOSE;
		case STEP_1_CHOOSE:
			return STEP.STEP_2;
		case STEP_2:
			return STEP.STEP_2_CHOOSE;
		case STEP_2_CHOOSE:
			return STEP.STEP_3;
		default:
			return STEP.STEP_1;
		}
	}
	
    // GET THE PREVIOUS STEP / STATE
	private STEP getPreviousStep(STEP step) {
		switch (step) {
		case STEP_1:
			return STEP.STEP_1;
		case STEP_1_CHOOSE:
			return STEP.STEP_1;
		case STEP_2:
			return STEP.STEP_1_CHOOSE;
		case STEP_2_CHOOSE:
			return STEP.STEP_2;
		case STEP_3:
			return STEP.STEP_2_CHOOSE;
		default:
			return STEP.STEP_1;
		}
	}
    
    // ADD NEXT VIEW TO VIEWANIMATOR AND TRANSITION TO NEW VIEW
	private void transitionToNewView(STEP step, long index) {
    	mCurrentStep = step;
    	
    	switch (step) {
    	case STEP_1:
        	va.addView((RelativeLayout) View.inflate(this, R.layout.activity_wizard_1, null));
        	va.showNext();
        	break;
    	case STEP_2:
        	mCurrentService = index;
    		va.addView((RelativeLayout) View.inflate(this, R.layout.activity_wizard_2, null));
        	va.showNext();
        	break;
    	case STEP_3:
    		mCurrentBase = index;
        	va.addView((RelativeLayout) View.inflate(this, R.layout.activity_wizard_3, null));
        	va.showNext();
        	break;
    	case STEP_1_CHOOSE:
    		va.addView(populateBaseWizardView(VIEW_TYPE.VIEW_SERVICES, index));
        	va.showNext();
        	break;
    	case STEP_2_CHOOSE:
    		va.addView(populateBaseWizardView(VIEW_TYPE.VIEW_BASES, mCurrentService));
        	va.showNext();
    		break;
    	default:
    	
    	}    	
	}
	
    @SuppressWarnings("deprecation")
	private LinearLayout populateBaseWizardView(VIEW_TYPE viewBases, long index) {
    	Cursor cursor;
    	Service service = null;
    	final SimpleCursorAdapter adapter;
    	String[] columns;
    	int to[];
    	
    	// LOAD LAYOUT XML    
        LinearLayout ll = (LinearLayout) View.inflate(this, R.layout.base_list_view, null);
        
        switch (viewBases) {
        case VIEW_BASES:
        	try {
				service = new Service(getBaseContext(), index);
			} catch (Exception e1) {
				Log.d("Exception", e1.getMessage());
			}
        	Uri serviceUri = Uri.withAppendedPath(AttaBaseProvider.CONTENT_URI_SERVICE, String.valueOf(index));
        	serviceUri = Uri.withAppendedPath(serviceUri, "base");
        	cursor = managedQuery(serviceUri, null, null, null, null);
            columns = new String[] {AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME, AttaBaseContract.LocationSchema.COLUMN_NAME_NICE_LOCATION};
            to = new int[] {R.id.name_entry, R.id.name_entry_sub};
            adapter = new SimpleCursorAdapter(this, R.layout.base_list_item_has_sub, cursor, columns, to);
        	break;
        case VIEW_SERVICES:
    		cursor = managedQuery(AttaBaseProvider.CONTENT_URI_SERVICE, null, null, null, null);
            columns = new String[] {AttaBaseContract.ServiceSchema.COLUMN_NAME_SERVICE_NAME};
            to = new int[] {R.id.name_entry};
            adapter = new SimpleCursorAdapter(this, R.layout.base_list_item, cursor, columns, to);
        	break;
		default:
			cursor = null;
			adapter = null;
			break;
		}
        
        // INITIALIZE THE HEADER
        LinearLayout headerLl = (LinearLayout) ll.findViewById(R.id.baseInformation);
        Header header;
		try {
			header = new Header(viewBases, headerLl, service, null);
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
        
        if (adapter != null){
	        // INITIALIZE THE LIST
	        ListView locations = (ListView) ll.findViewById(R.id.baseListView1);
			locations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long id) {
					transitionToNewView(getNextStep(mCurrentStep), id);
				}
			});
	        // POPULATE THE LIST WITH LOCATIONS
	        locations.setAdapter(adapter);
        }
        return ll;
	}
	
	/* BUTTON CALLBACK */
    public void stepContinue(View view) {
    	if (mCurrentStep != STEP.STEP_3)
    		transitionToNewView(getNextStep(mCurrentStep), AttaBaseContract.NO_BASE);
    	else {
    		AttaBaseContract.setHomeBase(getBaseContext(), mCurrentBase);
    		AttaBaseContract.setHomeService(getBaseContext(), mCurrentService);
    		finish();
    	}
    }

}
