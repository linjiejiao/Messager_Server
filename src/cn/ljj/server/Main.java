
package cn.ljj.server;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import cn.ljj.server.database.SqliteDatabase;
import cn.ljj.server.log.Log;

public class Main {
    public static final String TAG = "Main";

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
            Map <String, Object> values = new HashMap<String, Object>();
            values.put("name", "jiajian");
            values.put("occupation", "computer");
//            db.insert("people", values);
            db.delete("people", null, null);
            long t = System.currentTimeMillis();
            for(int i=0;i<100;i++){
                db.insert("people", values);
            }
            Log.i(TAG , "insert 100 people cost: " + (System.currentTimeMillis() - t));
            db.delete("people", null, null);
            String[] sqls = new String[100];
            for(int i=0;i<100;i++){
                sqls[i] = "insert into people (name, occupation) values ('jiajian', 'computer');";
            }
            t = System.currentTimeMillis();
            db.executeBatch(sqls);
            Log.i(TAG , "Batch insert 100 people cost: " + (System.currentTimeMillis() - t));
//            db.update("people", values, "name=?", new Object[]{"jiajian"});
            ResultSet rs = db.rawQuery("select * from people "
                    , null);
//              while (rs.next()) {
//                  System.out.println("name = " + rs.getString("name"));
//                  System.out.println("job = " + rs.getString("occupation"));
//                  System.out.println("real = " + rs.getString("real_data"));
//              }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
