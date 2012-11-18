package co.bantamstudio.attabase;

import android.provider.BaseColumns;

public class AttaBaseContract {
	
	public static final String IMPORT_SOURCE_CSV = "attaBase.csv"; 
	public static final String APP_STRING = "co.bantamstudio.attabase";
	
	// PREFERENCES
	public static final String PREFS_IMPORTED_BOOL = "importedBool";
	public static final String PREFS_HOME_BASE_INT = "homeBaseInt";
	public static final String PREFS_HOME_SERVICE_INT = "homeServiceInt";
	
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "attaBase.db";
	public static final int TOTAL_ROWS = 16000;
	
	private AttaBaseContract() {}
	
	public class BaseSchema implements BaseColumns{
		public static final String TABLE_NAME = "base";
		public static final String COLUMN_NAME_BASE_NAME = "basename";
		public static final String COLUMN_NAME_BASE_SERVICE = "baseservice";	
		public static final String STRING_CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + " (" +
						_ID + " INTEGER PRIMARY KEY," +
						COLUMN_NAME_BASE_NAME + " TEXT," +
						COLUMN_NAME_BASE_SERVICE + " INTEGER REFERENCES " + 
							AttaBaseContract.ServiceSchema.TABLE_NAME + "(" + 
							AttaBaseContract.ServiceSchema._ID + ") ON UPDATE CASCADE" +
				")";
		public static final String STRING_DROP_TABLE =
				"DROP TABLE IF EXISTS " + TABLE_NAME;
		public static final String STRING_INSERT_ROW =
				"INSERT INTO " + TABLE_NAME + " (" +
						COLUMN_NAME_BASE_NAME + ", " + 
						COLUMN_NAME_BASE_SERVICE + ") VALUES (?,?)";
	}
	public class ServiceSchema implements BaseColumns{
		public static final String TABLE_NAME = "service";
		public static final String COLUMN_NAME_SERVICE_NAME = "servicename";
		public static final String STRING_CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + " (" +
						_ID + " INTEGER PRIMARY KEY," +
						COLUMN_NAME_SERVICE_NAME + " TEXT" + 
				")";
		public static final String STRING_DROP_TABLE =
				"DROP TABLE IF EXISTS " + TABLE_NAME;
		public static final String STRING_INSERT_ROW =
				"INSERT INTO " + TABLE_NAME + " (" +
						COLUMN_NAME_SERVICE_NAME + ") VALUES (?)";
	}
	public class LocationTypeSchema implements BaseColumns{
		public static final String TABLE_NAME = "directory";
		public static final String COLUMN_NAME_DIRECTORY_NAME = "directoryname";
		public static final String STRING_CREATE_TABLE = 
				"CREATE TABLE " + TABLE_NAME + " (" +
						_ID + " INTEGER PRIMARY KEY," +
						COLUMN_NAME_DIRECTORY_NAME + " TEXT" +
				")";
		public static final String STRING_DROP_TABLE =
				"DROP TABLE IF EXISTS " + TABLE_NAME;
		public static final String STRING_INSERT_ROW =
				"INSERT INTO " + TABLE_NAME + " (" +
						COLUMN_NAME_DIRECTORY_NAME + ") VALUES (?)";
	}
	public class LocationSchema implements BaseColumns{
		public static final String TABLE_NAME = "location";
		public static final String COLUMN_NAME_LOCATION_NAME = "locationname";
		public static final String COLUMN_NAME_LOCATION_TYPE = "locationtype";
		public static final String COLUMN_NAME_DOD_ID = "dod_id";
		public static final String COLUMN_NAME_BASE = "base";
		public static final String COLUMN_NAME_ADDRESS1 = "address1";
		public static final String COLUMN_NAME_ADDRESS2 = "address2";
		public static final String COLUMN_NAME_ADDRESS3 = "address3";
		public static final String COLUMN_NAME_ADDRESS4 = "address4";
		public static final String COLUMN_NAME_CITY = "city";
		public static final String COLUMN_NAME_STATE = "state";
		public static final String COLUMN_NAME_COUNTRY = "country";
		public static final String COLUMN_NAME_ZIP_CODE = "zip";
		public static final String COLUMN_NAME_PHONE1 = "commercial1";
		public static final String COLUMN_NAME_PHONE2 = "commercial2";
		public static final String COLUMN_NAME_PHONE3 = "commercial3";
		public static final String COLUMN_NAME_FAX = "commercialfax";
		public static final String COLUMN_NAME_DSN = "dsn";
		public static final String COLUMN_NAME_DSN_FAX = "dsnfax";
		public static final String COLUMN_NAME_WEBSITE1 = "website1";
		public static final String COLUMN_NAME_WEBSITE2 = "website2";
		public static final String COLUMN_NAME_WEBSITE3 = "website3";
		
		public static final String STRING_CREATE_TABLE = 
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

						COLUMN_NAME_LOCATION_TYPE + " INTEGER REFERENCES " + 
							AttaBaseContract.LocationTypeSchema.TABLE_NAME + "(" + 
							AttaBaseContract.LocationTypeSchema._ID + ") ON UPDATE CASCADE, " +
							
						COLUMN_NAME_BASE + " INTEGER REFERENCES " + 
							AttaBaseContract.BaseSchema.TABLE_NAME + "(" + 
							AttaBaseContract.BaseSchema._ID + ") ON UPDATE CASCADE " +
				")";
		public static final String STRING_DROP_TABLE =
				"DROP TABLE IF EXISTS " + TABLE_NAME;
		public static final String STRING_INSERT_ROW =
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
						COLUMN_NAME_BASE + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	}
}
