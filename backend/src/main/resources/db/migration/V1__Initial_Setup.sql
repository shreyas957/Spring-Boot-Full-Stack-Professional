CREATE SEQUENCE customer_id_seq;

CREATE TABLE customer(
    id BIGINT DEFAULT nextval('customer_id_seq') PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    age INT NOT NULL,
    gender TEXT NOT NULL
);

-- ALTER TABLE customer
--     ADD CONSTRAINT customer_email_unique UNIQUE (email);
--
-- ALTER TABLE customer
--     ADD COLUMN gender TEXT NOT NULL;

