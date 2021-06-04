create table softwaremanage.forecast
(
    id        int auto_increment
        primary key,
    date      varchar(100)  null,
    grade     int default 0 null,
    intensity int default 0 null,
    type      int default 0 null,
    picture   varchar(100)  null,
    code      varchar(20)   null
)
    collate = utf8_bin;

