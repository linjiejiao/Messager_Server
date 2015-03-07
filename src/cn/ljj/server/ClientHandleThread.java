
package cn.ljj.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import cn.ljj.message.IPMessage;
import cn.ljj.message.User;
import cn.ljj.message.composerparser.MessageComposer;
import cn.ljj.message.composerparser.MessageParser;
import cn.ljj.message.composerparser.UserComposer;
import cn.ljj.message.composerparser.UserParser;
import cn.ljj.server.authority.LogInAuthority;
import cn.ljj.server.database.DatabasePersister;
import cn.ljj.server.log.Log;

public class ClientHandleThread implements Runnable {
    public static final String TAG = "ClientHandleThread";

    private Socket mSocket = null;
    private boolean isRunning = false;
    private OutputStream mOutputStream = null;
    private User mUser = null;
    private ServerThread mServer = null;
    private ClientConnPool mClients = null;
    private DatabasePersister mDatabasePersister = null;
    private int mMsgIndex = 0;
    private int mTransactionIndex = 0;

    public ClientHandleThread(Socket s, ServerThread server) {
        mSocket = s;
        mServer = server;
        mClients = mServer.getAllClients();
        mDatabasePersister = DatabasePersister.getInstance();
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
                if (mSocket.isClosed() || !mSocket.isBound() || !mSocket.isConnected()
                        || mSocket.isInputShutdown()) {
                    break;
                }
                IPMessage msg = MessageParser.parseMessage(ins);
                Log.d(TAG, "msg = " + msg);
                if (msg != null) {
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
            if (getUser() != null) {
                synchronized (mClients) {
                    mClients.remove(getUser().getIdentity());
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

    public boolean writeToTarget(byte[] data) throws IOException {
        if (mOutputStream == null) {
            return false;
        }
        mOutputStream.write(data);
        return true;
    }

    private void handleMessage(IPMessage msg) throws Exception {
        mMsgIndex = msg.getMessageIndex();
        mTransactionIndex = msg.getTransactionId();
        switch (msg.getMessageType()) {
            case IPMessage.MESSAGE_TYPE_LOGIN:
                User user = UserParser.parseUser(msg.getBody());
                if (LogInAuthority.getInstance().authorize(user)) {
                    mUser = user;
                    Log.d(TAG, "handleMessage MESSAGE_TYPE_LOGIN user=" + user);
                    synchronized (mClients) {
                        mClients.put(user.getIdentity(), this);
                    }
                } else {
                    responLoginFailed();
                    throw new Exception("Authorize failed");
                }
                break;
            case IPMessage.MESSAGE_TYPE_MESSAGE:
            case IPMessage.MESSAGE_TYPE_RESPOND:
                if (getUser() == null) { // Did not login yet
                    return;
                }
                if (LogInAuthority.getInstance().isUserExist(msg.getToId())) {
                    mDatabasePersister.persistNewMessage(msg);
                }
                break;
            case IPMessage.MESSAGE_TYPE_CHANGE_STATUS:
                if (getUser() == null) { // Did not login yet
                    return;
                }
                User newUser = UserParser.parseUser(msg.getBody());
                if (getUser().equals(newUser)) {
                    boolean update = mDatabasePersister.updateUser(newUser);
                    if (!update) {
                        responChangeStatusFailed();
                    }
                }
                break;
            case IPMessage.MESSAGE_TYPE_GET_USERS:
                if (getUser() == null) { // Did not login yet
                    return;
                }
                msg.setDate(System.currentTimeMillis() + "");
                msg.setMessageIndex(++mMsgIndex);
                msg.setBody(getAllUserStatus());
                byte[] data = MessageComposer.composeMessage(msg);
                writeToTarget(data);
                break;
            default:
        }
    }

    public User getUser() {
        return mUser;
    }

    private void responLoginFailed() {
        respon("Login Failed");
    }

    private void responChangeStatusFailed() {
        respon("Change status Failed");
    }

    public void respon(String resp) {
        IPMessage msg = new IPMessage();
        msg.setBody(resp.getBytes());
        msg.setDate(System.currentTimeMillis() + "");
        msg.setFromId(0);
        msg.setToId(getUser().getIdentity());
        msg.setMessageId(0);
        msg.setMessageIndex(++mMsgIndex);
        msg.setMessageType(IPMessage.MESSAGE_TYPE_RESPOND);
        msg.setTransactionId(mTransactionIndex);
        try {
            writeToTarget(MessageComposer.composeMessage(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] getAllUserStatus() {
        byte[] bytes = new byte[0];
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
             List<User> users = LogInAuthority.getInstance().getAllUsers(true);
             for(User user : users){
                 baos.write(UserComposer.composeUser(user));
             }
            bytes = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    @Override
    public String toString() {
        if (mUser == null) {
            return "ClientHandleThread [mSocket=" + mSocket + ", isRunning=" + isRunning + "] "
                    + mUser;
        }
        return mUser.toString();
    }

}
