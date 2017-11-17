package co.bantamstudio.attabase;

import java.util.HashSet;
import java.util.Set;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Location {
	private long locIndex;
	private String locationName;
	private String locationPhone1;
	private String locationPhone2;
	private String locationPhone3;
	private String locationType;
	private String locationId;
	private String locationAddress1;
	private String locationAddress2;
	private String locationAddress3;
	private String locationAddress4;
	private String locationCity;
	private String locationState;
	private String locationCountry;
	private String locationZip;
	private String locationPhoneFax;
	private String dsn;
	private String dsnFax;
	private String website1;
	private String website2;
	private String website3;
	private Service service;
	private Base base;
	
	Location(Service service, Base base, long locIndex,
			String locationName,
			String locationPhone1,
			String locationPhone2,
			String locationPhone3,
			String locationType,
			String locationId,
			String locationAddress1,
			String locationAddress2,
			String locationAddress3,
			String locationAddress4,
			String locationCity,
			String locationState,
			String locationCountry,
			String locationZip,
			String locationPhoneFax,
			String dsn,
			String dsnFax,
			String website1,
			String website2,
			String website3){
		this.locIndex = locIndex;
		this.service = service;
		this.base = base;
		this.locationName = locationName;
		this.locationPhone1 = locationPhone1;
		this.locationPhone2 = locationPhone2;
		this.locationPhone3 = locationPhone3;
		this.locationType = locationType;
		this.locationId = locationId;
		this.locationAddress1 = locationAddress1;
		this.locationAddress2 = locationAddress2;
		this.locationAddress3 = locationAddress3;
		this.locationAddress4 = locationAddress4;
		this.locationCity = locationCity;
		this.locationState = locationState;
		this.locationCountry = locationCountry;
		this.locationZip = locationZip;
		this.locationPhoneFax = locationPhoneFax;
		this.dsn = dsn;
		this.dsnFax = dsnFax;
		this.website1 = website1;
		this.website2 = website2;
		this.website3 = website3;
	}
	
	Location(Context context, long locIndex, Service service, Base base) throws Exception{
		this.locIndex = locIndex;
		this.service = service;
		this.base = base;
		
		ContentResolver cr = context.getContentResolver();
		Uri locationUri = Uri.withAppendedPath(AttaBaseProvider.CONTENT_URI_LOCATION, String.valueOf(locIndex));
		Cursor location = cr.query(locationUri, null, null, null, null);
        //Cursor location = mDbHelper.getLocation(locIndex);
        if (location.moveToFirst()){
        	locationName = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_NAME));
        	locationType = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_LOCATION_TYPE));
        	locationId = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_DOD_ID));
        	locationAddress1 = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS1));
        	locationAddress2 = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS2));
        	locationAddress3 = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS3));
        	locationAddress4 = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_ADDRESS4));
        	locationCity = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_CITY));
        	locationState = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_STATE));
        	locationCountry = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_COUNTRY));
        	locationZip = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_ZIP_CODE));
        	locationPhone1 = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE1));
        	locationPhone2 = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE2));
        	locationPhone3 = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_PHONE3));
        	locationPhoneFax = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_FAX));
        	dsn = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_DSN));
        	dsnFax = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_DSN_FAX));
        	website1 = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE1));
        	website2 = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE2));
        	website3 = location.getString(location.getColumnIndex(AttaBaseContract.LocationSchema.COLUMN_NAME_WEBSITE3));
        }
        else 
        	throw new Exception("not a valid location id");
        location.close();
	}
	
	public long getLocIndex() {
		return locIndex;
	}

	public String getLocationName() {
		return locationName;
	}


	public String getLocationPhone1() {
		return locationPhone1;
	}

	public String getLocationPhone2() {
		return locationPhone2;
	}

	public String getLocationPhone3() {
		return locationPhone3;
	}

	public String getLocationType() {
		return locationType;
	}

	public String getLocationId() {
		return locationId;
	}

	public String getLocationAddress1() {
		return locationAddress1;
	}

	public String getLocationAddress2() {
		return locationAddress2;
	}

	public String getLocationAddress3() {
		return locationAddress3;
	}

	public String getLocationAddress4() {
		return locationAddress4;
	}

	public String getLocationCity() {
		return locationCity;
	}

	public String getLocationState() {
		return locationState;
	}

	public String getLocationCountry() {
		return locationCountry;
	}

	public String getLocationZip() {
		return locationZip;
	}

	public String getLocationPhoneFax() {
		return locationPhoneFax;
	}

	public String getDsn() {
		return dsn;
	}

	public String getDsnFax() {
		return dsnFax;
	}

	public String getWebsite1() {
		return website1;
	}
	public String getWebsite2() {
		return website2;
	}

	public String getWebsite3() {
		return website3;
	}

	public Service getService() {
		return service;
	}

	public Base getBase() {
		return base;
	}

	public Set<String> getKeywords() {
		Set<String> keywords = new HashSet<String>();
		if (getLocationCity() != null)
			keywords.add(getLocationCity());
		if (getLocationCountry() != null)
			keywords.add(getLocationCountry());
		if (getLocationName() != null)
			keywords.add(getLocationName());
		if (getLocationState() != null)
			keywords.add(getLocationState());
		if (getLocationType() != null)
			keywords.add(getLocationType());
		if (getLocationZip() != null)
			keywords.add(getLocationZip());
		
		return keywords;

	}
}
