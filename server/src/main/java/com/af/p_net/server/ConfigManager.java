package com.af.p_net.server;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pedja on 9.11.15. 12.07.
 * This class is part of the p-net
 * Copyright © 2015 ${OWNER}
 */
public class ConfigManager
{
    private static final String CONFIG_FILENAME = "smc_daemon.conf";
    private static final String LOG_FILENAME = "smc_daemon.log";
    private static final File CONFIG_FILE;
    public static final File CONFIG_FOLDER;
    public static final File LOG_FILE;

    static
    {
        CONFIG_FOLDER = new File(System.getProperty("user.home"), ".smc_daemon");
        CONFIG_FOLDER.mkdirs();
        CONFIG_FILE = new File(CONFIG_FOLDER, CONFIG_FILENAME);
        LOG_FILE = new File(CONFIG_FOLDER, LOG_FILENAME);
    }
    private Map<String, String> mConfigMap;

    private static ConfigManager instance;

    public static synchronized ConfigManager getInstance()
    {
        if(instance == null)
        {
            instance = new ConfigManager();
        }
        return instance;
    }

    private ConfigManager()
    {
        if(!CONFIG_FILE.exists())
        {
            mConfigMap = new HashMap<>();
        }
        else
        {
            mConfigMap = readConfigFromFile(CONFIG_FILE);
        }
    }

    private Map<String, String> readConfigFromFile(File file)
    {
        Map<String, String> map = new HashMap<>();
        try
        {
            List<String> lines = FileUtils.readLines(file);
            for(String line : lines)
            {
                String[] split = line.split("=");
                if(split.length < 2)
                    continue;
                map.put(split[0], split[1]);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return map;
    }

    public int getInt(String key, int fallback)
    {
        try
        {
            return Integer.parseInt(key);
        }
        catch (NumberFormatException e)
        {
            return fallback;
        }
    }

    public long getLong(String key, long fallback)
    {
        try
        {
            return Long.parseLong(key);
        }
        catch (NumberFormatException e)
        {
            return fallback;
        }
    }

    public float getFloat(String key, float fallback)
    {
        try
        {
            return Float.parseFloat(key);
        }
        catch (NumberFormatException e)
        {
            return fallback;
        }
    }

    public double getDouble(String key, double fallback)
    {
        try
        {
            return Double.parseDouble(key);
        }
        catch (NumberFormatException e)
        {
            return fallback;
        }
    }

    public boolean getBoolean(String key, boolean fallback)
    {
        try
        {
            return Boolean.parseBoolean(key);
        }
        catch (NumberFormatException e)
        {
            return fallback;
        }
    }

    public String getString(String key)
    {
        return mConfigMap.get(key);
    }
}
