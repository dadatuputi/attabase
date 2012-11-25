package co.bantamstudio.attabase;

import java.util.List;
import java.util.Locale;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class AttaBaseProvider extends ContentProvider {

	public static String AUTHORITY = AttaBaseContract.APP_STRING + ".AttaBaseProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	public static final Uri CONTENT_URI_BASE = Uri.parse("content://" + AUTHORITY + "/base");
	public static final Uri CONTENT_URI_LOCATION = Uri.parse("content://" + AUTHORITY + "/location");
	public static final Uri CONTENT_URI_SERVICE = Uri.parse("content://" + AUTHORITY + "/service");
	
	// MIME types
	public static final String ATTABASE_SERVICE_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AttaBaseContract.APP_STRING_VND;
	public static final String ATTABASE_BASE_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AttaBaseContract.APP_STRING_VND;
	public static final String ATTABASE_LOCATION_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AttaBaseContract.APP_STRING_VND;
	
	private AttaBaseDatabase mAttaBaseDatabase;
	
	// URI MATCHER
	private static final int SEARCH_SERVICES = 0;
	private static final int GET_SERVICE = 1;
	private static final int SEARCH_BASES = 2;
	private static final int GET_BASE = 3;
	private static final int SEARCH_LOCATIONS = 4;
	private static final int GET_LOCATION = 5;
	private static final int SEARCH_SUGGEST_BASE = 6;
	private static final int SEARCH_SUGGEST_LOCATION = 7;
	private static final int GET_BASE_ADDRESS = 8;
	private static final int GET_SERVICE_BASES = 9;
	private static final UriMatcher sURIMatcher = buildUriMatcher();
	
	private static UriMatcher buildUriMatcher() {
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		// to get definitions...
		matcher.addURI(AUTHORITY, "service", SEARCH_SERVICES);
		matcher.addURI(AUTHORITY, "service/#", GET_SERVICE);
		matcher.addURI(AUTHORITY, "service/#/base", GET_SERVICE_BASES);
		matcher.addURI(AUTHORITY, "base", SEARCH_BASES);
		matcher.addURI(AUTHORITY, "base/#", GET_BASE);
		matcher.addURI(AUTHORITY, "base/#/address", GET_BASE_ADDRESS);
		matcher.addURI(AUTHORITY, "location", SEARCH_LOCATIONS);
		matcher.addURI(AUTHORITY, "location/#", GET_LOCATION);
		// to get suggestions...
		matcher.addURI(AUTHORITY, "base/"+SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST_BASE);
		matcher.addURI(AUTHORITY, "location/"+SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST_LOCATION);
		
		return matcher;
	}
	
	
	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
		case SEARCH_BASES:
		case GET_BASE:
			return ATTABASE_BASE_MIME_TYPE;
		case SEARCH_SERVICES:
		case GET_SERVICE:
			return ATTABASE_SERVICE_MIME_TYPE;
		case SEARCH_LOCATIONS:
		case GET_LOCATION:
			return ATTABASE_LOCATION_MIME_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " +uri);
		}
	}



	@Override
	public boolean onCreate() {
		mAttaBaseDatabase = new AttaBaseDatabase(getContext());
		return true;
	}

	
    /**
     * Handles all the base searches and suggestion queries from the Search Manager.
     * When requesting a specific base, service or location, the uri alone is required.
     * When searching all of the dictionary for matches, the selectionArgs argument must carry
     * the search query as the first element.
     * All other arguments are ignored.
     */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		switch (sURIMatcher.match(uri)) {
		case SEARCH_SUGGEST_BASE:
			if (selectionArgs == null)
				throw new IllegalArgumentException("SelectionArgs must be provided for the Uri: " + uri);
			return getSuggestionsBase(selectionArgs[0]);
		case SEARCH_SUGGEST_LOCATION:
			if (selectionArgs == null)
				throw new IllegalArgumentException("SelectionArgs must be provided for the Uri: " + uri);
			return getSuggestionsLocations(selectionArgs[0]);
		case SEARCH_BASES:
			if(selectionArgs == null)
				return allBases();
			return searchBase(selectionArgs[0]);
		case SEARCH_SERVICES:
			if(selectionArgs == null)
				return allServices();
			return searchServices(selectionArgs[0]);
		case GET_SERVICE_BASES:
			return allBases(uri);
		case SEARCH_LOCATIONS:
			if(selectionArgs == null)
				return allLocations();
			return searchLocations(selectionArgs[0]);
		case GET_BASE_ADDRESS:
			return getBaseAddress(uri);
		case GET_BASE:
			return getBase(uri);
		case GET_SERVICE:
			return getService(uri);
		case GET_LOCATION:
			return getLocation(uri);
		default:
			throw new IllegalArgumentException("Unknown Uri: " + uri);
			
		}
	}

	// RETURN ALL OF TYPE
	private Cursor allLocations() {
		String[] columns = new String[] {
				BaseColumns._ID,
				AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME,
				AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS1,
				AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS2,
				AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS3,
				AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS4,
				AttaBaseContract.LocationSchema.COLUMN_NAME_CITY,
				AttaBaseContract.LocationSchema.COLUMN_NAME_STATE,
				AttaBaseContract.LocationSchema.COLUMN_NAME_ZIP_CODE,
				AttaBaseContract.LocationSchema.COLUMN_NAME_COUNTRY,
				AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE1,
				AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE2,
				AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE3,
				AttaBaseContract.LocationSchema.COLUMN_NAME_FAX,
				AttaBaseContract.LocationSchema.COLUMN_NAME_DSN,
				AttaBaseContract.LocationSchema.COLUMN_NAME_DSN_FAX,
				AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE1,
				AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE2,
				AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE3,
				AttaBaseContract.LocationSchema.COLUMN_NAME_BASE,
				AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_TYPE,
				AttaBaseContract.LocationSchema.COLUMN_NAME_SEARCH_LOCATION,
		};
		return mAttaBaseDatabase.getLocation(columns);
	}
	private Cursor allServices() {
		String[] columns = new String[] {
				BaseColumns._ID,
				AttaBaseContract.ServiceSchema.COLUMN_NAME_SERVICE_NAME
		};
		return mAttaBaseDatabase.getService(columns);
	}
	private Cursor allBases() {
		String[] columns = new String[] {
				"a." + BaseColumns._ID,
				"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_SEARCH_LOCATION
		};
		return mAttaBaseDatabase.getService(columns);
	}
	private Cursor allBases(Uri uri) {
		List<String> segments = uri.getPathSegments();
		String serviceId = segments.get(1);
		String[] columns = new String[] {
				"a." + BaseColumns._ID,
				"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_SEARCH_LOCATION,
//				"(c." + AttaBaseContract.LocationSchema.COLUMN_NAME_CITY + 
//					"|| ' ' ||c." + AttaBaseContract.LocationSchema.COLUMN_NAME_STATE + 
//					"|| ' ' ||c." + AttaBaseContract.LocationSchema.COLUMN_NAME_COUNTRY + 
//					") AS " + AttaBaseContract.LocationSchema.TEMP_COLUMN_NAME_LOCATION
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_NICE_LOCATION
		};
		return mAttaBaseDatabase.getBases(serviceId, columns);
	}


	// SEARCH
	private Cursor searchLocations(String string) {
		string = string.toLowerCase(Locale.US);
		String[] columns = new String[]{
				BaseColumns._ID,
				AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME,
				AttaBaseContract.LocationSchema.COLUMN_NAME_SEARCH_LOCATION
		};
		return mAttaBaseDatabase.getLocationMatches(string, columns);
	}
	private Cursor searchServices(String string) {
		string = string.toLowerCase(Locale.US);
		String[] columns = new String[]{
				BaseColumns._ID,
				AttaBaseContract.ServiceSchema.COLUMN_NAME_SERVICE_NAME,
				AttaBaseContract.LocationSchema.COLUMN_NAME_SEARCH_LOCATION
		};
		return mAttaBaseDatabase.getServiceMatches(string, columns);
	}
	private Cursor searchBase(String string) {
		string = string.toLowerCase(Locale.US);
		String[] columns = new String[]{
				BaseColumns._ID,
				AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME,
				AttaBaseContract.LocationSchema.COLUMN_NAME_SEARCH_LOCATION
		};
		return mAttaBaseDatabase.getBaseMatches(string, columns);
	}

	// GET METHODS
	private Cursor getBase(Uri uri) {
		String baseId = uri.getLastPathSegment();
		String[] columns = new String[] {
				"c." + BaseColumns._ID,
				"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_SEARCH_LOCATION,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_NICE_LOCATION,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME
		};
		return mAttaBaseDatabase.getBase(baseId, columns);
	}
	private Cursor getService(Uri uri) {
		String serviceId = uri.getLastPathSegment();
		String[] columns = new String[] {
				BaseColumns._ID,
				AttaBaseContract.ServiceSchema.COLUMN_NAME_SERVICE_NAME
		};
		return mAttaBaseDatabase.getService(serviceId, columns);
	}
	private Cursor getLocation(Uri uri) {
		String locId = uri.getLastPathSegment();
		String[] columns = new String[] {
				"c." + BaseColumns._ID,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS1,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS2,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS3,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS4,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_CITY,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_STATE,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_ZIP_CODE,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_COUNTRY,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE1,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE2,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE3,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_FAX,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_DSN,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_DSN_FAX,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE1,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE2,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE3,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_BASE,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_TYPE,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_SEARCH_LOCATION,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_DOD_ID
		};
		return mAttaBaseDatabase.getLocation(locId, columns);
	}
	private Cursor getBaseAddress(Uri uri) {
		// TODO get it to return a proper address. Right now it returns the location at the base ID
		List<String> segments = uri.getPathSegments();
		String baseId = segments.get(1);
		String[] columns = new String[] {
				"c." + BaseColumns._ID,
				"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS1,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS2,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS3,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS4,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_CITY,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_STATE,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_ZIP_CODE,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_COUNTRY,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE1,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE2,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE3,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_FAX,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_DSN,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_DSN_FAX,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE1,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE2,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE3,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_BASE,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_TYPE,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_SEARCH_LOCATION,
				"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_DOD_ID,
				"d." + AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME,
		};
		return mAttaBaseDatabase.getBaseAddress(baseId, columns);
	}


	private Cursor getSuggestionsBase(String string) {
		string = string.toLowerCase(Locale.US);
		String[] columns = new String[] {
			BaseColumns._ID,
			AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME,
			AttaBaseContract.LocationSchema.COLUMN_NAME_SEARCH_LOCATION,
			SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
		};
		
		return mAttaBaseDatabase.getBaseMatches(string, columns);
	}
	
	private Cursor getSuggestionsLocations(String string) {
		string = string.toLowerCase(Locale.US);
		String[] columns = new String[] {
			BaseColumns._ID,
			AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME,
			AttaBaseContract.LocationSchema.COLUMN_NAME_SEARCH_LOCATION,
			SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
		};
		
		return mAttaBaseDatabase.getLocationMatches(string, columns);
	}


	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
}
