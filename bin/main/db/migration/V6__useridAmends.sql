ALTER TABLE user_table
ADD COLUMN userid varchar(255) not null;

ALTER TABLE user_table
ALTER COLUMN is_deleted
SET DEFAULT FALSE;

ALTER TABLE user_table_aud
ADD COLUMN userid varchar(255) not null;

ALTER TABLE user_table_aud
ALTER COLUMN is_deleted
SET DEFAULT FALSE;

ALTER TABLE products
ALTER COLUMN userid TYPE varchar(255);

ALTER TABLE products
ALTER COLUMN userid SET not null;

ALTER TABLE products
ALTER COLUMN is_deleted
SET DEFAULT FALSE;

ALTER TABLE products_aud
ALTER COLUMN userid TYPE varchar(255);

ALTER TABLE products_aud
ALTER COLUMN userid SET not null;

ALTER TABLE products_aud
ALTER COLUMN is_deleted
SET DEFAULT FALSE;