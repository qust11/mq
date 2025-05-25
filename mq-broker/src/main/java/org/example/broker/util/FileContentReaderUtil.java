package org.example.broker.util;

import com.alibaba.fastjson2.JSONArray;
import org.example.broker.model.EagleMqTopicModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

/**
 * @author qushutao
 * @since 2025-05-25
 */
public class FileContentReaderUtil {

    public static String readContent(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while (bufferedReader.ready()) {
                sb.append(bufferedReader.readLine());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public static List<EagleMqTopicModel> readTopicModel(String filePath) {
        String content = readContent(filePath);
        return JSONArray.parseArray(content, EagleMqTopicModel.class);
    }
}
