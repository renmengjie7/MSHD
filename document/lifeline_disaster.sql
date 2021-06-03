-- auto-generated definition
create table lifeline_disaster
(
    id             int auto_increment primary key,
    lifeline_id    char(19)      not null comment '生命线灾情编码',
    province       varchar(255)  null,
    city           varchar(255)  null,
    country        varchar(255)  null,
    town           varchar(255)  null,
    village        varchar(255)  null,
    location       varchar(255)  null,
    date           datetime      null,
    category       int default 0 null comment '0交通 1供水 2输油 3燃气 4电力 5通信 6水利 ',
    grade          int unsigned  null comment '灾害程度',
    type           int           null comment '小类 ',
    picture        varchar(255)  null,
    reporting_unit varchar(100)  null comment '上报单位',
    note           varchar(50)   null comment '灾情描述',
    earthquake_id  char(26)      not null comment '震情编码'
)
    collate = utf8_bin;

