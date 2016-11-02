drop table if exists t_index_int;
create table t_index_int(ftenantid varchar(44) not null,fentityname varchar(44) not null,finstanceid varchar(44) not null,
ffieldnum int, fvalue int
);
create index i_index_int on t_index_int(ftenantid,fentityname,ffieldnum,fvalue);

drop table if exists t_index_string;
create table t_index_string(ftenantid varchar(44) not null,fentityname varchar(44) not null,finstanceid varchar(44) not null,
ffieldnum int, fvalue text,fvalueu text
);
create index i_index_string on t_index_string(ftenantid,fentityname,ffieldnum,fvalue);
create index i_index_stringu on t_index_string(ftenantid,fentityname,ffieldnum,fvalueu);

drop table if exists t_index_boolean;
create table t_index_boolean(ftenantid varchar(44) not null,fentityname varchar(44) not null,finstanceid varchar(44) not null,
ffieldnum int, fvalue boolean
);
create index i_index_boolean on t_index_boolean(ftenantid,fentityname,ffieldnum,fvalue);

drop table if exists t_index_numeric;
create table t_index_numeric(ftenantid varchar(44) not null,fentityname varchar(44) not null,finstanceid varchar(44) not null,
ffieldnum int, fvalue numeric
);
create index i_index_numeric on t_index_numeric(ftenantid,fentityname,ffieldnum,fvalue);

drop table if exists t_index_date;
create table t_index_date(ftenantid varchar(44) not null,fentityname varchar(44) not null,finstanceid varchar(44) not null,
ffieldnum int, fvalue date
);
create index i_index_date on t_index_date(ftenantid,fentityname,ffieldnum,fvalue);

drop table if exists t_index_time;
create table t_index_time(ftenantid varchar(44) not null,fentityname varchar(44) not null,finstanceid varchar(44) not null,
ffieldnum int, fvalue time
);
create index i_index_time on t_index_time(ftenantid,fentityname,ffieldnum,fvalue);
