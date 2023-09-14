timeliness_items
SELECT * FROM ${src_table} WHERE (to_unix_timestamp(${src_field}, '${datetime_format}')-to_unix_timestamp('${deadline}', '${datetime_format}') <= 0) AND (to_unix_timestamp(${src_field}, '${datetime_format}')-to_unix_timestamp('${begin_time}', '${datetime_format}') >= 0) AND (${src_filter})

SELECT * FROM ${src_table} WHERE (${src_field} < '${deadline}') AND (${src_field} >= '${begin_time}') AND (${src_filter})


    day_range:
select round(avg(statistics_value),2) as day_avg from t_ds_dq_task_statistics_value where data_time >=date_trunc('DAY', ${data_time}) and data_time < date_add(date_trunc('day', ${data_time}),1) and unique_code = ${unique_code} and statistics_name = '${statistics_name}'

select round(avg(statistics_value),2) as day_avg from t_ds_dq_task_statistics_value where data_time >=date_sub(date_format(${data_time}, '%Y-%m-%d'),interval 1 day) and data_time < date_format(${data_time}, '%Y-%m-%d') and unique_code = ${unique_code} and statistics_name = '${statistics_name}'


    week_range:
select round(avg(statistics_value),2) as week_avg from t_ds_dq_task_statistics_value where  data_time >= date_trunc('WEEK', ${data_time}) and data_time <date_trunc('day', ${data_time}) and unique_code = ${unique_code} and statistics_name = '${statistics_name}'

select round(avg(statistics_value),2) as week_avg from t_ds_dq_task_statistics_value where  data_time >= date_sub(date_format(${data_time}, '%Y-%m-%d'),interval 1 week) and data_time <date_format(${data_time}, '%Y-%m-%d') and unique_code = ${unique_code} and statistics_name = '${statistics_name}'


    month_range
select round(avg(statistics_value),2) as month_avg from t_ds_dq_task_statistics_value where  data_time >= date_trunc('MONTH', ${data_time}) and data_time <date_trunc('day', ${data_time}) and unique_code = ${unique_code} and statistics_name = '${statistics_name}'

select round(avg(statistics_value),2) as month_avg from t_ds_dq_task_statistics_value where  data_time >= date_sub(date_format(${data_time}, '%Y-%m-%d'),interval 1 month) and data_time <date_format(${data_time}, '%Y-%m-%d') and unique_code = ${unique_code} and statistics_name = '${statistics_name}'


    last_seven_days
select round(avg(statistics_value),2) as last_7_avg from t_ds_dq_task_statistics_value where  data_time >= date_add(date_trunc('day', ${data_time}),-7) and  data_time <date_trunc('day', ${data_time}) and unique_code = ${unique_code} and statistics_name = '${statistics_name}'

select round(avg(statistics_value),2) as last_7_avg from t_ds_dq_task_statistics_value where  data_time >= date_sub(date_format(${data_time}, '%Y-%m-%d'),interval 7 day) and  data_time <date_format(${data_time}, '%Y-%m-%d') and unique_code = ${unique_code} and statistics_name = '${statistics_name}'


    last_thirty_days
select round(avg(statistics_value),2) as last_30_avg from t_ds_dq_task_statistics_value where  data_time >= date_add(date_trunc('day', ${data_time}),-30) and  data_time < date_trunc('day', ${data_time}) and unique_code = ${unique_code} and statistics_name = '${statistics_name}'

select round(avg(statistics_value),2) as last_30_avg from t_ds_dq_task_statistics_value where  data_time >= date_sub(date_format(${data_time}, '%Y-%m-%d'),interval 30 day) and  data_time < date_format(${data_time}, '%Y-%m-%d') and unique_code = ${unique_code} and statistics_name = '${statistics_name}'