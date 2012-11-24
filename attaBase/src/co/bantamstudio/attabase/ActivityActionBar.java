package co.bantamstudio.attabase;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import android.os.Bundle;


public class ActivityActionBar extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_action_bar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		MenuItem miPrefs = menu.add("Preferences");
        miPrefs.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
 
        miPrefs.setOnMenuItemClickListener(new OnMenuItemClickListener() {
 
            public boolean onMenuItemClick(MenuItem item) {
 
//                Intent prefIntent = new Intent(ActivityActionBar.this,
//                        Preference.class);
// 
//                ActivityActionBar.this.startActivity(prefIntent);
// 
                  return true;
            }
        }
 
        );
 
        // Adding some empty menu items, just to test the ActionBar's display changes
        // in portrait and landscape orientations.
        menu.add("Other Stuff")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add("Great Stuff")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add("Probably Hidden").setShowAsAction(
                MenuItem.SHOW_AS_ACTION_IF_ROOM);
 
        return true;
	}

}
