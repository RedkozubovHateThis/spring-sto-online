ALTER TABLE subscription ADD FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE users ADD FOREIGN KEY (current_subscription_id) REFERENCES subscription(id);
ALTER TABLE payment_record ADD FOREIGN KEY (subscription_id) REFERENCES subscription(id);
ALTER TABLE payment_record ADD FOREIGN KEY (subscription_addon_id) REFERENCES subscription_addon(id);
ALTER TABLE payment_record DROP COLUMN document_id;
