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

package org.apache.dolphinscheduler.data.quality;

/**
 * Constants
 */
public final class Constants {

    private Constants() {
        throw new IllegalStateException("Construct Constants");
    }

    public static final String TO_UNIX_TIMESTAMP = "to_unix_timestamp";

    public static final String MYSQL_STR_TO_DATE = "str_to_date";

    public static final String POSTGRESQL_TO_TIMESTAMP = "to_timestamp";

    public static final String STATISTICS_TABLE_NAME = "statistics_table";

    public static final String COMPARISON_TABLE_NAME = "comparison_table";

    public static final String STATISTICS_NAME = "{ {statistics_name} }";

    public static final String STATISTICS_VALUE = "{ {statistics_value} }";

    public static final String COMPARISON_NAME = "{ {comparison_name} }";

    public static final String INDEX = "index";

    public static final String DATABASE = "database";

    public static final String TABLE = "table";

    public static final String URL = "url";

    public static final String USER = "user";

    public static final String PASSWORD = "password";

    public static final String DRIVER = "driver";

    public static final String EMPTY = "";

    public static final String SQL = "sql";

    public static final String DOTS = ".";

    public static final String INPUT_TABLE = "input_table";

    public static final String OUTPUT_TABLE = "output_table";

    public static final String TMP_TABLE = "tmp_table";

    public static final String DB_TABLE = "dbtable";

    public static final String JDBC = "jdbc";

    public static final String SAVE_MODE = "save_mode";

    public static final String APPEND = "append";

    public static final String SPARK_APP_NAME = "spark.app.name";

    /**
     * date format of yyyy-MM-dd HH:mm:ss
     */
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
}
