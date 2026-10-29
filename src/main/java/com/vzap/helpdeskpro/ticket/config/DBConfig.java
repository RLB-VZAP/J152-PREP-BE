
package com.vzap.helpdeskpro.config;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 *
 * @author rossb
 */
public class DBConfig
{
    private static final BasicDataSource dataSource;
    
    static{
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setUrl("jdbc:mysql://localhost:3306/helpdeskpro?useSSL=false");
        dataSource.setMaxIdle(50);
        dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setMinIdle(5);
    }
    
    public static Connection getCon() throws SQLException{
        return dataSource.getConnection();
    }
    
    public static void close()throws SQLException{
        dataSource.close();
    }
    
}
