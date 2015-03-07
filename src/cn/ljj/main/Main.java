
package cn.ljj.main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import cn.ljj.message.Headers;
import cn.ljj.message.IPMessage;
import cn.ljj.message.User;
import cn.ljj.message.composerparser.UserComposer;
import cn.ljj.message.composerparser.UserParser;
import cn.ljj.server.ServerThread;
import cn.ljj.server.authority.LogInAuthority;
import cn.ljj.server.database.AbstractDatabase;
import cn.ljj.server.database.DatabaseFactory;
import cn.ljj.server.database.DatabasePersister;
import cn.ljj.server.log.Log;

public class Main {
    public static final String TAG = "Main";

    public static void main(String[] args) {
         ServerThread mServer = new ServerThread();
         new Thread(mServer).start();
         System.out.println("StartupInit init");

        try {
//            testMultiUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] getAllUserStatus() {
        byte[] bytes = new byte[0];
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
             List<User> users = LogInAuthority.getInstance().getAllUsers(false);
             for(User user : users){
                 byte[] userDate = UserComposer.composeUser(user);
//                 Log.e(TAG, "getAllUserStatus =" + Arrays.toString(userDate));
                 baos.write(userDate);
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
        Log.e(TAG, "bytes=" + Arrays.toString(bytes));
        return bytes;
    }
    
    static void testMultiUser(){
        User user = new User();
        user.setName("name_123");
        user.setIdentity(123);
        user.setPassword("123");
        user.setStatus(1);

        try {
            Log.i(TAG, "user before\t=" + user);
            Log.i(TAG, "user after\t=" + UserParser.parseUser(UserComposer.composeUser(user)));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        List<User> users = UserParser.parseUsers(getAllUserStatus());
        Log.e(TAG, "users=" + users);
    }
    
    static void testUserAuthority() {
//         AbstractDatabase db = DatabaseFactory.getDatabase();
//         DatabaseFactory.initDatabaseData(db);
         User user = new User();
         user.setName("name_123");
         user.setIdentity(123);
         user.setPassword("123");
         user.setStatus(1);
         
         LogInAuthority authority = LogInAuthority.getInstance();
         Log.i(TAG, "authority = " + authority.authorize(user));
    }

    static void testUserRW() throws SQLException {
        AbstractDatabase db = DatabaseFactory.getDatabase();
        User user = new User();
        user.setName("name");
        user.setIdentity(1234567890);
        user.setPassword("123456789");
        user.setStatus(1);

        DatabasePersister.getInstance().persistNewUser(user);
        ResultSet rs = db.rawQuery("select * from user", null);
        Log.i(TAG, "user before\t=" + user);
        Log.i(TAG, "user after\t=" + DatabasePersister.getInstance().getUser(rs));
        rs.close();
        // DatabasePersister.getInstance().deleteUser(user);
    }

    static void testMessageRW() throws SQLException {
        AbstractDatabase db = DatabaseFactory.getDatabase();
        IPMessage msg = new IPMessage();
        msg.setBody("meggagebody".getBytes());
        msg.setDate("date");
        msg.setFromName("from");
        msg.setToName("to");
        msg.setMessageIndex(1);
        msg.setMessageType(IPMessage.MESSAGE_TYPE_LOGIN);
        msg.setTransactionId(3);
        msg.setFromId(123456);
        msg.setToId(8888);
        msg.setMessageId(999);
        DatabasePersister.getInstance().persistNewMessage(msg);
        ResultSet rs = db.rawQuery("select * from message", null);
        Log.i(TAG, "msg before\t=" + msg);
        Log.i(TAG, "msg after\t=" + DatabasePersister.getInstance().getMessage(rs));
        rs.close();
        // DatabasePersister.getInstance().deleteMessage(msg);
    }
}
