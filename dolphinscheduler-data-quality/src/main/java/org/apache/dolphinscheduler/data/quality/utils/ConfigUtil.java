package org.apache.dolphinscheduler.data.quality.utils;

import org.apache.dolphinscheduler.data.quality.DataQualityApplication;
import org.apache.dolphinscheduler.data.quality.config.DataQualityConfiguration;
import org.apache.dolphinscheduler.data.quality.config.ReaderConfig;
import org.apache.dolphinscheduler.data.quality.config.TransformerConfig;
import org.apache.dolphinscheduler.data.quality.config.WriterConfig;
import org.apache.dolphinscheduler.data.quality.plugin.Reader;
import org.apache.dolphinscheduler.data.quality.plugin.Transfer;
import org.apache.dolphinscheduler.data.quality.plugin.Writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigUtil {

    public static List<Reader> getReaderList(DataQualityConfiguration dataQualityConfiguration) {
        List<Reader> readerList = new ArrayList<>();
        List<ReaderConfig> readerConfigs = dataQualityConfiguration.getReaderConfigs();
        for (ReaderConfig readerConfig : readerConfigs) {
            Map<String, Object> config = readerConfig.getConfig();
            Reader reader = new Reader(config);
            readerList.add(reader);
        }
        return readerList;
    }

    public static List<Transfer> getTransferList(DataQualityConfiguration dataQualityConfiguration) {
        List<Transfer> transferList = new ArrayList<>();
        List<TransformerConfig> transformerConfigs = dataQualityConfiguration.getTransformerConfigs();
        for (TransformerConfig transformerConfig : transformerConfigs) {
            Map<String, Object> config = transformerConfig.getConfig();
            Transfer transfer = new Transfer(config);
            transferList.add(transfer);
        }
        return transferList;
    }

    public static List<Writer> getWriterList(DataQualityConfiguration dataQualityConfiguration) {
        List<Writer> writerList = new ArrayList<>();
        List<WriterConfig> writerConfigs = dataQualityConfiguration.getWriterConfigs();
        for (WriterConfig writerConfig : writerConfigs) {
            Map<String, Object> config = writerConfig.getConfig();
            Writer writer = new Writer(config);
            writerList.add(writer);
        }
        return writerList;
    }

}
