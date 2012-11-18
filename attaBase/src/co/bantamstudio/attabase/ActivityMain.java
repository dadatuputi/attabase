package co.bantamstudio.attabase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

public class ActivityMain extends Activity {
	
	public final static String EXTRA_MESSAGE = "co.bantamstudio.attaBase.MESSAGE";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        
        // Load boolean flag 
        SharedPreferences prefs = getSharedPreferences(AttaBaseContract.APP_STRING, Context.MODE_PRIVATE);
        boolean hasImported = prefs.getBoolean(AttaBaseContract.PREFS_IMPORTED_BOOL, false);
        
        // If initial import hasn't occurred, start import activity
        if (!hasImported){
        	Intent intent = new Intent(this.getApplicationContext(), ActivityImportCSV.class);
        	startActivity(intent);
        }
        else {
	        Intent intent = new Intent(this.getApplicationContext(), ActivityHome.class);
	        startActivity(intent);
        }
        
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        
        // Activity being restarted from stopped state    
    }
}
