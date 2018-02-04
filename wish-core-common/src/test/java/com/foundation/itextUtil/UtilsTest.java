/*
package com.foundation.itextUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.foundation.common.itextUtils.Base64Utils;
import com.foundation.common.itextUtils.CustomFont;
import com.foundation.common.itextUtils.ItextUtils;
import com.foundation.common.itextUtils.WaterMarks;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

public class UtilsTest {

	//文档生成测试
	@Test
	public void test1() throws DocumentException, IOException {
		// 默认中文字体
		BaseFont font = CustomFont.getDefaultFont();
		// 默认字体大小、样式
		Font normalFont = new Font(font, 10, Font.NORMAL);
		//统一段落标题字体
		Font pHeaderFont = new Font(font, 20, Font.BOLD);
		//统一节标题字体
		Font pSectionFont = new Font(font, 14, Font.BOLD);

		Document doc = ItextUtils.createNewPdf("标题", "fqh", "测试");

		PdfWriter writer = null;

		try {
			writer = PdfWriter.getInstance(doc, new BufferedOutputStream(new FileOutputStream(new File("c:\\test.pdf"))));
		} catch (FileNotFoundException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		doc.open();
		//文档标题
		ItextUtils.addHeader(doc, "测试", 20);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 500; i++) {
			sb.append(UUID.randomUUID());
		}
		//添加内容
		doc.add(new Phrase("测试文档，哈哈哈呵呵", normalFont));
		//添加水印
		new WaterMarks("test.pdf", "云谷内部文档", 120, 45);
		doc.close();
	}

	*/
/**
	 * 图片生成测试
	 *
	 * @throws InterruptedException
	 *//*

	@Test
	public void test2() throws InterruptedException {
		WebDriver driver = new InternetExplorerDriver();
		driver.get("http://localhost:8888/line.html");
		WebElement e = driver.findElement(By.tagName("body"));
		Base64Utils.base2Img(e.getText(), "d:/javaide/12345.png");
		driver.close();
	}
}
	*/
