package com.srandroid.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.srandroid.database.TableRecords.RecordItem;
import com.srandroid.database.TableScripts.ScriptItem;

import android.util.Xml;

public class ScriptXMLParser 
{
	// ignore namespace
    private static final String ns = null;
    
    
    private String sectionname;
    private String mode;
    private String order;
    private String promptphase;
    private String speakerdisplay;
    
    
    public ScriptXMLParser() 
	{
    	
	}

    
    public ScriptItem parseScriptMetadata(InputStream in) throws XmlPullParserException, IOException 
    {
        try 
        {
        	 XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
        	 xppf.setNamespaceAware(false); 
        	 XmlPullParser parser = xppf.newPullParser();
        	 
        	 parser.setInput(in, null);
        	
//            XmlPullParser parser = Xml.newPullParser();
//            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//            parser.setInput(in, null);
        	 
            parser.nextTag();
            return pullMetadata(parser);
        } finally {
            in.close();
        }
    }
    

	

	public List<RecordItem> parseScriptRecordings(InputStream in) throws XmlPullParserException, IOException 
    {
        try 
        {
		   	 XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
		   	 xppf.setNamespaceAware(false); 
		   	 XmlPullParser parser = xppf.newPullParser();
		   	 
		   	 parser.setInput(in, null);
        	
//            XmlPullParser parser = Xml.newPullParser();
//            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//            parser.setInput(in, null);
            parser.nextTag();
            return pullScriptRecordings(parser);
        } finally {
            in.close();
        }
    }

	
    
    
    
    private ScriptItem pullMetadata(XmlPullParser parser) throws XmlPullParserException, IOException
    {
    	ScriptItem scriptItem = new ScriptItem();
    	
    	parser.require(XmlPullParser.START_TAG, ns, "script");
    	while (parser.next() != XmlPullParser.END_TAG) 
    	{
            if (parser.getEventType() != XmlPullParser.START_TAG) 
            {
                continue;
            }
            
            // Only one metadata element 
            if (parser.getName().equals("metadata")) 
            {
            	parser.nextTag();
            	// read the first key element
            	if(parser.getName().equals("key")) 
            	{
            		readKeyValue(parser, scriptItem);
            		return scriptItem;
            	}
            	
            } 
        }
    	
    	return null;
	}

    
    private List<RecordItem> pullScriptRecordings(XmlPullParser parser) throws XmlPullParserException, IOException 
    {
        List<RecordItem> recordItemList = new  ArrayList<RecordItem>();

        parser.require(XmlPullParser.START_TAG, ns, "script");
        while (parser.next() != XmlPullParser.END_TAG) 
        {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            
            String name = parser.getName();
            if (name.equals("recordingscript")) 
            {
            	parser.require(XmlPullParser.START_TAG, ns, "recordingscript");
                while (parser.next() != XmlPullParser.END_TAG) 
                {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    
                	parser.require(XmlPullParser.START_TAG, ns, "section");
                	if(parser.getName().equals("section"))
    	        	{
                		parser.require(XmlPullParser.START_TAG, ns, "section");
    	        		this.mode = parser.getAttributeValue(null, "mode");
    	        		this.sectionname = parser.getAttributeValue(null, "name");
    	        		this.order = parser.getAttributeValue(null, "order");
    	        		this.promptphase = parser.getAttributeValue(null, "promptphase");
    	        		this.speakerdisplay = parser.getAttributeValue(null, "speakerdisplay");
    	        	}
    		        		
                	parser.nextTag();
            		parser.require(XmlPullParser.START_TAG, ns, "recording");
            		if(parser.getName().equals("recording"))
            				recordItemList.add(readRecording(parser));
            		else  skip(parser);
                    
                }
                  
            }
        }  
        return recordItemList;
    }



    private RecordItem readRecording(XmlPullParser parser) throws XmlPullParserException, IOException 
    {
		RecordItem recordItem = new RecordItem();
		
		recordItem.setMode(this.mode);
		recordItem.setSectionname(this.sectionname);
		recordItem.setOrder(this.order);
		recordItem.setPromptphase(this.promptphase);
		recordItem.setSpeakerdisplay(this.speakerdisplay);
    	
    	recordItem.setItemcode(parser.getAttributeValue(null, "itemcode"));
    	recordItem.setPostrecdelay(parser.getAttributeValue(null, "postrecdelay"));
    	recordItem.setPrerecdelay(parser.getAttributeValue(null, "prerecdelay"));
    	recordItem.setRecduration(parser.getAttributeValue(null, "recduration"));
    	
    	if (parser.next() != XmlPullParser.END_TAG) 
        {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                return null;
            }
            
            if(parser.getName().equals("recinstructions"))
            {
            	parser.require(XmlPullParser.START_TAG, ns, "recinstructions");
        		recordItem.setRecinstructions(readText(parser));
                parser.require(XmlPullParser.END_TAG, ns, "recinstructions");
                parser.nextTag();
            	
            }
            
            if(parser.getName().equals("recprompt"))
            {
            	parser.next();
            	if(parser.getEventType() == XmlPullParser.TEXT)
            	{
            		recordItem.setRecprompt(readText(parser));
            		parser.require(XmlPullParser.END_TAG, ns, "recprompt");
                    parser.nextTag();
            	}else if( (parser.getEventType() == XmlPullParser.START_TAG) 
            			&& (parser.getName().equals("mediaitem")) )
            			{
		            		parser.require(XmlPullParser.START_TAG, ns, "mediaitem");
		            		recordItem.setRecprompt(readText(parser));
		                    parser.require(XmlPullParser.END_TAG, ns, "mediaitem");
		                    parser.nextTag();
		                    parser.nextTag();
            			}
            }
            
            if(parser.getName().equals("reccomment"))
            {
            	parser.require(XmlPullParser.START_TAG, ns, "reccomment");
            	recordItem.setReccomment(readText(parser));
                parser.require(XmlPullParser.END_TAG, ns, "reccomment");
                parser.nextTag();
            }
            
            return recordItem;
        }
    	
    	
		return null;
	}


	private void readKeyValue(XmlPullParser parser, ScriptItem scriptItem)
    		throws XmlPullParserException, IOException
	{
    	String keyText = null;
    	String valueText = null;
    	while(parser.getName().equals("key"))
    	{
    		parser.require(XmlPullParser.START_TAG, ns, "key");
            keyText = readText(parser);
            parser.require(XmlPullParser.END_TAG, ns, "key");
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, ns, "value");
            valueText = readText(parser);
            parser.require(XmlPullParser.END_TAG, ns, "value");
            
            if(keyText.equals("DatabaseName")) scriptItem.setDatabaseName(valueText);
            if(keyText.equals("ScriptName")) scriptItem.setScriptName(valueText);
            if(keyText.equals("ScriptAuthor")) scriptItem.setScriptAuthor(valueText);
            if(keyText.equals("EmailAuthor")) scriptItem.setEmailAuthor(valueText);
            
            parser.nextTag();
    	}
    	
	}
    
    
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException 
    {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException 
    {
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

	
}
