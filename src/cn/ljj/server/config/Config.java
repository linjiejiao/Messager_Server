package cn.ljj.server.config;

import java.io.File;

import cn.ljj.server.database.DatabaseFactory;

public class Config {
    public static final int DATABASE_TYPE = DatabaseFactory.DATABASE_TYPE_SQLITE;
    
    public static final String DATABASE_LOCATION = "." +File.separator + "db"+File.separator +"database.db";
}
