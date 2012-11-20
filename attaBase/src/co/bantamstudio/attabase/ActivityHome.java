package co.bantamstudio.attabase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

public class ActivityHome extends Activity {
	
	public final static String EXTRA_MESSAGE = "co.bantamstudio.attaBase.MESSAGE";
	
	private int mCurrentBase;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load boolean flag 
        SharedPreferences prefs = getSharedPreferences(AttaBaseContract.APP_STRING, Context.MODE_PRIVATE);
        boolean hasImported = prefs.getBoolean(AttaBaseContract.PREFS_IMPORTED_BOOL, false);
        mCurrentBase = prefs.getInt(AttaBaseContract.PREFS_HOME_BASE_INT, AttaBaseContract.NO_BASE);
        
        // If initial import hasn't occurred, start import activity
        if (!hasImported){
        	Intent intent = new Intent(this.getApplicationContext(), ActivityImportCSV.class);
        	startActivity(intent);
        }
        else if(mCurrentBase == AttaBaseContract.NO_BASE){
        	Intent intent = new Intent(this.getApplicationContext(), ActivityWizard.class);
        	startActivity(intent);
        }        
        
        setContentView(R.layout.activity_home);
    }
    
    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        

        
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        
        // Activity being restarted from stopped state    
    }
}
