package org.apache.dolphinscheduler.data.quality.plugin;

import org.apache.dolphinscheduler.data.quality.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Map;

public class Reader {
    private static final Logger logger = LoggerFactory.getLogger(Reader.class);

    private final String database;
    private final String password;
    private final String driver;
    private final String user;
    private final String output_table;
    private final String table;
    private final String url;

    public Reader(Map<String,Object> config) {
        this.database = config.get(Constants.DATABASE).toString();
        this.password = config.get(Constants.PASSWORD).toString();
        this.driver = config.get(Constants.DRIVER).toString();
        this.user = config.get(Constants.USER).toString();
        this.output_table = config.get(Constants.OUTPUT_TABLE).toString();
        this.table = config.get(Constants.TABLE).toString();
        this.url = config.get(Constants.URL).toString();
    }

    public String getOutputTable() {
        return output_table;
    }

    public String getTable() {
        return table;
    }

    public String executeCountQuery(String sql) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                if(rs.getString(1) == null) {
                    return "0";
                } else {
                    return rs.getString(1);
                }
            }
        } catch (Exception e) {
            logger.error("{}: 查询失败", sql);
            e.printStackTrace();
            System.exit(-1);
        } finally {
            try {
                // 关闭连接
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error("{}:关闭失败", database);
                e.printStackTrace();
                System.exit(-1);
            }
        }
        return null;
    }
}
