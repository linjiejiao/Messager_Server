package cn.ljj.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServerThread implements Runnable{
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
			while (isRunning) {
				try {
					Socket s = mServer.accept();
					ClientHandleThread client = new ClientHandleThread(s, this);
					new Thread(client).start();
//					synchronized (sClients) {
//						sClients.add(client);
//					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			isRunning = false;
		}
	}
	public boolean isRunning() {
		return isRunning;
	}
	
	public void stop(){
		isRunning = false;
		synchronized (mClients) {
		    Set<Integer> ids = mClients.keySet();
			for(int id : ids){
			    mClients.get(id).stop();
			}
			mClients.clear();
		}
	}
	
	public Map<Integer, ClientHandleThread> getAllClients(){
		return mClients;
	}
}
