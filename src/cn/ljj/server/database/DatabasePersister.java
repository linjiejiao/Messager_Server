
package cn.ljj.server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import cn.ljj.message.Headers;
import cn.ljj.message.IPMessage;
import cn.ljj.server.database.TableDefines.*;
import cn.ljj.server.log.Log;
import cn.ljj.user.User;

public class DatabasePersister extends DatabaseObservable {
    public static final String TAG = "persistNewMessage";
    private static DatabasePersister sInstance = null;

    private DatabasePersister() {

    }

    public static DatabasePersister getInstance() {
        if (sInstance == null) {
            sInstance = new DatabasePersister();
        }
        return sInstance;
    }

    public boolean persistNewMessage(IPMessage msg, AbstractDatabase db) {
        boolean ret = false;
        if (checkMessage(msg)) {
            return ret;
        }
        String sql = ("insert into " + MessageColunms.TABLE_NAME + " ("
                + MessageColunms.FROM_ID + ", "
                + MessageColunms.FROM_NAME + ", "
                + MessageColunms.TO_ID + ", "
                + MessageColunms.TO_NAME + ", "
                + MessageColunms.DATE + ", "
                + MessageColunms.MSG_ID + ", "
                + MessageColunms.MSG_TYPE + ", "
                + MessageColunms.MSG_BODY + ", "
                + MessageColunms.MSG_INDEX + ", "
                + MessageColunms.TRANSACTION_ID + ", "
                +")values(?,?,?,?,?,?,?,?,?,?);");
        Log.i(TAG, "persistNewMessage sql=" + sql);
        PreparedStatement prep = null;
        try {
            prep = db.getConnection().prepareStatement(sql);
            prep.setInt(MessageColunms.INFDEX_FROM_ID, msg.getFromId());
            prep.setString(MessageColunms.INFDEX_FROM_NAME, msg.getFromName());
            prep.setInt(MessageColunms.INFDEX_TO_ID, msg.getToId());
            prep.setString(MessageColunms.INFDEX_TO_NAME, msg.getToName());
            prep.setString(MessageColunms.INFDEX_DATE, msg.getDate());
            prep.setInt(MessageColunms.INFDEX_MSG_ID, msg.getMessageId());
            prep.setInt(MessageColunms.INFDEX_MSG_TYPE, msg.getMessageType());
            prep.setBytes(MessageColunms.INFDEX_MSG_BODY, msg.getBody());
            prep.setInt(MessageColunms.INFDEX_MSG_INDEX, msg.getMessageIndex());
            prep.setInt(MessageColunms.INFDEX_TRANSACTION_ID, msg.getTransactionId());
            prep.executeUpdate();
            ret = true;
        }catch(Exception e){
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }

    public boolean persistNewUser(User user, AbstractDatabase db) {
        boolean ret = false;
        if (!checkUser(user)) {
            return ret;
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(UserColunms.IDENTITY, user.getIdentity());
        values.put(UserColunms.NAME, user.getName());
        values.put(UserColunms.PASSWORD, user.getPassword());
        values.put(UserColunms.STATUS, user.getStatus());
        ret = db.insert(UserColunms.TABLE_NAME, values) > 0;
        return ret;
    }

    public boolean updateUser(User user, AbstractDatabase db) {
        boolean ret = false;
        if (!checkUser(user)) {
            return ret;
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(UserColunms.IDENTITY, user.getIdentity());
        values.put(UserColunms.NAME, user.getName());
        values.put(UserColunms.PASSWORD, user.getPassword());
        values.put(UserColunms.STATUS, user.getStatus());
        ret = db.update(UserColunms.TABLE_NAME, values,
                UserColunms.IDENTITY + "=" + user.getIdentity(), null) > 0;
        return ret;
    }

    private boolean checkUser(User user) {
        if (user.getIdentity() == 0 || user.getName() == null || user.getName().length() <= 0
                || user.getPassword() == null || user.getPassword().length() <= 0
                || user.getStatus() < 0) {
            return false;
        }
        return true;
    }

    private boolean checkMessage(IPMessage msg) {
        if (msg.getToId() <= 0 || msg.getFromId() <= 0) {
            return false;
        }
        return true;
    }

    public IPMessage getMessage(ResultSet rs){
        int fromId = 0;
        int toId = 0;
        String fromName = null;
        String toName = null;
        String date = null;
        int messageType = Headers.MESSAGE_TYPE_BASE;
        int messageIndex = -1;
        int messageId = -1;
        int transactionId = 0;
        byte[] body = null;
        try {
            if(rs.getRow() == 0){
                return null;
            }
            fromId = rs.getInt(MessageColunms.FROM_ID);
            toId = rs.getInt(MessageColunms.TO_ID);
            fromName = rs.getString(MessageColunms.FROM_NAME);
            toName = rs.getString(MessageColunms.TO_NAME);
            date = rs.getString(MessageColunms.DATE);
            messageType = rs.getInt(MessageColunms.MSG_TYPE);
            messageIndex = rs.getInt(MessageColunms.MSG_INDEX);
            messageId = rs.getInt(MessageColunms.MSG_ID);
            transactionId = rs.getInt(MessageColunms.TRANSACTION_ID);
//            rs.getB
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        IPMessage msg = new IPMessage();
        msg.setBody(body);
        msg.setDate(date);
        msg.setFromId(fromId);
        msg.setFromName(fromName);
        msg.setMessageId(messageId);
        msg.setMessageIndex(messageIndex);
        msg.setMessageType(messageType);
        msg.setToId(toId);
        msg.setToName(toName);
        msg.setTransactionId(transactionId);
        return msg;
    }
}
