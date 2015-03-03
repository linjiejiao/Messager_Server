
package cn.ljj.server;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import cn.ljj.server.database.SqliteDatabase;

public class Main {
    public static void main(String[] args) {
//        ServerThread mServer = new ServerThread();
//        new Thread(mServer).start();
//        System.out.println("StartupInit init");
        try {
            SqliteDatabase db = new SqliteDatabase();
            db.open("db/test.db");
//            ResultSet rs = db.rawQuery("select * from people where name=? or name=? order by name"
//                    , new String[]{"'Turing'", "'Wittgenstein'"});
//            ResultSet rs = db.query("people", new String[]{"name", "occupation"},
//                    "name=? or name=?", new String[]{"'Turing'", "'Wittgenstein'"},
//                    null, null, null);
//            db.delete("people", "name=?", new String[]{"'Wittgenstein'"});
            Map <String, String> values = new HashMap<String, String>();
            values.put("name", "'jiajian'");
            values.put("occupation", "'11111112'");
//            db.insert("people", values);
            db.update("people", values, "name=?", new String[]{"'Gandhi'"});
            ResultSet rs = db.rawQuery("select * from people "
                  , null);
            while (rs.next()) {
                System.out.println("name = " + rs.getString("name"));
                System.out.println("job = " + rs.getString("occupation"));
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
