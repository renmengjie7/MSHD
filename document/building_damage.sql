create table softwaremanage.building_damage
(
    id                    int auto_increment
        primary key,
    building_damage_id    char(19)                 not null comment '受伤灾情编码',
    province              varchar(255)             null,
    city                  varchar(255)             null,
    country               varchar(255)             null,
    town                  varchar(255)             null,
    village               varchar(255)             null,
    location              varchar(255)             null,
    date                  varchar(100)             null,
    category              int         default 0    null comment '房屋破坏土木/房屋破坏砖木/房屋破坏砖混/房屋破坏框架/房屋破坏其他  分别对应0124',
    basicallyIntactSquare double      default 0    null,
    damagedSquare         double      default 0    null,
    destroyedSquare       double      default 0    null,
    note                  varchar(10) default '严重' null,
    reporting_unit        varchar(100)             null comment '上报单位',
    earthquakeId          char(26)                 null comment '震情编码'
)
    collate = utf8_bin;

