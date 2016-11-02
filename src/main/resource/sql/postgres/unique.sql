drop table if exists t_unique_int;
create table t_unique_int(ftenantid varchar(44) not null,fentityname varchar(44) not null,finstanceid varchar(44) not null,
ffieldnum int, fvalue int,
primary key(ftenantid,fentityname,ffieldnum,fvalue)
);

drop table if exists t_unique_string;
create table t_unique_string(ftenantid varchar(44) not null,fentityname varchar(44) not null,finstanceid varchar(44) not null,
ffieldnum int, fvalue text,fvalueu text,
primary key(ftenantid,fentityname,ffieldnum,fvalue)
);
create index i_unique_stringu on t_unique_string(ftenantid,fentityname,ffieldnum,fvalueu);

drop table if exists t_unique_numeric;
create table t_unique_numeric(ftenantid varchar(44) not null,fentityname varchar(44) not null,finstanceid varchar(44) not null,
ffieldnum int, fvalue numeric,
primary key(ftenantid,fentityname,ffieldnum,fvalue)
);

drop table if exists t_unique_date;
create table t_unique_date(ftenantid varchar(44) not null,fentityname varchar(44) not null,finstanceid varchar(44) not null,
ffieldnum int, fvalue date,
primary key(ftenantid,fentityname,ffieldnum,fvalue)
);

drop table if exists t_unique_time;
create table t_unique_time(ftenantid varchar(44) not null,fentityname varchar(44) not null,finstanceid varchar(44) not null,
ffieldnum int, fvalue time,
primary key(ftenantid,fentityname,ffieldnum,fvalue)
);


