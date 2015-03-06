
package cn.ljj.server.log;

public class Log {
    public static void i(String tag, String log) {
        System.out.println("I:" + tag + " - " + log);
    }

    public static void e(String tag, String log) {
        System.out.println("E:" + tag + " - " + log);
    }

    public static void w(String tag, String log) {
        System.out.println("W:" + tag + " - " + log);
    }

    public static void d(String tag, String log) {
        System.out.println("D:" + tag + " - " + log);
    }
}
