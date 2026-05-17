ALTER TABLE `customer_address`
    ADD COLUMN `province_code` varchar(20) DEFAULT NULL COMMENT '省编码' AFTER `receiver_mobile`,
    ADD COLUMN `province_name` varchar(50) DEFAULT NULL COMMENT '省名称' AFTER `province_code`,
    ADD COLUMN `city_code` varchar(20) DEFAULT NULL COMMENT '市编码' AFTER `province_name`,
    ADD COLUMN `city_name` varchar(50) DEFAULT NULL COMMENT '市名称' AFTER `city_code`,
    ADD COLUMN `area_name` varchar(50) DEFAULT NULL COMMENT '区县名称' AFTER `area_code`,
    ADD COLUMN `address_tag` varchar(50) DEFAULT NULL COMMENT '地址标签：家/公司/学校/父母/朋友/自定义' AFTER `address_type`,
    ADD COLUMN `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度' AFTER `address_tag`,
    ADD COLUMN `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度' AFTER `longitude`,
    ADD COLUMN `full_address` varchar(800) DEFAULT NULL COMMENT '完整地址快照' AFTER `street_address`;
