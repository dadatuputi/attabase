package co.bantamstudio.attabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

public class AttaBaseDatabase {
	
	private final LocationDbHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	public AttaBaseDatabase(Context context) {
		mDbHelper = new LocationDbHelper(context);
		mDb = mDbHelper.getReadableDatabase();
	}
	
	public SQLiteDatabase getReadableDb() {
		return mDb;
	}
	
	public void close(){
		if (mDbHelper != null)
			mDbHelper.close();
	}

	// GET OBJECTS
	public Cursor getBase(String baseId, String[] columns) {
		String selection = "a." + AttaBaseContract.BaseSchema._ID + " = ?";
		String[] selectionArgs = new String[] {baseId};
		
		return query(selection, selectionArgs, columns, AttaBaseContract.AttaBaseSchema.TABLE_SERVICE_BASE_LOC, null, null, null);
	}
	public Cursor getLocation(String locId, String[] columns) {
		String selection = "c." + AttaBaseContract.LocationSchema._ID + " = ?";
		String[] selectionArgs = new String[] {locId};
		
		return query(selection, selectionArgs, columns, AttaBaseContract.AttaBaseSchema.TABLE_ALL, null, null, null);
	}
	public Cursor getService(String serviceId, String[] columns) {
		String selection = "rowid = ?";
		String[] selectionArgs = new String[] {serviceId};
		
		return query(selection, selectionArgs, columns, AttaBaseContract.ServiceSchema.TABLE_NAME, null, null, null);
	}
	public Cursor getBase(String[] columns) {
		return query(null, null, columns, AttaBaseContract.AttaBaseSchema.TABLE_SERVICE_BASE_LOC, null, null, null);
	}
	
	public Cursor getService(String[] columns) {
		return query(null, null, columns, AttaBaseContract.ServiceSchema.TABLE_NAME, null, null, null);
	}
	public Cursor getLocation(String[] columns) {
		return query(null, null, columns, AttaBaseContract.AttaBaseSchema.TABLE_ALL, null, null, null);
	}
	public Cursor getBases(String serviceId, String[] columns) {
		return query("b." + AttaBaseContract.ServiceSchema._ID + " = ?",
						new String[] {serviceId},
						columns,
						AttaBaseContract.AttaBaseSchema.TABLE_ALL, 
						"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME, 
						null, 
						"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME + " COLLATE NOCASE");
		
		// TODO Sort correctly, so installation address is first if present
	}
	public Cursor getBaseAddress(String baseId, String[] columns) {
		return query("a." + AttaBaseContract.BaseSchema._ID + " = ? AND d." + AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME + " = ? ",
				new String[] {baseId, AttaBaseContract.LocationTypeSchema.BASE_ADDRESS_TYPE},
				columns,
				AttaBaseContract.AttaBaseSchema.TABLE_ALL, 
				null, 
				null, 
				null);
	}
	
	// SEARCH / GET MATCHES
	public Cursor getServiceMatches(String string, String[] columns) {
		String selection = AttaBaseContract.ServiceSchema.COLUMN_NAME_SERVICE_NAME + " MATCH ?";
		String[] selectionArgs = new String[] {string+"*"};
		return query(selection, selectionArgs, columns, AttaBaseContract.ServiceSchema.TABLE_NAME, null, null, null);
		
        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE <KEY_WORD> MATCH 'query*'
         * which is an FTS3 search for the query text (plus a wildcard) inside the word column.
         *
         * - "rowid" is the unique id for all rows but we need this value for the "_id" column in
         *    order for the Adapters to work, so the columns need to make "_id" an alias for "rowid"
         * - "rowid" also needs to be used by the SUGGEST_COLUMN_INTENT_DATA alias in order
         *   for suggestions to carry the proper intent data.
         *   These aliases are defined in the DictionaryProvider when queries are made.
         * - This can be revised to also search the definition text with FTS3 by changing
         *   the selection clause to use FTS_VIRTUAL_TABLE instead of KEY_WORD (to search across
         *   the entire table, but sorting the relevance could be difficult.
         */
	}
	public Cursor getBaseMatches(String string, String[] columns) {
		String selection = AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME + " MATCH ?";
		String[] selectionArgs = new String[] {string+"*"};
		return query(selection, selectionArgs, columns, AttaBaseContract.AttaBaseSchema.TABLE_SERVICE_BASE_LOC, null, null, null);
	}
	public Cursor getLocationMatches(String string, String[] columns) {
		String selection = AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME + " MATCH ?";
		String[] selectionArgs = new String[] {string+"*"};
		return query(selection, selectionArgs, columns, AttaBaseContract.AttaBaseSchema.TABLE_ALL, null, null, null);
	}
	
	
	private Cursor query(String selection, String[] selectionArgs, String[] columns, String tables, String groupBy, String having, String sortOrder) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(tables);
		//builder.setProjectionMap(mColumnMap);
		builder.setDistinct(true);
		
		Cursor cursor = builder.query(mDb, 
										columns, 
										selection, 
										selectionArgs, 
										groupBy, 
										having, 
										sortOrder);
		
		if (cursor == null){
			return null;
		} else if (!cursor.moveToFirst()){
			cursor.close();
			return null;
		}
		return cursor;
	}

	
	private class LocationDbHelper extends SQLiteOpenHelper {

		private SQLiteDatabase dbReadable;
		
		LocationDbHelper(Context context){
			super(context, AttaBaseContract.DATABASE_NAME, null, AttaBaseContract.DATABASE_VERSION);
			//dbReadable = this.getReadableDatabase();
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
			// Set up Databases
			dbReadable = db;
			dbReadable.execSQL(AttaBaseContract.ServiceSchema.STRING_CREATE_TABLE);
			dbReadable.execSQL(AttaBaseContract.BaseSchema.STRING_CREATE_TABLE);
			dbReadable.execSQL(AttaBaseContract.LocationTypeSchema.STRING_CREATE_TABLE);
			dbReadable.execSQL(AttaBaseContract.LocationSchema.STRING_CREATE_TABLE);
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
		
		// GET ALL SERVICES NOT SORTED ALPHABETICALLY
//		public Cursor getAllServices() {
//			return dbReadable.query(true, 
//					AttaBaseContract.ServiceSchema.TABLE_NAME,
//					null,
//					null,
//					null,
//					null,
//					null,
//					null, //AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME + " COLLATE NOCASE",
//					null);
//		}
//		
//		// GET ALL BASES, SORTED ALPHABETICALLY
//		public Cursor getAllBases(int service) {
//			String q = 
//					"SELECT " +
//						"a.*" + 
//					" FROM " + 
//						AttaBaseContract.BaseSchema.TABLE_NAME + " a" +
//						" INNER JOIN " + AttaBaseContract.ServiceSchema.TABLE_NAME + " b" +
//						" ON a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_SERVICE + 
//							" = b." + AttaBaseContract.ServiceSchema._ID + 
//					" WHERE " + 
//						"b." + AttaBaseContract.ServiceSchema._ID + " = ?" + 
//					" ORDER BY a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME + " COLLATE NOCASE";
//			return dbReadable.rawQuery(q, new String[]{Integer.toString(service)});	
//		}
//		
//			
//		public Cursor getAllLocationTypes() {
//			Cursor cServices = dbReadable.query(AttaBaseContract.LocationTypeSchema.TABLE_NAME, new String[] {"_id",  AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME}, null, null, null, null, null);
//			return cServices;
//		}
//		
//		public Cursor getAllLocations() {
//			Cursor cServices = dbReadable.query(AttaBaseContract.LocationSchema.TABLE_NAME, new String[] {"_id",  AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME}, null, null, null, null, null);
//			return cServices;
//		}
//		
//		// GET ALL LOCATIONS FROM BASE
//		public Cursor getBaseLocations(int baseNumber, CharSequence constraint) {
//			String qConstraint = (constraint.toString().equals(""))?"":
//					" AND (" + 
//						"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME + " LIKE ? " +
//						//"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_CITY + " LIKE ? OR " +
//						//"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_COUNTRY + " LIKE ? OR " +
//						//"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_STATE + " LIKE ? OR " +
//						//"d." + AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME + " LIKE ?" + 
//						")";
//			String q = 
//					"SELECT " +
//						"c." + AttaBaseContract.LocationTypeSchema._ID + 
//						", c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME +
//						", d." + AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME +
//						", c." + AttaBaseContract.LocationSchema.COLUMN_NAME_CITY +
//						", c." + AttaBaseContract.LocationSchema.COLUMN_NAME_COUNTRY + 
//						", c." + AttaBaseContract.LocationSchema.COLUMN_NAME_STATE + 
//					" FROM " + 
//						AttaBaseContract.BaseSchema.TABLE_NAME + " a" + 
//						" INNER JOIN " + AttaBaseContract.ServiceSchema.TABLE_NAME + " b ON " + 
//							"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_SERVICE + " = " + 
//							"b." + AttaBaseContract.ServiceSchema._ID + 
//						" INNER JOIN " + AttaBaseContract.LocationSchema.TABLE_NAME + " c ON " +
//							"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_BASE + " = " +
//							"a." + AttaBaseContract.BaseSchema._ID +
//						" INNER JOIN " + AttaBaseContract.LocationTypeSchema.TABLE_NAME + " d ON " +
//							"d." + AttaBaseContract.LocationTypeSchema._ID + " = " +
//							"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_TYPE +
//					" WHERE " +
//						"a." + AttaBaseContract.BaseSchema._ID + " = " + baseNumber + 
//						qConstraint + 
//					" ORDER BY c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME + " COLLATE NOCASE";
//				
//			String selectArgs[] = (constraint.toString().equals(""))?null: 
//					new String[] {constraint.toString()};
//			return dbReadable.rawQuery(q, selectArgs);
//		}
//		
//		// GET SINGLE LOCATION
////		public Cursor getBaseLocation(int loc) {
////			String q = 
////					"SELECT *" +
////					" FROM " + 
////						AttaBaseContract.LocationSchema.TABLE_NAME + " a" + 
////					" WHERE " +
////						"a." + AttaBaseContract.LocationSchema._ID + " = " + loc;						
////						
////			return dbReadable.rawQuery(q, null);	
////		}
//		
//		// GET ALL LOCATION TYPES FROM BASE SORTED ALPHABETICALLY 
//		public Cursor getBaseLocationTypes(int baseNumber) {
//			String q = 
//					"SELECT DISTINCT " +
//						"d.*" + 
//					" FROM " + 
//						AttaBaseContract.BaseSchema.TABLE_NAME + " a" + 
//						" INNER JOIN " + AttaBaseContract.ServiceSchema.TABLE_NAME + " b ON " + 
//							"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_SERVICE + " = " + 
//							"b." + AttaBaseContract.ServiceSchema._ID + 
//						" INNER JOIN " + AttaBaseContract.LocationSchema.TABLE_NAME + " c ON " +
//							"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_BASE + " = " +
//							"a." + AttaBaseContract.BaseSchema._ID +
//						" INNER JOIN " + AttaBaseContract.LocationTypeSchema.TABLE_NAME + " d ON " +
//							"d." + AttaBaseContract.LocationTypeSchema._ID + " = " +
//							"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_TYPE +
//					" WHERE " +
//						"a." + AttaBaseContract.BaseSchema._ID + " = " + baseNumber +
//					" GROUP BY " +
//						AttaBaseContract.LocationTypeSchema._ID + 
//					" ORDER BY UPPER(" + AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME + ")";
//						
//			return dbReadable.rawQuery(q, null);	
//		}
//		
//		// GET SINGLE BASE (NOT LOCATION)
//		public Cursor getBase(int baseNumber) {
//			String q = 
//					"SELECT *" +
//					" FROM " + 
//						AttaBaseContract.BaseSchema.TABLE_NAME + " a" + 
//						" INNER JOIN " + AttaBaseContract.ServiceSchema.TABLE_NAME + " b ON " + 
//							"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_SERVICE + " = " + 
//							"b." + AttaBaseContract.ServiceSchema._ID + 
//					" WHERE " +
//						"a." + AttaBaseContract.BaseSchema._ID + " = " + baseNumber;
//			return dbReadable.rawQuery(q, null);	
//		}
//		
//		// GET SINGLE LOCATION
//		public Cursor getLocation(int loc) {
//			String q = 
//					"SELECT *" +
//					" FROM " + 
//						AttaBaseContract.LocationSchema.TABLE_NAME + " a" + 
//					" WHERE " +
//						"a." + AttaBaseContract.LocationSchema._ID + " = " + loc;
//			return dbReadable.rawQuery(q, null);	
//		}
//		
//		// GET INSTALLATION ADDRESS FOR BASE
//		public Cursor getBaseAddress(int baseNumber) {
//			String q = 
//					"SELECT " +
//						"c." + AttaBaseContract.LocationSchema._ID + 
//						", d." + AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME +
//					" FROM " + 
//						AttaBaseContract.BaseSchema.TABLE_NAME + " a" + 
//						" INNER JOIN " + AttaBaseContract.ServiceSchema.TABLE_NAME + " b ON " + 
//							"a." + AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_SERVICE + " = " + 
//							"b." + AttaBaseContract.ServiceSchema._ID + 
//						" INNER JOIN " + AttaBaseContract.LocationSchema.TABLE_NAME + " c ON " +
//							"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_BASE + " = " +
//							"a." + AttaBaseContract.BaseSchema._ID +
//						" INNER JOIN " + AttaBaseContract.LocationTypeSchema.TABLE_NAME + " d ON " +
//							"d." + AttaBaseContract.LocationTypeSchema._ID + " = " +
//							"c." + AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_TYPE +
//					" WHERE " +
//						"a." + AttaBaseContract.BaseSchema._ID + " = " + baseNumber + " AND " +
//						"d." + AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME + " = '" + AttaBaseContract.LocationTypeSchema.BASE_ADDRESS_TYPE + "'";
//						
//			return dbReadable.rawQuery(q, null);	
//		}
//		
//		// GET SINGLE SERVICE
//		public Cursor getService(int service) {
//			String q = 
//					"SELECT *" +
//					" FROM " + 
//						AttaBaseContract.ServiceSchema.TABLE_NAME + " a" + 
//					" WHERE " +
//						"a." + AttaBaseContract.ServiceSchema._ID + " = " + service;
//			return dbReadable.rawQuery(q, null);	
//		}


	}













































}
