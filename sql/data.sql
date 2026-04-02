-- 潮流商城 - 示例数据 (Sample Data)
-- Run schema.sql first, then this file

USE shopping;

-- Categories
INSERT INTO `category` (`name`, `icon`, `sort`, `parent_id`, `create_time`, `update_time`) VALUES
('手机数码', 'Iphone', 1, 0, NOW(), NOW()),
('电脑办公', 'Monitor', 2, 0, NOW(), NOW()),
('家用电器', 'Refrigerator', 3, 0, NOW(), NOW()),
('服装鞋帽', 'Shirt', 4, 0, NOW(), NOW()),
('美妆护肤', 'Present', 5, 0, NOW(), NOW()),
('运动户外', 'Football', 6, 0, NOW(), NOW()),
('图书音像', 'Reading', 7, 0, NOW(), NOW()),
('食品生鲜', 'Food', 8, 0, NOW(), NOW());

-- Products (手机数码)
INSERT INTO `product` (`name`, `description`, `price`, `stock`, `sales`, `category_id`, `main_image`, `images`, `status`, `create_time`, `update_time`) VALUES
('Apple iPhone 15 Pro 256GB 钛金属', 'A17 Pro芯片，钛金属机身，ProMotion自适应刷新率，USB-C接口，支持Apple Pay', 8999.00, 500, 1280, 1, 'https://via.placeholder.com/400x400/1a1a1a/ffffff?text=iPhone15Pro', 'https://via.placeholder.com/400x400/1a1a1a/ffffff?text=iPhone15Pro', 1, NOW(), NOW()),
('Samsung Galaxy S24 Ultra 512GB', 'Snapdragon 8 Gen 3，内置S Pen，200MP主摄，AI智能功能', 9299.00, 300, 856, 1, 'https://via.placeholder.com/400x400/2c3e50/ffffff?text=S24Ultra', 'https://via.placeholder.com/400x400/2c3e50/ffffff?text=S24Ultra', 1, NOW(), NOW()),
('小米14 Pro 256GB 陶瓷白', '第三代骁龙8，徕卡光学镜头，小米澎湃OS，无线充电80W', 4999.00, 800, 2340, 1, 'https://via.placeholder.com/400x400/ecf0f1/333333?text=Xiaomi14Pro', 'https://via.placeholder.com/400x400/ecf0f1/333333?text=Xiaomi14Pro', 1, NOW(), NOW()),
('Huawei Mate 60 Pro 512GB', '麒麟9000S芯片，卫星通话，昆仑玻璃，超级快充', 6999.00, 400, 1560, 1, 'https://via.placeholder.com/400x400/c0392b/ffffff?text=Mate60Pro', 'https://via.placeholder.com/400x400/c0392b/ffffff?text=Mate60Pro', 1, NOW(), NOW()),
('OnePlus 12 256GB', '骁龙8 Gen 3，哈苏影像，100W超级闪充，冰川蓝', 4299.00, 600, 980, 1, 'https://via.placeholder.com/400x400/3498db/ffffff?text=OnePlus12', 'https://via.placeholder.com/400x400/3498db/ffffff?text=OnePlus12', 1, NOW(), NOW()),
('AirPods Pro 第二代', '主动降噪，通透模式，H2芯片，USB-C充电', 1799.00, 1200, 3200, 1, 'https://via.placeholder.com/400x400/ffffff/333333?text=AirPodsPro2', 'https://via.placeholder.com/400x400/ffffff/333333?text=AirPodsPro2', 1, NOW(), NOW());

-- Products (电脑办公)
INSERT INTO `product` (`name`, `description`, `price`, `stock`, `sales`, `category_id`, `main_image`, `images`, `status`, `create_time`, `update_time`) VALUES
('MacBook Pro 14寸 M3 Pro', 'M3 Pro芯片，18GB统一内存，512GB SSD，Liquid Retina XDR显示屏', 15999.00, 200, 420, 2, 'https://via.placeholder.com/400x400/2c3e50/ffffff?text=MacBookPro', 'https://via.placeholder.com/400x400/2c3e50/ffffff?text=MacBookPro', 1, NOW(), NOW()),
('联想ThinkPad X1 Carbon', 'i7-1365U，16GB内存，512GB SSD，14寸2.8K OLED，商务轻薄', 12999.00, 150, 280, 2, 'https://via.placeholder.com/400x400/1a1a1a/ffffff?text=ThinkPadX1', 'https://via.placeholder.com/400x400/1a1a1a/ffffff?text=ThinkPadX1', 1, NOW(), NOW()),
('华为MateBook X Pro 2024', '酷睿Ultra 7，16GB内存，1TB SSD，14.2寸3.1K OLED', 9999.00, 300, 560, 2, 'https://via.placeholder.com/400x400/bdc3c7/333333?text=MateBookXPro', 'https://via.placeholder.com/400x400/bdc3c7/333333?text=MateBookXPro', 1, NOW(), NOW()),
('戴尔Dell XPS 15', 'i9-13900H，32GB DDR5，1TB SSD，15.6寸4K OLED触控', 16999.00, 100, 180, 2, 'https://via.placeholder.com/400x400/27ae60/ffffff?text=DellXPS15', 'https://via.placeholder.com/400x400/27ae60/ffffff?text=DellXPS15', 1, NOW(), NOW());

-- Products (服装鞋帽)
INSERT INTO `product` (`name`, `description`, `price`, `stock`, `sales`, `category_id`, `main_image`, `images`, `status`, `create_time`, `update_time`) VALUES
('Nike Air Max 270 运动鞋', '气垫缓震，透气网面鞋面，橡胶外底，男女同款', 899.00, 2000, 5600, 4, 'https://via.placeholder.com/400x400/e74c3c/ffffff?text=NikeAirMax', 'https://via.placeholder.com/400x400/e74c3c/ffffff?text=NikeAirMax', 1, NOW(), NOW()),
('Adidas Ultraboost 23', 'Boost缓震中底，Primeknit+鞋面，Continental橡胶外底', 1099.00, 1500, 4200, 4, 'https://via.placeholder.com/400x400/3498db/ffffff?text=Ultraboost23', 'https://via.placeholder.com/400x400/3498db/ffffff?text=Ultraboost23', 1, NOW(), NOW()),
('优衣库UNIQLO 摇粒绒外套', '双面摇粒绒，保暖舒适，多色可选，男女款', 299.00, 5000, 12000, 4, 'https://via.placeholder.com/400x400/e67e22/ffffff?text=UNIQLO摇粒绒', 'https://via.placeholder.com/400x400/e67e22/ffffff?text=UNIQLO摇粒绒', 1, NOW(), NOW()),
('ZARA 修身西装套装', '精纺面料，修身版型，商务休闲两用，含西裤', 799.00, 800, 1800, 4, 'https://via.placeholder.com/400x400/8e44ad/ffffff?text=ZARA西装', 'https://via.placeholder.com/400x400/8e44ad/ffffff?text=ZARA西装', 1, NOW(), NOW());

-- Products (美妆护肤)
INSERT INTO `product` (`name`, `description`, `price`, `stock`, `sales`, `category_id`, `main_image`, `images`, `status`, `create_time`, `update_time`) VALUES
('SK-II 神仙水 230ml', '烟酰胺，半乳糖酵母，透明质酸，改善肤质，柔嫩肌肤', 1080.00, 500, 2800, 5, 'https://via.placeholder.com/400x400/f1c40f/333333?text=SKII神仙水', 'https://via.placeholder.com/400x400/f1c40f/333333?text=SKII神仙水', 1, NOW(), NOW()),
('雅诗兰黛 小棕瓶精华 50ml', '密集修护，改善细纹，提亮肤色，国际畅销款', 780.00, 600, 3500, 5, 'https://via.placeholder.com/400x400/8e44ad/ffffff?text=雅诗兰黛小棕瓶', 'https://via.placeholder.com/400x400/8e44ad/ffffff?text=雅诗兰黛小棕瓶', 1, NOW(), NOW()),
('兰蔻小黑瓶精华肌底液 50ml', '腺苷，玻色因，透明质酸，肌底修护，紧致提升', 830.00, 400, 2100, 5, 'https://via.placeholder.com/400x400/1a1a1a/ffffff?text=兰蔻小黑瓶', 'https://via.placeholder.com/400x400/1a1a1a/ffffff?text=兰蔻小黑瓶', 1, NOW(), NOW());

-- Products (运动户外)
INSERT INTO `product` (`name`, `description`, `price`, `stock`, `sales`, `category_id`, `main_image`, `images`, `status`, `create_time`, `update_time`) VALUES
('迪卡侬 登山帐篷 2人', '铝合金杆，防水PU3000+，双层帐篷，收纳袋包装', 399.00, 1000, 3200, 6, 'https://via.placeholder.com/400x400/27ae60/ffffff?text=迪卡侬帐篷', 'https://via.placeholder.com/400x400/27ae60/ffffff?text=迪卡侬帐篷', 1, NOW(), NOW()),
('Keep 哑铃套装 10kg×2', '电镀哑铃，防腐防锈，固定重量，配置收纳架', 299.00, 2000, 5800, 6, 'https://via.placeholder.com/400x400/95a5a6/ffffff?text=Keep哑铃', 'https://via.placeholder.com/400x400/95a5a6/ffffff?text=Keep哑铃', 1, NOW(), NOW());

-- Products (图书音像)
INSERT INTO `product` (`name`, `description`, `price`, `stock`, `sales`, `category_id`, `main_image`, `images`, `status`, `create_time`, `update_time`) VALUES
('《深度学习》（花书）', 'Ian Goodfellow著，中文版，神经网络与机器学习权威教材', 129.00, 3000, 8000, 7, 'https://via.placeholder.com/400x400/2980b9/ffffff?text=深度学习花书', 'https://via.placeholder.com/400x400/2980b9/ffffff?text=深度学习花书', 1, NOW(), NOW()),
('《Java核心技术 卷I》第12版', 'Cay S. Horstmann著，涵盖Java SE 17，Java经典教程', 99.00, 2500, 6500, 7, 'https://via.placeholder.com/400x400/e74c3c/ffffff?text=Java核心技术', 'https://via.placeholder.com/400x400/e74c3c/ffffff?text=Java核心技术', 1, NOW(), NOW()),
('《Vue.js设计与实现》', '霍春阳著，Vue 3核心原理深度解析', 89.00, 1800, 4200, 7, 'https://via.placeholder.com/400x400/42b883/ffffff?text=Vue.js设计与实现', 'https://via.placeholder.com/400x400/42b883/ffffff?text=Vue.js设计与实现', 1, NOW(), NOW());

-- Products (食品生鲜)
INSERT INTO `product` (`name`, `description`, `price`, `stock`, `sales`, `category_id`, `main_image`, `images`, `status`, `create_time`, `update_time`) VALUES
('有机黑枸杞 100g', '青海柴达木有机黑枸杞，花青素丰富，花开品质', 88.00, 5000, 9800, 8, 'https://via.placeholder.com/400x400/8e44ad/ffffff?text=有机黑枸杞', 'https://via.placeholder.com/400x400/8e44ad/ffffff?text=有机黑枸杞', 1, NOW(), NOW()),
('进口牛油果 6枚装', '进口牛油果，成熟度均匀，富含不饱和脂肪酸，顺丰冷链', 59.00, 8000, 15000, 8, 'https://via.placeholder.com/400x400/27ae60/ffffff?text=牛油果', 'https://via.placeholder.com/400x400/27ae60/ffffff?text=牛油果', 1, NOW(), NOW());

-- Default users (password for both: admin123)
-- BCrypt hash of "admin123"
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `role`, `create_time`, `update_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@shopping.com', '13800138000', 'admin', NOW(), NOW()),
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'test@shopping.com', '13900139000', 'user', NOW(), NOW());
