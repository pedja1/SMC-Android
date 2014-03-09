package com.pedjaapps.obbpacker;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pedja on 11/1/13.
 */
public class SAXXMLHandler extends DefaultHandler
{
    private String tempVal;

    // Event Handlers
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException
    {
        if (qName.equalsIgnoreCase("manifest"))
        {
            tempVal = null;
            tempVal = attributes.getValue("android:versionCode");
        }
    }

    public String getTempVal()
    {
        return tempVal;
    }
}
