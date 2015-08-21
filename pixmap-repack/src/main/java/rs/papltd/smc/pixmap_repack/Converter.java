package rs.papltd.smc.pixmap_repack;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * Created by pedja on 22.6.14..
 */
public class Converter
{
    /*col_rect (x y w h), width, height*/
    public static void main(String[] args) throws JSONException
    {
        try
        {
            File pixmapFolder = new File("/home/pedja/workspace/SMC-Android/android/assets/data2/pixmaps");
			Collection<File> files = FileUtils.listFiles(pixmapFolder, new String[]{"settings"}, true);
            for(File file : files)
            {
				if(file.isDirectory())continue;
                System.out.println("Processing: " + file.getName());

                String fileContent = readFileContents(file);

                fileContent = fixIt(file, fileContent);

                String newPath = file.getAbsolutePath().replace("pixmaps", "pixmaps2");
                new File(newPath).getParentFile().mkdirs();
                PrintWriter writer = new PrintWriter(newPath, "UTF-8");

                writer.print(fileContent);
                writer.flush();
                writer.close();
            }
			System.out.println("DONE, " + files.size() + " processed.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static String fixIt(File file, String fileContent)
    {
        String[] lines = fileContent.split("\n");

        int offset = 0;
        float originalHeight = -1;
        boolean postProcessColRect = false;
        String baseFile = null;
        for(String line : lines)
        {
            String[] data = line.split(" ");
            if("width".equals(data[0]))
            {
                data[1] = (Float.parseFloat(data[1]) / 64f) + "";
            }
            else if("height".equals(data[0]))
            {
                float tmp = Float.parseFloat(data[1]);
                data[1] = (tmp / 64f) + "";
                originalHeight = tmp;
            }
            else if("base".equals(data[0]))
            {
                baseFile = data[1];
            }
            else if("col_rect".equals(data[0]))
            {
                if(originalHeight < 0)
                {
                    postProcessColRect = true;
                }
                else
                {
                    processColRect(data, originalHeight);
                }
            }
            if(postProcessColRect)
            {
                if(originalHeight < 0 && baseFile != null)
                {
                    originalHeight = readHeightFromBase(new File(file.getParent(), baseFile));
                }
                if(originalHeight > 0)processColRect(data, originalHeight);
            }
            lines[offset] = arrayToString(data, " ");
            offset++;
        }
        return arrayToString(lines, "\n");
    }

    private static float readHeightFromBase(File baseFile)
    {
        String content = readFileContents(baseFile);
        String[] lines = content.split("\n");
        for(String line : lines)
        {
            String[] data = line.split(" ");
            if(data.length > 0 && "height".equals(data[0]))
            {
                return Float.parseFloat(data[1]);
            }
        }
        return -1;
    }

    private static void processColRect(String[] data, float fullHeight)
    {
        float x = Float.parseFloat(data[1]);
        float y = Float.parseFloat(data[2]);
        float w = Float.parseFloat(data[3]);
        float h = Float.parseFloat(data[4]);

        //invert y
        y = fullHeight - h - y;

        //fix them
        data[1] = (x / 64f) + "";
        data[2] = (y / 64f) + "";
        data[3] = (w / 64f) + "";
        data[4] = (h / 64f) + "";
    }

    private static String arrayToString(String[] array, String divider)
    {
        StringBuilder builder = new StringBuilder();
        int offset = 0;
        for(String l : array)
        {
            if(offset != 0)builder.append(divider);
            builder.append(l);
            offset++;
        }
        return builder.toString();
    }

    private static String readFileContents(File file)
    {
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null)
            {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (br != null)
                {
                    br.close();
                }
            }
            catch (IOException ignore) {}
        }
        return null;
    }

}
