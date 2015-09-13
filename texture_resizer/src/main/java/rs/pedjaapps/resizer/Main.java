package rs.pedjaapps.resizer;

import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;

/**
 * Created by pedja on 12.9.15..
 */
public class Main
{
    //formula targetResolutionHeight / (gameCameraHeight / (targetHeight / 64))
    //example 1080 / (9 / (70 / 64))

    public static void main(String[] args) throws IOException
    {
        float[] resolutionHeights = new float[]{1080f, 768f, 540f};
        Collection<File> files = FileUtils.listFiles(new File("/home/pedja/workspace/SMC-Android/texture_resizer/playground/pixmaps/"), new String[]{"png"}, true);
        for (float resolutionHeight : resolutionHeights)
        {
            for (File file : files)
            {
                System.out.println("Processing: " + file.getName() + "@" + resolutionHeight);
                BufferedImage image = ImageIO.read(file);
                BufferedImage resized = resizeImage(image, file, resolutionHeight);
                File newFile = new File(file.getAbsolutePath().replace("pixmaps", "pixmaps_" + (int)resolutionHeight));
                newFile.getParentFile().mkdirs();
                ImageIO.write(resized, "png", newFile);
                //break;
            }
        }
        System.out.println("done");
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, File file, float targetResolutionHeight) throws IOException
    {
        int height = Math.min(calculateNewHeight(file, targetResolutionHeight), originalImage.getHeight());
        int width = (int) ((float)height * ((float)originalImage.getWidth() / (float)originalImage.getHeight()));

        return Scalr.resize(originalImage, Scalr.Method.QUALITY, width, height, Scalr.OP_ANTIALIAS);
    }

    private static int calculateNewHeight(File file, float targetResolutionHeight) throws IOException
    {
        float targetHeight = findTargetHeight(file);
        int newHeight = (int) Math.min(targetResolutionHeight, (targetResolutionHeight / (9f / (targetHeight / 64f))));
        if ((newHeight & (newHeight - 1)) == 0) return newHeight;//already pow 2

        //calc nearest pow 2
        int larger = 2;
        while (larger < newHeight)
        {
            larger *= 2;
        }
        int smaller = larger / 2;

        return larger - newHeight > newHeight - smaller ? smaller : larger;
    }

    private static int findTargetHeight(File file) throws IOException
    {
        File settings = new File(file.getAbsolutePath().replaceAll("\\.png", ".settings"));
        String settingsData = FileUtils.readFileToString(settings);
        String[] lines = settingsData.split("\n");

        if (lines.length > 0)
        {
            String[] data = lines[0].split(" ");
            if ("base".equals(data[0]))
            {
                String path = settings.getAbsolutePath();
                String baseFileName = path.replaceAll(path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")), data[1].replaceAll("\\.png", ""));
                File baseFile = new File(baseFileName);
                if (baseFile.exists())
                {
                    String baseSettingsData = FileUtils.readFileToString(baseFile);
                    String[] baseLines = baseSettingsData.split("\n");
                    String[] tmp = new String[lines.length + baseLines.length];
                    System.arraycopy(lines, 0, tmp, 0, lines.length);
                    System.arraycopy(baseLines, 0, tmp, lines.length, baseLines.length);

                    lines = tmp;
                }
            }
        }

        int heght = 0;

        for (String s : lines)
        {
            String[] data = s.split(" ");
            if ("height".equals(data[0]))
            {
                heght = Integer.parseInt(data[1].trim());
            }
        }
        if (heght > 0)
            return heght;
        else
            throw new RuntimeException("cant determine height");
    }

}
