import org.apache.dolphinscheduler.data.quality.Constants;
import org.apache.dolphinscheduler.data.quality.config.DataQualityConfiguration;
import org.apache.dolphinscheduler.data.quality.plugin.Reader;
import org.apache.dolphinscheduler.data.quality.plugin.Transfer;
import org.apache.dolphinscheduler.data.quality.plugin.Writer;
import org.apache.dolphinscheduler.data.quality.utils.ConfigUtil;
import org.apache.dolphinscheduler.data.quality.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DataQualityApplication {

    private static final Logger logger = LoggerFactory.getLogger(DataQualityApplication.class);

    public static void main(String[] args) {

        String dataQualityParameter = "{\"name\":\"$t(timeliness_check)\",\"env\":{\"type\":\"batch\",\"config\":null},\"readers\":[{\"type\":\"JDBC\",\"config\":{\"database\":\"dwd\",\"password\":\"7cW8mw6eYms1M3x7\",\"driver\":\"com.mysql.cj.jdbc.Driver\",\"user\":\"dataxuser\",\"output_table\":\"dwd_pay_all_order_df_old\",\"table\":\"pay_all_order_df_old\",\"url\":\"jdbc:mysql://192.168.16.58:3310/dwd?allowLoadLocalInfile=false&autoDeserialize=false&allowLocalInfile=false&allowUrlInLocalInfile=false\"} },{\"type\":\"JDBC\",\"config\":{\"database\":\"dolps\",\"password\":\"dolps_bcwl.#\",\"driver\":\"com.mysql.cj.jdbc.Driver\",\"user\":\"dolps_bc\",\"output_table\":\"t_ds_dq_task_statistics_value\",\"table\":\"t_ds_dq_task_statistics_value\",\"url\":\"jdbc:mysql://rm-bp1z7j81np1s56547.mysql.rds.aliyuncs.com:3306/dolps?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowLoadLocalInfile=false&autoDeserialize=false&allowLocalInfile=false&allowUrlInLocalInfile=false\"} }],\"transformers\":[{\"type\":\"sql\",\"config\":{\"index\":1,\"output_table\":\"comparison_table\",\"sql\":\"select round(avg(statistics_value),2) as day_avg from t_ds_dq_task_statistics_value where data_time >=date_trunc('DAY', '2023-08-11 11:39:34') and data_time < date_add(date_trunc('day', '2023-08-11 11:39:34'),1) and unique_code = 'WSX39DUF3UGOHOQJW31ZDYE8+VXRPO20C2BEBTBAAN0=' and statistics_name = 'timeliness_count.timeliness'\"} },{\"type\":\"sql\",\"config\":{\"index\":2,\"output_table\":\"tmp2\",\"sql\":\"SELECT * FROM dwd_pay_all_order_df_old WHERE (statetime <= '2023-08-11') AND (statetime >= '2023-08-10')  \"} },{\"type\":\"sql\",\"config\":{\"index\":3,\"output_table\":\"statistics_table\",\"sql\":\"select count(*) as timeliness from  (SELECT * FROM dwd_pay_all_order_df_old WHERE (statetime <= '2023-08-11') AND (statetime >= '2023-08-10')   ) as timeliness_items \"} }],\"writers\":[{\"type\":\"JDBC\",\"config\":{\"database\":\"dolps\",\"password\":\"dolps_bcwl.#\",\"driver\":\"com.mysql.cj.jdbc.Driver\",\"user\":\"dolps_bc\",\"table\":\"t_ds_dq_execute_result\",\"url\":\"jdbc:mysql://rm-bp1z7j81np1s56547.mysql.rds.aliyuncs.com:3306/dolps?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowLoadLocalInfile=false&autoDeserialize=false&allowLocalInfile=false&allowUrlInLocalInfile=false\",\"sql\":\"insert into t_ds_dq_execute_result (rule_type, rule_name, process_definition_id, process_instance_id, task_instance_id, statistics_value, comparison_value, comparison_type, check_type, threshold, operator, failure_strategy, error_output_path, create_time, update_time) values ( 0, '$t(timeliness_check)', 0, 1059210, 1059210, { {statistics_name} }, { {comparison_name} }, 2, 0, 1000, 1, 0, '/user/dolphinscheduler/data_quality_error_data/0_1197836_test_dq', '2023-08-11 11:39:34', '2023-08-11 11:39:34' ) \"} },{\"type\":\"JDBC\",\"config\":{\"database\":\"dolps\",\"password\":\"dolps_bcwl.#\",\"driver\":\"com.mysql.cj.jdbc.Driver\",\"user\":\"dolps_bc\",\"table\":\"t_ds_dq_task_statistics_value\",\"url\":\"jdbc:mysql://rm-bp1z7j81np1s56547.mysql.rds.aliyuncs.com:3306/dolps?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowLoadLocalInfile=false&autoDeserialize=false&allowLocalInfile=false&allowUrlInLocalInfile=false\",\"sql\":\"insert into t_ds_dq_task_statistics_value (process_definition_id, task_instance_id, rule_id, unique_code, statistics_name, statistics_value, data_time, create_time, update_time) values ( 0, 1059210, 8, 'WSX39DUF3UGOHOQJW31ZDYE8+VXRPO20C2BEBTBAAN0=', 'timeliness_count.timeliness', { {statistics_value} }, '2023-08-11 11:39:34', '2023-08-11 11:39:34', '2023-08-11 11:39:34' ) \"} }]}";
        DataQualityConfiguration dataQualityConfiguration = JsonUtils.fromJson(dataQualityParameter,DataQualityConfiguration.class);
        if (dataQualityConfiguration == null) {
            logger.info("DataQualityConfiguration is null");
            System.exit(-1);
        } else {
            dataQualityConfiguration.validate();
        }

        String statistics_name = "null";
        String comparison_name = "null";

        //获取到所有reader
        List<Reader> readerList = ConfigUtil.getReaderList(dataQualityConfiguration);
        logger.info("reader解析完成");

        //获取到所有transfer
        List<Transfer> transferList = ConfigUtil.getTransferList(dataQualityConfiguration);
        logger.info("transfer解析完成");

        //计算出statistics_name 或者 comparison_name
        for (Transfer transfer : transferList) {
            // 只有statistics_table 和 statistics_table需要执行
            if (Constants.STATISTICS_TABLE_NAME.equalsIgnoreCase(transfer.getOutputTable()) ||
                    Constants.COMPARISON_TABLE_NAME.equalsIgnoreCase(transfer.getOutputTable())) {
                for (Reader reader : readerList) {
                    // 找到对应的reader
                    if (transfer.isTableNameContain(reader.getOutputTable())) {
                        //表进行替换
                        String countQuerySql = transfer.replaceTableName(reader.getOutputTable(), reader.getTable());
                        if (Constants.STATISTICS_TABLE_NAME.equalsIgnoreCase(transfer.getOutputTable())) {
                            System.out.println(countQuerySql);
//                            statistics_name = reader.executeCountQuery(countQuerySql);
                        } else {
                            System.out.println(countQuerySql);
//                            comparison_name = reader.executeCountQuery(countQuerySql);
                        }
                    }
                }
            }
        }

        if (statistics_name == null) {
            logger.error("statistics_name 结果值没有计算出");
            System.exit(-1);
        }
        logger.info("statistics_name解析完成: {}", statistics_name);
        logger.info("comparison_name解析完成: {}", comparison_name == null? "null": comparison_name);

        //将结果写入表中
        List<Writer> writerList = ConfigUtil.getWriterList(dataQualityConfiguration);
        logger.info("writer解析完成");

        for (Writer writer : writerList) {
            if (comparison_name != null) {
                writer.executeInsertSql(statistics_name, comparison_name);
            } else {
                writer.executeInsertSql(statistics_name);
            }
        }

        logger.info("结果写入表中! 执行结束! 完成!!!\n\n\n");

    }
}
