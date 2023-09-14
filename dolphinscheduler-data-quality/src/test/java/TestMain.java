import org.apache.dolphinscheduler.data.quality.DataQualityApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMain {

    private static final Logger logger = LoggerFactory.getLogger(DataQualityApplication.class);


    public static final String OLD_TIMELINES = "(to_unix_timestamp(${src_field}, '${datetime_format}')-"
            + "to_unix_timestamp('${deadline}', '${datetime_format}') <= 0) AND "
            + "(to_unix_timestamp(${src_field}, '${datetime_format}')-"
            + "to_unix_timestamp('${begin_time}', '${datetime_format}') >= 0)";

    public static final String NEW_TIMELINES = "(${src_field} <= '${deadline}') AND (${src_field} >= '${begin_time}')";

    public static void main(String[] args) {
       String sql = "SELECT * FROM ${src_table} WHERE (to_unix_timestamp(${src_field}, '${datetime_format}')-to_unix_timestamp('${deadline}', '${datetime_format}') <= 0) AND (to_unix_timestamp(${src_field}, '${datetime_format}')-to_unix_timestamp('${begin_time}', '${datetime_format}') >= 0) AND (${src_filter}) ";

        System.out.println(sql.replace(OLD_TIMELINES, NEW_TIMELINES));
    }
}
