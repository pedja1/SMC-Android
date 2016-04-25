import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;

/**
 * Created by pedja on 6.3.16..
 */
public class Main
{
    public static void main(String[] args)
    {
        String in = "/home/pedja/workspace/SMC-Android/android/assets/data";

        String[] ress = new String[]{"_1440", "_1080", "_720", "_480"};
        for(String res : ress)
        {
            Collection<File> files = FileUtils.listFiles(new File(in + res + "/environment/tiles"), new String[]{"png"}, true);
            for (File file : files)
            {
                File dir = file.getParentFile();
                File newName = new File(dir, "t-" + file.getName());
                System.out.println(newName);
                file.renameTo(newName);
            }
        }
    }
}
