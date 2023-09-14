package org.apache.dolphinscheduler.data.quality.plugin;

import org.apache.dolphinscheduler.data.quality.Constants;

import org.slf4j.LoggerFactory;

import java.util.Map;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Writer {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Writer.class);

    private final String database;
    private final String password;
    private final String driver;
    private final String user;
    private final String table;
    private final String url;
    private String sql;

    public Writer(Map<String, Object> config) {
        this.database = config.get(Constants.DATABASE).toString();
        this.password = config.get(Constants.PASSWORD).toString();
        this.driver = config.get(Constants.DRIVER).toString();
        this.user = config.get(Constants.USER).toString();
        this.table = config.get(Constants.TABLE).toString();
        this.url = config.get(Constants.URL).toString();
        this.sql = config.get(Constants.SQL).toString();
    }

    public void executeInsertSql(String statisticsName) {
        sql = sql.replace(Constants.STATISTICS_NAME, statisticsName)
                .replace(Constants.STATISTICS_VALUE, statisticsName);
        execute();
    }

    public void executeInsertSql(String statisticsName, String comparisonName) {
        sql = sql.replace(Constants.STATISTICS_NAME, statisticsName)
                .replace(Constants.STATISTICS_VALUE, statisticsName)
                .replace(Constants.COMPARISON_NAME, comparisonName);
        execute();
    }


    private void execute() {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
            int rowsAffected = stmt.executeUpdate(sql);
            if (rowsAffected >= 1) {
                logger.info("{} 执行成功", sql);
            } else {
                logger.error("{} 执行失败", sql);
                throw new Exception(sql + ": 插入失败");
            }
        } catch (Exception e) {
            logger.error("{}: 插入失败", sql);
            e.printStackTrace();
            System.exit(-1);
        } finally {
            try {
                // 关闭连接
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
    }

}
