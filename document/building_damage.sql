create table softwaremanage.building_damage
(
    id                      int auto_increment
        primary key,
    building_damage_id      char(19)                 not null comment '受伤灾情编码',
    province                varchar(255)             null,
    city                    varchar(255)             null,
    country                 varchar(255)             null,
    town                    varchar(255)             null,
    village                 varchar(255)             null,
    location                varchar(255)             null,
    date                    varchar(100)             null,
    category                int         default 0    null comment '房屋破坏土木/人员受伤/人员失踪分别对应012',
    basically_intact_square double      default 0    null,
    damaged_square          double      default 0    null,
    destroyed_square        double      default 0    null,
    slight_damaged_square     double      default 0    null,
    moderate_damaged_square   double      default 0    null,
    serious_damaged_square    double      default 0    null,
    note                    varchar(10) default '严重' null,
    reporting_unit          varchar(100)             null comment '上报单位',
    earthquake_id           char(26)                 null comment '震情编码'
)
    collate = utf8_bin;

