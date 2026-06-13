ALTER TABLE `customer`
    DROP INDEX `uk_third_party_source`;

CREATE INDEX `idx_customer_third_party_source`
    ON `customer` (`third_party_id`, `source_channel`);
