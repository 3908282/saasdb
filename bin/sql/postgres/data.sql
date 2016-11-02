drop table if exists t_data;
create table t_data(ftenantid varchar(44) not null,fentityname varchar(44) not null,finstanceid varchar(44) not null,
fdeleted boolean,fdeletedtime time,
fvalue0 text,fvalue1 text,fvalue2 text,fvalue3 text,fvalue4 text,fvalue5 text,fvalue6 text,fvalue7 text,fvalue8 text,fvalue9 text,
fvalue10 text,fvalue11 text,fvalue12 text,fvalue13 text,fvalue14 text,fvalue15 text,fvalue16 text,fvalue17 text,fvalue18 text,fvalue19 text,
fvalue20 text,fvalue21 text,fvalue22 text,fvalue23 text,fvalue24 text,fvalue25 text,fvalue26 text,fvalue27 text,fvalue28 text,fvalue29 text,
fvalue30 text,fvalue31 text,fvalue32 text,fvalue33 text,fvalue34 text,fvalue35 text,fvalue36 text,fvalue37 text,fvalue38 text,fvalue39 text,
fvalue40 text,fvalue41 text,fvalue42 text,fvalue43 text,fvalue44 text,fvalue45 text,fvalue46 text,fvalue47 text,fvalue48 text,fvalue49 text,

fcreatedtime time not null,fcreateduser varchar(44) not null,flastmodifiedtime time not null,flastmodifieduser varchar(44) not null,
primary key(finstanceid)
);

create index i_data_entity on t_data(ftenantid,fentityname);
create index i_data_inst on t_data(ftenantid,fentityname,finstanceid);

