
package cn.ljj.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import cn.ljj.message.Headers;
import cn.ljj.message.IPMessage;
import cn.ljj.message.composerparser.MessageComposer;
import cn.ljj.message.composerparser.MessageParser;
import cn.ljj.message.composerparser.UserComposer;
import cn.ljj.message.composerparser.UserParser;
import cn.ljj.server.database.DatabasePersister;
import cn.ljj.server.database.SqliteDatabase;
import cn.ljj.server.log.Log;
import cn.ljj.user.User;

public class Main {
    public static final String TAG = "Main";

    public static void main(String[] args) {
//        ServerThread mServer = new ServerThread();
//        new Thread(mServer).start();
//        System.out.println("StartupInit init");
        try {
            SqliteDatabase db = new SqliteDatabase();
            db.open("db/database.db");

            IPMessage msg = new IPMessage();
            User user = new User();
            user.setName("name");
            user.setIdentity(1234567890);
            user.setPassword("123456789");
            user.setStatus(1);
            try {
                Log.i(TAG, "user before=" + user);
                Log.i(TAG, "user after=" + UserParser.parseUser(UserComposer.composeUser(user)));
            } catch (IOException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }

            try {
                msg.setBody(UserComposer.composeUser(user));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            msg.setDate("date");
            msg.setFromName(user.getName());
            msg.setToName("host");
            msg.setMessageIndex(1);
            msg.setMessageType(Headers.MESSAGE_TYPE_LOGIN);
            msg.setTransactionId(3);
            msg.setFromId(user.getIdentity());
            msg.setToId(8888);
            msg.setMessageId(999);
            DatabasePersister.getInstance().persistNewMessage(msg, db);
            try {
                Log.i(TAG, "msg before=" + msg);
                Log.i(TAG, "msg after="
                        + MessageParser.parseMessage(
                                new ByteArrayInputStream(MessageComposer.composeMessage(msg))));
            } catch (IOException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
