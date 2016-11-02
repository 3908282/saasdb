drop table if exists t_relationship;

create table t_relationship(ftenantid varchar(44) not null,fentityname varchar(44) not null,finstanceid varchar(44) not null,ffieldnum int not null,
ftargetentityname varchar(44) not null,ftargetinstanceid varchar(44) not null,
fdeleted boolean,fdeletedtime time,
primary key(ftenantid,fentityname,finstanceid,ffieldnum)
);

create index i_rel_reverse on t_relationship(ftenantid,fentityname,ftargetentityname,ftargetinstanceid);