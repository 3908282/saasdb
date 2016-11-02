--t_entity
drop table if exists t_entity;
create table t_entity(ftenantid varchar(44) not null,fname varchar(44) not null,
falias varchar(100) not null,fdesc text, 
fclassname varchar(256), fidfieldname varchar(18) default 'id', fidfielddatatype int,
fcreatedtime time not null,fcreateduser varchar(44) not null,flastmodifiedtime time not null,flastmodifieduser varchar(44) not null,
primary key(ftenantid,fname)
);

drop table if exists t_field;
create table t_field(ftenantid varchar(44) not null,fentityname varchar(44) not null,fname varchar(40) not null,falias varchar(100) not null,fdesc text, 
fdatatype int,flength int,fprecise int,frefentity varchar(44),fdefaultvalue text,
fnullable boolean,fisindex boolean,fisunique boolean,
ffieldnum int,ffieldname varchar(10),fdeleted boolean default false,
fcreatedtime time not null,fcreateduser varchar(44) not null,flastmodifiedtime time not null,flastmodifieduser varchar(44) not null,
primary key(ftenantid,fentityname,fname)
);

create index i_field_entity on t_field(ftenantid,fentityname);

