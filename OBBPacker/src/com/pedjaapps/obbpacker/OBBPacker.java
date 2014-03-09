package com.pedjaapps.obbpacker;

import com.android.jobb.Main;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by pedja on 2/27/14.
 */
public class OBBPacker
{
    public static void main(String[] args)
    {
        File androidManifest = new File("/home/pedja/workspace/SMC-Android/", "AndroidManifest.xml");
        String obbKey = "s3cr3tm@r10chr0n1cl3s";
        String versionCode;
        String packageName = "rs.papltd.smc";
        try
        {
            versionCode = getVersionCode(readFileContent(androidManifest.getAbsolutePath(), StandardCharsets.UTF_8));
        }
        catch (IOException | ParserConfigurationException | SAXException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        args = new String[]
                {
                        "-d", "./data/",
                        "-o", "main." + versionCode + "." + packageName + ".obb",
                        /*"-k", obbKey,*/
                        "-pn", packageName,
                        "-pv", versionCode
                };
        Main.main(args);
    }

    public static String readFileContent(String filename, Charset charset) throws IOException
    {
        RandomAccessFile raf = null;
        try
        {
            raf = new RandomAccessFile(filename, "r");
            byte[] buffer = new byte[(int) raf.length()];
            raf.readFully(buffer);
            return new String(buffer, charset);
        }
        finally
        {
            closeStream(raf);
        }
    }


    private static void closeStream(Closeable c)
    {
        if (c != null)
        {
            try
            {
                c.close();
            }
            catch (IOException ex)
            {
                // do nothing
            }
        }
    }

    private static String getVersionCode(String androidManifest)
            throws ParserConfigurationException, SAXException, IOException
    {
        XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        // create a SAXXMLHandler
        SAXXMLHandler saxHandler = new SAXXMLHandler();
        // store handler in XMLReader
        xmlReader.setContentHandler(saxHandler);
        // the process starts
        xmlReader.parse(new InputSource(new ByteArrayInputStream(androidManifest.getBytes("UTF-8"))));
        return saxHandler.getTempVal();
    }
}
