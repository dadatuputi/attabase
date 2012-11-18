package co.bantamstudio.attabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

public class ActivityImportCSV extends Activity {

	ProgressBar importProgress;
	TextView basesImported;
	TextView locationsImported;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_import_csv_importing);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_show_all_entries, menu);
        return true;
    }
    
    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        
        // Load boolean flag 
        SharedPreferences prefs = getSharedPreferences(AttaBaseContract.APP_STRING, Context.MODE_PRIVATE);
        boolean hasImported = prefs.getBoolean(AttaBaseContract.PREFS_IMPORTED_BOOL, false);
        
        if (hasImported)
        	showCompletedViews();
        else
        	setContentView(R.layout.activity_import_csv_start);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void initialImport(View view) {
    	setContentView(R.layout.activity_import_csv_importing);
    	
        LocationDbHelper mDbHelper = new LocationDbHelper(this);
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        
        // FOR TESTING
        mDbHelper.onUpgrade(db, 1, 1);
        
        importProgress = (ProgressBar)findViewById(R.id.importProgressBar);
        //importProgress.setMax(AttaBaseContract.TOTAL_ROWS);
        basesImported = (TextView)findViewById(R.id.textView3);
        locationsImported = (TextView)findViewById(R.id.textView5);
        
        
        new ImportCSV().execute(db);
    }
    
    public void leaveImport(View view){
    	this.finish();
    }
    
    private void showCompletedViews(){
    	// Once import is complete, show continue button
    	Button continueButton = (Button)findViewById(R.id.importContinueButton);
    	continueButton.setVisibility(Button.VISIBLE);
    	
    	importProgress.setVisibility(ProgressBar.INVISIBLE);
    	
    	TextView importMessage = (TextView)findViewById(R.id.importMessage);
    	importMessage.setText(getString(R.string.import_completed_message));
    	importMessage.setTextSize(15);
    }
    
    private void onComplete(){
    	
    	// Update the boolean preference flagging whether or not the import has happened
    	SharedPreferences prefs = getSharedPreferences(AttaBaseContract.APP_STRING, Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putBoolean(AttaBaseContract.PREFS_IMPORTED_BOOL, true);
    	editor.commit();
    	
    	showCompletedViews();
    }
    
    private void setProgress(Integer progress, int count, int totalBases){
    	importProgress.setProgress(progress);
    	basesImported.setText(Integer.toString(totalBases));
    	locationsImported.setText(Integer.toString(count));
    }
    
    private class ImportCSV extends AsyncTask<SQLiteDatabase, Integer, Integer>{
    	
    	// DOCUMENTATION: http://developer.android.com/reference/android/os/AsyncTask.html
    	
    	private int totalBases, count = 0;
    	
		@Override
		protected Integer doInBackground(SQLiteDatabase... dbs) {

			SQLiteDatabase db = dbs[0];
			
			AssetManager am = getAssets();
			InputStream is;
			InputStreamReader isr;
			try {
				is = am.open(AttaBaseContract.IMPORT_SOURCE_CSV);
				isr = new InputStreamReader(is);
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
			
			int skipline = 1;
			CSVReader reader = new CSVReader(isr, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, CSVParser.DEFAULT_ESCAPE_CHARACTER, skipline);

			Hashtable<String, Integer> services = new Hashtable<String, Integer>();
			Hashtable<String, Integer> installation = new Hashtable<String, Integer>();
			Hashtable<String, Integer> locationType = new Hashtable<String, Integer>();
			
			String branch, dir, base;			
			String[] nextLine;
			long newRowId;
			
			// Use SQL Compiled Statements to speed up inserts
			SQLiteStatement insertBaseStmt = db.compileStatement(AttaBaseContract.BaseSchema.STRING_INSERT_ROW);
			SQLiteStatement insertServiceStmt = db.compileStatement(AttaBaseContract.ServiceSchema.STRING_INSERT_ROW);
			SQLiteStatement insertLocationTypeStmt = db.compileStatement(AttaBaseContract.LocationTypeSchema.STRING_INSERT_ROW);
			SQLiteStatement insertLocationStmt = db.compileStatement(AttaBaseContract.LocationSchema.STRING_INSERT_ROW);
			
			try {
				db.beginTransaction();
				try {
				
					while ((nextLine = reader.readNext()) != null){
						//Branch	 Directory	 Installation	 Title	 ID	 Address Line1	 Address Line2	 Address Line3	 Address Line4	 City	 State	 Country	 Zip Code	 Commercial Phone1	 Commercial Phone2	 Commercial Phone3	 Commercial Fax	 DSN Phone	 DSN Fax	 Web Site Address1	 Web Site Address2	 Web Site Address 3
						if (isCancelled()) break;
						
						// Service Table
						branch = nextLine[0].trim();
						if (!services.containsKey(branch)){
							//Add value to branch table
							//values = new ContentValues();
							//values.put(AttaBaseContract.ServiceSchema.COLUMN_NAME_SERVICE_NAME, branch);
					        //newRowId = db.insert(AttaBaseContract.ServiceSchema.TABLE_NAME, null, values); 
					        insertServiceStmt.bindString(1, branch);
					        newRowId = insertServiceStmt.executeInsert();			        
							//add key/value to keys
					        services.put(branch, (int) newRowId);
						}
						
						// Base Table
						base = nextLine[2].trim();
						if (!installation.containsKey(base)){
							//Add value to base table
							insertBaseStmt.bindString(1, base);
							insertBaseStmt.bindLong(2, services.get(branch));
							newRowId = insertBaseStmt.executeInsert();
							//add key/value to keys
					        installation.put(base, (int) newRowId);
					        totalBases++;
						}
						
						// Directory Table
						dir = nextLine[1].trim();
						if (!locationType.containsKey(dir)){
							//Add value to branch table
							//values = new ContentValues();
							//values.put(AttaBaseContract.LocationTypeSchema.COLUMN_NAME_DIRECTORY_NAME, dir);
					        //newRowId = db.insert(AttaBaseContract.LocationTypeSchema.TABLE_NAME, null, values);
					        insertLocationTypeStmt.bindString(1, dir);
					        newRowId = insertLocationTypeStmt.executeInsert();
							//add key/value to keys
					        locationType.put(dir, (int) newRowId);
						}
						
						
						// Add Listing to Location Table
						insertLocationStmt.bindString(1, nextLine[3].trim());
						insertLocationStmt.bindString(2, nextLine[4].trim());
						insertLocationStmt.bindString(3, nextLine[5].trim());
						insertLocationStmt.bindString(4, nextLine[6].trim());
						insertLocationStmt.bindString(5, nextLine[7].trim());
						insertLocationStmt.bindString(6, nextLine[8].trim());
						insertLocationStmt.bindString(7, nextLine[9].trim());
						insertLocationStmt.bindString(8, nextLine[10].trim());
						insertLocationStmt.bindString(9, nextLine[11].trim());
						insertLocationStmt.bindString(10, nextLine[12].trim());
						insertLocationStmt.bindString(11, nextLine[13].trim());
					    insertLocationStmt.bindString(12, nextLine[14].trim());
					    insertLocationStmt.bindString(13, nextLine[15].trim());
					    insertLocationStmt.bindString(14, nextLine[16].trim());
					    insertLocationStmt.bindString(15, nextLine[17].trim());
					    insertLocationStmt.bindString(16, nextLine[18].trim());
					    insertLocationStmt.bindString(17, nextLine[19].trim());
					    insertLocationStmt.bindString(18, nextLine[20].trim());
					    insertLocationStmt.bindString(19, nextLine[21].trim());
					    insertLocationStmt.bindLong(20, locationType.get(dir));
					    insertLocationStmt.bindLong(21, installation.get(base));
					    newRowId = insertLocationStmt.executeInsert();
					    
						int percent = (int) ((++count/(float)AttaBaseContract.TOTAL_ROWS) * 100);
						publishProgress(percent);
						
						// COMMENT THE FOLLOWING LINE FOR PRODUCTION
						//if (count > 50) break;
					}
					reader.close();
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
			
				
			return count;
		}
    	
		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			setProgress(progress[0], count, totalBases);
		}
		
		@Override
		protected void onPostExecute(Integer result){
			super.onPostExecute(result);
			setProgress(100, count, totalBases);
			onComplete();			
		}
	}
}
