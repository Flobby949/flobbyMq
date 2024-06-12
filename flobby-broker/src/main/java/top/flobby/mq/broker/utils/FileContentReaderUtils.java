package top.flobby.mq.broker.utils;

import com.alibaba.fastjson2.JSON;
import top.flobby.mq.broker.model.TopicModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 简化版本的文件读取工具
 * @create : 2024-06-12 10:40
 **/

public class FileContentReaderUtils {

    public static String readFromFile(String fileName) {
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            StringBuffer sb = new StringBuffer();
            while (in.ready()) {
                sb.append(in.readLine());
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String content = FileContentReaderUtils.readFromFile("F:\\flobbyMq\\broker\\config\\flobbymq-topic.json");
        System.out.println(content);
        List<TopicModel> topicModels = JSON.parseArray(content, TopicModel.class);
        System.out.println("topicModels = " + topicModels);
    }
}
