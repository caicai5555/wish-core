package com.foundation.common.excel;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel数据读取工具类，POI实现，兼容Excel2003，及Excel2007
 * <P>
 * File name : ExcelReaderUtil.java
 * </P>
 * <P>
 * Author : chengchen
 * </P>
 * <P>
 * Date : 2015年12月10日
 * </P>
 */
public class ExcelReaderUtil {
	private static final Logger LOGGER = Logger.getLogger(ExcelReaderUtil.class);
	Workbook wb = null;
	List<String[]> dataList = new ArrayList<String[]>(100);

	/**
	 * 
	 * 构造函数
	 *
	 * @param path
	 */
	public ExcelReaderUtil(String path) {
		try {
			InputStream inp = new FileInputStream(path);
			wb = WorkbookFactory.create(inp);
		} catch (FileNotFoundException e) {
			LOGGER.error("文件没找到", e);
		} catch (InvalidFormatException e) {
			LOGGER.error("无效格式", e);
		} catch (IOException e) {
			LOGGER.error("IO异常", e);
		}
	}

	/**
	 * 
	 * 构造函数
	 *
	 * @param inp
	 */
	public ExcelReaderUtil(InputStream inp) {
		try {
			wb = WorkbookFactory.create(inp);
		} catch (FileNotFoundException e) {
			LOGGER.error("文件没找到", e);
		} catch (InvalidFormatException e) {
			LOGGER.error("无效格式", e);
		} catch (IOException e) {
			LOGGER.error("IO异常", e);
		}
	}

	/**
	 * 取Excel所有数据，包含header ExcelReaderUtil.getAllData()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2015年12月10日
	 * </P>
	 * 
	 * @param sheetIndex
	 * @return
	 */
	public List<String[]> getAllData(int sheetIndex) {
		int columnNum = 0;
		Sheet sheet = wb.getSheetAt(sheetIndex);
		if (sheet.getRow(0) != null) {
			columnNum = sheet.getRow(0).getLastCellNum() - sheet.getRow(0).getFirstCellNum();
		}
		if (columnNum > 0) {
			for (Row row : sheet) {
				String[] singleRow = new String[columnNum];
				int n = 0;
				for (int i = 0; i < columnNum; i++) {
					Cell cell = row.getCell(i, Row.CREATE_NULL_AS_BLANK);
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BLANK:
						singleRow[n] = "";
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						singleRow[n] = Boolean.toString(cell.getBooleanCellValue());
						break;
					// 数值
					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
							singleRow[n] = String.valueOf(cell.getDateCellValue());
						} else {
							cell.setCellType(Cell.CELL_TYPE_STRING);
							String temp = cell.getStringCellValue();
							// 判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串
							if (temp.indexOf(".") > -1) {
								singleRow[n] = String.valueOf(new Double(temp)).trim();
							} else {
								singleRow[n] = temp.trim();
							}
						}
						break;
					case Cell.CELL_TYPE_STRING:
						singleRow[n] = cell.getStringCellValue().trim();
						break;
					case Cell.CELL_TYPE_ERROR:
						singleRow[n] = "";
						break;
					case Cell.CELL_TYPE_FORMULA:
						cell.setCellType(Cell.CELL_TYPE_STRING);
						singleRow[n] = cell.getStringCellValue();
						if (singleRow[n] != null) {
							singleRow[n] = singleRow[n].replaceAll("#N/A", "").trim();
						}
						break;
					default:
						singleRow[n] = "";
						break;
					}
					n++;
				}
				if ("".equals(singleRow[0])) {
					continue;
				} // 如果第一行为空，跳过
				dataList.add(singleRow);
			}
		}
		return dataList;
	}

	/**
	 * 返回Excel最大行index值，实际行数要加1 ExcelReaderUtil.getRowNum()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2015年12月10日
	 * </P>
	 * 
	 * @param sheetIndex
	 * @return
	 */
	public int getRowNum(int sheetIndex) {
		Sheet sheet = wb.getSheetAt(sheetIndex);
		return sheet.getLastRowNum();
	}

	/**
	 * 返回数据的列数 ExcelReaderUtil.getColumnNum()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2015年12月10日
	 * </P>
	 * 
	 * @param sheetIndex
	 * @return
	 */
	public int getColumnNum(int sheetIndex) {
		Sheet sheet = wb.getSheetAt(sheetIndex);
		Row row = sheet.getRow(0);
		if (row != null && row.getLastCellNum() > 0) {
			return row.getLastCellNum();
		}
		return 0;
	}

	/**
	 * 获取某一行数据 ExcelReaderUtil.getRowData()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2015年12月10日
	 * </P>
	 * 
	 * @param sheetIndex
	 * @param rowIndex
	 * @return
	 */
	public String[] getRowData(int sheetIndex, int rowIndex) {
		String[] dataArray = null;
		if (rowIndex > this.getColumnNum(sheetIndex)) {
			return dataArray;
		} else {
//			dataArray = new String[this.getColumnNum(sheetIndex)];
			return this.dataList.get(rowIndex);
		}

	}

	/**
	 * 获取某一列数据 ExcelReaderUtil.getColumnData()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2015年12月10日
	 * </P>
	 * 
	 * @param sheetIndex
	 * @param colIndex
	 * @return
	 */
	public String[] getColumnData(int sheetIndex, int colIndex) {
		String[] dataArray = null;
		if (colIndex > this.getColumnNum(sheetIndex)) {
			return dataArray;
		} else {
			if (this.dataList != null && this.dataList.size() > 0) {
				dataArray = new String[this.getRowNum(sheetIndex) + 1];
				int index = 0;
				for (String[] rowData : dataList) {
					if (rowData != null) {
						dataArray[index] = rowData[colIndex];
						index++;
					}
				}
			}
		}
		return dataArray;

	}
}
