package co.bantamstudio.attabase;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static String EXTRA_MESSAGE = "co.bantamstudio.attaBase.MESSAGE";
    private Base mCurrentBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ads
        MobileAds.initialize(this);

    }

    @Override
    protected void onStart(){
        super.onStart();

        // CHECK TO SEE IF USER HAS SET DEFAULT BASE / SERVICE
        final SharedPreferences prefs = getSharedPreferences(AttaBaseContract.APP_STRING, Context.MODE_PRIVATE);
        boolean hasImported = prefs.getBoolean(AttaBaseContract.PREFS_IMPORTED_BOOL, false);
        boolean firstRun = prefs.getBoolean(AttaBaseContract.PREFS_FIRSTRUN_BOOL, true);
        long base = prefs.getLong(AttaBaseContract.PREFS_HOME_BASE_INT, AttaBaseContract.NO_BASE);
        long service = prefs.getLong(AttaBaseContract.PREFS_HOME_SERVICE_INT, AttaBaseContract.NO_SERVICE);

        // IF INITIAL IMPORT HASN'T OCCURRED, IMPORT CSV FILE
        if (!hasImported){
            Intent intent = new Intent(getApplicationContext(), ActivityImportCSV.class);
            startActivity(intent);
        }
        else if(firstRun){
            // Pop up dialog asking if they want to select a base
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(getString(R.string.first_run_msg));
            alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(AttaBaseContract.PREFS_FIRSTRUN_BOOL, false);
                    editor.apply();
                }
            });
            alert.show();
        }

        // TRY TO BUILD A BASE OBJECT WITH USER SETTINGS
        try {
            mCurrentBase = new Base(this, new Service(this,service), base);
        } catch (Exception e) {
            mCurrentBase = null;
        }

        // Set up layout
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up drawers and menus
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        populateHomeScreen();
    }

    @Override
    protected void onResume() {
        // INITIALIZE ADS
//        SharedPreferences settingsPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean ads = settingsPrefs.getBoolean(AttaBaseContract.PREFS_ADS_BOOLEAN, false);
//        AdView adView = (AdView)this.findViewById(R.id.adView);
//        if (!ads){
//            AdRequest adReq = new AdRequest();
//            if (mCurrentBase != null) {
//                adReq.setKeywords(mCurrentBase.getKeywords());
//            }
//            adView.loadAd(adReq);
//            adView.setVisibility(View.VISIBLE);
//        } else {
//            adView.setVisibility(View.INVISIBLE);
//        }

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        getSupportActionBar().setDisplayOptions(
            ActionBar.DISPLAY_SHOW_CUSTOM |
            ActionBar.DISPLAY_SHOW_HOME |
            ActionBar.DISPLAY_SHOW_TITLE |
            ActionBar.DISPLAY_USE_LOGO);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_feedback:
                Uri websiteUri = Uri.parse(AttaBaseContract.FEEDBACK_LINK);
                Intent intent = new Intent(Intent.ACTION_VIEW, websiteUri);
                startActivity(intent);
                return true;
            case R.id.menu_donate:
                Uri donateUri = Uri.parse(AttaBaseContract.PAYPAL_DONATE);
                Intent donateIntent = new Intent(Intent.ACTION_VIEW, donateUri);
                startActivity(donateIntent);
                return true;
            case R.id.menu_settings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

        }
        // IF BASE IS NULL, DON'T SHOW ANYTHING BUT THE BROWSE BUTTON & MESSAGE
        else {
            ((TextView)ll.findViewById(R.id.baseName)).setText("No default base set");
            ((LinearLayout)ll.findViewById(R.id.addressGroup)).setVisibility(LinearLayout.GONE);
            ((LinearLayout)ll.findViewById(R.id.phoneGroup)).setVisibility(LinearLayout.GONE);
            ((LinearLayout)ll.findViewById(R.id.websiteGroup)).setVisibility(LinearLayout.GONE);
            //((LinearLayout)ll.findViewById(R.id.noBaseText)).setVisibility(LinearLayout.VISIBLE);
        }
    }

    // BUTTON AT BOTTOM OF HOME SCREEN TO BROWSE ALL LOCATIONS
    public void buttonBrowse(View view){
        Intent intent = new Intent(getBaseContext(), ActivityBaseList.class);
        startActivity(intent);
    }
    // VIEW HOME BASE
    public void goToBase(View view){
        Intent intent = new Intent(getBaseContext(), ActivityBaseList.class);
        intent.putExtra(AttaBaseContract.BASE_LIST_STATE, AttaBaseContract.BASE_LIST_BASE);
        intent.putExtra(AttaBaseContract.BASE_LIST_SERVICE_INDEX, mCurrentBase.getService().getServiceIndex());
        intent.putExtra(AttaBaseContract.BASE_LIST_BASE_INDEX, mCurrentBase.getBaseIndex());
        startActivity(intent);
    }
    public void goToWebsite(View view){
        Uri websiteUri = Uri.parse(mCurrentBase.getLocation().getWebsite1());

        Intent intent = new Intent(Intent.ACTION_VIEW, websiteUri);
        startActivity(intent);
    }
    public void goToDialer(View view){
        Uri phoneUri = Uri.parse("tel:"+mCurrentBase.getLocation().getLocationPhone1());

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

            Intent intent = new Intent(Intent.ACTION_VIEW, locationUri);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.d(AttaBaseContract.APP_STRING, e.getMessage());
        }
    }
}
