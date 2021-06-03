-- auto-generated definition
create table secondary_disaster
(
    id             int auto_increment primary key,
    secondary_id   char(19)      not null comment '次生灾情编码',
    province       varchar(255)  null,
    city           varchar(255)  null,
    country        varchar(255)  null,
    town           varchar(255)  null,
    village        varchar(255)  null,
    location       varchar(255)  null,
    date           datetime      null,
    category       int default 0 null comment '0崩塌 1滑坡 2泥石流 3岩溶塌陷 4地裂缝 5地面沉降 6其他  ',
    type           int           null comment '小类 ',
    status         int           null comment '破坏等级',
    picture        varchar(255)  null,
    reporting_unit varchar(100)  null comment '上报单位',
    note           varchar(50)   null comment '灾情描述',
    earthquake_id  char(26)      null comment '震情编码'
)
    collate = utf8_bin;

