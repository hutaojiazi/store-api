ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO PUBLIC;

CREATE TABLE store_orders
(
    id               character varying(255)   NOT NULL,
    status           character varying(64)   NOT NULL,
    created_at       timestamptz
);

ALTER TABLE ONLY store_orders
    ADD CONSTRAINT store_orders_pkey PRIMARY KEY (id);

CREATE TABLE store_products
(
    id               character varying(255)   NOT NULL,
    name             character varying(64)   NOT NULL,
    price            numeric(16,6),
    url              character varying(255)
);

ALTER TABLE ONLY store_products
    ADD CONSTRAINT store_products_pkey PRIMARY KEY (id);

CREATE TABLE store_order_product
(
    order_id         character varying(255)     NOT NULL,
    product_id       character varying(255)     NOT NULL,
    quantity         integer  NOT NULL
);