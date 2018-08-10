package com.thinkwin.common.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: yinchunlei
 * Date: 2017/8/2.
 * Company: thinkwin
 * 修改导入功能辅助类
 */
public class ReadExcel {

    private static Workbook wb;
    private static Sheet sheet;
    private static Row row;

    /**
     * 读取Excel表格表头的内容
     *
     * @return String 表头内容的数组
     */
    public static String[] readExcelTitle(String path) {
        try {
            if(StringUtils.isNotBlank(path)) {
                wb = loadWorkbook(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if(null == wb){
            return null;
        }
        //获取第0个sheet页
        sheet = wb.getSheetAt(0);
        if(null == sheet){
            return null;
        }
        //读取第二行表头(需要根据模板的样式决定在第几行开始取值)
        row = sheet.getRow(2);
        if(null == row){
            return null;
        }
        // 标题总列数
        int colNum = row.getPhysicalNumberOfCells();
        String[] title = new String[colNum];
        for (int i = 0; i < colNum; i++) {
            title[i] = getCellFormatValue(row.getCell((short) i));
        }
        return title;
    }


    /**
     * 根据表头内容和定义好的变量和中文map，进行变量名的匹配
     *
     * @param path
     * @param tabelHead
     * @return
     * @throws Exception
     */
    public static String[] impotrHead(String path, Map<String, String> tabelHead) throws Exception {
        if(StringUtils.isNotBlank(path)) {
            String[] ss = readExcelTitle(path);
            if(null != ss && ss.length > 0){
            String headName = "";
            boolean flag = true;
            for (int i = 0; i < ss.length; i++) {
                if (flag) {
                    flag = false;
                    for (Map.Entry<String, String> entry : tabelHead.entrySet()) {
                        if (ss[i].equals(entry.getKey())) {
                            if (headName.equals("")) {
                                headName = entry.getValue();
                            } else {
                                headName = headName + "," + entry.getValue();
                            }
                            flag = true;
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
            if (flag) {
                String[] fieldNames = headName.split(",");
                return fieldNames;
            }
        }
        }
        return null;

    }

    /**
     * 读取Excel数据内容
     *
     * @return Map 包含单元格数据内容的Map对象
     */
    public static Map<Integer, String> readExcelContent(String path) {
        Map<Integer, String> content = new HashMap<Integer, String>();
        String str = "";
        try {
            wb = loadWorkbook(path);
        } catch (Exception e) {
            e.printStackTrace();
            return content;
        }
        sheet = wb.getSheetAt(0);
        // 得到总行数
        int rowNum = sheet.getLastRowNum();
        row = sheet.getRow(0);
        int colNum = row.getPhysicalNumberOfCells();
        // 正文内容应该从第四行开始,第一行为表头的标题（i决定在第几行开始）
        for (int i = 3; i <= rowNum; i++) {
            row = sheet.getRow(i);
            int j = 0;
            while (j < colNum) {
                // 每个单元格的数据内容用"-"分割开，以后需要时用String类的replace()方法还原数据
                // 也可以将每个单元格的数据设置到一个javabean的属性中，此时需要新建一个javabean
                // str += getStringCellValue(row.getCell((short) j)).trim() +
                // "-";
                str += getCellFormatValue(row.getCell((short) j)).trim() + ",";
                j++;
            }
            content.put(i, str.substring(0, str.length() - 1));
            str = "";
        }
        return content;
    }

    /**
     * 根据HSSFCell类型设置数据
     *
     * @param cell
     * @return
     */
    private static String getCellFormatValue(Cell cell) {
        String cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
                // 如果当前Cell的Type为NUMERIC
                case Cell.CELL_TYPE_NUMERIC: {
                    // 取得当前的Cell字符串,数值型以字符串方便处理，处理科学计数问题
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;
                }
                // 如果当前Cell的Type为STRIN
                case Cell.CELL_TYPE_STRING:
                    // 取得当前的Cell字符串
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;
                // 默认的Cell值
                default:
                    cellvalue = " ";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;

    }


    protected static <T> Object parseValueWithType(String value, Class<?> type) {
        Object result = null;
        try { // 根据属性的类型将内容转换成对应的类型
            if (Boolean.TYPE == type) {
                result = Boolean.parseBoolean(value);
            } else if (Byte.TYPE == type) {
                result = Byte.parseByte(value);
            } else if (Short.TYPE == type) {
                result = Short.parseShort(value);
            } else if (Integer.TYPE == type) {
                result = Integer.parseInt(value);
            } else if (Long.TYPE == type) {
                result = Long.parseLong(value);
            } else if (Float.TYPE == type) {
                result = Float.parseFloat(value);
            } else if (Double.TYPE == type) {
                result = Double.parseDouble(value);
            } else if (Class.forName("java.lang.Double") == type) {
                result = Double.parseDouble(value);
            } else {
                result = (Object) value;
            }
        } catch (Exception e) {
            // 把异常吞掉直接返回null
        }
        return result;
    }


    public static <T> List<T> readExcels(Class<T> clazz, Map<String, String> oldMap, String[] fieldNames, Map<Integer, String> excelMaps) throws Exception {
        List<T> dataModels = new ArrayList<T>();
        for (int i = 0; i < excelMaps.size(); i++) {
            T target = clazz.newInstance();
            //此处是为了去除导入excel的头做对比（根据模板决定get的行数）
            String s = excelMaps.get(i+3);
            if(StringUtils.isNotBlank(s)) {
                String row[] = s.split(",");
                for (int j = 0; j < fieldNames.length; j++) {
                    String fieldName = fieldNames[j];
                    if (fieldName == null) {
                        continue; // 过滤serialVersionUID属性
                    }
                    if (j == row.length) {
                        break;
                    }
                    // 获取excel单元格的内容
                    String cell = row[j];
                    String content = "";
                    if (cell != null) {
                        content = cell;
                    }
				/*	target 指定对象
					fieldName 属性名
					argTypes 参数类型
					args 参数列表*/
                    Field field = clazz.getDeclaredField(fieldName);
                    invokeSetter(target, fieldName,
                            parseValueWithType(content, field.getType()));
                }
            }
            dataModels.add(target);
        }
        return dataModels;
    }

    public static <T> void invokeSetter(T target, String fieldName, Object args)
            throws NoSuchFieldException, SecurityException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        // 如果属性名为xxx，则方法名为setXxx
        String methodName = "set" + firstCharUpperCase(fieldName);
        Class<?> clazz = target.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        Method method = clazz.getMethod(methodName, field.getType());
        method.invoke(target, args);
    }

    public static String firstCharUpperCase(String str) {
        StringBuffer buffer = new StringBuffer(str);
        if (buffer.length() > 0) {
            char c = buffer.charAt(0);
            buffer.setCharAt(0, Character.toUpperCase(c));
        }
        return buffer.toString();
    }

    public static Workbook loadWorkbook(String fileName) throws Exception {
        if (null == fileName) {
            return null;
        }
        Workbook wb = null;
        //该判断是为了判断格式是否符合规定的格式
        if (fileName.toLowerCase().endsWith(".xls")) {
            try {
                InputStream in = new FileInputStream(fileName);
                POIFSFileSystem fs = new POIFSFileSystem(in);
                wb = new HSSFWorkbook(fs);
                in.close();
            } catch (Exception e) {
                wb = null;
                e.printStackTrace();
            }
        } else if (fileName.toLowerCase().endsWith(".xlsx")) {
            try {
                wb = new XSSFWorkbook(fileName);
            } catch (Exception e) {
                wb = null;
                e.printStackTrace();
            }
        }
        return wb;
    }
}
