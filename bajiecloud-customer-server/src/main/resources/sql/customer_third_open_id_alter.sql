ALTER TABLE `customer`
    ADD COLUMN `third_open_id` varchar(64) DEFAULT NULL COMMENT '第三方平台OpenID' AFTER `third_party_id`;
