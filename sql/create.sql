create table if not exists tool_brand
(
    brand_key  identity primary key,
    brand_name text not null unique
);

create table if not exists tool_type
(
    type_key     identity primary key,
    type_name    text   not null unique,
    daily_charge double not null,
    weekday      boolean,
    weekend      boolean,
    holiday      boolean
);

create table if not exists tool
(
    tool_code text not null primary key,
    type_key  long not null references tool_type (type_key),
    brand_key long not null references tool_brand (brand_key)
);
