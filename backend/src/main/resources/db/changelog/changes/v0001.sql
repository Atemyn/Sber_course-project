create table document (
    id bigserial not null,
    doc_type varchar(127) not null,
    organization varchar(255) not null,
    description varchar(512) not null,
    creation_date date not null,
    patient varchar(255) not null,
    status varchar(20) not null,
    primary key (id)
);