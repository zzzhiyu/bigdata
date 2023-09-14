package org.apache.dolphinscheduler.data.quality;

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
        logger.info("\n\n\nDataQualityApplication开始执行");

        if (args.length < 1) {
            logger.error("Can not find DataQualityConfiguration");
            System.exit(-1);
        }

        String dataQualityParameter = args[0];

        DataQualityConfiguration dataQualityConfiguration = JsonUtils.fromJson(dataQualityParameter,DataQualityConfiguration.class);
        if (dataQualityConfiguration == null) {
            logger.info("DataQualityConfiguration is null");
            System.exit(-1);
        } else {
            dataQualityConfiguration.validate();
        }

        String statistics_name = null;
        String comparison_name = null;

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
                            logger.info("开始执行sql: {}", countQuerySql);
                            statistics_name = reader.executeCountQuery(countQuerySql);
                            logger.info("执行成功, 结果为:{}", statistics_name);
                        } else {
                            logger.info("开始执行sql: {}", countQuerySql);
                            comparison_name = reader.executeCountQuery(countQuerySql);
                            logger.info("执行成功, 结果为:{}", comparison_name);
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
        logger.info("结果写入成功!!!");

        logger.info("结果写入表中! 执行结束! 完成!!!\n\n\n");

    }
}
