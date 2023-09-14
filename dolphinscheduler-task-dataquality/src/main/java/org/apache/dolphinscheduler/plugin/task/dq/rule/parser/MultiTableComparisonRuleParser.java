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

package org.apache.dolphinscheduler.plugin.task.dq.rule.parser;

import static org.apache.dolphinscheduler.plugin.task.api.utils.DataQualityConstants.*;

import org.apache.dolphinscheduler.plugin.task.api.DataQualityTaskExecutionContext;
import org.apache.dolphinscheduler.plugin.task.api.parser.ParameterUtils;
import org.apache.dolphinscheduler.plugin.task.dq.exception.DataQualityException;
import org.apache.dolphinscheduler.plugin.task.dq.rule.RuleManager;
import org.apache.dolphinscheduler.plugin.task.dq.rule.parameter.BaseConfig;
import org.apache.dolphinscheduler.plugin.task.dq.rule.parameter.DataQualityConfiguration;
import org.apache.dolphinscheduler.plugin.task.dq.utils.RuleParserUtils;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MultiTableComparisonRuleParser
 */
public class MultiTableComparisonRuleParser implements IRuleParser {

    private String checkAndReplace(String sql, String checkValue, String replaceSrc) {
        if (StringUtils.isEmpty(checkValue) && StringUtils.isNotEmpty(sql)) {
            return sql.replace(replaceSrc, "");
        }
        return sql;
    }

    private int setTransformerConfig(int index,
                                     String sql,
                                     String tableName,
                                     Map<String, String> inputParameterValueResult,
                                     List<BaseConfig> transformerConfigList) {
        Map<String, Object> config = new HashMap<>();
        config.put(INDEX, index++);
        config.put(SQL, ParameterUtils.convertParameterPlaceholders(sql, inputParameterValueResult));
        config.put(OUTPUT_TABLE, tableName);
        BaseConfig transformerConfig = new BaseConfig(SQL, config);
        transformerConfigList.add(transformerConfig);
        return index;
    }

    private List<BaseConfig> getTransformerConfigList(Map<String, String> inputParameterValue) {
        List<BaseConfig> transformerConfigList = new ArrayList<>();

        int index = 1;

        String statisticsExecuteSql = inputParameterValue.get(STATISTICS_EXECUTE_SQL);
        statisticsExecuteSql =
                checkAndReplace(statisticsExecuteSql, inputParameterValue.get(SRC_FILTER), "WHERE (${src_filter})");
        statisticsExecuteSql = checkAndReplace(statisticsExecuteSql, inputParameterValue.get(TARGET_FILTER),
                "WHERE (${target_filter})");

        index = setTransformerConfig(index, statisticsExecuteSql, RuleManager.STATISTICS_TABLE, inputParameterValue,
                transformerConfigList);

        String comparisonExecuteSql = inputParameterValue.get(COMPARISON_EXECUTE_SQL);
        comparisonExecuteSql =
                checkAndReplace(comparisonExecuteSql, inputParameterValue.get(SRC_FILTER), "WHERE (${src_filter})");
        comparisonExecuteSql = checkAndReplace(comparisonExecuteSql, inputParameterValue.get(TARGET_FILTER),
                "WHERE (${target_filter})");

        setTransformerConfig(index, comparisonExecuteSql, RuleManager.COMPARISON_TABLE, inputParameterValue,
                transformerConfigList);

        return transformerConfigList;
    }

    @Override
    public DataQualityConfiguration parse(Map<String, String> inputParameterValue,
                                          DataQualityTaskExecutionContext context) throws DataQualityException {

        List<BaseConfig> readerConfigList =
                RuleParserUtils.getReaderConfigList(inputParameterValue, context);
        RuleParserUtils.addStatisticsValueTableReaderConfig(readerConfigList, context);

        List<BaseConfig> transformerConfigList = getTransformerConfigList(inputParameterValue);

        // TODO 替换执行的SQL
        String writerSql = RuleManager.BASE_INSERT;
        writerSql = writerSql.replace("${comparison_name}", "{{comparison_name}}");

        List<BaseConfig> writerConfigList = RuleParserUtils.getWriterConfigList(
                ParameterUtils.convertParameterPlaceholders(writerSql, inputParameterValue),
                context);

        return new DataQualityConfiguration(
                context.getRuleName(),
                RuleParserUtils.getEnvConfig(),
                readerConfigList,
                writerConfigList,
                transformerConfigList);
    }
}
