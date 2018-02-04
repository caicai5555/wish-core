package com.foundation.common.bean;

import org.apache.commons.lang.StringUtils;

/**
 * @author fqh
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}(用一句话描述该文件做什么)
 * @date ${date} ${time}
 */
public interface Constants {

    /**
     * 删除标记（0：正常；1：删除；2：审核；）
     */
    public static final Integer DEL_FLAG_NORMAL = 0;
    public static final Integer DEL_FLAG_DELETE = 1;
    public static final Integer DEL_FLAG_AUDIT = 2;

    /**
     * 启用禁用标记
     * 1：启用
     * 2：禁用
     */
    public static final Integer ACTIVE_FLAG_ACTIVING=1;
    public static final Integer ACTIVE_FLAG_UNACTIVE=0;



    /**
     * 用户类型（1系统管理员 2普通用户 3医生 4 医生且普通用户 5工作帐号）
     */
    interface UserRole {
        public static final Integer Administrator = 1;
        public static final Integer Common = 2;
        public static final Integer Doctor = 3;
        public static final Integer DoctorANDCommon = 4;
        public static final Integer WorkAccount = 5;
    }

    /**
     * 知情同意书签署情况(1妻子 2丈夫 3双方)
     */
    public enum InformedConsentSignedEnum {

        Wife("1", "妻子同意"),
        Husband("2", "丈夫同意"),
        All("3", "妻子丈夫都同意");

        InformedConsentSignedEnum(String index, String value) {
            this.index = index;
            this.value = value;
        }

        private String index;
        private String value;

        public String getValue() {
            return value;
        }

        public String getIndex() {
            return index;
        }

    }

    /**
     * 男
     */
    public static final Integer SEX_MAN = 0;
    /**
     * 女
     */
    public static final Integer SEX_WOMAN = 1;

    /**
     * 特殊符号
     */
    interface SpecialSymbol {
        String DOLLAR = "$";

        String AT = "@";

        /**
         * 英文逗号
         */
        String COMMA = ",";

        /**
         * 中文逗号
         */
        String COMMA_CHINESE = "，";

        String EMPTY = "";

        String OBLIQUE_LINE = "/";

        String POINT = ".";

        String SINGLE_QUOTATION = "'";

        String DOUBLE_QUOTATION = "\"";


        String SEMICOLON = ";";

        String LEFT_BRACE = "{";

        String RIGHT_BRACE = "}";

        String TIME = "time";

        String DATE = "date";

        String BLANK = " ";

        String LEFT_ = "(";

        String RIGHT_ = ")";

        String PLUS = "+";

        String UNIT = "g";

        String indexOpt = "<opt>";

        String endOpt = "</opt>";
    }

    interface JsonKey {
        /**
         * 返回信息key:success标识
         */
        String SUCCESS = "success";
        /**
         * 返回信息key:msg标识
         */
        String MSG = "msg";
    }

    public static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";

    interface DbOperator {
        /**
         * 值为Map<String, List>
         */
        String IN = "in";
        /**
         * 值为Map<String, List>
         */
        String NOT_IN = "notIn";
        /**
         * 值为 Map
         */
        String OR = "or";
        /**
         * 值为 LinkedHashMap
         */
        String ORDER_BY = "orderBy";
        /**
         * 值为 List
         */
        String GROUP_BY = "groupBy";
        /**
         * 递增
         */
        String ASC = "asc";
        /**
         * 递减
         */
        String DESC = "desc";
    }

    /**
     * 民族枚举
     */
    enum NationTypeEnum {
        hanzu("1", "汉族"),
        mengguzu("2", "蒙古族"),
        huizu("3", "回族"),
        zangzu("4", "藏族"),
        weiwuerzu("5", "维吾尔族"),
        miaozu("6", "苗族"),
        mizu("7", "彝族"),
        zhuangzu("8", "壮族"),
        buyizu("9", "布依族"),
        chaoxianzu("10", "朝鲜族"),
        manzu("11", "满族"),
        tongzu("12", "侗族"),
        yaozu("13", "瑶族"),
        baizu("14", "白族"),
        tujiazu("15", "土家族"),
        hanizu("16", "哈尼族"),
        hashakezu("17", "哈萨克族"),
        daizu("18", "傣族"),
        lizu("19", "黎族"),
        lilizu("20", "傈僳族"),
        wazu("21", "佤族"),
        qinzu("22", "畲族"),
        gaoshanzu("23", "高山族"),
        lakuzu("24", "拉祜族"),
        shuizu("25", "水族"),
        dongxiangzu("26", "东乡族"),
        naxizu("27", "纳西族"),
        jingpozu("28", "景颇族"),
        heerkezu("29", "柯尔克孜族"),
        tuzu("30", "土族"),
        dakaierzu("31", "达斡尔族"),
        melaozu("32", "仫佬族"),
        qiangzu("33", "羌族"),
        bulangzu("34", "布朗族"),
        lasazu("35", "撒拉族"),
        maonanzu("36", "毛南族"),
        qilaozu("37", "仡佬族"),
        cibaizu("38", "锡伯族"),
        achangzu("39", "阿昌族"),
        pumizu("40", "普米族"),
        tajikezu("41", "塔吉克族"),
        nuzu("42", "怒族"),
        wuzibiekezu("43", "乌孜别克族"),
        eluosizu("44", "俄罗斯族"),
        ewenkezu("45", "鄂温克族"),
        deangzu("46", "德昂族"),
        baoanzuzu("47", "保安族"),
        yuguzu("48", "裕固族"),
        jingzu("49", "京族"),
        tataerzuzu("50", "塔塔尔族"),
        dulongzu("51", "独龙族"),
        elunchunzu("52", "鄂伦春族"),
        hezhezu("53", "赫哲族"),
        menbazu("54", "门巴族"),
        luobazu("55", "珞巴族"),
        jinuozu("56", "基诺族"),
        weifenleiminzu("57", "未分类民族"),
        other("99", "资料不详");

        NationTypeEnum(String key, String value) {
            this.key = key;
            this.value = value;
        }

        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static String getNationValueByKey(String key) {
            if (StringUtils.isBlank(key)) {
                return null;
            }
            for (NationTypeEnum type : NationTypeEnum.values()) {
                if (type.getKey().equals(key.trim())) {
                    return type.getValue();
                }
            }
            return other.getValue();
        }
    }

    /**
     * 用户证件类型枚举
     */
    enum CertificateTypeEnum {
        shenfenzhegn(1, "身份证"),
        junguanzhegn(2, "军官证"),
        huzhao(3, "护照"),
        other(4, "其他有效证件");

        CertificateTypeEnum(Integer index, String value) {
            this.index = index;
            this.value = value;
        }

        private Integer index;
        private String value;

        public Integer getIndex() {
            return index;
        }

        public String getValue() {
            return value;
        }


        public static String getCertificateTypeByKey(Integer index) {
            if (index==null) {
                return null;
            }
            for (CertificateTypeEnum type : CertificateTypeEnum.values()) {
                if (type.getIndex().intValue()==index.intValue()) {
                    return type.getValue();
                }
            }
            return null;
        }
    }

    /**
     * 用户教育程度枚举
     */
    enum EducationEnum {
        weizhi(0, "未知"),
        wenmang(1, "文盲"),
        xiaoxue(2, "小学"),
        chuzhong(3, "初中"),
        gaozhong(4, "高中/中专/中技"),
        daxue(5, "大专/大本"),
        yanjiushegnOrUp(6, "研究生及以上");

        EducationEnum(Integer index, String value) {
            this.index = index;
            this.value = value;
        }

        private Integer index;
        private String value;

        public Integer getIndex() {
            return index;
        }

        public String getValue() {
            return value;
        }


        public static String getEducationTypeByKey(Integer index) {
            if (index==null) {
                return null;
            }
            for (EducationEnum type : EducationEnum.values()) {
                if (type.getIndex().intValue()==index.intValue()) {
                    return type.getValue();
                }
            }
            return weizhi.getValue();
        }
    }

    /**
     * 用户职业枚举
     */
    enum OccupationEnum {
        weizhi(0, "未知"),
        nongmin(1, "农民"),
        gongren(2, "工人"),
        fuwuye(3, "服务业"),
        jingshagn(4, "经商"),
        jiawu(5, "家务"),
        laoshiAndgongwuyuan(6, "教师/公务员/职员等"),
        other(7, "其他");

        OccupationEnum(Integer index, String value) {
            this.index = index;
            this.value = value;
        }

        private Integer index;
        private String value;

        public Integer getIndex() {
            return index;
        }

        public String getValue() {
            return value;
        }


        public static String getOccupationTypeByKey(Integer index) {
            if (index==null) {
                return null;
            }
            for (OccupationEnum type : OccupationEnum.values()) {
                if (type.getIndex().intValue()==index.intValue()) {
                    return type.getValue();
                }
            }
            return weizhi.getValue();
        }
    }


}
