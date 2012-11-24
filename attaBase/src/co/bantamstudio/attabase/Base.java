package co.bantamstudio.attabase;

import android.database.Cursor;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

public class Base {
	private String baseString;
	private Service service;
	private Location location;
	private long baseIndex;


	Base(Context context, Service service, long baseIndex) throws Exception{
		if (service == null){
			throw new Exception("need a valid service");
		}
		this.service = service;
		this.baseIndex = baseIndex;
		
		ContentResolver cr = context.getContentResolver();
		Uri baseUri = Uri.parse(AttaBaseProvider.CONTENT_URI_BASE.toString()+"/"+baseIndex);
		Cursor baseInfo = cr.query(baseUri, null, null, null, null);
		//Cursor baseInfo = mDbHelper.getBase(baseIndex);
    	if (baseInfo.moveToFirst()){
    		baseString = baseInfo.getString(baseInfo.getColumnIndex(AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME));
        }
    	else {
    		baseInfo.close();
    		throw new Exception("no base found");
    	}
    	baseInfo.close();
    	
    	baseUri = Uri.parse(AttaBaseProvider.CONTENT_URI_BASE.toString()+"/"+baseIndex+"/address");
    	Cursor locationInfo = cr.query(baseUri, null, null, null, null);
    	//Cursor locationInfo = mDbHelper.getBaseAddress(baseIndex);
    	if (locationInfo.moveToFirst()){
    		location = new Location(service, this, locationInfo.getInt(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema._ID)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_TYPE)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_DOD_ID)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS1)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS2)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS3)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS4)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_CITY)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_STATE)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_COUNTRY)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_ZIP_CODE)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE1)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE2)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE3)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_FAX)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_DSN)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_DSN_FAX)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE1)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE2)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE3)));
    	}
    	else
    		location = null;
	}


	public String getBaseString() {
		return baseString;
	}

	public String getServiceString() {
		return service.getServiceString();
	}


	public Service getService() {
		return service;
	}
	
	public long getBaseIndes(){
		return baseIndex;
	}
	
	public Location getLocation(){
		return location;
	}
	

}
