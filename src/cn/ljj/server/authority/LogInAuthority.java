package cn.ljj.server.authority;

import cn.ljj.message.User;

public class LogInAuthority {
    private static LogInAuthority sInstance;
	public static final String TAG = "LogInAuthority";
	private LogInAuthority() {

	}

	public static LogInAuthority getInstance() {
		if (sInstance == null) {
			sInstance = new LogInAuthority();
		}
		return sInstance;
	}
	
	public boolean authorize(User user ){
		System.out.println(TAG  + " authorize:" + user);
		return true;
	}
	
	
}
