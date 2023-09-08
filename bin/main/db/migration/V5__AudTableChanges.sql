ALTER TABLE user_table_aud DROP constraint user_table_aud_pkey;
ALTER TABLE user_table_aud ALTER COLUMN acc_id DROP identity;
ALTER TABLE user_table_aud ALTER COLUMN acc_id DROP not null;

ALTER TABLE products_aud DROP constraint products_aud_pkey;
ALTER TABLE products_aud ALTER COLUMN id DROP identity;
ALTER TABLE products_aud ALTER COLUMN id DROP not null;

ALTER TABLE basket_aud DROP constraint basket_aud_pkey;
ALTER TABLE basket_aud ALTER COLUMN id DROP identity;
ALTER TABLE basket_aud ALTER COLUMN id DROP not null;

ALTER TABLE payments_aud DROP constraint payments_aud_pkey;
ALTER TABLE payments_aud ALTER COLUMN id DROP identity;
ALTER TABLE payments_aud ALTER COLUMN id DROP not null;

ALTER TABLE wallet_aud DROP constraint wallet_aud_pkey;
ALTER TABLE wallet_aud ALTER COLUMN id DROP identity;
ALTER TABLE wallet_aud ALTER COLUMN id DROP not null;

ALTER TABLE grocery_orders_aud DROP constraint grocery_orders_aud_pkey;
ALTER TABLE grocery_orders_aud ALTER COLUMN order_id DROP identity;
ALTER TABLE grocery_orders_aud ALTER COLUMN order_id DROP not null;