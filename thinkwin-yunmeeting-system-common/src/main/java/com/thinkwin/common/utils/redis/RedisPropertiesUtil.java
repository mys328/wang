package com.thinkwin.common.utils.redis;

import com.thinkwin.common.utils.*;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;

import java.io.*;
import java.util.*;

public class RedisPropertiesUtil {
    private static final CompositeConfiguration config = new CompositeConfiguration();
    private String fileName;
    private Properties p;
    private FileInputStream in;
    private FileOutputStream out;

    static {
        config.addConfiguration(new SystemConfiguration());
        try {
            config.addConfiguration(new PropertiesConfiguration("redis.properties"));
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static String getStrValue(String key) {
        return config.getString(key);
    }

    public static String getString(String key) {
        return config.getString(key);
    }

    public static String getString(String key, String defaultValue) {
        return config.getString(key, defaultValue);
    }

    public static void set(String key, String value) {
        config.setProperty(key, value);
    }

    public static int getInt(String key) {
        return config.getInt(key);
    }

    public static int getInt(String key, int defaultValue) {
        return config.getInt(key, defaultValue);
    }

    public static Integer getInteger(String key, Integer defaultValue) {
        return config.getInteger(key, defaultValue);
    }

    public static long getLong(String key) {
        return config.getLong(key);
    }

    public static long getLong(String key, long defaultValue) {
        return config.getLong(key, defaultValue);
    }

    public static Long getLong(String key, Long defaultValue) {
        return config.getLong(key, defaultValue);
    }

    public static String[] getStringArray(String key) {
        return config.getStringArray(key);
    }

    public static List getList(String key) {
        return config.getList(key);
    }

    public static float getFloat(String key) {
        return config.getFloat(key);
    }

    public static float getFloat(String key, float defaultValue) {
        return config.getFloat(key, defaultValue);
    }

    public static Float getFloat(String key, Float defaultValue) {
        return config.getFloat(key, defaultValue);
    }

    public static double getDouble(String key) {
        return config.getDouble(key);
    }

    public static double getDouble(String key, double defaultValue) {
        return config.getDouble(key, defaultValue);
    }

    public static Double getDouble(String key, Double defaultValue) {
        return config.getDouble(key, defaultValue);
    }

    public static Properties fromMap(Map map) {
        if ((map instanceof Properties)) {
            return (Properties) map;
        }

        Properties p = new Properties();

        Iterator itr = map.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();

            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            if (value != null) {
                p.setProperty(key, value);
            }
        }

        return p;
    }

    public static void copyProperties(Properties from, Properties to) {
        Iterator itr = from.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            to.setProperty((String) entry.getKey(), (String) entry.getValue());
        }
    }

    public static void fromProperties(Properties p, Map map) {
        map.clear();
        Iterator itr = p.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            map.put(entry.getKey(), entry.getValue());
        }
    }

    public static Properties load(String s) throws IOException {
        Properties p = new Properties();
        load(p, s);
        return p;
    }

    public static void load(Properties p, String s) throws IOException {
        if (Validator.isNotNull(s)) {
            s = UnicodeFormatter.toString(s);
            s = StringUtil.replace(s, "\\u003d", "=");
            s = StringUtil.replace(s, "\\u000a", "\n");
            s = StringUtil.replace(s, "\\u0021", "!");
            s = StringUtil.replace(s, "\\u0023", "#");
            s = StringUtil.replace(s, "\\u0020", " ");
            s = StringUtil.replace(s, "\\u005c", "\\");

            p.load(new ByteArrayInputStream(s.getBytes()));

            List propertyNames = Collections.list(p.propertyNames());

            for (int i = 0; i < propertyNames.size(); i++) {
                String key = (String) propertyNames.get(i);
                String value = p.getProperty(key);

                if (value != null) {
                    value = value.trim();
                    p.setProperty(key, value);
                }
            }
        }
    }

    /**
     * @deprecated
     */
    private static void merge(Properties p1, Properties p2) {
        Enumeration enu = p2.propertyNames();

        while (enu.hasMoreElements()) {
            String key = (String) enu.nextElement();
            String value = p2.getProperty(key);
            p1.setProperty(key, value);
        }
    }

    private static String list(Map map) {
        Properties props = fromMap(map);
        ByteArrayMaker bam = new ByteArrayMaker();
        PrintStream ps = new PrintStream(bam);
        props.list(ps);
        return bam.toString();
    }

    private static void list(Map map, PrintStream out) {
        Properties props = fromMap(map);
        props.list(out);
    }

    private static void list(Map map, PrintWriter out) {
        Properties props = fromMap(map);
        props.list(out);
    }

    public static String toString(Properties p) {
        StringMaker sm = new StringMaker();
        Enumeration enu = p.propertyNames();
        while (enu.hasMoreElements()) {
            String key = (String) enu.nextElement();
            sm.append(key);
            sm.append("=");
            sm.append(p.getProperty(key));
            sm.append("\n");
        }
        return sm.toString();
    }

    public static void trimKeys(Properties p) {
        Enumeration enu = p.propertyNames();
        while (enu.hasMoreElements()) {
            String key = (String) enu.nextElement();
            String value = p.getProperty(key);
            String trimmedKey = key.trim();
            if (!key.equals(trimmedKey)) {
                p.remove(key);
                p.setProperty(trimmedKey, value);
            }
        }
    }

    public RedisPropertiesUtil(String fileName) {
        this.fileName = fileName;
        File file = new File(fileName);
        try {
            this.in = new FileInputStream(file);
            this.p = new Properties();

            this.p.load(this.in);
            this.in.close();
        } catch (FileNotFoundException e) {
            System.err.println("配置文件没有找到！");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("读取配置文件错误！");
            e.printStackTrace();
        }
    }

    public void list() {
        this.p.list(System.out);
    }

    public String getValue(String itemName) {
        return this.p.getProperty(itemName);
    }

    public String getValue(String itemName, String defaultValue) {
        return this.p.getProperty(itemName, defaultValue);
    }

    public void setValue(String itemName, String value) {
        this.p.setProperty(itemName, value);
    }

    public void saveFile(String fileName, String description) throws Exception {
        try {
            File f = new File(fileName);
            this.out = new FileOutputStream(f);
            this.p.store(this.out, description);
            this.out.close();
        } catch (IOException ex) {
            throw new Exception("无法保存指定的配置文件:" + fileName);
        }
    }

    public void saveFile(String fileName) throws Exception {
        saveFile(fileName, "");
    }

    public void saveFile() throws Exception {
        if (this.fileName.length() == 0) throw new Exception("需指定保存的配置文件名");
        saveFile(this.fileName);
    }

    public void deleteValue(String itemName) {
        this.p.remove(itemName);
    }
}
