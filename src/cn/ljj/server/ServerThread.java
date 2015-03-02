package cn.ljj.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThread implements Runnable{
	private static List<ClientHandleThread> sClients = new ArrayList<ClientHandleThread>();
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
					ClientHandleThread client = new ClientHandleThread(s);
					new Thread(client).start();
					synchronized (sClients) {
						sClients.add(client);
					}
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
		synchronized (sClients) {
			for(ClientHandleThread c : sClients){
				c.stop();
			}
			sClients.clear();
		}
	}
	
	public static List<ClientHandleThread> getAllClients(){
		return sClients;
	}
}
