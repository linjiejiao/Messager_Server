
package cn.ljj.main;

import java.sql.ResultSet;
import java.sql.SQLException;

import cn.ljj.message.Headers;
import cn.ljj.message.IPMessage;
import cn.ljj.message.User;
import cn.ljj.server.authority.LogInAuthority;
import cn.ljj.server.config.Config;
import cn.ljj.server.database.AbstractDatabase;
import cn.ljj.server.database.DatabaseFactory;
import cn.ljj.server.database.DatabasePersister;
import cn.ljj.server.log.Log;

public class Main {
    public static final String TAG = "Main";

    public static void main(String[] args) {
        // ServerThread mServer = new ServerThread();
        // new Thread(mServer).start();
        // System.out.println("StartupInit init");
        try {
            testUserAuthority();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void testUserAuthority() {
         AbstractDatabase db = DatabaseFactory.getDatabase();
         db.open(Config.DATABASE_LOCATION);
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
        db.open(Config.DATABASE_LOCATION);
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
        db.open(Config.DATABASE_LOCATION);
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
