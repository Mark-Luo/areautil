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