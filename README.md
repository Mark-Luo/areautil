# areautil
中国行政地区工具生成器
1.上 中华人民共和国民政部官网拿最新地区数据 http://www.mca.gov.cn/article/sj/xzqh/2019/
2.拷贝到excel中
格式要求：
110000	北京市
110101	东城区
110102	西城区
110105	朝阳区
110106	丰台区

3.建表语句
drop table if exists sys_area;

/*==============================================================*/
/* Table: sys_area                                              */
/*==============================================================*/
create table sys_area
(
   id                   bigint UNSIGNED not null auto_increment,
   area_code            varchar(32) not null comment '区域行政编码',
   area_value           varchar(128) not null comment '区域行政名称',
   parent_code          varchar(32) comment '上级地区编码',
   area_level           tinyint(3) not null comment '层级',
   gmt_create           timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
   gmt_modified         timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   create_id            bigint UNSIGNED comment '创建人',
   modified_id          bigint UNSIGNED comment '修改人',
   note                 varchar(200) comment '备注',
   version              tinyint(3) UNSIGNED default 0 comment '版本',
   is_deleted           tinyint(3) UNSIGNED default 0 comment '0：正常 1：已删除',
   is_can_use           tinyint(3) UNSIGNED default 1 comment '1：正常 0：不可用',
   primary key (id)
);

alter table sys_area comment '地区表';
4.下载代码执行程序
5.访问http://localhost:8080/swagger-ui.html，上传文件！
注意：execl模板和sql参考代码source下面文件，数据库和wagger-ui访问地址和你代码配置相关


