
package cn.ljj.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.ljj.server.database.DatabaseObservable.IDatabaseObserver;
import cn.ljj.server.database.DatabasePersister;

public class ServerThread implements Runnable, IDatabaseObserver {
    private Map<Integer, ClientHandleThread> mClients = new HashMap<Integer, ClientHandleThread>();

    private boolean isRunning = false;
    ServerSocket mServer;

    @Override
    public void run() {
        isRunning = true;
        try {
            mServer = new ServerSocket();
            InetSocketAddress inetAddr = new InetSocketAddress(8888);
            mServer.bind(inetAddr);
            DatabasePersister.getInstance().registerObserver(this);
            while (isRunning) {
                try {
                    Socket s = mServer.accept();
                    ClientHandleThread client = new ClientHandleThread(s, this);
                    new Thread(client).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() {
        isRunning = false;
        synchronized (mClients) {
            Set<Integer> ids = mClients.keySet();
            for (int id : ids) {
                mClients.get(id).stop();
            }
            mClients.clear();
        }
    }

    public Map<Integer, ClientHandleThread> getAllClients() {
        return mClients;
    }

    @Override
    public void onDatabaseChanged(String table, int operate, Object obj) {
        
    }
}
