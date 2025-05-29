package org.example.broker.constant;

/**
 * @author qushutao
 * @since 2025-05-25
 */
public class BrokerConstant {

//    public static final String MQ_HOME = "E:\\developeCode\\java\\bastmq";
//    public static final String MQ_HOME = "\\bastmq";

    public static final String MQ_HOME = "..\\bastmq";


    public static final String BASIC_COMMITLOG_PATH = "\\broker\\store\\";
    public static final String BASIC_CONSUMEQUEUE_PATH = "\\broker\\consumequeue\\";

    public static final String CONFIG_TOPIC_JSON_PATH = "\\broker\\config\\mq_topic.json";

    public static final String FIRST_LOG_FILE_NAME = "00000000";

    public static final int COMMIT_LOG_FILE_SIZE =  1024 * 1024;
    public static final String CONSUME_QUEUE_OFFSET_MODEL_PATH = "\\broker\\config\\consume_msg.json";
}
