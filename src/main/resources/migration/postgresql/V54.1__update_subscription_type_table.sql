DELETE FROM payment_record;
DELETE FROM subscription;
DELETE FROM subscription_type;
ALTER TABLE subscription_type DROP COLUMN is_free;
ALTER TABLE subscription_type DROP COLUMN document_cost;
ALTER TABLE subscription_type DROP COLUMN documents_count;
ALTER TABLE subscription_type ADD COLUMN subscription_option character varying (25) not null default 'AD';

INSERT INTO subscription_type(subscription_option, sort_position, name, cost, duration_days)
VALUES (
        'AD', 10, 'Реклама', 5000, 30
       );
INSERT INTO subscription_type(subscription_option, sort_position, name, cost, duration_days)
VALUES (
        'OPERATOR', 20, 'Оператор', 5000, 30
       );
