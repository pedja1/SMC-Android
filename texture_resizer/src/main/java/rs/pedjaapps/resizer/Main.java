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
@SuppressWarnings("ALL")
public class Main
{
    private enum ResChar
    {
        _1440(160),
        _1080(120),
        _720(80),
        _480(53);

        int mTargetRes;

        ResChar(int targetRes)
        {
            mTargetRes = targetRes;
        }
    }

    private enum ResBackground
    {
        _1440(750),
        _1080(750),
        _720(500),
        _480(330);

        int mTargetRes;

        ResBackground(int targetRes)
        {
            mTargetRes = targetRes;
        }
    }

    private enum ResTiles
    {
        _1440(128),
        _1080(120),
        _720(80),
        _480(53);

        int mTargetRes;

        ResTiles(int targetRes)
        {
            mTargetRes = targetRes;
        }
    }

    private enum ResCoin
    {
        _1440(96),
        _1080(72),
        _720(48),
        _480(32);

        int mTargetRes;

        ResCoin(int targetRes)
        {
            mTargetRes = targetRes;
        }
    }

    private enum ResCloud
    {
        _1440(143),
        _1080(107),
        _720(71),
        _480(47);

        int mTargetRes;

        ResCloud(int targetRes)
        {
            mTargetRes = targetRes;
        }
    }

    private enum ResObject
    {
        _1440(1),
        _1080(1),
        _720(1.5f),
        _480(2.25f);

        float mScale;

        ResObject(float scale)
        {
            mScale = scale;
        }
    }

    public static void main(String[] args) throws IOException
    {
        //processCharacters();
        //processBackground();
        //processCoins();
        //processTiles();
        //processClouds();
        processObjects();

        System.out.println("done");
    }

    private static void processCharacters() throws IOException
    {
        String in = "/home/pedja/workspace/SMC-Android/texture_resizer/in_out/data/character";
        Collection<File> files = FileUtils.listFiles(new File(in), new String[]{"png"}, true);

        for (File file : files)
        {
            System.out.println("Processing: " + file.getName());
            BufferedImage image = ImageIO.read(file);
            for (ResChar resChar : ResChar.values())
            {
                System.out.println("res: " + resChar);
                BufferedImage resized = resizeImage(image, resChar.mTargetRes);
                File newFile = new File(file.getAbsolutePath().replace("data", "data" + resChar.toString()));
                newFile.getParentFile().mkdirs();
                ImageIO.write(resized, "png", newFile);
            }
        }
    }

    private static void processBackground() throws IOException
    {
        String in = "/home/pedja/workspace/SMC-Android/texture_resizer/in_out/data/environment/backgrounds";
        Collection<File> files = FileUtils.listFiles(new File(in), new String[]{"png"}, true);

        for (File file : files)
        {
            System.out.println("Processing: " + file.getName());
            BufferedImage image = ImageIO.read(file);
            for (ResBackground resBackground : ResBackground.values())
            {
                System.out.println("res: " + resBackground);
                BufferedImage resized = resizeImage(image, resBackground.mTargetRes);
                File newFile = new File(file.getAbsolutePath().replace("data", "data" + resBackground.toString()));
                newFile.getParentFile().mkdirs();
                ImageIO.write(resized, "png", newFile);
            }
        }
    }

    private static void processCoins() throws IOException
    {
        String in = "/home/pedja/workspace/SMC-Android/texture_resizer/in_out/data/environment/coins";
        Collection<File> files = FileUtils.listFiles(new File(in), new String[]{"png"}, true);

        for (File file : files)
        {
            System.out.println("Processing: " + file.getName());
            BufferedImage image = ImageIO.read(file);
            for (ResCoin resCoins : ResCoin.values())
            {
                System.out.println("res: " + resCoins);
                BufferedImage resized = resizeImage(image, resCoins.mTargetRes);
                File newFile = new File(file.getAbsolutePath().replace("data", "data" + resCoins.toString()));
                newFile.getParentFile().mkdirs();
                ImageIO.write(resized, "png", newFile);
            }
        }
    }

    private static void processTiles() throws IOException
    {
        String in = "/home/pedja/workspace/SMC-Android/texture_resizer/in_out/data/environment/tiles";
        Collection<File> files = FileUtils.listFiles(new File(in), new String[]{"png"}, true);

        for (File file : files)
        {
            System.out.println("Processing: " + file.getName());
            BufferedImage image = ImageIO.read(file);
            for (ResTiles resTiles : ResTiles.values())
            {
                System.out.println("res: " + resTiles);
                BufferedImage resized = resizeImage(image, resTiles.mTargetRes);
                File newFile = new File(file.getAbsolutePath().replace("data", "data" + resTiles.toString()));
                newFile.getParentFile().mkdirs();
                ImageIO.write(resized, "png", newFile);
            }
        }
    }

    private static void processClouds() throws IOException
    {
        File file = new File("/home/pedja/workspace/SMC-Android/texture_resizer/in_out/data/environment/clouds/cloud-3.png");

        System.out.println("Processing: " + file.getName());
        BufferedImage image = ImageIO.read(file);
        for (ResCloud resTiles : ResCloud.values())
        {
            System.out.println("res: " + resTiles);
            BufferedImage resized = resizeImage(image, resTiles.mTargetRes);
            File newFile = new File(file.getAbsolutePath().replace("data", "data" + resTiles.toString()));
            newFile.getParentFile().mkdirs();
            ImageIO.write(resized, "png", newFile);
        }

    }

    private static void processObjects() throws IOException
    {
        String in = "/home/pedja/workspace/SMC-Android/texture_resizer/in_out/data/environment/objects";
        Collection<File> files = FileUtils.listFiles(new File(in), new String[]{"png"}, true);

        for (File file : files)
        {
            System.out.println("Processing: " + file.getName());
            BufferedImage image = ImageIO.read(file);
            for (ResObject resObject : ResObject.values())
            {
                System.out.println("res: " + resObject);
                BufferedImage resized = resizeImageWitScaleOfOriginal(image, resObject.mScale);
                File newFile = new File(file.getAbsolutePath().replace("data", "data" + resObject.toString()));
                newFile.getParentFile().mkdirs();
                ImageIO.write(resized, "png", newFile);
            }
        }
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetHeight) throws IOException
    {
        int height = originalImage.getHeight() < targetHeight ? originalImage.getHeight() : targetHeight;
        int width = (int) ((float) height * ((float) originalImage.getWidth() / (float) originalImage.getHeight()));

        return Scalr.resize(originalImage, Scalr.Method.QUALITY, width, height, Scalr.OP_ANTIALIAS);
    }

    private static BufferedImage resizeImageWitScaleOfOriginal(BufferedImage originalImage, float scale) throws IOException
    {
        float height = (float)originalImage.getHeight() / scale;
        int width = (int) ((float) height * ((float) originalImage.getWidth() / (float) originalImage.getHeight()));

        return Scalr.resize(originalImage, Scalr.Method.QUALITY, width, (int)height, Scalr.OP_ANTIALIAS);
    }
}
