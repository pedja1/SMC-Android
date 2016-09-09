package com.af.p_net.server;

/**
 * Created by pedja on 5.4.16. 12.02.
 * This class is part of the p-net
 * Copyright © 2016 ${OWNER}
 */
public class MainTest
{
    public static void main(String[] args) throws Exception
    {
        MaryoGameDaemon daemon = new MaryoGameDaemon();
        daemon.init(null);
        daemon.start();
    }
}
