package com.af.p_net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import rs.pedjaapps.smc.kryo.CancelMatchmaking;
import rs.pedjaapps.smc.kryo.Data;
import rs.pedjaapps.smc.kryo.OpponentLeft;
import rs.pedjaapps.smc.kryo.KryoClassRegistar;
import rs.pedjaapps.smc.kryo.MatchmakingFailed;
import rs.pedjaapps.smc.kryo.MatchmakingSuccess;
import rs.pedjaapps.smc.kryo.ServerFull;
import rs.pedjaapps.smc.utility.RandomString;


/**
 * Created by pedja on 9.11.15. 11.32.
 * This class is part of the p-net
 * Copyright © 2015 ${OWNER}
 */
public class MaryoGameDaemon extends Listener implements Daemon
{
    private static final OpponentLeft OPPONENT_LEFT = new OpponentLeft();
    private static final MatchmakingFailed MATCH_MAKING_FAILED = new MatchmakingFailed();
    private static final MatchmakingSuccess MATCH_MAKING_SUCCESS = new MatchmakingSuccess();
    private static final ServerFull SERVER_FULL = new ServerFull();
    private final static Logger LOGGER = Logger.getLogger(MaryoGameDaemon.class.getName());
    private Server mKryoServer;

    private final Map<String, Room> rooms;
    private final List<Connection> lobby;
    private AtomicInteger connectionCount;

    private BlockingQueue<WorkRequest> mWorkQueue;
    private List<WorkerThread> mWorkers;

    private final ExecutorService executorService;

    private int mMaxAllowedConnections;

    private RandomString mRandomString;

    public MaryoGameDaemon()
    {
        rooms = new HashMap<>();
        lobby = new ArrayList<>();
        connectionCount = new AtomicInteger();
        mWorkQueue = new LinkedBlockingQueue<>();
        mWorkers = new ArrayList<>(ConfigManager.getInstance().getInt("workers_count", Runtime.getRuntime().availableProcessors()));
        executorService = Executors.newCachedThreadPool();
        mMaxAllowedConnections = ConfigManager.getInstance().getInt("max_allowed_connections", 100);
        mRandomString = new RandomString(32);
    }

    @Override
    public void init(DaemonContext context) throws DaemonInitException, IOException
    {
        LOGGER.setLevel(Level.INFO);
        FileHandler fileHandler = new FileHandler(ConfigManager.LOG_FILE.getAbsolutePath());
        fileHandler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(fileHandler);

        mKryoServer = new Server();
        KryoClassRegistar.registerClasses(mKryoServer.getKryo());
        mKryoServer.addListener(this);

        int workersCount = ConfigManager.getInstance().getInt("workers_count", Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < workersCount; i++)
        {
            WorkerThread worker = new WorkerThread();
            mWorkers.add(worker);
            worker.start();
        }
    }

    @Override
    public void start() throws Exception
    {
        mKryoServer.start();
        int tcp = ConfigManager.getInstance().getInt("tcp_port", 50591);
        int udp = ConfigManager.getInstance().getInt("udp_port", 50592);
        mKryoServer.bind(tcp, udp);
        LOGGER.info("Started on port tcp:" + tcp + " udp: " + udp);
    }

    @Override
    public void connected(Connection connection)
    {
        if (connectionCount.get() >= mMaxAllowedConnections)
        {
            connection.sendTCP(SERVER_FULL);
            connection.close();
            return;
        }
        LOGGER.info("Client connected:" + connection);
        synchronized (lobby)
        {
            connection.setArbitraryData(new ConnectionData());
            lobby.add(connection);
        }
        connectionCount.incrementAndGet();
        LOGGER.info(String.format("Connections in lobby: '%d', Rooms: '%d', Total connections: '%d'", lobby.size(), rooms.size(), connectionCount.get()));
    }

    @Override
    public void received(Connection connection, Object o)
    {
        if(o instanceof Data)
        {
            mWorkQueue.add(new WorkRequest(connection, WorkRequest.Type.send_position, o));
        }
        else if(o instanceof CancelMatchmaking)
        {
            getConnectionData(connection).inMatchmaking = false;
        }
    }

    @Override
    public void disconnected(Connection connection)
    {
        LOGGER.info("Client disconnected:" + connection);
        mWorkQueue.add(new WorkRequest(connection, WorkRequest.Type.disconnect));
        connectionCount.decrementAndGet();
        LOGGER.info(String.format("Connections in lobby: '%d', Rooms: '%d', Total connections: '%d'", lobby.size(), rooms.size(), connectionCount.get()));
    }

    @Override
    public void stop() throws Exception
    {
        mKryoServer.stop();
        lobby.clear();
        rooms.clear();
        for (WorkerThread worker : mWorkers)
        {
            worker.quit();
        }
        mWorkers.clear();
        executorService.shutdown();
    }

    @Override
    public void destroy()
    {
        if (mKryoServer != null)
        {
            mKryoServer.close();
            mKryoServer = null;
        }
    }

    private class WorkerThread extends Thread
    {
        private boolean mQuit;

        @Override
        public void run()
        {
            while (true)
            {
                final WorkRequest workRequest;
                try
                {
                    // Take a request from the queue.
                    workRequest = mWorkQueue.take();
                }
                catch (InterruptedException e)
                {
                    // We may have been interrupted because it was time to quit.
                    if (mQuit)
                    {
                        return;
                    }
                    continue;
                }
                switch (workRequest.type)
                {
                    case disconnect:
                    {
                        synchronized (lobby)
                        {
                            for (int i = lobby.size() - 1; i >= 0; i--)
                            {
                                if (lobby.get(i) == workRequest.connection)
                                    lobby.remove(i);
                            }
                        }
                        synchronized (rooms)
                        {
                            for (String id : rooms.keySet())
                            {
                                Room room = rooms.get(id);
                                Connection player1 = room.player1;
                                Connection player2 = room.player2;
                                if (player1 != workRequest.connection && player2 != workRequest.connection)
                                    continue;

                                synchronized (lobby)
                                {
                                    if (player1 == workRequest.connection)
                                    {
                                        lobby.add(player2);
                                        player2.sendTCP(OPPONENT_LEFT);
                                    }
                                    else
                                    {
                                        lobby.add(player1);
                                        player1.sendTCP(OPPONENT_LEFT);
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case send_position:
                    {
                        //noinspection SuspiciousMethodCalls
                        Room room = rooms.get(getConnectionData(workRequest.connection).roomId);
                        if (room == null || room.player1 == null || room.player2 == null)
                        {
                            workRequest.connection.sendTCP(OPPONENT_LEFT);
                        }
                        else
                        {
                            if (room.player1 == workRequest.connection)
                            {
                                room.player2.sendUDP(workRequest.o);
                            }
                            else
                            {
                                room.player1.sendUDP(workRequest.o);
                            }
                        }
                        break;
                    }
                    case find_match:
                    {
                        if(getConnectionData(workRequest.connection).inMatchmaking)
                            break;
                        Runnable runnable = new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                while (lobby.size() <= 1)//we are also in the
                                {
                                    if(!workRequest.connection.isConnected() || !getConnectionData(workRequest.connection).inMatchmaking)
                                    {
                                        return;
                                    }
                                    try
                                    {
                                        Thread.sleep(3000);
                                    }
                                    catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                if(!workRequest.connection.isConnected() || !getConnectionData(workRequest.connection).inMatchmaking)
                                {
                                    return;
                                }
                                Connection player2 = null;
                                synchronized (lobby)
                                {
                                    //if somehow we are still empty, just quit matchmaking
                                    if (lobby.size() <= 1)
                                    {
                                        workRequest.connection.sendTCP(MATCH_MAKING_FAILED);
                                    }
                                    else
                                    {
                                        for (Connection connection : lobby)
                                        {
                                            if (connection != workRequest.connection)
                                            {
                                                player2 = connection;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (player2 == null)
                                {
                                    workRequest.connection.sendTCP(MATCH_MAKING_FAILED);
                                }
                                else
                                {
                                    Room selectedRoom = null;
                                    synchronized (rooms)
                                    {
                                        for (Room room : rooms.values())
                                        {
                                            if (room.isAvailable())
                                            {
                                                selectedRoom = room;
                                                break;
                                            }
                                        }
                                    }

                                    if (selectedRoom == null)
                                    {
                                        String id = mRandomString.nextString();
                                        selectedRoom = new Room(id, workRequest.connection, player2);
                                        synchronized (rooms)
                                        {
                                            rooms.put(id, selectedRoom);
                                        }
                                    }
                                    else
                                    {
                                        selectedRoom.setPlayer1(workRequest.connection);
                                        selectedRoom.setPlayer2(player2);
                                    }
                                    selectedRoom.player1.sendTCP(MATCH_MAKING_SUCCESS);
                                    selectedRoom.player2.sendTCP(MATCH_MAKING_SUCCESS);
                                }
                            }
                        };
                        executorService.execute(runnable);
                        break;
                    }
                }
                LOGGER.info(workRequest.type.toString());
            }
        }

        void quit()
        {
            mQuit = false;
            interrupt();
        }
    }

    private ConnectionData getConnectionData(Connection connection)
    {
        return (ConnectionData) connection.getArbitraryData();
    }

    private static class WorkRequest
    {
        enum Type
        {
            disconnect, find_match, send_position
        }

        private final Connection connection;
        private final Type type;
        private final Object o;

        WorkRequest(Connection connection, Type type)
        {
            this.connection = connection;
            this.type = type;
            this.o = null;
        }

        WorkRequest(Connection connection, Type type, Object o)
        {
            this.connection = connection;
            this.type = type;
            this.o = o;
        }
    }

    private static class Room
    {
        final String id;
        private Connection player1, player2;

        Room(String id, Connection player1, Connection player2)
        {
            this.id = id;
            this.player1 = player1;
            this.player2 = player2;
            player1.setArbitraryData(id);
            player2.setArbitraryData(id);
        }

        public Connection getPlayer1()
        {
            return player1;
        }

        public void setPlayer1(Connection player1)
        {
            this.player1 = player1;
            player1.setArbitraryData(id);
        }

        public Connection getPlayer2()
        {
            return player2;
        }

        public void setPlayer2(Connection player2)
        {
            this.player2 = player2;
            player2.setArbitraryData(id);
        }

        boolean isAvailable()
        {
            return player1 == null && player2 == null;
        }
    }

    private static class ConnectionData
    {
        String roomId;
        boolean inMatchmaking;
    }
}
