package co.bantamstudio.attabase;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.BaseColumns;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AttaBaseContract {
	
	// STATICS
	static final int LEFT = -1;
	static final int RIGHT = 1;
	static final int NO_BASE = -1;
	static final int NO_SERVICE = -1;
	
	//public static final String IMPORT_SOURCE_CSV = "attaBase.csv";
	static final String IMPORT_SOURCE_ZIP = "attaBase.zip";
	static final String APP_STRING = "studio.bantam.attabase";
	static final String APP_STRING_VND = "vnd.bantamstudio.attabase";
	static final String BASE_LIST_STATE = APP_STRING+"_base_list_state";
	static final String BASE_LIST_BASE_INDEX = APP_STRING+"_base_list_base_index";
	static final String BASE_LIST_SERVICE_INDEX = APP_STRING+"_base_list_service_index";
	static final String BASE_LIST_LOCATION_INDEX = APP_STRING+"_base_list_location_index";
	
	// STATES TO PASS TO BASE LIST
	static final String BASE_LIST_BASE = "base_list_base";
	static final String BASE_LIST_LOCATION = "base_list_location";
	
	// PAYPAL
	static final String PAYPAL_DONATE = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=TJSPTZXU5S6HQ";
	
	// FEEDBACK
	static final String FEEDBACK_LINK = "http://bantamstudio.co/attabase/feedback";

	// PREFERENCES
	static final String PREFS_IMPORTED_BOOL = "importedBool";
	static final String PREFS_FIRSTRUN_BOOL = "firstRunBool";
	static final String PREFS_HOME_BASE_INT = "homeBaseInt";
	static final String PREFS_HOME_SERVICE_INT = "homeServiceInt";
	static final String PREFS_ADS_BOOLEAN = "pref_disable_ads";
	
	static final int DATABASE_VERSION = 1;
	static final String DATABASE_NAME = "attaBase.db";

	private AttaBaseContract() {}
	
	static void setHomeBase(Context context, long baseId){
		SharedPreferences prefs = context.getSharedPreferences(AttaBaseContract.APP_STRING, Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putLong(AttaBaseContract.PREFS_HOME_BASE_INT, baseId);
    	editor.apply();
	}
	static void setHomeService(Context context, long serviceId){
		SharedPreferences prefs = context.getSharedPreferences(AttaBaseContract.APP_STRING, Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putLong(AttaBaseContract.PREFS_HOME_SERVICE_INT, serviceId);
    	editor.apply();
	}
	
	
	class AttaBaseSchema implements BaseColumns{
		static final String TABLE_ALL =
				AttaBaseContract.BaseSchema.TABLE_NAME + " a" + 
				" INNER JOIN " + AttaBaseContract.ServiceSchema.TABLE_NAME + " b ON " + 
					"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_SERVICE + " = " + 
					"b." + AttaBaseContract.ServiceSchema._ID + 
				" INNER JOIN " + AttaBaseContract.LocationSchema.TABLE_NAME + " c ON " +
					"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_BASE + " = " +
					"a." + AttaBaseContract.BaseSchema._ID +
				" INNER JOIN " + AttaBaseContract.LocationTypeSchema.TABLE_NAME + " d ON " +
					"d." + AttaBaseContract.LocationTypeSchema._ID + " = " +
					"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_TYPE;
		
		static final String TABLE_SERVICE_BASE =
				AttaBaseContract.BaseSchema.TABLE_NAME + " a" + 
				" INNER JOIN " + AttaBaseContract.ServiceSchema.TABLE_NAME + " b ON " + 
					"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_SERVICE + " = " + 
					"b." + AttaBaseContract.ServiceSchema._ID;
		
		static final String TABLE_SERVICE_BASE_LOC =
				AttaBaseContract.BaseSchema.TABLE_NAME + " a" + 
				" INNER JOIN " + AttaBaseContract.ServiceSchema.TABLE_NAME + " b ON " + 
					"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_SERVICE + " = " + 
					"b." + AttaBaseContract.ServiceSchema._ID + 
				" INNER JOIN " + AttaBaseContract.LocationSchema.TABLE_NAME + " c ON " +
					"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_BASE + " = " +
					"a." + AttaBaseContract.BaseSchema._ID;
	}
	class BaseSchema implements BaseColumns{
		static final String TABLE_NAME = "base";
		static final String COLUMN_NAME_BASE_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
		static final String COLUMN_NAME_BASE_SERVICE = "baseservice";
		static final String STRING_CREATE_TABLE =
				"CREATE TABLE " + TABLE_NAME + " (" +
						_ID + " INTEGER PRIMARY KEY," +
						COLUMN_NAME_BASE_NAME + " TEXT," +
						COLUMN_NAME_BASE_SERVICE + " INTEGER REFERENCES " + 
							AttaBaseContract.ServiceSchema.TABLE_NAME + "(" + 
							AttaBaseContract.ServiceSchema._ID + ") ON UPDATE CASCADE" +
				")";
		static final String STRING_DROP_TABLE =
				"DROP TABLE IF EXISTS " + TABLE_NAME;
		static final String STRING_INSERT_ROW =
				"INSERT INTO " + TABLE_NAME + " (" +
						COLUMN_NAME_BASE_NAME + ", " + 
						COLUMN_NAME_BASE_SERVICE + ") VALUES (?,?)";
	}
	class ServiceSchema implements BaseColumns{
		static final String TABLE_NAME = "service";
		static final String COLUMN_NAME_SERVICE_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
		static final String STRING_CREATE_TABLE =
				"CREATE TABLE " + TABLE_NAME + " (" +
						_ID + " INTEGER PRIMARY KEY," +
						COLUMN_NAME_SERVICE_NAME + " TEXT" + 
				")";
		static final String STRING_DROP_TABLE =
				"DROP TABLE IF EXISTS " + TABLE_NAME;
		static final String STRING_INSERT_ROW =
				"INSERT INTO " + TABLE_NAME + " (" +
						COLUMN_NAME_SERVICE_NAME + ") VALUES (?)";
	}
	class LocationTypeSchema implements BaseColumns{
		static final String TABLE_NAME = "directory";
		static final String COLUMN_NAME_DIRECTORY_NAME = "directoryname";
		static final String BASE_ADDRESS_TYPE = "Location";
		static final String STRING_CREATE_TABLE =
				"CREATE TABLE " + TABLE_NAME + " (" +
						_ID + " INTEGER PRIMARY KEY," +
						COLUMN_NAME_DIRECTORY_NAME + " TEXT" +
				")";
		static final String STRING_DROP_TABLE =
				"DROP TABLE IF EXISTS " + TABLE_NAME;
		static final String STRING_INSERT_ROW =
				"INSERT INTO " + TABLE_NAME + " (" +
						COLUMN_NAME_DIRECTORY_NAME + ") VALUES (?)";
	}
	class LocationSchema implements BaseColumns{
		static final String TABLE_NAME = "location";
		static final String COLUMN_NAME_LOCATION_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
		static final String COLUMN_NAME_LOCATION_TYPE = "locationtype";
		static final String COLUMN_NAME_DOD_ID = "dod_id";
		static final String COLUMN_NAME_BASE = "base";
		static final String COLUMN_NAME_ADDRESS1 = "address1";
		static final String COLUMN_NAME_ADDRESS2 = "address2";
		static final String COLUMN_NAME_ADDRESS3 = "address3";
		static final String COLUMN_NAME_ADDRESS4 = "address4";
		static final String COLUMN_NAME_CITY = "city";
		static final String COLUMN_NAME_STATE = "state";
		static final String COLUMN_NAME_COUNTRY = "country";
		static final String COLUMN_NAME_ZIP_CODE = "zip";
		static final String COLUMN_NAME_PHONE1 = "commercial1";
		static final String COLUMN_NAME_PHONE2 = "commercial2";
		static final String COLUMN_NAME_PHONE3 = "commercial3";
		static final String COLUMN_NAME_FAX = "commercialfax";
		static final String COLUMN_NAME_DSN = "dsn";
		static final String COLUMN_NAME_DSN_FAX = "dsnfax";
		static final String COLUMN_NAME_WEBSITE1 = "website1";
		static final String COLUMN_NAME_WEBSITE2 = "website2";
		static final String COLUMN_NAME_WEBSITE3 = "website3";
		static final String COLUMN_NAME_SEARCH_LOCATION = SearchManager.SUGGEST_COLUMN_TEXT_2;
		static final String COLUMN_NAME_NICE_LOCATION = "nice_locations";

		static final String STRING_CREATE_TABLE =
				"CREATE TABLE " + TABLE_NAME + " (" +
						_ID + " INTEGER PRIMARY KEY," +
						COLUMN_NAME_LOCATION_NAME + " TEXT," +
						COLUMN_NAME_DOD_ID + " INTEGER, " +
						COLUMN_NAME_ADDRESS1 + " TEXT, " +
						COLUMN_NAME_ADDRESS2 + " TEXT, " + 
						COLUMN_NAME_ADDRESS3 + " TEXT, " +
						COLUMN_NAME_ADDRESS4 + " TEXT, " + 
						COLUMN_NAME_CITY + " TEXT, " +
						COLUMN_NAME_STATE + " TEXT, " +
						COLUMN_NAME_COUNTRY + " TEXT, " +
						COLUMN_NAME_ZIP_CODE + " TEXT, " +
						COLUMN_NAME_PHONE1 + " TEXT, " +
						COLUMN_NAME_PHONE2 + " TEXT, " +
						COLUMN_NAME_PHONE3 + " TEXT, " +
						COLUMN_NAME_FAX + " TEXT, " +
						COLUMN_NAME_DSN + " TEXT, " +
						COLUMN_NAME_DSN_FAX + " TEXT, " +
						COLUMN_NAME_WEBSITE1 + " TEXT, " +
						COLUMN_NAME_WEBSITE2 + " TEXT, " +
						COLUMN_NAME_WEBSITE3 + " TEXT, " +	
						COLUMN_NAME_NICE_LOCATION + " TEXT, " +

						COLUMN_NAME_LOCATION_TYPE + " INTEGER REFERENCES " + 
							AttaBaseContract.LocationTypeSchema.TABLE_NAME + "(" + 
							AttaBaseContract.LocationTypeSchema._ID + ") ON UPDATE CASCADE, " +
							
						COLUMN_NAME_BASE + " INTEGER REFERENCES " + 
							AttaBaseContract.BaseSchema.TABLE_NAME + "(" + 
							AttaBaseContract.BaseSchema._ID + ") ON UPDATE CASCADE, " +
							
						COLUMN_NAME_SEARCH_LOCATION + " TEXT " +
				")";
		static final String STRING_DROP_TABLE =
				"DROP TABLE IF EXISTS " + TABLE_NAME;
		static final String STRING_INSERT_ROW =
				"INSERT INTO " + TABLE_NAME + " (" +
						COLUMN_NAME_LOCATION_NAME + ", " +
						COLUMN_NAME_DOD_ID + ", " + 
						COLUMN_NAME_ADDRESS1 + ", " + 
						COLUMN_NAME_ADDRESS2 + ", " + 
						COLUMN_NAME_ADDRESS3 + ", " + 
						COLUMN_NAME_ADDRESS4 + ", " + 
						COLUMN_NAME_CITY + ", " + 
						COLUMN_NAME_STATE + ", " + 
						COLUMN_NAME_COUNTRY + ", " + 
						COLUMN_NAME_ZIP_CODE + ", " + 
						COLUMN_NAME_PHONE1 + ", " + 
						COLUMN_NAME_PHONE2 + ", " + 
						COLUMN_NAME_PHONE3 + ", " + 
						COLUMN_NAME_FAX + ", " + 
						COLUMN_NAME_DSN + ", " + 
						COLUMN_NAME_DSN_FAX + ", " + 
						COLUMN_NAME_WEBSITE1 + ", " + 
						COLUMN_NAME_WEBSITE2 + ", " + 
						COLUMN_NAME_WEBSITE3 + ", " + 
						COLUMN_NAME_LOCATION_TYPE + ", " + 
						COLUMN_NAME_BASE + ", " +
						COLUMN_NAME_SEARCH_LOCATION + ", " +
						COLUMN_NAME_NICE_LOCATION + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	}
	
	static Animation horizontalAnimation(float startingX, int direction) {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, startingX,
				Animation.RELATIVE_TO_PARENT, startingX + (direction * 1.0f),
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(250);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}
	static Animation noAnimation() {
		Animation noAnim = new AlphaAnimation(1,1);
		noAnim.setDuration(0);
		return noAnim;
	}
}
