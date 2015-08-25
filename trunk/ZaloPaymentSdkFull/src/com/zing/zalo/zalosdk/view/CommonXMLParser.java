package com.zing.zalo.zalosdk.view;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.zing.zalo.zalosdk.model.ViewFactory;
import com.zing.zalo.zalosdk.model.ZBankPager;
import com.zing.zalo.zalosdk.payment.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class CommonXMLParser {

	protected Activity activity = null;
	LinearLayout viewRoot;
	public LinearLayout getViewRoot() {
		return viewRoot;
	}

	ViewFactory factory;
	String page;
	String path;
	int id = 0;
	public ViewFactory getFactory() {
		return factory;
	}
	
	public CommonXMLParser(Activity activity, String page) {
		this.activity = activity;
		this.page = page;
		this.id = 0;
		path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
		factory = new ViewFactory();
		viewRoot = (LinearLayout) activity.findViewById(R.id.view_root);
	}
	public void loadViewFromXml() throws IOException, XmlPullParserException, JSONException {
		InputStream in = null;
		try {
			String path = this.path + File.separator + "Unzip/test.xml";
			File file = new File(path);
			in = new BufferedInputStream(new FileInputStream(file));
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readFeed(parser);
        } finally {
            in.close();
        }
	}
	
	protected void readFeed(XmlPullParser parser)
			throws XmlPullParserException, IOException, JSONException {

		parser.require(XmlPullParser.START_TAG, null, "root");
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        // Starts by looking for the entry tag
	        if (name.equals(page)){
	        	readPage(parser);
	        	
	        }else {
	        	skip(parser);
	        }
	        
	    } 
		
	}
	
	private void readPage(XmlPullParser parser) throws XmlPullParserException, IOException, JSONException {
		
		parser.require(XmlPullParser.START_TAG, null, page);
		while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String type = parser.getName();
            if (type.equals("static")) {
            	readStaticComponent(parser);
            	
            }else if (type.equals("dynamic")) {
            	readDynamicComponent(parser);
            }
            
    	}
	}
	
	private void readStaticComponent(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "static");
		
		while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name1 = parser.getName();
            String id = parser.getAttributeValue(null, "id");
            
			if (parser.next() == XmlPullParser.TEXT) {
				if (name1.equals("TextView")) {
					
					String text = parser.getText();

					if (id.equals("zalosdk_payment_choosing")) {
						((TextView) activity.findViewById(R.id.zalosdk_atmacc)).setText(text);
					} else if (id.equals("zalosdk_zingcard")) {
						((TextView) activity.findViewById(R.id.zalosdk_zingcard)).setText(text);
					} else if (id.equals("zalosdk_mobile_card")) {
						((TextView) activity.findViewById(R.id.zalosdk_mobile_card)).setText(text);
					} else if (id.equals("zalosdk_mobileacc")) {
						((TextView) activity.findViewById(R.id.zalosdk_mobileacc)).setText(text);
					} else if (id.equals("zalosdk_atmacc")) {
						((TextView) activity.findViewById(R.id.zalosdk_atmacc)).setText(text);
					} else if (id.equals("zalosdk_billinfo")) {
						((TextView) activity.findViewById(R.id.zalosdk_billinfo)).setText(text);
					} else if (id.equals("zalosdk_ok_ctl")) {
						((TextView) activity.findViewById(R.id.zalosdk_ok_ctl)).setText(text);
					} else if (id.equals("zalosdk_hotline")) {
						((TextView) activity.findViewById(R.id.zalosdk_hotline)).setText(text);
					} else if (id.equals("zalosdk_hotline_ctl")) {
						((TextView) activity.findViewById(R.id.zalosdk_hotline_ctl)).setText(text);
					} else if (id.equals("zalosdk_otp_note_ctl")) {
						((TextView) activity.findViewById(R.id.zalosdk_otp_note_ctl)).setText(text);
					} 
					

				} else if (name1.equals("ImageView")) {
					if (id.equals("zalosdk_zalopay_logo")) {
						setImgFromFile(R.id.zalosdk_zalopay_logo,parser);
					} else if (id.equals("zalosdk_back_ctl")) {
						setImgFromFile(R.id.zalosdk_back_ctl,parser);
					} else if (id.equals("zalosdk_exit_ctl")) {
						setImgFromFile(R.id.zalosdk_exit_ctl,parser);
					} else if (id.equals("zalosdk_show_ctl")) {
						setImgFromFile(R.id.zalosdk_show_ctl,parser);
					} else if (id.equals("zalosdk_info_ctl")) {
						setImgFromFile(R.id.zalosdk_info_ctl,parser);
					} else if (id.equals("zalosdk_ic_zingcard")) {
						setImgFromFile(R.id.zalosdk_ic_zingcard,parser);
					} else if (id.equals("zalosdk_ic_mobile_card")) {
						setImgFromFile(R.id.zalosdk_ic_mobile_card,parser);
					} else if (id.equals("zalosdk_ic_phone")) {
						setImgFromFile(R.id.zalosdk_ic_phone,parser);
					} else if (id.equals("zalosdk_ic_atm")) {
						setImgFromFile(R.id.zalosdk_ic_atm,parser);
					}
					
				}
			}
            parser.nextTag();
            
    	}
		
		parser.require(XmlPullParser.END_TAG, null, "static");
	}
	
	private void readDynamicComponent(XmlPullParser parser) throws XmlPullParserException, IOException, JSONException {
	
    	parser.require(XmlPullParser.START_TAG, null, "dynamic");
    	while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name1 = parser.getName();
            if (name1.equals("Compound")) {
            	while (parser.next() != XmlPullParser.END_TAG) {
	                if (parser.getEventType() != XmlPullParser.START_TAG) {
	                    continue;
	                }
	                String name2 = parser.getName();
	                JSONObject o = new JSONObject();
	                if (name2.equals("TextView")) {
	                	parser.require(XmlPullParser.START_TAG, null, "TextView");
	                	String append = parser.getAttributeValue(null, "append");
	                	if (parser.next() == XmlPullParser.TEXT) {
	                		
							o.put("text", parser.getText());
							if (append!=null) {
								o.put("append", append);
							}
	                		View v = factory.produce(activity, ViewFactory.TEXT_VIEW, o).generateView();
	                		getViewRoot().addView(v, id++);
	                		
	                	}
	                	parser.nextTag();
	                	parser.require(XmlPullParser.END_TAG, null, "TextView");
	                } else if (name2.equals("EditText")) {
	                	o = parseZEdit(parser);
	                	View v = factory.produce(activity, ViewFactory.EDIT_VIEW, o).generateView();
	                	getViewRoot().addView(v, id++); 

	                } else if (name2.equals("LinearLayout")) {
	                	o = parseZLinearLayout(parser);
//	                	View v = factory.produce(activity, ViewFactory.LINEAR_LAYOUT, o).generateView();
//	                	getViewRoot().addView(v, id++);
	                } else if (name2.equals("Pager")) {
	                	o = parseZPager(parser);
	                	factory.produce(activity, ViewFactory.PAGER, o);
	                } else if (name2.equals("BankPager")) {
	                	parseZBankPager(parser);
	                } else if (name2.equals("HiddenEditText")) {
	                	o = parseHiddenEditText(parser);
	                	factory.produce(activity, ViewFactory.HIDDEN_EDIT_VIEW, o);
	                } else if (name2.equals("ImageView")) {
	                	o = parseZImageView(parser);
	                	View v = factory.produce(activity, ViewFactory.IMAGE_VIEW, o).generateView();
	                	getViewRoot().addView(v, id++); 
	                } else {
	                	skip(parser);
	                }
           
            	}
            }
            
    	}
    	
    	parser.require(XmlPullParser.END_TAG, null, "dynamic");
	}
	
	protected void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
	
	protected void setImgFromFile(int id, XmlPullParser parser) {
		String path = this.path + parser.getText();
		ImageView i = ((ImageView) activity.findViewById(id));
		i.setImageDrawable(Drawable.createFromPath(path));	 
	}
	
	protected String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}	
	
	protected JSONObject parseZImageView(XmlPullParser parser) throws IOException, XmlPullParserException, JSONException {
		parser.require(XmlPullParser.START_TAG, null, "ImageView");
		String width = null;
		String height = null;
		String type = null;
		String scaleType = null;
		JSONObject o = new JSONObject();
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("type")) {
	        	type = readText(parser);   	
	        } else if (name.equals("width")) {
	        	width = readText(parser);
	        } else if (name.equals("height")) {
	        	height = readText(parser);
	        } else if (name.equals("scaleType")) {
	        	scaleType = readText(parser);
	        } else {
	        	skip(parser);
	        }
	        
	    }
	    o.put("type", type);
	    o.put("width", width);
	    o.put("height", height);
	    o.put("scaleType", scaleType);
	    return o;
	}
	protected JSONObject parseHiddenEditText(XmlPullParser parser) throws IOException, XmlPullParserException, JSONException {
		
		parser.require(XmlPullParser.START_TAG, null, "HiddenEditText");
		String param = null;
		String id = null;
		JSONObject o = new JSONObject();
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("id")) {
	        	id = readText(parser);
	        	id = String.valueOf(activity.getResources().getIdentifier(id, "id", activity.getPackageName()));
	        } else if (name.equals("param")) {
	        	param = readText(parser);
	        }
	    }
	    o.put("id", id);
	    o.put("param", param);
	    return o;
	}
	
	protected void parseZBankPager(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "BankPager");
		ZBankPager bankPager = new ZBankPager(activity, null);
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("bank")) {
	        	String bank = readText(parser);
	        	bankPager.setBank(bank);
	        }
	    }
		
		factory.addAbstractView(bankPager);
	}
	protected JSONObject parseZPager(XmlPullParser parser) throws IOException, XmlPullParserException, JSONException {
		parser.require(XmlPullParser.START_TAG, null, "Pager");
		String value = null;
		JSONObject o = new JSONObject();
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("value")) {
	        	value = readText(parser);
	        }
	    }
	    o.put("value", value);
	    return o;
	}
	protected JSONObject parseZEdit(XmlPullParser parser) throws IOException, XmlPullParserException, JSONException {
		parser.require(XmlPullParser.START_TAG, null, "EditText");
		String hint = null;
    	String param = null;
    	String height = null;
    	String width = null;
    	String require = null;
    	String errClientMess = null;
    	String cache = null;
    	String background = null;
    	String imeOptions = null;
    	String inputType = null;
    	String maxLength = null;
    	String padding = null;
    	String layout_marginBottom = null;
    	String layout_marginTop = null;
    	String singleLine = null;
    	String textSize = null;
    	String textStyle = null;
    	JSONObject o = new JSONObject();
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("hint")) {
	            hint = readText(parser);
	        } else if (name.equals("param")) {
	        	param = readText(parser);
	        } else if (name.equals("height")) {
	        	height = readText(parser);
	        } else if (name.equals("width")) {
	        	width = readText(parser);
	        } else if (name.equals("height")) {
	        	height = readText(parser);
	        } else if (name.equals("require")) {
	        	require = readText(parser);
	        } else if (name.equals("errClientMess")) {
	        	errClientMess = readText(parser);
	        } else if (name.equals("cache")) {
	        	cache = readText(parser);
	        } else if (name.equals("background")) {
	        	background = readText(parser);
	        } else if (name.equals("imeOptions")) {
	        	imeOptions = readText(parser);
	        } else if (name.equals("inputType")) {
	        	inputType = readText(parser);
	        } else if (name.equals("maxLength")) {
	        	maxLength = readText(parser);
	        } else if (name.equals("padding")) {
	        	padding = readText(parser);
	        } else if (name.equals("layout_marginBottom")) {
	        	layout_marginBottom = readText(parser);
	        } else if (name.equals("layout_marginTop")) {
	        	layout_marginTop = readText(parser);
	        } else if (name.equals("singleLine")) {
	        	singleLine = readText(parser);
	        } else if (name.equals("textSize")) {
	        	textSize = readText(parser);
	        } else if (name.equals("textStyle")) {
	        	textStyle = readText(parser);
	        }    
	        
	    }
	    o.put("hint", hint);
    	o.put("param", param);
    	o.put("width", width);
    	o.put("height", height);
    	o.put("require", require);
    	o.put("errClientMess", errClientMess);
    	o.put("cache", cache);
    	o.put("background", background);
    	o.put("imeOptions", imeOptions);
    	o.put("inputType", inputType);
    	o.put("maxLength", maxLength);
    	o.put("padding", padding);
    	o.put("layout_marginBottom", layout_marginBottom);
    	o.put("singleLine", singleLine);
    	o.put("textSize", textSize);
    	o.put("textStyle", textStyle);
    	o.put("layout_marginTop", layout_marginTop);
	    return o;
	}
	
	private JSONObject parseZLinearLayout (XmlPullParser parser) throws IOException, XmlPullParserException, JSONException {
		String width = null;
		String height = null;
		String orientation = null;
		String layout_gravity = null;
		String gravity = null;
		String layout_marginTop = null;
		String layout_marginBottom = null;
		
		JSONObject o = new JSONObject();
		List<View> lViews = new ArrayList<View>();
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("width")) {
	        	width = readText(parser);
	        } else if (name.equals("height")) {
	        	height = readText(parser);
	        } else if (name.equals("orientation")) {
	        	orientation = readText(parser);
	        } else if (name.equals("layout_gravity")) {
	        	layout_gravity = readText(parser); 
	        } else if (name.equals("gravity")) {
	        	gravity = readText(parser); 
	        } else if (name.equals("layout_marginTop")) {
	        	layout_marginTop = readText(parser); 
	        } else if (name.equals("layout_marginBottom")) {
	        	layout_marginBottom = readText(parser); 
	        } else if (name.equals("TextView")) {
	        	parser.require(XmlPullParser.START_TAG, null, "TextView");
            	String append = parser.getAttributeValue(null, "append");
            	String width1 = parser.getAttributeValue(null, "width");
            	String height1 = parser.getAttributeValue(null, "height");
            	String margin = parser.getAttributeValue(null, "margin");
            	String gravity1 = parser.getAttributeValue(null, "gravity");
            	String background = parser.getAttributeValue(null, "background");
            	String textColor = parser.getAttributeValue(null, "textColor");
            	String param = parser.getAttributeValue(null, "param");
            	String value = parser.getAttributeValue(null, "value");
            	if (parser.next() == XmlPullParser.TEXT) {
            		JSONObject o1= new JSONObject();
					o1.put("text", parser.getText());
					o1.put("append", append);
					o1.put("width", width1);
					o1.put("height", height1);
					o1.put("margin", margin);
					o1.put("gravity", gravity1);
					o1.put("background", background);
					o1.put("textColor", textColor);
					o1.put("param", param);
					o1.put("value", value);
            		View v = factory.produce(activity, ViewFactory.TEXT_VIEW, o1).generateView();  		
            		lViews.add(v);
            	}
            	parser.nextTag();
            	parser.require(XmlPullParser.END_TAG, null, "TextView");
	        } else if (name.equals("EditText")) {
	        	JSONObject o1= new JSONObject();
	        	o1 = parseZEdit(parser);
	        	View v = factory.produce(activity, ViewFactory.EDIT_VIEW, o1).generateView();  		
        		lViews.add(v);
	        } else {
	        	skip(parser);
	        }
	        
	    }
	    o.put("width", width);
	    o.put("height", height);
        o.put("orientation", orientation);
        o.put("layout_gravity", layout_gravity);
        o.put("gravity", gravity);
        o.put("layout_marginTop", layout_marginTop);
        o.put("layout_marginBottom", layout_marginBottom);
        
        LinearLayout v = (LinearLayout) factory.produce(activity, ViewFactory.LINEAR_LAYOUT, o).generateView();
        for (View view : lViews) {
			v.addView(view);
		}
        
    	getViewRoot().addView(v, id++);
    	
        return o;
	}
}
