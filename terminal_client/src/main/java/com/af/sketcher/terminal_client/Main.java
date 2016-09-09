package com.af.sketcher.terminal_client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.Scanner;

import rs.pedjaapps.smc.kryo.CancelMatchmaking;
import rs.pedjaapps.smc.kryo.Data;
import rs.pedjaapps.smc.kryo.KryoClassRegistar;
import rs.pedjaapps.smc.kryo.MatchmakingFailed;
import rs.pedjaapps.smc.kryo.MatchmakingSuccess;
import rs.pedjaapps.smc.kryo.OpponentLeft;
import rs.pedjaapps.smc.kryo.ServerFull;

/**
 * Created by pedja on 12.5.16. 08.54.
 * This class is part of the sketcher
 * Copyright © 2016 ${OWNER}
 */
public class Main
{
    private static final Object lock = new Object();
    private static boolean running = true;

    public static void main(String[] args) throws IOException
    {
        Client client = new Client();
        KryoClassRegistar.registerClasses(client.getKryo());
        client.start();
        client.connect(5000, "localhost", 50591, 50592);

        client.addListener(new Listener()
        {
            @Override
            public void received(Connection connection, Object o)
            {
                if(o instanceof Data)
                {
                    Data data = (Data) o;
                    System.out.println(String.format("Data: x: %d, y: %d", data.posX, data.posY));
                }
                else if (o instanceof OpponentLeft)
                {
                    System.out.println("Opponent Left: " + o);
                }
                else if(o instanceof CancelMatchmaking)
                {
                    System.out.println("CancelMatchmaking: " + o);
                }
                else if(o instanceof MatchmakingSuccess)
                {
                    System.out.println("MatchmakingSuccess: " + o);
                }
                else if(o instanceof MatchmakingFailed)
                {
                    System.out.println("MatchmakingFailed: " + o);
                }
                else if(o instanceof ServerFull)
                {
                    System.out.println("ServerFull: " + o);
                }
            }

            @Override
            public void disconnected(Connection connection)
            {
                System.exit(1);
            }
        });

        Options options = new Options();
        options.addOption("e", "exit", false, "Exit");
        options.addOption("c", "create-room", true, "Create room");
        options.addOption("j", "join-room", true, "Join room");
        options.addOption("s", "send", false, "send point");
        options.addOption("l", "login", true, "login");
        options.addOption("t", "start", false, "start");

        CommandLineParser parser = new DefaultParser();

        Scanner scanner = new Scanner(System.in);
        while (running)
        {
            String cmd = scanner.nextLine();
            String[] cmds = cmd.split("\\s(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
            for(int i = 0; i < cmds.length; i++)
            {
                cmds[i] = cmds[i].replaceAll("\"", "");
            }

            CommandLine cl;
            try
            {
                cl = parser.parse(options, cmds);
            }
            catch (ParseException e)
            {
                System.err.println(e.getMessage());
                continue;
            }
            if(cl.hasOption("e"))
            {
                running = false;
            }
            else if(cl.hasOption("c"))
            {
                /*String roomName = cl.getOptionValue("c");
                CreateRoomRequest createRoomRequest = new CreateRoomRequest(roomName);
                client.sendTCP(createRoomRequest);*/
            }
            else if(cl.hasOption("j"))
            {
                /*String roomName = cl.getOptionValue("j");
                JoinRoomRequest request = new JoinRoomRequest(roomName);
                client.sendTCP(request);*/
            }
            else if(cl.hasOption("s"))
            {
                /*Point po = new Point(1, 1, false);
                client.sendTCP(po);*/
            }
            else if(cl.hasOption("l"))
            {
                String value = cl.getOptionValue("l");
                String[] userPass = value.split(":");
                if(userPass.length < 2)
                {
                    System.err.println("Invalid format");
                    continue;
                }
                /*LoginRequest loginRequest = new LoginRequest(userPass[0], userPass[1]);
                client.sendTCP(loginRequest);*/
            }
            else if(cl.hasOption("t"))
            {
                /*StartGameRequest startGameRequest = new StartGameRequest();
                client.sendTCP(startGameRequest);*/
            }
        }
    }
}
