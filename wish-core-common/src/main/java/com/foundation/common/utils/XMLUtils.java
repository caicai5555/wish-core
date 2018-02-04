package com.foundation.common.utils;

import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.foundation.common.bean.Constants;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSONObject;

/**
 * 
* @ClassName: XMLUtils 
* @Description: XML操作类（xsd）
* @author chengchen 
* @date 2016年10月17日 下午5:17:03 
*
 */
public class XMLUtils {

	/**
	 * 
	* @Title: validatorXML 
	* @Description: 验证xml是否符合xsd的规范
	* @author chengchen
	* @date 2016年10月17日 下午5:17:30 
	* @param @param inputStream
	* @param @return
	* @param @throws Exception    设定参数 
	* @return JSONObject    返回类型 
	* @throws
	 */
	public static JSONObject validatorXML(InputStream inputStream,String xsdUrl) throws Exception {
		JSONObject result = new JSONObject();
		if (StringUtils.isBlank(xsdUrl)) {
			result.put(Constants.JsonKey.SUCCESS, false);
			result.put(Constants.JsonKey.MSG, "xsd path is empty!");
			return result;
		}
		URL url = XMLUtils.class.getResource(xsdUrl);
		try {
			Source xmlFile = new StreamSource(inputStream);
			SchemaFactory schemaFactory = SchemaFactory.newInstance(Constants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(url);
			Validator validator = schema.newValidator();
			try {
				validator.validate(xmlFile);
				result.put(Constants.JsonKey.SUCCESS, true);
			} catch (SAXException e) {
				result.put(Constants.JsonKey.SUCCESS, false);
				result.put(Constants.JsonKey.MSG, e.getLocalizedMessage());
			}
		} catch (Exception e1) {
			throw new Exception("XMLUtil.validatorXML error.", e1);
		} finally {
			if (null != inputStream) {
				inputStream.close();
			}
		}
		return result;
	}

	/**
	 * 
	* @Title: xml2Obj 
	* @Description: xml对象转换为实体对象
	* @author chengchen
	* @date 2016年10月17日 下午5:24:45 
	* @param @param packageName
	* @param @param inputStream
	* @param @return
	* @param @throws Exception    设定参数 
	* @return Object    返回类型 
	* @throws
	 */
	public static Object xml2Obj(String packageName, InputStream inputStream) throws Exception {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(packageName);
			Unmarshaller u = jc.createUnmarshaller();
			return u.unmarshal(inputStream);
		} catch (Exception e) {
			throw new Exception("XMLUtil.xml2Obj error.", e);
		} finally {
			if (null != inputStream) {
				inputStream.close();
			}
		}
	}
}
