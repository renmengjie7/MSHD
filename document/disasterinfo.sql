create table softwaremanage.disasterinfo
(
    id             int auto_increment
        primary key,
    unified_id     char(19)     null comment '统一编码',
    d_id           char(26)     null comment '基本灾情码  地理信息+年+月+日+时+分+秒',
    province       varchar(255) null,
    city           varchar(255) null,
    country        varchar(255) null,
    town           varchar(255) null,
    village        varchar(255) null,
    date           datetime     null,
    location       varchar(100) null,
    longitude      double(5, 2) null,
    latitude       double(4, 2) null,
    depth          float        null,
    magnitude      float        null,
    picture        varchar(20)  null,
    reporting_unit varchar(50)  null
)
    collate = utf8_bin;

