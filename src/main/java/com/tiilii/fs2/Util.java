package com.tiilii.fs2;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.Properties;

/**
 * Created by hunter on 2017/7/5.
 */
public class Util {
    /**
     * 取得项目路径
     *
     * @return
     */
    public static String getRootPath(){
        String rootPath = Util.class.getResource("/").getPath();
        String rootPath2 = rootPath.substring(1, rootPath.length());
        String rootPath3 = rootPath2.replace("/WEB-INF/classes", "");
        return rootPath3;
    }

    /**
     * 获取系统system.properties属性值
     * @param key
     * @return
     */
    public static String getSystemProperty(String key) {
        Properties props = null;
        String value = null;
        try {
            Resource resource = new ClassPathResource("../config/system.properties");
            props = PropertiesLoaderUtils.loadProperties(resource);
            value = props.getProperty(key);
            if (value == null) value = "";
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
