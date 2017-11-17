package co.bantamstudio.attabase;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Header {
	private TextView textView1;
    private TextView textView2;
    private ImageView iv;
	private String text1 = "";
	private String text2 = "";
	private int textColor1 = Color.BLACK;
	private int textColor2 = Color.BLACK;
	private int backgroundColor = Color.WHITE;
	private long text1Size = 22;
	private long text2Size = 12;
	
	public Header(ActivityBaseList.VIEW_TYPE viewType, LinearLayout ll, Service service, Base base) throws Exception {
        textView1 = (TextView) ll.findViewById(R.id.text1);
        textView2 = (TextView) ll.findViewById(R.id.text2);
        iv = (ImageView) ll.findViewById(R.id.serviceIcon);
        
    	switch (viewType) {
		case VIEW_BASE:
			if (base == null)throw new Exception("this view type needs a valid base");
			backgroundColor = base.getService().getColor1();
	    	textColor1 = textColor2 = base.getService().getColor2();
			text1 = base.getBaseString();
	        text2 = base.getService().getServiceString();
	        iv.setVisibility(ImageView.VISIBLE);
	        iv.setImageResource(base.getService().getServiceSymbol());
	        iv.setBackgroundColor(backgroundColor);
			break;
		case VIEW_BASES:
			if (service == null)throw new Exception("this view type needs a valid service");
	    	backgroundColor = service.getColor1();
	    	textColor1 = textColor2 = service.getColor2();
			text1 = service.getServiceString();
	        iv.setVisibility(ImageView.VISIBLE);
	        iv.setImageResource(service.getServiceSymbol());
	        iv.setBackgroundColor(backgroundColor);
	    	break;
		case VIEW_SERVICES:
		case VIEW_LOCATION:
		default:
			throw new Exception("no header for current view type");
		}
    	
        if (this.text1.equals("") && this.text2.equals(""))
        	throw new Exception("no header for current view type");
        else{
        	textView1.setText(this.text1);
        	textView1.setTextColor(textColor1);
        	textView1.setTextSize(text1Size);
        	
        	if (textView2.getVisibility() != TextView.GONE){
        		textView2.setText(this.text2);
        		textView2.setTextColor(textColor2);	    
        		textView2.setTextSize(text2Size);
        	}			    
		    ll.setBackgroundColor(backgroundColor);
        }  
        
	}
	
	
}
