UPDATE service_work SET by_price = TRUE WHERE ( by_price IS NULL OR by_price = FALSE ) AND price IS NOT NULL AND price_norm IS NULL AND time_value IS NULL;
