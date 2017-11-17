package co.bantamstudio.attabase;

import java.util.HashSet;
import java.util.Set;

import android.database.Cursor;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

class Base {
	private String baseString;
	private Service service;
	private Location location;
	private long baseIndex;
	private boolean hasAddress;


	Base(Context context, Service service, long baseIndex) throws Exception{
		if (service == null){
			throw new Exception("need a valid service");
		}
		this.service = service;
		this.baseIndex = baseIndex;
		
		ContentResolver cr = context.getContentResolver();
		Uri baseUri = Uri.withAppendedPath(AttaBaseProvider.CONTENT_URI_BASE, String.valueOf(baseIndex));
		baseUri = Uri.withAppendedPath(baseUri, "address");
    	Cursor locationInfo = cr.query(baseUri, null, null, null, null);
    	if (locationInfo != null && locationInfo.moveToFirst()){
    		baseString = locationInfo.getString(1);
    		location = new Location(service, this, locationInfo.getInt(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema._ID)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE1)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE2)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE3)),
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
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_FAX)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_DSN)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_DSN_FAX)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE1)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE2)),
    				locationInfo.getString(locationInfo.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE3)));
    		hasAddress = true;
    		locationInfo.close();
    	}
    	else {
    		hasAddress = false;    		
    		// Close cursor if no address found
    		if (locationInfo != null)
    			locationInfo.close();
    		
    		location = null;
    		
    		baseUri = Uri.withAppendedPath(AttaBaseProvider.CONTENT_URI_BASE, String.valueOf(baseIndex));
    		Cursor baseInfo = cr.query(baseUri, null, null, null, null);
        	if (baseInfo != null && baseInfo.moveToFirst()){
        		baseString = baseInfo.getString(baseInfo.getColumnIndex(AttaBaseContract.BaseSchema.COLUMN_NAME_BASE_NAME));
        		baseInfo.close();
            }
        	else {
        		if (baseInfo != null)
        			baseInfo.close();
        		throw new Exception("no base found");
        	}
    	}
	}
	
	String getBaseString() {
		return baseString;
	}
	private String getServiceString() {
		return service.getServiceString();
	}
	Service getService() {
		return service;
	}
	long getBaseIndex(){
		return baseIndex;
	}
	Location getLocation(){
		return location;
	}
	boolean hasAddress() {
		return hasAddress;
	}
	
	Set<String> getKeywords(){
		Set<String> keywords = new HashSet<>();
		if (getBaseString() != null)
			keywords.add(getBaseString());
		if (getServiceString() != null)
			keywords.add(getServiceString());
		if (getLocation() != null)
			keywords.addAll(getLocation().getKeywords());
		if (getService() != null)
			keywords.addAll(getService().getKeywords());
		
		return keywords;
	}
}
