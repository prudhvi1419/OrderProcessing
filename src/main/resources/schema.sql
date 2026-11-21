-- Defines commission_results explicitly so the month columns come first.
-- Runs on every startup; IF NOT EXISTS leaves an existing table untouched.
-- Other tables are managed by Hibernate (spring.jpa.hibernate.ddl-auto=update).
CREATE TABLE IF NOT EXISTS commission_results (
    id                BIGINT         NOT NULL AUTO_INCREMENT,
    commission_period VARCHAR(7)     NOT NULL,
    commission_month  VARCHAR(20)    NOT NULL,
    advisor_id        VARCHAR(255)   NOT NULL,
    order_count       BIGINT         NOT NULL,
    amount_per_sale   DECIMAL(19, 2) NOT NULL,
    commission_amount DECIMAL(19, 2) NOT NULL,
    total_sales       DECIMAL(19, 2) NOT NULL,
    calculated_at     DATETIME(6)    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_commission_advisor_period UNIQUE (advisor_id, commission_period)
);
