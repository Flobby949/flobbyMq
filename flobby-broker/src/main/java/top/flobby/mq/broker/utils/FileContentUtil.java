package top.flobby.mq.broker.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 简化版本的文件读写工具
 * @create : 2024-06-12 10:40
 **/

public class FileContentUtil {

    /**
     * 读取文件内容
     *
     * @param filePath 文件路径
     * @return {@link String }
     */
    public static String readFromFile(String filePath) {
        try (BufferedReader in = new BufferedReader(new FileReader(filePath))) {
            StringBuffer sb = new StringBuffer();
            while (in.ready()) {
                sb.append(in.readLine());
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件覆盖写入
     *
     * @param filePath 文件路径
     * @param content  内容
     */
    public static void overwriteToFile(String filePath, String content) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
