create table softwaremanage.distressed_people
(
    id             int auto_increment
        primary key,
    people_id      char(19)      not null comment '' 受伤灾情编码 '',
    province       varchar(255)  null,
    city           varchar(255)  null,
    country        varchar(255)  null,
    town           varchar(255)  null,
    village        varchar(255)  null,
    location       varchar(255)  null,
    date           datetime      null,
    number         int           null comment '' 受伤人数 '',
    category       int default 0 null comment '' 人员死亡/人员受伤/人员失踪分别对应012 '',
    reporting_unit varchar(100)  null comment '' 上报单位 '',
    earthquakeId   char(26)      null comment '' 震情编码 ''
)
    collate = utf8_bin;

