CREATE TABLE addresses
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    street     VARCHAR(100),
    city       VARCHAR(50),
    state      VARCHAR(50),
    zip_code   VARCHAR(20),
    country    VARCHAR(50),
    phone      VARCHAR(20),
    is_default BOOLEAN,
    user_id    BIGINT                                  NOT NULL,
    CONSTRAINT pk_addresses PRIMARY KEY (id)
);

CREATE TABLE blog_posts
(
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title               VARCHAR(255),
    content             VARCHAR(255),
    author              VARCHAR(255),
    created_at          TIMESTAMP WITHOUT TIME ZONE,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    image_url           VARCHAR(255),
    tags                VARCHAR(255),
    slug                VARCHAR(255),
    excerpt             VARCHAR(255),
    created_by_admin_id BIGINT,
    CONSTRAINT pk_blog_posts PRIMARY KEY (id)
);

CREATE TABLE cart_items
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    cart_id    BIGINT,
    product_id BIGINT,
    quantity   INTEGER,
    CONSTRAINT pk_cart_items PRIMARY KEY (id)
);

CREATE TABLE categories
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(50),
    description VARCHAR(200),
    image_url   VARCHAR(255),
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE comments
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    author_name VARCHAR(255),
    content     VARCHAR(255),
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    post_id     BIGINT,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);

CREATE TABLE contact_message
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name       VARCHAR(255),
    email      VARCHAR(255),
    message    VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    resolved   BOOLEAN                                 NOT NULL,
    CONSTRAINT pk_contactmessage PRIMARY KEY (id)
);

CREATE TABLE order_items
(
    id                BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    order_id          BIGINT,
    product_id        BIGINT,
    quantity          INTEGER,
    price_at_purchase DECIMAL,
    CONSTRAINT pk_order_items PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    order_number  VARCHAR(50)                             NOT NULL,
    user_id       BIGINT                                  NOT NULL,
    address_id    BIGINT                                  NOT NULL,
    subtotal      DECIMAL(19, 2)                          NOT NULL,
    tax           DECIMAL(19, 2)                          NOT NULL,
    shipping_fee  DECIMAL(19, 2)                          NOT NULL,
    total         DECIMAL(19, 2)                          NOT NULL,
    status        VARCHAR(20)                             NOT NULL,
    order_date    TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    delivery_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

CREATE TABLE password_reset_token
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    token       VARCHAR(255),
    email       VARCHAR(255),
    expiry_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_passwordresettoken PRIMARY KEY (id)
);

CREATE TABLE payments
(
    id                     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    order_id               BIGINT                                  NOT NULL,
    payment_method         VARCHAR(20)                             NOT NULL,
    payment_status         VARCHAR(20)                             NOT NULL,
    gateway_transaction_id VARCHAR(100),
    amount                 DECIMAL(19, 2)                          NOT NULL,
    currency               VARCHAR(3)                              NOT NULL,
    transaction_id         VARCHAR(100),
    payment_date           TIMESTAMP WITHOUT TIME ZONE,
    gateway_response       TEXT,
    failure_reason         TEXT,
    refund_amount          DECIMAL(19, 2),
    refund_date            TIMESTAMP WITHOUT TIME ZONE,
    country_code           VARCHAR(2),
    mobile_number          VARCHAR(20),
    card_last_four         VARCHAR(4),
    created_at             TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    updated_at             TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_payments PRIMARY KEY (id)
);

CREATE TABLE product_specifications
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    spec_key   VARCHAR(50)                             NOT NULL,
    spec_value VARCHAR(200)                            NOT NULL,
    product_id BIGINT                                  NOT NULL,
    CONSTRAINT pk_product_specifications PRIMARY KEY (id)
);

CREATE TABLE products
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name           VARCHAR(255)                            NOT NULL,
    description    TEXT,
    price          DECIMAL(10, 2)                          NOT NULL,
    discount_price DECIMAL(10, 2),
    stock_quantity INTEGER                                 NOT NULL,
    image_url      VARCHAR(512),
    category_id    BIGINT,
    active         BOOLEAN                                 NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

CREATE TABLE review
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    content    VARCHAR(255),
    rating     INTEGER                                 NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    product_id BIGINT,
    user_id    BIGINT,
    CONSTRAINT pk_review PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(20),
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE shopping_carts
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_id     BIGINT,
    total_price DECIMAL,
    CONSTRAINT pk_shopping_carts PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    role_id INTEGER NOT NULL,
    user_id BIGINT  NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id)
);

CREATE TABLE users
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    username VARCHAR(20),
    email    VARCHAR(50),
    password VARCHAR(120),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE wishlist_products
(
    product_id  BIGINT NOT NULL,
    wishlist_id BIGINT NOT NULL,
    CONSTRAINT pk_wishlist_products PRIMARY KEY (product_id, wishlist_id)
);

CREATE TABLE wishlists
(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_id VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_wishlists PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_74165e195b2f7b25de690d14a UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_77584fbe74cc86922be2a3560 UNIQUE (username);

ALTER TABLE orders
    ADD CONSTRAINT uc_orders_order_number UNIQUE (order_number);

ALTER TABLE payments
    ADD CONSTRAINT uc_payments_gateway_transaction UNIQUE (gateway_transaction_id);

ALTER TABLE payments
    ADD CONSTRAINT uc_payments_order UNIQUE (order_id);

ALTER TABLE payments
    ADD CONSTRAINT uc_payments_transaction UNIQUE (transaction_id);

ALTER TABLE shopping_carts
    ADD CONSTRAINT uc_shopping_carts_user UNIQUE (user_id);

ALTER TABLE addresses
    ADD CONSTRAINT FK_ADDRESSES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE cart_items
    ADD CONSTRAINT FK_CART_ITEMS_ON_CART FOREIGN KEY (cart_id) REFERENCES shopping_carts (id);

ALTER TABLE cart_items
    ADD CONSTRAINT FK_CART_ITEMS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_POST FOREIGN KEY (post_id) REFERENCES blog_posts (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_ADDRESS FOREIGN KEY (address_id) REFERENCES addresses (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE payments
    ADD CONSTRAINT FK_PAYMENTS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE product_specifications
    ADD CONSTRAINT FK_PRODUCT_SPECIFICATIONS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE review
    ADD CONSTRAINT FK_REVIEW_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE review
    ADD CONSTRAINT FK_REVIEW_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE shopping_carts
    ADD CONSTRAINT FK_SHOPPING_CARTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE wishlist_products
    ADD CONSTRAINT fk_wispro_on_product FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE wishlist_products
    ADD CONSTRAINT fk_wispro_on_wishlist FOREIGN KEY (wishlist_id) REFERENCES wishlists (id);
