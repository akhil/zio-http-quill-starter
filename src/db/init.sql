create table CHARGE
(
    id varchar(255) not null primary key,
    amount integer not null,
    currency varchar(255) not null,
    status varchar(255) not null,
    _rawdata TEXT not null
);

create table PAYPAL_WEBHOOK_DATA
(
    id varchar(255) not null primary key,
    created_time varchar(255) not null,
    resource_type varchar(255) not null,
    event_type varchar(255) not null,
    summary varchar(2000) not null,
    event_version varchar(255),
    resource_version varchar(255),
    resource TEXT not null,
    links varchar(10000),
    _rawdata TEXT not null
)