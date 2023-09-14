/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dolphinscheduler.plugin.task.dq.rule;

import static org.apache.dolphinscheduler.plugin.task.api.TaskConstants.SINGLE_QUOTES;
import static org.apache.dolphinscheduler.plugin.task.api.utils.DataQualityConstants.COMPARISON_TYPE;
import static org.apache.dolphinscheduler.plugin.task.api.utils.DataQualityConstants.UNIQUE_CODE;

import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.plugin.task.api.DataQualityTaskExecutionContext;
import org.apache.dolphinscheduler.plugin.task.api.enums.dp.RuleType;
import org.apache.dolphinscheduler.plugin.task.api.parser.BusinessTimeUtils;
import org.apache.dolphinscheduler.plugin.task.dq.exception.DataQualityException;
import org.apache.dolphinscheduler.plugin.task.dq.rule.entity.DqRuleInputEntry;
import org.apache.dolphinscheduler.plugin.task.dq.rule.parameter.DataQualityConfiguration;
import org.apache.dolphinscheduler.plugin.task.dq.rule.parser.IRuleParser;
import org.apache.dolphinscheduler.plugin.task.dq.rule.parser.MultiTableAccuracyRuleParser;
import org.apache.dolphinscheduler.plugin.task.dq.rule.parser.MultiTableComparisonRuleParser;
import org.apache.dolphinscheduler.plugin.task.dq.rule.parser.SingleTableCustomSqlRuleParser;
import org.apache.dolphinscheduler.plugin.task.dq.rule.parser.SingleTableRuleParser;
import org.apache.dolphinscheduler.plugin.task.dq.utils.RuleParserUtils;
import org.apache.dolphinscheduler.spi.enums.CommandType;

import java.util.Date;
import java.util.Map;

/**
 * RuleManager is responsible for parsing the input parameters to the DataQualityConfiguration
 * And DataQualityConfiguration will be used in DataQualityApplication
 */
public class RuleManager {

    public static final String TIMELINES_ITEMS = "timeliness_items";
    public static final String TIMELINES_ITEMS_SQL =
            "SELECT * FROM ${src_table} WHERE (${src_field} < '${deadline}') AND (${src_field} >= '${begin_time}') AND (${src_filter})";

    public static final String DAY_RANGE = "day_range";
    public static final String DAY_RANGE_SQL =
            "select round(avg(statistics_value),2) as day_avg from t_ds_dq_task_statistics_value where data_time >=date_sub(date_format(${data_time}, '%Y-%m-%d'),interval 1 day) and data_time < date_format(${data_time}, '%Y-%m-%d') and unique_code = ${unique_code} and statistics_name = '${statistics_name}'";

    public static final String WEEK_RANGE = "week_range";
    public static final String WEEK_RANGE_SQL =
            "select round(avg(statistics_value),2) as week_avg from t_ds_dq_task_statistics_value where  data_time >= date_sub(date_format(${data_time}, '%Y-%m-%d'),interval 1 week) and data_time <date_format(${data_time}, '%Y-%m-%d') and unique_code = ${unique_code} and statistics_name = '${statistics_name}'";

    public static final String MONTH_RANGE = "month_range";
    public static final String MONTH_RANGE_SQL =
            "select round(avg(statistics_value),2) as month_avg from t_ds_dq_task_statistics_value where  data_time >= date_sub(date_format(${data_time}, '%Y-%m-%d'),interval 1 month) and data_time <date_format(${data_time}, '%Y-%m-%d') and unique_code = ${unique_code} and statistics_name = '${statistics_name}'";

    public static final String LAST_SEVEN_DAYS = "last_seven_days";
    public static final String LAST_SEVEN_DAYS_SQL =
            "select round(avg(statistics_value),2) as last_7_avg from t_ds_dq_task_statistics_value where  data_time >= date_sub(date_format(${data_time}, '%Y-%m-%d'),interval 7 day) and  data_time <date_format(${data_time}, '%Y-%m-%d') and unique_code = ${unique_code} and statistics_name = '${statistics_name}'";

    public static final String LAST_THIRTY_DAYS = "last_thirty_days";
    public static final String LAST_THIRTY_DAYS_SQL =
            "select round(avg(statistics_value),2) as last_30_avg from t_ds_dq_task_statistics_value where  data_time >= date_sub(date_format(${data_time}, '%Y-%m-%d'),interval 30 day) and  data_time < date_format(${data_time}, '%Y-%m-%d') and unique_code = ${unique_code} and statistics_name = '${statistics_name}'";

    public static final String STATISTICS_TABLE = "statistics_table";

    public static final String COMPARISON_TABLE = "comparison_table";

    public static final String[] OUTPUT_TABLES = {"day_range", "week_range", "month_range", "last_seven_days",
            "last_thirty_days", "total_count"};

    private final Map<String, String> inputParameterValue;
    private final DataQualityTaskExecutionContext dataQualityTaskExecutionContext;

    private static final String NONE_COMPARISON_TYPE = "0";

    // TODO 增加insert语句
    public static final String BASE_INSERT =
            "insert into t_ds_dq_execute_result "
                    + "(rule_type, rule_name, process_definition_id, process_instance_id, task_instance_id, "
                    + "statistics_value, comparison_value, comparison_type, check_type, threshold, operator, "
                    + "failure_strategy, error_output_path, create_time, update_time) values ( "
                    + "${rule_type}, ${rule_name}, ${process_definition_id}, ${task_instance_id}, ${task_instance_id}, "
                    + "{{statistics_name}}, ${comparison_name}, ${comparison_type}, ${check_type}, ${threshold}, ${operator}, "
                    + "${failure_strategy}, '${error_output_path}', ${create_time}, ${update_time} ) ";

    // private static final String BASE_SQL =
    // "select ${rule_type} as rule_type,"
    // + "${rule_name} as rule_name,"
    // + "${process_definition_id} as process_definition_id,"
    // + "${process_instance_id} as process_instance_id,"
    // + "${task_instance_id} as task_instance_id,"
    // + "${statistics_name} AS statistics_value,"
    // + "${comparison_name} AS comparison_value,"
    // + "${comparison_type} AS comparison_type,"
    // + "${check_type} as check_type,"
    // + "${threshold} as threshold,"
    // + "${operator} as operator,"
    // + "${failure_strategy} as failure_strategy,"
    // + "'${error_output_path}' as error_output_path,"
    // + "${create_time} as create_time,"
    // + "${update_time} as update_time ";
    //
    // public static final String DEFAULT_COMPARISON_WRITER_SQL =
    // BASE_SQL + "from ${statistics_table} full join ${comparison_table}";
    //
    // public static final String MULTI_TABLE_COMPARISON_WRITER_SQL =
    // BASE_SQL
    // + "from ( ${statistics_execute_sql} ) tmp1 "
    // + "join ( ${comparison_execute_sql} ) tmp2";
    //
    // public static final String SINGLE_TABLE_CUSTOM_SQL_WRITER_SQL =
    // BASE_SQL
    // + "from ( ${statistics_table} ) tmp1 "
    // + "join ${comparison_table}";
    // public static final String TASK_STATISTICS_VALUE_WRITER_SQL =
    // "select "
    // + "${process_definition_id} as process_definition_id,"
    // + "${task_instance_id} as task_instance_id,"
    // + "${rule_id} as rule_id,"
    // + "${unique_code} as unique_code,"
    // + "'${statistics_name}'AS statistics_name,"
    // + "${statistics_name} AS statistics_value,"
    // + "${data_time} as data_time,"
    // + "${create_time} as create_time,"
    // + "${update_time} as update_time "
    // + "from ${statistics_table}";

    // TODO 增加insert语句
    public static final String TASK_STATISTICS_VALUE_WRITER_INSERT =
            "insert into t_ds_dq_task_statistics_value "
                    + "(process_definition_id, task_instance_id, rule_id, unique_code, statistics_name, "
                    + "statistics_value, data_time, create_time, update_time) values ( "
                    + "${process_definition_id}, ${task_instance_id}, ${rule_id}, ${unique_code}, '${statistics_name}', "
                    + "{{statistics_value}}, ${data_time}, ${create_time}, ${update_time} ) ";

    public RuleManager(Map<String, String> inputParameterValue,
                       DataQualityTaskExecutionContext dataQualityTaskExecutionContext) {
        this.inputParameterValue = inputParameterValue;
        this.dataQualityTaskExecutionContext = dataQualityTaskExecutionContext;
    }

    /**
     * @return DataQualityConfiguration
     * @throws RuntimeException RuntimeException
     */
    public DataQualityConfiguration generateDataQualityParameter() throws RuntimeException {

        Map<String, String> inputParameterValueResult =
                RuleParserUtils.getInputParameterMapFromEntryList(
                        JSONUtils.toList(dataQualityTaskExecutionContext.getRuleInputEntryList(),
                                DqRuleInputEntry.class));
        inputParameterValueResult.putAll(inputParameterValue);
        inputParameterValueResult.putAll(BusinessTimeUtils.getBusinessTime(CommandType.START_PROCESS, new Date()));
        inputParameterValueResult.putIfAbsent(COMPARISON_TYPE, NONE_COMPARISON_TYPE);
        inputParameterValueResult.put(UNIQUE_CODE,
                SINGLE_QUOTES + RuleParserUtils.generateUniqueCode(inputParameterValueResult) + SINGLE_QUOTES);

        IRuleParser ruleParser = null;
        switch (RuleType.of(dataQualityTaskExecutionContext.getRuleType())) {
            case SINGLE_TABLE:
                ruleParser = new SingleTableRuleParser();
                break;
            case SINGLE_TABLE_CUSTOM_SQL:
                ruleParser = new SingleTableCustomSqlRuleParser();
                break;
            case MULTI_TABLE_ACCURACY:
                ruleParser = new MultiTableAccuracyRuleParser();
                break;
            case MULTI_TABLE_COMPARISON:
                ruleParser = new MultiTableComparisonRuleParser();
                break;
            default:
                throw new DataQualityException("rule type is not support");
        }

        return ruleParser.parse(inputParameterValueResult, dataQualityTaskExecutionContext);
    }
}
