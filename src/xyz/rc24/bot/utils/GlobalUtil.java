package xyz.rc24.bot.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class GlobalUtil {
    public static void saveData(String type, Map data) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            String path = "data" + File.separator + type + ".yml";
            FileOutputStream output = new FileOutputStream(new File(path), false);
            mapper.writerWithDefaultPrettyPrinter().writeValue(output, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
