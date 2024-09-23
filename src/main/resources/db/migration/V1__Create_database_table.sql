create table if not exists public."file"
(
    s3_path             varchar(512) not null primary key,
    text                text  not null,
    topic               int
);