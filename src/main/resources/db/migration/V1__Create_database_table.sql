create table if not exists public."lorefiles"
(
    s3_path             varchar(512) not null primary key,
    text                text  not null,
    topic               int
);

ALTER TABLE lorefiles ADD COLUMN ts tsvector
    GENERATED ALWAYS AS (to_tsvector('english', text)) STORED;

CREATE INDEX ts_idx ON lorefiles USING GIN (ts);
