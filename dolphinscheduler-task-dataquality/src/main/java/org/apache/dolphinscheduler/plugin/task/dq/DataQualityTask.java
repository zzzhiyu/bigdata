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

package org.apache.dolphinscheduler.plugin.task.dq;

import static org.apache.dolphinscheduler.common.constants.DateConstants.YYYY_MM_DD_HH_MM_SS;
import static org.apache.dolphinscheduler.plugin.task.api.TaskConstants.SLASH;
import static org.apache.dolphinscheduler.plugin.task.api.TaskConstants.UNDERLINE;
import static org.apache.dolphinscheduler.plugin.task.api.utils.DataQualityConstants.CREATE_TIME;
import static org.apache.dolphinscheduler.plugin.task.api.utils.DataQualityConstants.DATA_TIME;
import static org.apache.dolphinscheduler.plugin.task.api.utils.DataQualityConstants.ERROR_OUTPUT_PATH;
import static org.apache.dolphinscheduler.plugin.task.api.utils.DataQualityConstants.PROCESS_DEFINITION_ID;
import static org.apache.dolphinscheduler.plugin.task.api.utils.DataQualityConstants.PROCESS_INSTANCE_ID;
import static org.apache.dolphinscheduler.plugin.task.api.utils.DataQualityConstants.REGEXP_PATTERN;
import static org.apache.dolphinscheduler.plugin.task.api.utils.DataQualityConstants.RULE_ID;
import static org.apache.dolphinscheduler.plugin.task.api.utils.DataQualityConstants.RULE_NAME;
import static org.apache.dolphinscheduler.plugin.task.api.utils.DataQualityConstants.RULE_TYPE;
import static org.apache.dolphinscheduler.plugin.task.api.utils.DataQualityConstants.TASK_INSTANCE_ID;
import static org.apache.dolphinscheduler.plugin.task.api.utils.DataQualityConstants.UPDATE_TIME;

import org.apache.dolphinscheduler.common.constants.Constants;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.plugin.datasource.api.utils.CommonUtils;
import org.apache.dolphinscheduler.plugin.task.api.AbstractYarnTask;
import org.apache.dolphinscheduler.plugin.task.api.DataQualityTaskExecutionContext;
import org.apache.dolphinscheduler.plugin.task.api.TaskExecutionContext;
import org.apache.dolphinscheduler.plugin.task.api.model.Property;
import org.apache.dolphinscheduler.plugin.task.api.model.ResourceInfo;
import org.apache.dolphinscheduler.plugin.task.api.parameters.AbstractParameters;
import org.apache.dolphinscheduler.plugin.task.api.parameters.dataquality.DataQualityParameters;
import org.apache.dolphinscheduler.plugin.task.api.parser.ParamUtils;
import org.apache.dolphinscheduler.plugin.task.api.parser.ParameterUtils;
import org.apache.dolphinscheduler.plugin.task.api.utils.ArgsUtils;
import org.apache.dolphinscheduler.plugin.task.dq.rule.RuleManager;
import org.apache.dolphinscheduler.plugin.task.dq.rule.parameter.DataQualityConfiguration;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * In DataQualityTask, the input parameters will be converted into DataQualityConfiguration,
 * which will be converted into a string as the parameter of DataQualityApplication,
 * and DataQualityApplication is spark application
 */
public class DataQualityTask extends AbstractYarnTask {

    private static final String JAVA_COMMAND = "java -jar";

    /**
     * spark2 command
     */
    // private static final String SPARK2_COMMAND = "${SPARK_HOME2}/bin/spark-submit";

    private DataQualityParameters dataQualityParameters;

    private final TaskExecutionContext dqTaskExecutionContext;

    public DataQualityTask(TaskExecutionContext taskExecutionContext) {
        super(taskExecutionContext);
        this.dqTaskExecutionContext = taskExecutionContext;
    }

    @Override
    public void init() {
        logger.info("data quality task params {}", dqTaskExecutionContext.getTaskParams());
        // 解析dolphinscheduler任务配置的参数
        dataQualityParameters =
                JSONUtils.parseObject(dqTaskExecutionContext.getTaskParams(), DataQualityParameters.class);
        // 参数不能为空
        if (null == dataQualityParameters) {
            logger.error("data quality params is null");
            return;
        }
        // 检查参数的有效性
        if (!dataQualityParameters.checkParameters()) {
            throw new RuntimeException("data quality task params is not valid");
        }
        // 获取inputParameter的参数
        Map<String, String> inputParameter = dataQualityParameters.getRuleInputParameter();
        // 去掉空格
        for (Map.Entry<String, String> entry : inputParameter.entrySet()) {
            if (entry != null && entry.getValue() != null) {
                entry.setValue(entry.getValue().trim());
            }
        }

        // 获取数据源配置信息
        DataQualityTaskExecutionContext dataQualityTaskExecutionContext =
                dqTaskExecutionContext.getDataQualityTaskExecutionContext();
        // 将一些信息写入inputParameter
        operateInputParameter(inputParameter, dataQualityTaskExecutionContext);

        // 生成rule管理
        RuleManager ruleManager = new RuleManager(
                inputParameter,
                dataQualityTaskExecutionContext);

        // 生成配置的参数
        DataQualityConfiguration dataQualityConfiguration =
                ruleManager.generateDataQualityParameter();

        dataQualityParameters
                .getSparkParameters()
                .setMainArgs("\""
                        + replaceDoubleBrackets(
                                StringEscapeUtils.escapeJava(JSONUtils.toJsonString(dataQualityConfiguration)))
                        + "\"");

        // spark参数现在暂时不需要
        // dataQualityParameters
        // .getSparkParameters()
        // .setQueue(dqTaskExecutionContext.getQueue());

        setMainJarName();
    }

    private void operateInputParameter(Map<String, String> inputParameter,
                                       DataQualityTaskExecutionContext dataQualityTaskExecutionContext) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
        LocalDateTime time = LocalDateTime.now();
        String now = df.format(time);

        inputParameter.put(RULE_ID, String.valueOf(dataQualityTaskExecutionContext.getRuleId()));
        inputParameter.put(RULE_TYPE, String.valueOf(dataQualityTaskExecutionContext.getRuleType()));
        inputParameter.put(RULE_NAME, ArgsUtils.wrapperSingleQuotes(dataQualityTaskExecutionContext.getRuleName()));
        inputParameter.put(CREATE_TIME, ArgsUtils.wrapperSingleQuotes(now));
        inputParameter.put(UPDATE_TIME, ArgsUtils.wrapperSingleQuotes(now));
        inputParameter.put(PROCESS_DEFINITION_ID, String.valueOf(dqTaskExecutionContext.getProcessDefineId()));
        inputParameter.put(PROCESS_INSTANCE_ID, String.valueOf(dqTaskExecutionContext.getProcessInstanceId()));
        inputParameter.put(TASK_INSTANCE_ID, String.valueOf(dqTaskExecutionContext.getTaskInstanceId()));

        // 获取数据时间
        if (StringUtils.isEmpty(inputParameter.get(DATA_TIME))) {
            inputParameter.put(DATA_TIME, ArgsUtils.wrapperSingleQuotes(now));
        }

        // 正则表达式
        if (StringUtils.isNotEmpty(inputParameter.get(REGEXP_PATTERN))) {
            inputParameter.put(REGEXP_PATTERN,
                    StringEscapeUtils.escapeJava(StringEscapeUtils.escapeJava(inputParameter.get(REGEXP_PATTERN))));
        }

        // hdfs路径 可以删除
        if (StringUtils.isNotEmpty(dataQualityTaskExecutionContext.getHdfsPath())) {
            inputParameter.put(ERROR_OUTPUT_PATH,
                    dataQualityTaskExecutionContext.getHdfsPath()
                            + SLASH + dqTaskExecutionContext.getProcessDefineId()
                            + UNDERLINE + dqTaskExecutionContext.getProcessInstanceId()
                            + UNDERLINE + dqTaskExecutionContext.getTaskName());
        } else {
            inputParameter.put(ERROR_OUTPUT_PATH, "");
        }
    }

    @Override
    protected String buildCommand() {
        List<String> args = new ArrayList<>();

        // 删除相关配置
        // args.add(SPARK2_COMMAND);
        // args.addAll(SparkArgsUtils.buildArgs(dataQualityParameters.getSparkParameters()));

        args.add(JAVA_COMMAND);
        args.add(dataQualityParameters.getSparkParameters().getMainJar().getRes());
        args.add(dataQualityParameters.getSparkParameters().getMainArgs());

        // replace placeholder
        Map<String, Property> paramsMap = dqTaskExecutionContext.getPrepareParamsMap();
        // 参数进行替换
        String command =
                ParameterUtils.convertParameterPlaceholders(String.join(" ", args), ParamUtils.convert(paramsMap));
        logger.info("data quality task command: {}", command);

        return command;
    }

    @Override
    protected void setMainJarName() {
        ResourceInfo mainJar = new ResourceInfo();
        String basePath = System.getProperty("user.dir").replace(File.separator + "bin", "");
        mainJar.setRes(basePath + File.separator + "libs" + File.separator + CommonUtils.getDataQualityJarName());
        dataQualityParameters.getSparkParameters().setMainJar(mainJar);
    }

    @Override
    public AbstractParameters getParameters() {
        return dataQualityParameters;
    }

    private static String replaceDoubleBrackets(String mainParameter) {
        mainParameter = mainParameter
                .replace(Constants.DOUBLE_BRACKETS_LEFT, Constants.DOUBLE_BRACKETS_LEFT_SPACE)
                .replace(Constants.DOUBLE_BRACKETS_RIGHT, Constants.DOUBLE_BRACKETS_RIGHT_SPACE);
        if (mainParameter.contains(Constants.DOUBLE_BRACKETS_LEFT)
                || mainParameter.contains(Constants.DOUBLE_BRACKETS_RIGHT)) {
            return replaceDoubleBrackets(mainParameter);
        } else {
            return mainParameter;
        }
    }
}
