package cn.ljj.server.database;

import java.util.HashMap;
import java.util.Map;

import cn.ljj.message.IPMessage;
import cn.ljj.server.database.TableDefines.*;

public class DatabasePersister {
    public static boolean persistMessage(IPMessage msg, AbstractDatabase db){
        boolean ret= false;
        Map <String, Object> values = new HashMap<String, Object>();
        values.put(MessageColunms.FROM_ID, "admin");
        values.put(AdminColunms.PASSWORD, "admin");
        values.put(AdminColunms.IDENTITY, 88888);
        db.insert(MessageColunms.TABLE_NAME, values);
        return ret;
    }
}
