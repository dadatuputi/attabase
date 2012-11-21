package co.bantamstudio.attabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationDbHelper extends SQLiteOpenHelper {

	SQLiteDatabase dbReadable;
	
	public LocationDbHelper(Context context){
		super(context, AttaBaseContract.DATABASE_NAME, null, AttaBaseContract.DATABASE_VERSION);
		dbReadable = this.getReadableDatabase();
	}
	
	@Override
	public void onOpen(SQLiteDatabase db){
		super.onOpen(db);
		if(!db.isReadOnly()){
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {

		System.out.println("onCreate");
		
		// Set up Databases
		db.execSQL(AttaBaseContract.ServiceSchema.STRING_CREATE_TABLE);
		db.execSQL(AttaBaseContract.BaseSchema.STRING_CREATE_TABLE);
		db.execSQL(AttaBaseContract.LocationTypeSchema.STRING_CREATE_TABLE);
		db.execSQL(AttaBaseContract.LocationSchema.STRING_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop existing tables
		db.execSQL(AttaBaseContract.ServiceSchema.STRING_DROP_TABLE);
		db.execSQL(AttaBaseContract.BaseSchema.STRING_DROP_TABLE);
		db.execSQL(AttaBaseContract.LocationTypeSchema.STRING_DROP_TABLE);
		db.execSQL(AttaBaseContract.LocationSchema.STRING_DROP_TABLE);
		onCreate(db);
	}
	
	public Cursor getAllServices() {
		Cursor cServices = dbReadable.query(AttaBaseContract.ServiceSchema.TABLE_NAME, new String[] {"_id",  AttaBaseContract.ServiceSchema.COLUMN_NAME_SERVICE_NAME}, null, null, null, null, null);
		return cServices;
	}
	
	public Cursor getAllBases(int service) {
		String q = 
				"SELECT " +
					"a.*" + 
				" FROM " + 
					AttaBaseContract.BaseSchema.TABLE_NAME + " a" +
					" INNER JOIN " + AttaBaseContract.ServiceSchema.TABLE_NAME + " b" +
					" ON a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_SERVICE + 
						" = b." + AttaBaseContract.ServiceSchema._ID + 
				" WHERE " + 
					"b." + AttaBaseContract.ServiceSchema._ID + " = " + service;
		
		return dbReadable.rawQuery(q, null);	
	}
	
	
	
	public Cursor getAllLocationTypes() {
		Cursor cServices = dbReadable.query(AttaBaseContract.LocationTypeSchema.TABLE_NAME, new String[] {"_id",  AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME}, null, null, null, null, null);
		return cServices;
	}
	
	public Cursor getAllLocations() {
		Cursor cServices = dbReadable.query(AttaBaseContract.LocationSchema.TABLE_NAME, new String[] {"_id",  AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME}, null, null, null, null, null);
		return cServices;
	}
	
	public Cursor getBaseLocations(int baseNumber) {
		String q = 
				"SELECT " +
					"c." + AttaBaseContract.LocationTypeSchema._ID + 
					", c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME +
					", d." + AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME +
				" FROM " + 
					AttaBaseContract.BaseSchema.TABLE_NAME + " a" + 
					" INNER JOIN " + AttaBaseContract.ServiceSchema.TABLE_NAME + " b ON " + 
						"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_SERVICE + " = " + 
						"b." + AttaBaseContract.ServiceSchema._ID + 
					" INNER JOIN " + AttaBaseContract.LocationSchema.TABLE_NAME + " c ON " +
						"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_BASE + " = " +
						"a." + AttaBaseContract.BaseSchema._ID +
					" INNER JOIN " + AttaBaseContract.LocationTypeSchema.TABLE_NAME + " d ON " +
						"d." + AttaBaseContract.LocationTypeSchema._ID + " = " +
						"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_TYPE +
				" WHERE " +
					"a." + AttaBaseContract.BaseSchema._ID + " = " + baseNumber;						
					
		return dbReadable.rawQuery(q, null);	
	}
	
	public Cursor getBaseLocation(int loc) {
		String q = 
				"SELECT *" +
				" FROM " + 
					AttaBaseContract.LocationSchema.TABLE_NAME + " a" + 
				" WHERE " +
					"a." + AttaBaseContract.LocationSchema._ID + " = " + loc;						
					
		return dbReadable.rawQuery(q, null);	
	}
	
	public Cursor getBaseLocationTypes(int baseNumber) {
		String q = 
				"SELECT DISTINCT " +
					"d.*" + 
				" FROM " + 
					AttaBaseContract.BaseSchema.TABLE_NAME + " a" + 
					" INNER JOIN " + AttaBaseContract.ServiceSchema.TABLE_NAME + " b ON " + 
						"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_SERVICE + " = " + 
						"b." + AttaBaseContract.ServiceSchema._ID + 
					" INNER JOIN " + AttaBaseContract.LocationSchema.TABLE_NAME + " c ON " +
						"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_BASE + " = " +
						"a." + AttaBaseContract.BaseSchema._ID +
					" INNER JOIN " + AttaBaseContract.LocationTypeSchema.TABLE_NAME + " d ON " +
						"d." + AttaBaseContract.LocationTypeSchema._ID + " = " +
						"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_TYPE +
				" WHERE " +
					"a." + AttaBaseContract.BaseSchema._ID + " = " + baseNumber +
				" GROUP BY " +
					AttaBaseContract.LocationTypeSchema._ID;
					
		return dbReadable.rawQuery(q, null);	
	}
	
	public Cursor getBase(int baseNumber) {
		//Cursor cServices = dbReadable.query(AttaBaseContract.BaseSchema.TABLE_NAME, new String[] {"_id",  AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME}, null, null, null, null, null);
		String q = 
				"SELECT *" +
				" FROM " + 
					AttaBaseContract.BaseSchema.TABLE_NAME + " a" + 
					" INNER JOIN " + AttaBaseContract.ServiceSchema.TABLE_NAME + " b ON " + 
						"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_SERVICE + " = " + 
						"b." + AttaBaseContract.ServiceSchema._ID + 
				" WHERE " +
					"a." + AttaBaseContract.BaseSchema._ID + " = " + baseNumber;
		return dbReadable.rawQuery(q, null);	
		//		return cServices;
	}
	
	public Cursor getLocation(int loc) {
		String q = 
				"SELECT *" +
				" FROM " + 
					AttaBaseContract.LocationSchema.TABLE_NAME + " a" + 
				" WHERE " +
					"a." + AttaBaseContract.LocationSchema._ID + " = " + loc;
		return dbReadable.rawQuery(q, null);	
	}
	
	public Cursor getBaseAddress(int baseNumber) {
		String q = 
				"SELECT " +
					"c." + AttaBaseContract.LocationSchema._ID + 
					", d." + AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME +
				" FROM " + 
					AttaBaseContract.BaseSchema.TABLE_NAME + " a" + 
					" INNER JOIN " + AttaBaseContract.ServiceSchema.TABLE_NAME + " b ON " + 
						"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_SERVICE + " = " + 
						"b." + AttaBaseContract.ServiceSchema._ID + 
					" INNER JOIN " + AttaBaseContract.LocationSchema.TABLE_NAME + " c ON " +
						"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_BASE + " = " +
						"a." + AttaBaseContract.BaseSchema._ID +
					" INNER JOIN " + AttaBaseContract.LocationTypeSchema.TABLE_NAME + " d ON " +
						"d." + AttaBaseContract.LocationTypeSchema._ID + " = " +
						"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_TYPE +
				" WHERE " +
					"a." + AttaBaseContract.BaseSchema._ID + " = " + baseNumber + " AND " +
					"d." + AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME + " = '" + AttaBaseContract.LocationTypeSchema.BASE_ADDRESS_TYPE + "'";
					
		return dbReadable.rawQuery(q, null);	
	}
	
	public Cursor getService(int service) {
		String q = 
				"SELECT *" +
				" FROM " + 
					AttaBaseContract.ServiceSchema.TABLE_NAME + " a" + 
				" WHERE " +
					"a." + AttaBaseContract.ServiceSchema._ID + " = " + service;
		return dbReadable.rawQuery(q, null);	
	}
}
