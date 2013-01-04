package co.bantamstudio.attabase;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends SherlockPreferenceActivity {
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean ALWAYS_SIMPLE_PREFS = false;

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setupSimplePreferencesScreen();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
		this.getSherlock().getMenuInflater().inflate(R.menu.regular_settings, menu);
		
    	getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM |
                ActionBar.DISPLAY_HOME_AS_UP |
                ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE |
                ActionBar.DISPLAY_USE_LOGO);

    	return super.onCreateOptionsMenu(menu);
    }
      
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
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
		}

		return super.onOptionsItemSelected(item);
	}


	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}

		addPreferencesFromResource(R.xml.pref_general);
		
		CheckBoxPreference ads = (CheckBoxPreference) findPreference("pref_disable_ads");
		ads.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if ((Boolean)newValue)
					showDonateDialog(SettingsActivity.this);
				return true;
			}
		});
	}

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	
	private void showDonateDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(R.string.ad_alert_dialog);
		builder.setTitle(R.string.ad_alert_dialog_title);
		builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				Uri websiteUri = Uri.parse(AttaBaseContract.PAYPAL_DONATE);
		    	Intent intent = new Intent(Intent.ACTION_VIEW, websiteUri);
		    	AttaBaseContract.gaTracker.trackPageView("Donate");
		    	startActivity(intent);
			}
		});
		builder.setNegativeButton(R.string.alert_dialog_cancel, null);
		builder.show();
	}
	

	/**
	 * This fragment shows general preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);
			CheckBoxPreference ads = (CheckBoxPreference) findPreference("pref_disable_ads");
			ads.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					if ((Boolean)newValue){
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setMessage(R.string.ad_alert_dialog);
						builder.setTitle(R.string.ad_alert_dialog_title);
						builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								Uri websiteUri = Uri.parse(AttaBaseContract.PAYPAL_DONATE);
						    	Intent intent = new Intent(Intent.ACTION_VIEW, websiteUri);
						    	AttaBaseContract.gaTracker.trackPageView("Donate");
						    	startActivity(intent);
							}
						});
						builder.setNegativeButton(R.string.alert_dialog_cancel, null);
						builder.show();
					}
					return true;
				}
			});
		}
	}
}
