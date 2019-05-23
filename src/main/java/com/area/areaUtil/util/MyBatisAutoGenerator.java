package com.area.areaUtil.util;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.*;

/**
 * MyBatis自动生成器。
 * 1、支持项目初期自动生成所有的service、serviceimp、mapper、mapper.mxl、po，不用引入任何插件
 * 2、支持项目中随时生成某张表的service、serviceimp、mapper、mapper.mxl、po
 * 3、当某些表中字段变化较大，可随时重新生成对应的service、serviceimp、mapper、mapper.mxl、po
 * 4、支持自定义团队的各层的命名规范
 *
 * @author shuai.luo
 * @version v1.0.0
 */
public class MyBatisAutoGenerator {
    /**
     * 数据库连接注意设置：&useInformationSchema=true，否则读取注释信息可能为空。
     */
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true&useInformationSchema=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private static final String PATH_DO = System.getProperty("user.dir").replace('\\', '/') + "/src/main/java/com/area/areaUtil/po";
    private static final String PATH_DAO = System.getProperty("user.dir").replace('\\', '/') + "/src/main/java/com/area/areaUtil/dao";
    private static final String PATH_SERVICE = System.getProperty("user.dir").replace('\\', '/') + "/src/main/java/com/area/areaUtil/service";
    private static final String PATH_SERVICE_IMP = System.getProperty("user.dir").replace('\\', '/') + "/src/main/java/com/area/areaUtil/service/impl";
    private static final String PATH_MAPPER = System.getProperty("user.dir").replace('\\', '/') + "/src/main/java/com/area/areaUtil/dao/xml";

    private static final String PACKAGE_NAME_DAO = "com.area.areaUtil.dao";
    private static final String PACKAGE_NAME_DO = "com.area.areaUtil.po";
    private static final String PACKAGE_NAME_SERVICE = "com.area.areaUtil.service";

    private static final String KEY_COLUMN_NAME = "COLUMN_NAME";
    private static final String KEY_TYPE_NAME = "TYPE_NAME";
    private static final String KEY_REMARKS = "REMARKS";

    public static void main(String[] args) throws Exception {
        //-----------------------------通过jdbc获取数据库中的表结构 主键 各个表字段类型及应用生成实体类-------------------------------
        MyBatisAutoGenerator generator = new MyBatisAutoGenerator();
        // ====================================
        // % 生成所有的，指定表名生成单个的        ||
        // ====================================
//        generator.generate("just_for_test");
//        generator.generate("%");
        generator.generate("sys_area");

    }

    private void generate(String table) throws Exception {
        //1. JDBC连接MYSQL
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        //2.获取表的信息
        DatabaseMetaData metaData = conn.getMetaData();

        ResultSet tableRet = metaData.getTables(null, null, table, new String[]{"TABLE"});

        //3. 提取表的名字，表的注释
        String tableName = null;
        String tableRemark = null;
        while (tableRet.next()) {
            tableName = tableRet.getString("TABLE_NAME");
            tableRemark = tableRet.getString(KEY_REMARKS);

            if (StringUtils.isEmpty(tableRemark)) {
                tableRemark = tableName;
            }
            System.out.println(tableName + " " + tableRemark);

            //4. 提取表内的字段的名字和类型

            String columnName;
            String columnType;
            String remarks;
            ResultSet colRet = metaData.getColumns(null, "%", tableName, "%");

            List<Map<String, Object>> tableInfo = new ArrayList<>();
            while (colRet.next()) {
                Map<String, Object> row = new HashMap<>();
                columnName = colRet.getString(KEY_COLUMN_NAME);
                columnType = colRet.getString(KEY_TYPE_NAME);
                remarks = colRet.getString(KEY_REMARKS);
                if (StringUtils.isEmpty(remarks)) {
                    remarks = columnName;
                }
//            int dataSize = colRet.getInt("COLUMN_SIZE");
//            int digits = colRet.getInt("DECIMAL_DIGITS");
//            int nullable = colRet.getInt("NULLABLE");
                row.put(KEY_COLUMN_NAME, columnName);
                row.put(KEY_TYPE_NAME, columnType);
                row.put(KEY_REMARKS, remarks);
//                System.out.println(columnName + " " + columnType + " " + remarks);

                tableInfo.add(row);
            }

            if (null != tableInfo && tableInfo.size() > 0) {
                this.generateEntity("T" + setPascalCase(tableName), tableInfo, tableName, tableRemark);
                this.generateMapper(tableName, tableInfo);

                this.generateDAO(setPascalCase(table), tableRemark);

                //生成初始化的service接口
                this.generateService(setPascalCase(table), tableRemark);

                //生成初始化的service实现
                this.generateServiceImp(setPascalCase(table), tableRemark);

                //临时生成改表id语句
//                this.generateAlterIds(tableName, (String) tableInfo.get(0).get(KEY_REMARKS));

            }
        }

        conn.close();

    }

    /**
     * 生成改表id语句，模板：
     * ALTER TABLE `cas`.`abroad_info`
     * CHANGE COLUMN `abroad_info_id` `id` INT(11) UNSIGNED NOT NULL COMMENT '出国(境信息)主键' ;
     *
     * @param tableName tableName
     * @param pkRemark  pkRemark
     */
    private void generateAlterIds(String tableName, String pkRemark) {
        StringBuilder buf = new StringBuilder();
        buf.append("ALTER TABLE `cas`.`" + tableName + "`\n");
        buf.append("CHANGE COLUMN `id` `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '" + pkRemark + "';\n");
        System.out.println(buf.toString());

    }

    private void generateEntity(String className, List<Map<String, Object>> tableInfo, String tableName, String tableRemark) throws Exception {
        if (tableInfo != null && tableInfo.size() > 0) {
            System.out.println(className + "====开始生成。");
//            FileUtil.mkDirs(path_do);
            StringBuilder buf = new StringBuilder();
            buf.append("package ").append(PACKAGE_NAME_DO).append(";\n\n");

            //1、import 部分
            buf.append("import com.baomidou.mybatisplus.activerecord.Model;\n");
            buf.append("import com.baomidou.mybatisplus.annotations.TableField;\n");
            buf.append("import com.baomidou.mybatisplus.annotations.TableId;\n");
            buf.append("import com.baomidou.mybatisplus.annotations.TableName;\n");
            buf.append("import com.baomidou.mybatisplus.enums.IdType;\n");
            buf.append("import lombok.Data;\n");
            buf.append("import lombok.experimental.Accessors;\n\n");

            buf.append("import org.apache.commons.lang3.builder.ToStringStyle;\n");
            buf.append("import org.apache.commons.lang3.builder.ToStringBuilder;\n\n");

            buf.append("import java.io.Serializable;\n");
            buf.append("import java.util.Date;\n\n");

            //2、类注释
            buf.append("/**\n").append(" * " + tableRemark + "。\n").append(" *\n").
                    append(" * @author guohao.yang\n").
                    append(" * @version v1.0.0\n").
                    append(" */\n");

            //3、类注解
            buf.append("@Data\n").append("@Accessors(chain = true)\n").append("@TableName(\"" + tableName + "\")\n");

            //4、类定义+序列号
            buf.append("public class ").append(className).append(" extends Model<" + className + "> {\n\n");
            buf.append("    private static final long serialVersionUID = 1L;\n\n");

            //5、类属性
            Iterator var12 = tableInfo.iterator();

            int i = 0;
            String pkStr = null;
            while (var12.hasNext()) {
                Map<String, Object> row = (Map) var12.next();
                String field = row.get(KEY_COLUMN_NAME).toString();
                String fieldCamel = setCamelCase(field);

                //过滤掉提取到了BaseDO的字段：id,gmt_create,gmt_modified

                String type = this.getType((String) row.get(KEY_TYPE_NAME));
                String remark = (String) row.get(KEY_REMARKS);
                buf.append("    /**\n").append("     * " + remark + "。\n").append("     */\n");
                if (i == 0) {
                    pkStr = fieldCamel;
                    buf.append("    @TableId(value = \"" + field + "\", type = IdType.AUTO)\n");
                } else {
                    buf.append("    @TableField(\"" + field + "\")\n");
                }
                i++;
                buf.append("    private ").append(type).append(" ").append(fieldCamel).append(";\n\n");
            }

            buf.append("    @Override\n").
                    append("    protected Serializable pkVal() {\n").
                    append("        return this." + pkStr + ";\n").append("    }\n\n");

            buf.append("    @Override\n").
                    append("    public String toString() {\n").
                    append("        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);\n").append("    }\n\n");

            //@Override
            //    protected Serializable pkVal() {
            //        return this.adminRoleRefId;
            //    }

            buf.append("}");
            String fileName = PATH_DO + "/" + className + ".java";
            FileUtils.writeStringToFile(new File(fileName), buf.toString(), "UTF-8");
            System.out.println(className + "生成完毕。");
        }
    }

    private void generateDAO(String className, String remark) throws Exception {
        //基于MP的mapper层，初始化的dao（mapper）只有4行代码
        System.out.println(className + "Mapper.java 开始生成。");
        StringBuilder buf = new StringBuilder();
        //1、包和引用
        buf.append("package ").append(PACKAGE_NAME_DAO).append(";\n\n");

        buf.append("import com.baomidou.mybatisplus.mapper.BaseMapper;\n");
        buf.append("import " + PACKAGE_NAME_DO + ".T" + className + ";\n");

        //2、类注释
        buf.append("/**\n").append(" * " + remark + "Mapper接口。\n").append(" *\n").
                append(" * @author guohao.yang@talebase.com\n").
                append(" * @version v1.0.0\n").
                append(" */\n");

        //3、类定义
        buf.append("public interface ").append(className).append("Mapper extends BaseMapper<T" + className + "> {\n\n");
        buf.append("}");
        String fileName = PATH_DAO + "/" + className + "Mapper.java";
        FileUtils.writeStringToFile(new File(fileName), buf.toString(), "UTF-8");
        System.out.println(className + "Mapper.java 生成完毕。");
    }

    private void generateService(String className, String remark) throws Exception {
        //基于MP的service层，初始化的service只有4行代码
        System.out.println(className + "Service.java 开始生成。");
        StringBuilder buf = new StringBuilder();
        //1、包和引用
        buf.append("package ").append(PACKAGE_NAME_SERVICE).append(";\n\n");

        buf.append("import com.baomidou.mybatisplus.service.IService;\n");
        buf.append("import " + PACKAGE_NAME_DO + ".T" + className + ";\n");

        //2、类注释
        buf.append("/**\n").append(" * " + remark + "Service接口。\n").append(" *\n").
                append(" * @author guohao.yang@talebase.com\n").
                append(" * @version v1.0.0\n").
                append(" */\n");

        //3、类定义
        buf.append("public interface ").append(className).append("Service extends IService<T" + className + "> {\n\n");
        buf.append("}");
        String fileName = PATH_SERVICE + "/" + className + "Service.java";
        FileUtils.writeStringToFile(new File(fileName), buf.toString(), "UTF-8");
        System.out.println(className + "Service.java 生成完毕。");
    }

    private void generateServiceImp(String className, String remark) throws Exception {
        //基于MP的service impl层，初始化的10行左右的代码
        System.out.println(className + "ServiceImpl.java 开始生成。");
        StringBuilder buf = new StringBuilder();
        //1、包和引用
        buf.append("package ").append(PACKAGE_NAME_SERVICE).append(".impl;\n\n");

        buf.append("import com.baomidou.mybatisplus.service.impl.ServiceImpl;\n");
        buf.append("import " + PACKAGE_NAME_DAO + "." + className + "Mapper;\n");
        buf.append("import " + PACKAGE_NAME_DO + ".T" + className + ";\n");
        buf.append("import " + PACKAGE_NAME_SERVICE + "." + className + "Service;\n");
        buf.append("import org.springframework.context.annotation.Primary;\n");
        buf.append("import org.springframework.stereotype.Service;\n");

        //2、类注释
        buf.append("/**\n").append(" * " + remark + "Service接口实现。\n").append(" *\n").
                append(" * @author guohao.yang@talebase.com\n").
                append(" * @version v1.0.0\n").
                append(" */\n");

        //3、类定义
        buf.append("@Primary\n").append("@Service\n");
        buf.append("public class ").append(className).append("ServiceImpl extends ServiceImpl<" + className + "Mapper, T" +
                className + "> implements " + className + "Service {\n\n");
        buf.append("}");
        String fileName = PATH_SERVICE_IMP + "/" + className + "ServiceImpl.java";
        FileUtils.writeStringToFile(new File(fileName), buf.toString(), "UTF-8");
        System.out.println(className + "ServiceImpl.java 生成完毕。");
    }

    private void generateMapper(String tableName, List<Map<String, Object>> tableInfo) throws Exception {
        if (tableInfo != null && tableInfo.size() > 0) {
            String className = setPascalCase(tableName);
            System.out.println(className + "Mapper.xml 开始生成。");
            StringBuilder buf = new StringBuilder();
            buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            buf.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n\n");
            buf.append("<mapper namespace=\"").append(PACKAGE_NAME_DAO).append(".").append(className).append("Mapper\">\n\n");

            //1、resultMap映射，和DO对象解耦
            //这里注意两个属性：javaType
            //一个Java类的完全限定名，或一个类型别名（参加上面内建类型别名的列表）。
            // 如果你映射到一个JavaBean，MyBatis通常可以断定类型。然而，如果你映射到的是HashMap，那么你应该明确地指定javaType来保证所需的行为。
            //jdbcType
            //在这个表格之后的所支持的JDBC类型列表中的类型。
            // JDBC类型是仅仅需要对插入，更新和删除操作可能为空的列进行处理。这是JDBC的需要，而不是MyBatis的。
            // 如果你直接使用JDBC编程，你需要指定这个类型-但仅仅对可能为空的值。
            buf.append("\t<resultMap id=\"BaseResultMap\" type=\"").append(PACKAGE_NAME_DO).append(".T").append(className).append("\">\n");
            for (Map<String, Object> row : tableInfo) {
                String column = (String) row.get(KEY_COLUMN_NAME);
                buf.append("\t\t<result column=\"").append(column).append("\" property=\"").append(setCamelCase(column)).append("\"/>\n");
            }
            buf.append("\t</resultMap>\n\n");
            buf.append("</mapper>");

            String fileName = PATH_MAPPER + "/" + className + "Mapper.xml";
            FileUtils.writeStringToFile(new File(fileName), buf.toString());
            System.out.println(fileName + "生成完毕。");
        }

    }

    private String getType(String str) {
        String[] intType = new String[]{"int", "tinyint", "smallint", "mediumint", "bigint", "long"};
        if (StringUtils.isEmpty(str)) {
            return "";
        } else {
            str = str.toLowerCase();
            int i = 0;

            for (int n = intType.length; i < n; ++i) {
                if (str.startsWith(intType[i])) {
                    return "Integer";
                }
            }

            if (str.equals("timestamp")) {
                return "Date";
            }

            if (str.startsWith("float") || str.startsWith("decimal") || str.startsWith("double")) {
                return "Double";
            }
            return "String";
        }
    }

    private static String setFirstCharUpcase(String s) {
        if (s != null && s.length() >= 1) {
            char[] c = s.toCharArray();
            if (c.length > 0 && c[0] >= 97 && c[0] <= 122) {
                c[0] = (char) ((short) c[0] - 32);
            }

            return String.valueOf(c);
        } else {
            return s;
        }
    }

    private static String setPascalCase(String str) {
        String[] arr = str.split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(setFirstCharUpcase(s));
        }
        return sb.toString();
    }

    private static String setCamelCase(String str) {
        String[] arr = str.split("_");
        StringBuilder sb = new StringBuilder();
        sb.append(arr[0]);
        for (int i = 1; i < arr.length; i++) {
            sb.append(setFirstCharUpcase(arr[i]));
        }
        return sb.toString();
    }
}
