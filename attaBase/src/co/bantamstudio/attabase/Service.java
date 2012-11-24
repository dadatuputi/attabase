package co.bantamstudio.attabase;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri.Builder;
import co.bantamstudio.attabase.ActivityBaseList.SERVICE;

public class Service {
	private Context context;
	private SERVICE service;
	private int serviceSymbol;
	private int color1, color2;
	private String serviceString;
	private long serviceIndex;


	Service(Context context, long serviceIndex) throws Exception{
		this.context = context;
		this.serviceIndex = serviceIndex;
		
		// GET SERVICE TEXT
		ContentResolver cr = context.getContentResolver();
		Builder baseUri = AttaBaseProvider.CONTENT_URI_SERVICE.buildUpon();
		baseUri.appendPath("/" + serviceIndex);
		Cursor serviceInfo = cr.query(baseUri.build(), null, null, null, null);
		//Cursor serviceInfo = mDbHelper.getService(serviceIndex);
    	if (serviceInfo.moveToFirst()){
        	serviceString = serviceInfo.getString(serviceInfo.getColumnIndex(AttaBaseContract.ServiceSchema.COLUMN_NAME_SERVICE_NAME));
        }
    	else {
    		serviceInfo.close();
    		throw new Exception("no service found");
    	}
    	serviceInfo.close();
		
		// SET THE SERVICE ENUM AND SYMBOL
        switch ((int)serviceIndex) {
		case 1:
			this.service = SERVICE.ARMY;
			serviceSymbol = R.drawable.army_symbol;
			break;
		case 2:
			this.service = SERVICE.MARINES;
			serviceSymbol = R.drawable.marines_symbol;
			break;
		case 3:
			this.service = SERVICE.NAVY;
			serviceSymbol = R.drawable.navy_symbol;
			break;
		case 4:
			this.service = SERVICE.AIR_FORCE;
			serviceSymbol = R.drawable.af_symbol;
			break;
		default:
			break;
		}
		
        // GET SERVICE COLORS
		color1 = getColor1(service);
		color2 = getColor2(service);
	}
	
	private int getColor1(SERVICE service) {
		switch (service) {
		case AIR_FORCE:
			return context.getResources().getColor(R.color.af_blue);
		case ARMY:
			return context.getResources().getColor(R.color.army_green);
		case MARINES:
			return context.getResources().getColor(R.color.marines_scarlet);
		case NAVY:
			return context.getResources().getColor(R.color.navy_blue);
		default:
			return Color.WHITE;
		}
	}
	
	private int getColor2(SERVICE service) {
		switch (service) {
		case AIR_FORCE:
			return context.getResources().getColor(R.color.af_grey);
		case ARMY:
			return context.getResources().getColor(R.color.army_yellow);
		case MARINES:
			//return getResources().getColor(R.color.marines_scarlet);
			//return Color.WHITE;
		case NAVY:
			return context.getResources().getColor(R.color.navy_gold);
		default:
			return Color.BLACK;
		}
	}
	
	public int getColor1() {
		return color1;
	}

	public int getColor2() {
		return color2;
	}
	
	public SERVICE getService() {
		return service;
	}
	public int getServiceSymbol() {
		return serviceSymbol;
	}
	public String getServiceString() {
		return serviceString;
	}

	public long getServiceIndex() {
		return serviceIndex;
	}

}
