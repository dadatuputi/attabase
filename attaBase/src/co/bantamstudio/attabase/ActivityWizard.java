package co.bantamstudio.attabase;

import co.bantamstudio.attabase.ActivityBaseList.VIEW_TYPE;
import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.SherlockActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.ViewAnimator;

public class ActivityWizard extends SherlockActivity {
	ActionBarSherlock mSherlock = ActionBarSherlock.wrap(this);
	//private Set<Cursor> cursors;
	private SimpleCursorAdapter mAdapter;
	private LocationDbHelper mDbHelper;
	// http://www.qubi.us/2011/09/easy-viewanimator-transition-slide.html
	private ViewAnimator va;
	private SharedPreferences prefs;
	
	private static enum STEP {STEP_1, STEP_1_CHOOSE, STEP_2, STEP_2_CHOOSE, STEP_3};
	private STEP mCurrentStep = STEP.STEP_1;
	private int mCurrentService;
	private int mCurrentBase;
	
	// Animations
	private Animation leftToMiddle = AttaBaseContract.horizontalAnimation(-1.0f, AttaBaseContract.RIGHT);
	private Animation middleToRight = AttaBaseContract.horizontalAnimation(0.0f, AttaBaseContract.RIGHT);
	private Animation rightToMiddle = AttaBaseContract.horizontalAnimation(+1.0f, AttaBaseContract.LEFT);
	private Animation middleToLeft = AttaBaseContract.horizontalAnimation(0.0f, AttaBaseContract.LEFT);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard_list);

        va = (ViewAnimator)findViewById(R.id.listAnimator);
        va.setInAnimation(rightToMiddle);
        va.setOutAnimation(middleToLeft);
        
        mDbHelper = new LocationDbHelper(this.getApplicationContext());
        
        // Load set preferences
        prefs = getSharedPreferences(AttaBaseContract.APP_STRING, Context.MODE_PRIVATE);
        
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        
        if (va.getChildCount() == 0){
        	transitionToNewView(STEP.STEP_1, AttaBaseContract.NO_BASE);
        }
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

//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_wizard, menu);
//        return true;
//    }

    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    
    
    
    
    
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
	private void transitionToNewView(STEP step, int index) {
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
    		setCursorAdapterServicesAll();
    		va.addView(populateBaseWizardView(VIEW_TYPE.VIEW_SERVICES, index));
        	va.showNext();
        	break;
    	case STEP_2_CHOOSE:
    		setCursorAdapterService(mCurrentService);
    		va.addView(populateBaseWizardView(VIEW_TYPE.VIEW_BASES, mCurrentService));
        	va.showNext();
    		break;
    	default:
    	
    	}    	
	}
	
    private LinearLayout populateBaseWizardView(VIEW_TYPE viewBases, int index) {
        // LOAD LAYOUT XML    
        LinearLayout ll = (LinearLayout) View.inflate(this, R.layout.base_list_view, null);
        
        Service service = null;
        switch (viewBases) {
        case VIEW_BASES:
        	try {
				service = new Service(getBaseContext(), mDbHelper, index);
			} catch (Exception e1) {
				Log.d("Exception", e1.getMessage());
			}
        	//serviceIndex = index;
        	break;
        case VIEW_SERVICES:
		default:
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
        
        
        // INITIALIZE THE LIST
        ListView locations = (ListView) ll.findViewById(R.id.baseListView1);
		locations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				transitionToNewView(getNextStep(mCurrentStep), (int)arg3);
			}
		});
        // POPULATE THE LIST WITH LOCATIONS
        locations.setAdapter(mAdapter);

        return ll;
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

	/* BUTTON CALLBACKS */
    public void stepContinue(View view) {
    	if (mCurrentStep != STEP.STEP_3)
    		transitionToNewView(getNextStep(mCurrentStep), AttaBaseContract.NO_BASE);
    	else {
    		wrapUpWizard();
    	}
    }

	private void wrapUpWizard() {
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putInt(AttaBaseContract.PREFS_HOME_SERVICE_INT, mCurrentService);
    	editor.putInt(AttaBaseContract.PREFS_HOME_BASE_INT, mCurrentBase);
    	editor.commit();
    	
    	finish();
	}

}
