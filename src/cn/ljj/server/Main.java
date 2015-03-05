
package cn.ljj.server;

import java.sql.ResultSet;
import cn.ljj.message.Headers;
import cn.ljj.message.IPMessage;
import cn.ljj.message.User;
import cn.ljj.message.composerparser.UserComposer;
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
            AbstractDatabase db = DatabaseFactory.getDatabase();
            db.open("db/database.db");
            // DatabaseFactory.crateTables(db);
            IPMessage msg = new IPMessage();
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
            DatabasePersister.getInstance().deleteUser(user);
            
            msg.setBody(UserComposer.composeUser(user));
            msg.setDate("date");
            msg.setFromName(user.getName());
            msg.setToName("host");
            msg.setMessageIndex(1);
            msg.setMessageType(Headers.MESSAGE_TYPE_LOGIN);
            msg.setTransactionId(3);
            msg.setFromId(user.getIdentity());
            msg.setToId(8888);
            msg.setMessageId(999);
            DatabasePersister.getInstance().persistNewMessage(msg);
            rs = db.rawQuery("select * from message", null);
            Log.i(TAG, "msg before\t=" + msg);
            Log.i(TAG, "msg after\t=" + DatabasePersister.getInstance().getMessage(rs));
            rs.close();
            DatabasePersister.getInstance().deleteMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
