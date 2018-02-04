package com.foundation.common.excel;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;

/**
 * @author 作者 qinghuifan@bioeh.com:
 * @author qinghuifan
 * @version 创建时间：2016年3月9日 下午2:30:20
 * @dec Excel导出工具类
 */
public class ExcelWriter {

    private static Logger loggerError = LoggerFactory.getLogger("error");
    /**
     * 日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    private String title;
    private String[] headers;
    private String[] keys;
    private Collection<?> data;
    private String version=ExcelUtils.EXCEL_VERSION_XLS;//默认
    // 必填列，控制样式标红
    private Set<String> requiredKeys = new HashSet<>();
    // 为日期类型字段指定格式的Map
    private Map<String, String> formateDate;

    private Map<String, Method> getterCache;
    private Class<?>[] paramTypes = new Class<?>[]{};
    private Object[] callingArgs = new Object[]{};

    //多Sheet工作薄列表
    List<MutlSheetData> muiltSheet=null;
    private Map<String, Object> mapData;

    /**
     * 创建Excel输出类(默认xlsx)
     *
     * @param title   sheet的标题
     * @param headers 标题行
     * @param keys    列对应的field
     * @param data    数据
     */
    public ExcelWriter(String[] sheetTitle, List<String[]> headers,List<String[]> keys,List<List<Map<String, Object>>> datas) {
        muiltSheet=Lists.newArrayList();
        for (int i=0;i<sheetTitle.length;i++){
            MutlSheetData sheetData=new MutlSheetData(sheetTitle[i],headers.get(i),keys.get(i),datas.get(i));
            muiltSheet.add(sheetData);
        }
    }

    /**
     * 创建Excel输出类(默认xlsx)
     *
     * @param title   sheet的标题
     * @param headers 标题行
     * @param keys    列对应的field
     * @param data    数据
     */
    public ExcelWriter(String title, String[] headers, String[] keys, Collection<?> data) {
        this(title, headers, keys, data, ExcelUtils.EXCEL_VERSION_XLS);
    }


    /**
     * 创建Excel输出类(指定是xls还是xlsx)
     *
     * @param title   sheet的标题
     * @param headers 标题行
     * @param keys    列对应的field
     * @param data    数据
     * @param version 版本
     */
    public ExcelWriter(String title, String[] headers, String[] keys, Collection<?> data, String version) {
        this.title = title;
        this.headers = headers;
        this.keys = keys;
        this.data = data;
        this.version = version;
        getterCache = new HashMap<>();
    }


    /**
     * 直接返回输入流
     *
     * @return
     */
    public InputStream newInputStream() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writeTo(bos);
        return new ByteArrayInputStream(bos.toByteArray());
    }

    /**
     * 直接返回输入流
     *
     * @return
     */
    public InputStream newMultSheetInputStream() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writeMultSheetTo(bos);
        return new ByteArrayInputStream(bos.toByteArray());
    }

    /**
     * 输出到指定的输出流
     *
     * @param os
     */
    public void writeTo(OutputStream os) {
        try {
            generateWorkbook().write(os);
        } catch (IOException e) {
            loggerError.error("导出Excel时出现IO异常:", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 输出到指定的输出流
     *
     * @param os
     */
    public void writeMultSheetTo(OutputStream os) {
        try {
            generateMultSheelWorkbook().write(os);
        } catch (Exception e) {
            loggerError.error("导出Excel时出现异常:", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 返回Workbook对象，便于调用者进行个性化定制
     *
     * @return
     */
    public Workbook generateMultSheelWorkbook() throws Exception{
        if(muiltSheet==null){
            throw new Exception("导出多Sheet 数据为空，请检查!");
        }
        Workbook workbook = newWorkbook();
        Font headerFont = workbook.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 10);

        Font requiredHeaderFont = workbook.createFont();
        requiredHeaderFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        requiredHeaderFont.setFontName("Arial");
        requiredHeaderFont.setColor(Font.COLOR_RED);
        requiredHeaderFont.setFontHeightInPoints((short) 10);

        Font plainTextFont = workbook.createFont();
        plainTextFont.setFontName("Arial");
        plainTextFont.setFontHeightInPoints((short) 10);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);

        CellStyle requiredHeaderStyle = workbook.createCellStyle();
        requiredHeaderStyle.setFont(requiredHeaderFont);

        CellStyle numStyle = workbook.createCellStyle();
        numStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));// (自定义货币)格式
        numStyle.setAlignment(CellStyle.ALIGN_RIGHT);// 右对齐
        numStyle.setFont(plainTextFont);

        CellStyle numNotDotStyle = workbook.createCellStyle();
        numNotDotStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));// (数字类型
        numNotDotStyle.setAlignment(CellStyle.ALIGN_RIGHT);// 右对齐
        numNotDotStyle.setFont(plainTextFont);

        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setFont(plainTextFont);

        for(MutlSheetData st:muiltSheet){
            Sheet sheet = workbook.createSheet(st.getSheetTitle());
            sheet.setDefaultColumnWidth(30);
            // 标题行
            Row row = sheet.createRow(0);
            for (int i = 0; i < st.getHeaders().length; i++) {
                Cell cell = row.createCell(i);
                /*if (requiredKeys.contains(st.getKeys()[i]))//标头列标红
                    cell.setCellStyle(requiredHeaderStyle);
                else*/
                cell.setCellStyle(headerStyle);
                cell.setCellValue(st.getHeaders()[i]);
            }

            int index = 1;
            if(CollectionUtils.isNotEmpty(st.getData())){
                for(int i=0;i<st.getData().size();i++){
                    row = sheet.createRow(index);
                    try {
                        fillMuiltRow(st, row, st.getData().get(i), index++, textStyle, numStyle, numNotDotStyle);
                    } catch (ReflectiveOperationException e) {
                        loggerError.error("导出Excel时出现反射调用异常:", e);
                    }
                }
            }
        }
        return workbook;
    }







    /**
     * 返回Workbook对象，便于调用者进行个性化定制
     *
     * @return
     */
    public Workbook generateWorkbook() {
        Workbook workbook = newWorkbook();
        Sheet sheet = workbook.createSheet(title);
        sheet.setDefaultColumnWidth(30);

        Font headerFont = workbook.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 10);

        Font requiredHeaderFont = workbook.createFont();
        requiredHeaderFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        requiredHeaderFont.setFontName("Arial");
        requiredHeaderFont.setColor(Font.COLOR_RED);
        requiredHeaderFont.setFontHeightInPoints((short) 10);

        Font plainTextFont = workbook.createFont();
        plainTextFont.setFontName("Arial");
        plainTextFont.setFontHeightInPoints((short) 10);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);

        CellStyle requiredHeaderStyle = workbook.createCellStyle();
        requiredHeaderStyle.setFont(requiredHeaderFont);

        CellStyle numStyle = workbook.createCellStyle();
        numStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));// (自定义货币)格式
        numStyle.setAlignment(CellStyle.ALIGN_RIGHT);// 右对齐
        numStyle.setFont(plainTextFont);

        CellStyle numNotDotStyle = workbook.createCellStyle();
        numNotDotStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));// (数字类型
        numNotDotStyle.setAlignment(CellStyle.ALIGN_RIGHT);// 右对齐
        numNotDotStyle.setFont(plainTextFont);

        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setFont(plainTextFont);

        // 标题行
        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            if (requiredKeys.contains(keys[i]))
                cell.setCellStyle(requiredHeaderStyle);
            else
                cell.setCellStyle(headerStyle);
            cell.setCellValue(headers[i]);
        }

        Iterator<?> it = data.iterator();
        int index = 1;
        while (it.hasNext()) {
            row = sheet.createRow(index);
            try {
                fillRow(row, it.next(), index++, textStyle, numStyle, numNotDotStyle);
            } catch (ReflectiveOperationException e) {
                loggerError.error("导出Excel时出现反射调用异常:", e);
            }
        }
        return workbook;
    }

    private Row fillRow(Row row, Object o, int rowIndex, CellStyle cellStyle, CellStyle numCellStyle,
                        CellStyle numNotDotStyle) throws ReflectiveOperationException {
        int index = 0;
        for (String key : keys) {
            Cell cell = row.createCell(index++);
            Object fieldValue = "";

            if (o instanceof Map) {
                fieldValue = ((Map<?, ?>) o).get(key);
            } else if (o instanceof Object) {
                Method method = null;
                if (rowIndex == 1) {
                    method = o.getClass().getMethod("get" + Character.toUpperCase(key.charAt(0)) + key.substring(1), paramTypes);
                    getterCache.put(key, method);
                } else {
                    method = getterCache.get(key);
                }
                fieldValue = method.invoke(o, callingArgs);
            }

            cell.setCellStyle(cellStyle);
            if (fieldValue == null) {
                cell.setCellValue("");
            } else {
                if (fieldValue instanceof Number) {// 数字
                    if (fieldValue instanceof BigDecimal || fieldValue instanceof Double || fieldValue instanceof Float) {
                        cell.setCellStyle(numCellStyle);
                    } else if (fieldValue instanceof BigInteger || fieldValue instanceof Integer) {
                        cell.setCellStyle(numNotDotStyle);
                    }
                    cell.setCellValue(Double.valueOf(fieldValue.toString()));
                } else if (fieldValue instanceof Date) {// 日期
                    if (formateDate != null && formateDate.get(key) != null) {
                        cell.setCellValue(DateUtils.formatDate((Date) fieldValue, formateDate.get(key)));
                    } else {
                        cell.setCellValue(DateUtils.formatDate((Date) fieldValue, this.DEFAULT_DATE_FORMAT));
                    }
                } else {// 其他为字符串
                    if (StringUtils.isNotEmpty(fieldValue.toString()) && formateDate != null && formateDate.containsKey(key)) {
                        try {
                            Date date = org.apache.commons.lang.time.DateUtils.parseDate(fieldValue.toString(),
                                    ExcelUtils.PARSEPATTERNS);
                            cell.setCellValue(DateUtils.formatDate(date, formateDate.get(key)));
                        } catch (ParseException e) {
                            loggerError.error("无法将数据转换为日期格式：" + fieldValue.toString());
                            cell.setCellValue(fieldValue.toString());
                        }
                    } else {
                        cell.setCellValue(fieldValue.toString());
                    }
                }
            }
        }
        return row;
    }

    private Row fillMuiltRow(MutlSheetData st,Row row, Map<String,Object> o, int rowIndex, CellStyle cellStyle, CellStyle numCellStyle,
                             CellStyle numNotDotStyle) throws ReflectiveOperationException {
        int index = 0;
        for (String key : st.getKeys()) {
            Cell cell = row.createCell(index++);
            Object fieldValue = "";

            if (o instanceof Map) {
                fieldValue = ((Map<?, ?>) o).get(key);
            } else if (o instanceof Object) {
                Method method = null;
                if (rowIndex == 1) {
                    method = o.getClass().getMethod("get" + Character.toUpperCase(key.charAt(0)) + key.substring(1), paramTypes);
                    getterCache.put(key, method);
                } else {
                    method = getterCache.get(key);
                }
                fieldValue = method.invoke(o, callingArgs);
            }

            cell.setCellStyle(cellStyle);
            if (fieldValue == null) {
                cell.setCellValue("");
            } else {
                if (fieldValue instanceof Number) {// 数字
                    if (fieldValue instanceof BigDecimal || fieldValue instanceof Double || fieldValue instanceof Float) {
                        cell.setCellStyle(numCellStyle);
                    } else if (fieldValue instanceof BigInteger || fieldValue instanceof Integer) {
                        cell.setCellStyle(numNotDotStyle);
                    }
                    cell.setCellValue(Double.valueOf(fieldValue.toString()));
                } else if (fieldValue instanceof Date) {// 日期
                    if (formateDate != null && formateDate.get(key) != null) {
                        cell.setCellValue(DateUtils.formatDate((Date) fieldValue, formateDate.get(key)));
                    } else {
                        cell.setCellValue(DateUtils.formatDate((Date) fieldValue, this.DEFAULT_DATE_FORMAT));
                    }
                } else {// 其他为字符串
                    if (StringUtils.isNotEmpty(fieldValue.toString()) && formateDate != null && formateDate.containsKey(key)) {
                        try {
                            Date date = org.apache.commons.lang.time.DateUtils.parseDate(fieldValue.toString(),
                                    ExcelUtils.PARSEPATTERNS);
                            cell.setCellValue(DateUtils.formatDate(date, formateDate.get(key)));
                        } catch (ParseException e) {
                            loggerError.error("无法将数据转换为日期格式：" + fieldValue.toString());
                            cell.setCellValue(fieldValue.toString());
                        }
                    } else {
                        cell.setCellValue(fieldValue.toString());
                    }
                }
            }
        }
        return row;
    }


    private Workbook newWorkbook() {
        switch (version) {
            case ExcelUtils.EXCEL_VERSION_XLS:
                return new HSSFWorkbook();
            default:
                return new SXSSFWorkbook();//SXSSFWorkbook大数据量excel写入操作，
            // return new XSSFWorkbook();
        }
    }

    public void setRequiredKeys(Set<String> requiredKeys) {
        this.requiredKeys = requiredKeys;
    }

    public void setRequiredKeys(String... keys) {
        Collections.addAll(requiredKeys, keys);
    }

    public void setFormateDate(Map<String, String> formateDate) {
        this.formateDate = formateDate;
    }
}

class MutlSheetData{
    private String sheetTitle;//工作薄title
    private String[] headers;//工作簿数据头
    private String[] keys;//工作簿数据头
    private List<Map<String, Object>> data;//工作薄列

    public String getSheetTitle() {
        return sheetTitle;
    }

    public void setSheetTitle(String sheetTitle) {
        this.sheetTitle = sheetTitle;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    public MutlSheetData(String sheetTitle, String[] headers,String[] keys, List<Map<String, Object>> data) {
        this.sheetTitle = sheetTitle;
        this.headers = headers;
        this.keys=keys;
        this.data = data;
    }
}
