package cn.ljj.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import cn.ljj.message.Headers;
import cn.ljj.message.IPMessage;
import cn.ljj.message.User;
import cn.ljj.message.composerparser.MessageComposer;
import cn.ljj.message.composerparser.MessageParser;
import cn.ljj.message.composerparser.UserParser;
import cn.ljj.server.authority.LogInAuthority;

public class ClientHandleThread implements Runnable {
	private Socket mSocket;
	private boolean isRunning = false;
	private OutputStream mOutputStream;
	private User mUser;
	private ServerThread mServer = null;
	private Map<Integer, ClientHandleThread> mClients;

	public ClientHandleThread(Socket s, ServerThread server) {
		mSocket = s;
		mServer = server;
		mClients = mServer.getAllClients();
		if (s == null) {
			throw new NullPointerException();
		}
	}

	@Override
	public void run() {
		isRunning = true;
		InputStream ins = null;
		try {
			ins = mSocket.getInputStream();
			mOutputStream = mSocket.getOutputStream();
			while (isRunning) {
				if (mSocket.isClosed() || !mSocket.isBound()
						|| !mSocket.isConnected() || mSocket.isInputShutdown()) {
					break;
				}
				IPMessage msg = MessageParser.parseMessage(ins);
				System.out.println("msg = " + msg);
				if(msg != null){
					handleMessage(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ins != null) {
					ins.close();
				}
				if (mOutputStream != null) {
					mOutputStream.close();
					mOutputStream = null;
				}
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			isRunning = false;
			if(mUser != null){
    			synchronized (mClients) {
    			    mClients.remove(mUser.getIdentity());
    			}
			}
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void stop() {
		isRunning = false;
	}

	public boolean writeToClient(byte[] data) throws IOException {
		if (mOutputStream == null) {
			return false;
		}
		mOutputStream.write(data);
		return true;
	}

	@Override
	public String toString() {
		if(mUser == null){
			return "ClientHandleThread [mSocket=" + mSocket + ", isRunning="
					+ isRunning + "] " + mUser;
		}
		return mUser.toString();
	}

	private void handleMessage(IPMessage msg) throws Exception {
		switch (msg.getMessageType()) {
		case Headers.MESSAGE_TYPE_LOGIN:
			User user = UserParser.parseUser(msg.getBody());
			if(LogInAuthority.getInstance().authorize(user)){
				mUser = user;
	            synchronized (mClients) {
	                mClients.put(mUser.getIdentity(), this);
	            }
			}else{
				throw new Exception("Authorize failed");
			}
			break;
		case Headers.MESSAGE_TYPE_MESSAGE:
			System.out.println("handleMessage MESSAGE_TYPE_MESSAGE");
			synchronized (mClients) {
				ClientHandleThread target = mClients.get(msg.getToId());
                if(target != null){
                    boolean ret = target.writeToClient(MessageComposer.composeMessage(msg));
                    System.out.println("handleMessage client found write ret=" + ret);
                }else{
                    //write to db.
                }
			}
			break;
		case Headers.MESSAGE_TYPE_RESPOND:
			break;
		default:
		}
	}

	public User getUser() {
		return mUser;
	}
}
