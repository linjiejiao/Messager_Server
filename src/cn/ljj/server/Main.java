package cn.ljj.server;

public class Main {
	public static void main(String[] args) {
	    ServerThread mServer = new ServerThread();
        new Thread(mServer).start();
        System.out.println("StartupInit init"); 
	}
}
