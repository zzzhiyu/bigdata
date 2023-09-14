package org.apache.dolphinscheduler.data.quality.plugin;

import org.apache.dolphinscheduler.data.quality.Constants;

import java.util.Map;

public class Transfer {
    private final int index;
    private final String outputTable;
    private String sql;

    public Transfer(Map<String,Object> config) {
        this.index = (int) config.get(Constants.INDEX);
        this.outputTable = config.get(Constants.OUTPUT_TABLE).toString();
        this.sql = config.get(Constants.SQL).toString();
    }

    public String replaceTableName(String srcTableName, String dstTableName) {
        sql = sql.replace(srcTableName, dstTableName);
        return sql;
    }

    public String getOutputTable() {
        return outputTable;
    }

    public boolean isTableNameContain(String tableName) {
        return sql.contains(tableName);
    }

}
