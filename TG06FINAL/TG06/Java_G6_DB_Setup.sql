-- 刪除資料表（依 FK 順序倒退刪除）
DROP TABLE IF EXISTS complain;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS orderdetail;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS validate;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS vendor;
DROP TABLE IF EXISTS manager;

-- 建立 vendor 表
CREATE TABLE vendor (
  vendor_id INT PRIMARY KEY AUTO_INCREMENT,
  register_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  vendor_name VARCHAR(20) ,
  owner_name VARCHAR(10) ,
  owner_phone VARCHAR(10) ,
  vendor_phone VARCHAR(10) ,
  account VARCHAR(20) NOT NULL,
  password VARCHAR(8) NOT NULL,
  address VARCHAR(50) ,
  email VARCHAR(50) ,
  type ENUM('餐廳', '飲料店', '其他'),
  description VARCHAR(500),
  map_url VARCHAR(200),
  status ENUM('已停權', '可使用', '待審核'),
  mon_open BOOLEAN DEFAULT 1,
  tue_open BOOLEAN DEFAULT 1,
  wed_open BOOLEAN DEFAULT 1,
  thu_open BOOLEAN DEFAULT 1, 
  fri_open BOOLEAN DEFAULT 1,
  sat_open BOOLEAN DEFAULT 1,
  sun_open BOOLEAN DEFAULT 1,
  mon_start_time TIME DEFAULT NULL,
  mon_end_time TIME DEFAULT NULL,
  tue_start_time TIME DEFAULT NULL,
  tue_end_time TIME DEFAULT NULL,
  wed_start_time TIME DEFAULT NULL,
  wed_end_time TIME DEFAULT NULL,
  thu_start_time TIME DEFAULT NULL,
  thu_end_time TIME DEFAULT NULL,
  fri_start_time TIME DEFAULT NULL,
  fri_end_time TIME DEFAULT NULL,
  sat_start_time TIME DEFAULT NULL,
  sat_end_time TIME DEFAULT NULL,
  sun_start_time TIME DEFAULT NULL,
  sun_end_time TIME DEFAULT NULL
);

-- 建立 product 表
CREATE TABLE product (
  product_id INT PRIMARY KEY AUTO_INCREMENT,
  vendor_id INT NOT NULL,
  name VARCHAR(20) NOT NULL,
  price INT NOT NULL,
  description TEXT,
  picture_url VARCHAR(200),
  status ENUM('供應中', '暫停供應') NOT NULL,
  FOREIGN KEY (vendor_id) REFERENCES vendor(vendor_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 建立 user 表
CREATE TABLE `user` (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(10) ,
  telephone VARCHAR(10) ,
  account VARCHAR(20) NOT NULL,
  password VARCHAR(8) NOT NULL,
  register_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  status ENUM('已停權', '可使用')
);

-- 建立 manager 表
CREATE TABLE manager (
  manager_id INT PRIMARY KEY AUTO_INCREMENT,
  account VARCHAR(20) NOT NULL,
  password VARCHAR(8) NOT NULL
);

-- 建立 validate 表
CREATE TABLE validate (
  validate_id INT PRIMARY KEY AUTO_INCREMENT,
  vendor_id INT NOT NULL,
  manager_id INT,
  status ENUM('已停權', '可使用', '待審核') NOT NULL,
  validate_date DATETIME,
  rejection_reason TEXT,
  FOREIGN KEY (vendor_id) REFERENCES vendor(vendor_id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (manager_id) REFERENCES manager(manager_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 建立 order 表
CREATE TABLE `order` (
  order_id INT PRIMARY KEY AUTO_INCREMENT,
  type ENUM('自取','外送') NOT NULL,
  vendor_id INT NOT NULL,
  user_id INT NOT NULL,
  location ENUM('到店自取', '政大正門取餐處', '政大東側門取餐處', '政大西側門取餐處', '自強十舍取餐處') NOT NULL,
  finish_time DATETIME NOT NULL,
  status ENUM('已拒絕', '待接單', '準備中', '待取餐', '待送餐', '已完成') NOT NULL,
  money INT NOT NULL,
  note TEXT,
  deliverman_id INT,
  FOREIGN KEY (vendor_id) REFERENCES vendor(vendor_id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (deliverman_id) REFERENCES manager(manager_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 建立 orderdetail 表
CREATE TABLE orderdetail (
  orderdetail_id INT PRIMARY KEY AUTO_INCREMENT,
  order_id INT NOT NULL,
  product_id INT NOT NULL,
  name VARCHAR(20) NOT NULL,
  price INT NOT NULL,
  quantity INT NOT NULL,
  customization TEXT,
  FOREIGN KEY (order_id) REFERENCES `order`(order_id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 建立 comment 表
CREATE TABLE comment (
  comment_id INT PRIMARY KEY AUTO_INCREMENT,
  vendor_id INT NOT NULL,
  user_id INT NOT NULL,
  comment_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
  review TEXT,
  FOREIGN KEY (vendor_id) REFERENCES vendor(vendor_id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 建立 complain 表
CREATE TABLE complain (
  complain_id INT PRIMARY KEY AUTO_INCREMENT,
  order_id INT NOT NULL,
  manager_id INT,
  user_id INT NOT NULL,
  type ENUM('送餐問題', '服務問題', '菜品問題') NOT NULL,
  complain_content TEXT NOT NULL,
  status ENUM('未回覆', '已回覆') NOT NULL,
  reply_content TEXT,
  FOREIGN KEY (order_id) REFERENCES `order`(order_id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (manager_id) REFERENCES manager(manager_id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Insert into vendor
INSERT INTO vendor(vendor_id,register_date,vendor_name,owner_name,owner_phone,vendor_phone,account,password,address,email,type,description,map_url,status,mon_open,tue_open,wed_open,thu_open,fri_open,sat_open,sun_open,mon_start_time,mon_end_time,tue_start_time,tue_end_time,wed_start_time,wed_end_time,thu_start_time,thu_end_time,fri_start_time,fri_end_time,sat_start_time,sat_end_time,sun_start_time,sun_end_time) VALUES (1,'2025-01-01','八方雲集','趙庭豪','0900111222','0229001111','vendor_0001','00000001','台北市文山區指南路二段47號','vendor_0001@gmail.com','餐廳','道地美味平價實惠的煎餃、水餃，適、與麵類，學生吃飯的好選擇。','https://maps.app.goo.gl/dXWCHpFBhh2hrPAF7','可使用',1,1,1,1,1,1,1,'11:00:00','19:30:00','11:00:00','19:30:00','11:00:00','19:30:00','11:00:00','19:30:00','11:00:00','19:30:00','11:00:00','19:30:00','11:00:00','19:30:00');
INSERT INTO vendor(vendor_id,register_date,vendor_name,owner_name,owner_phone,vendor_phone,account,password,address,email,type,description,map_url,status,mon_open,tue_open,wed_open,thu_open,fri_open,sat_open,sun_open,mon_start_time,mon_end_time,tue_start_time,tue_end_time,wed_start_time,wed_end_time,thu_start_time,thu_end_time,fri_start_time,fri_end_time,sat_start_time,sat_end_time,sun_start_time,sun_end_time) VALUES (2,'2025-01-02','得正','張俊傑','0900111223','0229001112','vendor_0002','00000002','台北市文山區華興里指南路二段65號','vendor_0002@gmail.com','飲料店','最好喝的茶類手搖飲，堅持品質與口感。','https://maps.app.goo.gl/w7zhSLJyRPqENhe77','可使用',1,1,1,1,1,1,1,'11:00:00','21:00:00','11:00:00','21:00:00','11:00:00','21:00:00','11:00:00','21:00:00','11:00:00','21:00:00','11:00:00','21:00:00','11:00:00','17:30:00');
INSERT INTO vendor(vendor_id,register_date,vendor_name,owner_name,owner_phone,vendor_phone,account,password,address,email,type,description,map_url,status,mon_open,tue_open,wed_open,thu_open,fri_open,sat_open,sun_open,mon_start_time,mon_end_time,tue_start_time,tue_end_time,wed_start_time,wed_end_time,thu_start_time,thu_end_time,fri_start_time,fri_end_time,sat_start_time,sat_end_time,sun_start_time,sun_end_time) VALUES (3,'2025-01-03','波波恰恰大馬風味餐','趙思妤','0900111224','0229001113','vendor_0003','00000003','台北市文山區指南路二段48號','vendor_0003@gmail.com','餐廳','異國餐點餐點搭配舒適空間，是你午晚餐首選。','https://maps.app.goo.gl/zBDiJc9df22fjmMh6','可使用',1,1,1,1,1,0,1,'11:00:00','20:00:00','11:00:00','20:00:00','11:00:00','20:00:00','11:00:00','20:00:00','11:00:00','20:00:00','11:00:00','20:00:00','11:00:00','20:00:00');
INSERT INTO vendor(vendor_id,register_date,vendor_name,owner_name,owner_phone,vendor_phone,account,password,address,email,type,description,map_url,status,mon_open,tue_open,wed_open,thu_open,fri_open,sat_open,sun_open,mon_start_time,mon_end_time,tue_start_time,tue_end_time,wed_start_time,wed_end_time,thu_start_time,thu_end_time,fri_start_time,fri_end_time,sat_start_time,sat_end_time,sun_start_time,sun_end_time) VALUES (4,'2025-01-04','小木屋鬆餅','周琳琳','0900111225','0229001114','vendor_0004','00000004','台北市文山區指南路二段97號','vendor_0004@gmail.com','其他','好吃鬆餅，現點現做、快速方便，幸福感滿滿。','https://maps.app.goo.gl/aJdTyHUoyEoxnMZB8','可使用',1,1,1,1,1,1,1,'10:30:00','20:00:00','10:30:00','20:00:00','10:30:00','20:00:00','10:30:00','20:00:00','10:30:00','20:00:00','10:30:00','20:00:00','10:30:00','20:00:00');
INSERT INTO vendor(vendor_id,register_date,vendor_name,owner_name,owner_phone,vendor_phone,account,password,address,email,type,description,map_url,status,mon_open,tue_open,wed_open,thu_open,fri_open,sat_open,sun_open,mon_start_time,mon_end_time,tue_start_time,tue_end_time,wed_start_time,wed_end_time,thu_start_time,thu_end_time,fri_start_time,fri_end_time,sat_start_time,sat_end_time,sun_start_time,sun_end_time) VALUES (5,'2025-01-05','食鼎鵝肉','楊佩珊','0900111226','0229001115','vendor_0005','00000005','台北市文山區萬壽路7號','vendor_0005@gmail.com','餐廳','主打招牌鵝肉與家常料理，吃出家的味道。','https://maps.app.goo.gl/VL2FvWPSRtajywmV9','可使用',0,0,1,1,1,1,1,'11:00:00','19:30:00','11:00:00','19:30:00','11:00:00','19:30:00','11:00:00','19:30:00','11:00:00','19:30:00','11:00:00','19:30:00','11:00:00','19:30:00');
INSERT INTO vendor(vendor_id,register_date,vendor_name,owner_name,owner_phone,vendor_phone,account,password,address,email,type,description,map_url,status,mon_open,tue_open,wed_open,thu_open,fri_open,sat_open,sun_open,mon_start_time,mon_end_time,tue_start_time,tue_end_time,wed_start_time,wed_end_time,thu_start_time,thu_end_time,fri_start_time,fri_end_time,sat_start_time,sat_end_time,sun_start_time,sun_end_time) VALUES (6,'2025-01-06','政大茶亭','李子軒','0900111227','0229001116','vendor_0006','00000006','台北市文山區指南路二段119巷117號','vendor_0006@gmail.com','飲料店','嚴選蔬果、手工現榨，清涼解渴首選。','https://maps.app.goo.gl/a49fBbrQEAE6C99XA','可使用',1,1,1,1,1,1,1,'10:00:00','21:00:00','10:00:00','21:00:00','10:00:00','21:00:00','10:00:00','21:00:00','10:00:00','21:00:00','10:00:00','21:00:00','10:00:00','21:00:00');
INSERT INTO vendor(vendor_id,register_date,vendor_name,owner_name,owner_phone,vendor_phone,account,password,address,email,type,description,map_url,status,mon_open,tue_open,wed_open,thu_open,fri_open,sat_open,sun_open,mon_start_time,mon_end_time,tue_start_time,tue_end_time,wed_start_time,wed_end_time,thu_start_time,thu_end_time,fri_start_time,fri_end_time,sat_start_time,sat_end_time,sun_start_time,sun_end_time) VALUES (7,'2025-01-07','梁社漢排骨','吳怡君','0900111228','0229001117','vendor_0007','00000007','台北市文山區指南路二段127號','vendor_0007@gmail.com','餐廳','道地美味平價實惠，適合學生的好選擇。','https://maps.app.goo.gl/8gZ4MEy95Ex9BNYX9','可使用',1,1,1,1,1,1,1,'10:30:00','20:30:00','10:30:00','20:30:00','10:30:00','20:30:00','10:30:00','20:30:00','10:30:00','20:30:00','10:30:00','20:30:00','10:30:00','20:30:00');
INSERT INTO vendor(vendor_id,register_date,vendor_name,owner_name,owner_phone,vendor_phone,account,password,address,email,type,description,map_url,status,mon_open,tue_open,wed_open,thu_open,fri_open,sat_open,sun_open,mon_start_time,mon_end_time,tue_start_time,tue_end_time,wed_start_time,wed_end_time,thu_start_time,thu_end_time,fri_start_time,fri_end_time,sat_start_time,sat_end_time,sun_start_time,sun_end_time) VALUES (8,'2025-01-08','政大烤場','張志明','0900111229','0229001118','vendor_0008','00000008','台北市文山區指南路二段137 號','vendor_0008@gmail.com','其他','提供多樣炸物，政大附近宵夜首選。','https://maps.app.goo.gl/8YEkEEBqkLaeuJaR6','可使用',1,1,1,1,1,1,1,'16:00:00','23:00:00','16:00:00','23:00:00','16:00:00','23:00:00','16:00:00','23:00:00','16:00:00','23:00:00','16:00:00','23:00:00','16:00:00','23:00:00');
INSERT INTO vendor(vendor_id,register_date,vendor_name,owner_name,owner_phone,vendor_phone,account,password,address,email,type,description,map_url,status,mon_open,tue_open,wed_open,thu_open,fri_open,sat_open,sun_open,mon_start_time,mon_end_time,tue_start_time,tue_end_time,wed_start_time,wed_end_time,thu_start_time,thu_end_time,fri_start_time,fri_end_time,sat_start_time,sat_end_time,sun_start_time,sun_end_time) VALUES (9,'2025-01-09','口福豆漿','劉嘉豪','0900111230','0229001119','vendor_0009','00000009','台北市文山區萬壽路21號','vendor_0009@gmail.com','其他','台灣風味的早餐店，深受在地人喜愛。','https://maps.app.goo.gl/HcCnD1RqEKEos9TJ7','可使用',0,1,1,1,1,1,1,'06:00:00','11:00:00','06:00:00','11:00:00','06:00:00','11:00:00','06:00:00','11:00:00','06:00:00','11:00:00','06:00:00','11:00:00','06:00:00','11:00:00');
INSERT INTO vendor(vendor_id,register_date,vendor_name,owner_name,owner_phone,vendor_phone,account,password,address,email,type,description,map_url,status,mon_open,tue_open,wed_open,thu_open,fri_open,sat_open,sun_open,mon_start_time,mon_end_time,tue_start_time,tue_end_time,wed_start_time,wed_end_time,thu_start_time,thu_end_time,fri_start_time,fri_end_time,sat_start_time,sat_end_time,sun_start_time,sun_end_time) VALUES (10,'2025-01-10','滇味廚房','王雅婷','0900111231','0229001120','vendor_0010','00000010','台北市文山區指南路二段167號','vendor_0010@gmail.com','餐廳','主打雲南家常料理，酸辣口感令人喜愛。','https://maps.app.goo.gl/EjB69anbZsCfTBRF6','可使用',1,1,1,1,1,1,1,'11:00:00','21:00:00','11:00:00','21:00:00','11:00:00','21:00:00','11:00:00','21:00:00','11:00:00','21:00:00','11:00:00','21:00:00','11:00:00','21:00:00');
INSERT INTO vendor(vendor_id,register_date,vendor_name,owner_name,owner_phone,vendor_phone,account,password,address,email,type,description,map_url,status,mon_open,tue_open,wed_open,thu_open,fri_open,sat_open,sun_open,mon_start_time,mon_end_time,tue_start_time,tue_end_time,wed_start_time,wed_end_time,thu_start_time,thu_end_time,fri_start_time,fri_end_time,sat_start_time,sat_end_time,sun_start_time,sun_end_time) VALUES (11,'2025-01-11','政大原汁牛肉麵','陳國安','0900111232','0229001121','vendor_0011','00000011','台北市文山區指南路二段59號','vendor_0011@gmail.com','餐廳','文山區最佳牛肉麵，牛肉麵裡保證有牛肉。','https://maps.app.goo.gl/7ud6DNuCSQ7fw9EU6','待審核',0,1,1,1,1,1,1,'11:00:00','21:00:00','11:00:00','21:00:00','11:00:00','21:00:00','11:00:00','21:00:00','11:00:00','21:00:00','11:00:00','21:00:00','11:00:00','21:00:00');
INSERT INTO vendor(vendor_id,register_date,vendor_name,owner_name,owner_phone,vendor_phone,account,password,address,email,type,description,map_url,status,mon_open,tue_open,wed_open,thu_open,fri_open,sat_open,sun_open,mon_start_time,mon_end_time,tue_start_time,tue_end_time,wed_start_time,wed_end_time,thu_start_time,thu_end_time,fri_start_time,fri_end_time,sat_start_time,sat_end_time,sun_start_time,sun_end_time) VALUES (12,'2025-01-12','吉野家','陳佑民','0900111233','0229001122','vendor_0012','00000012','台北市文山區指南路二段103號','vendor_0012@gmail.com','餐廳','日本最紅連鎖牛丼品牌，雖然我的店倒了但Sukiya算什麼東西。','https://maps.app.goo.gl/2dt3jDUKqTSbaUAs8','已停權',1,1,1,1,1,1,1,'10:00:00','21:30:00','10:00:00','21:30:00','10:00:00','21:30:00','10:00:00','21:30:00','10:00:00','21:30:00','10:00:00','21:30:00','10:00:00','21:30:00');
INSERT INTO vendor(vendor_id,register_date,vendor_name,owner_name,owner_phone,vendor_phone,account,password,address,email,type,description,map_url,status,mon_open,tue_open,wed_open,thu_open,fri_open,sat_open,sun_open,mon_start_time,mon_end_time,tue_start_time,tue_end_time,wed_start_time,wed_end_time,thu_start_time,thu_end_time,fri_start_time,fri_end_time,sat_start_time,sat_end_time,sun_start_time,sun_end_time) VALUES (13,'2025-01-13','饗食天堂自九店','李蔡彥','0900111234','0229001123','vendor_0013','00000013','台北市文山區指南路二段64號','vendor_0013@gmail.com','餐廳','宿舍冰箱任你偷，自九饗食天堂盛大開幕，繳交宿舍費用即可一年365天吃到飽！','https://maps.app.goo.gl/pQonYc2MABo8m8Px6','已停權',1,1,1,1,1,1,1,'00:00:00','23:59:59','00:00:00','23:59:59','00:00:00','23:59:59','00:00:00','23:59:59','00:00:00','23:59:59','00:00:00','23:59:59','00:00:00','23:59:59');

-- Insert into product
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (1,1,'招牌水餃',7,'手工製作、內餡飽滿，經典推薦水餃。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_1.png','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (2,1,'韭菜水餃',7,'韭菜香氣濃郁，皮薄多汁，人氣必點。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_2.png','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (3,1,'招牌鍋貼',7,'外酥內嫩、煎得恰到好處的招牌鍋貼。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_3.png','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (4,1,'韭菜鍋貼',7,'韭菜風味鍋貼，酥香不膩，回味無窮。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_4.png','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (5,1,'酸辣湯',35,'酸辣開胃，湯頭濃郁，是搭配主食的最佳選擇。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_5.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (6,2,'檸檬春烏龍',55,'清香檸檬與烏龍茶融合，沁涼爽口。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_6.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (7,2,'香橙春烏龍',65,'橙香濃郁，解渴又有層次感。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_7.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (8,2,'甘蔗春烏龍',65,'甘蔗自然甘甜，茶香醇厚不苦澀。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_8.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (9,2,'焙烏龍奶茶',50,'炭焙烏龍與奶香的完美結合，回甘順口。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_9.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (10,2,'紅茶鮮奶',65,'精選紅茶搭配濃醇鮮奶，香醇順口。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_10.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (11,3,'大馬沙爹雞肉飯',125,'馬來風味沙爹醬，搭配嫩雞與香飯，經典之選。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_11.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (12,3,'海南雞飯',125,'正宗南洋海南雞，肉質滑嫩，香氣十足。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_12.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (13,3,'肉骨茶麵',120,'濃郁中藥湯頭與細麵完美融合，滋補養生。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_13.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (14,3,'大馬叻沙麵',130,'叻沙湯頭香辣濃郁，配料豐富，南洋風味十足。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_14.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (15,3,'油雞腿飯',135,'醬香入味的雞腿搭配白飯，份量十足。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_15.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (16,4,'蜂蜜鬆餅',40,'鬆軟香甜，蜂蜜自然香氣撲鼻，是甜點好選擇。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_16.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (17,4,'巧克力鬆餅',60,'濃郁巧克力淋醬，甜而不膩，療癒首選。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_17.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (18,4,'藍莓卡士達鬆餅',85,'卡士達香濃與藍莓微酸，口感層次豐富。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_18.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (19,4,'芋泥肉鬆夾心鬆餅',80,'綿密芋泥與鹹香肉鬆完美結合，甜鹹交融。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_19.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (20,4,'紐奧良雞腿蔬菜鬆餅',95,'鬆餅中夾入多汁雞腿與新鮮蔬菜，營養滿分。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_20.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (21,5,'經典美味餐',60,'經典組合，簡單美味。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_a.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (22,5,'小資精省餐',80,'加量不加價，配料豐富、份量剛剛好。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_b.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (23,5,'健康元氣餐',100,'豐富搭配，一次滿足。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_c.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (24,5,'頂級尊享餐',120,'高級配置，適合講究的一餐。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_d.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (25,5,'極致滿足餐',140,'超值套餐，滿足大胃王的最佳選擇。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_e.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (26,6,'經典美味餐',60,'經典組合，簡單美味。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_a.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (27,6,'小資精省餐',80,'加量不加價，配料豐富、份量剛剛好。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_b.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (28,6,'健康元氣餐',100,'豐富搭配，一次滿足。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_c.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (29,6,'頂級尊享餐',120,'高級配置，適合講究的一餐。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_d.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (30,6,'極致滿足餐',140,'超值套餐，滿足大胃王的最佳選擇。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_e.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (31,7,'經典美味餐',60,'經典組合，簡單美味。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_a.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (32,7,'小資精省餐',80,'加量不加價，配料豐富、份量剛剛好。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_b.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (33,7,'健康元氣餐',100,'豐富搭配，一次滿足。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_c.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (34,7,'頂級尊享餐',120,'高級配置，適合講究的一餐。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_d.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (35,7,'極致滿足餐',140,'超值套餐，滿足大胃王的最佳選擇。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_e.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (36,8,'經典美味餐',60,'經典組合，簡單美味。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_a.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (37,8,'小資精省餐',80,'加量不加價，配料豐富、份量剛剛好。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_b.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (38,8,'健康元氣餐',100,'豐富搭配，一次滿足。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_c.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (39,8,'頂級尊享餐',120,'高級配置，適合講究的一餐。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_d.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (40,8,'極致滿足餐',140,'超值套餐，滿足大胃王的最佳選擇。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_e.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (41,9,'經典美味餐',60,'經典組合，簡單美味。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_a.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (42,9,'小資精省餐',80,'加量不加價，配料豐富、份量剛剛好。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_b.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (43,9,'健康元氣餐',100,'豐富搭配，一次滿足。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_c.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (44,9,'頂級尊享餐',120,'高級配置，適合講究的一餐。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_d.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (45,9,'極致滿足餐',140,'超值套餐，滿足大胃王的最佳選擇。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_e.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (46,10,'經典美味餐',60,'經典組合，簡單美味。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_a.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (47,10,'小資精省餐',80,'加量不加價，配料豐富、份量剛剛好。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_b.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (48,10,'健康元氣餐',100,'豐富搭配，一次滿足。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_c.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (49,10,'頂級尊享餐',120,'高級配置，適合講究的一餐。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_d.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (50,10,'極致滿足餐',140,'超值套餐，滿足大胃王的最佳選擇。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_e.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (51,11,'經典美味餐',60,'經典組合，簡單美味。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_a.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (52,11,'小資精省餐',80,'加量不加價，配料豐富、份量剛剛好。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_b.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (53,11,'健康元氣餐',100,'豐富搭配，一次滿足。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_c.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (54,11,'頂級尊享餐',120,'高級配置，適合講究的一餐。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_d.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (55,11,'極致滿足餐',140,'超值套餐，滿足大胃王的最佳選擇。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_e.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (56,12,'經典美味餐',60,'經典組合，簡單美味。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_a.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (57,12,'小資精省餐',80,'加量不加價，配料豐富、份量剛剛好。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_b.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (58,12,'健康元氣餐',100,'豐富搭配，一次滿足。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_c.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (59,12,'頂級尊享餐',120,'高級配置，適合講究的一餐。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_d.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (60,12,'極致滿足餐',140,'超值套餐，滿足大胃王的最佳選擇。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_e.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (61,13,'經典美味餐',60,'經典組合，簡單美味。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_a.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (62,13,'小資精省餐',80,'加量不加價，配料豐富、份量剛剛好。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_b.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (63,13,'健康元氣餐',100,'豐富搭配，一次滿足。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_c.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (64,13,'頂級尊享餐',120,'高級配置，適合講究的一餐。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_d.jpeg','供應中');
INSERT INTO product(product_id,vendor_id,name,price,description,picture_url,status) VALUES (65,13,'極致滿足餐',140,'超值套餐，滿足大胃王的最佳選擇。','https://storage.googleapis.com/nccu_java_g6_final_project/img/product_e.jpeg','供應中');

-- Insert into user
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (1,'方國軒','0900111222','user_0001','00000001','2025-01-01','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (2,'李大同','0900111223','user_0002','00000002','2025-01-02','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (3,'林郁民','0900111224','user_0003','00000003','2025-01-03','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (4,'陳奕謙','0900111225','user_0004','00000004','2025-01-04','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (5,'沈心怡','0900111226','user_0005','00000005','2025-01-05','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (6,'張育嘉','0900111227','user_0006','00000006','2025-01-06','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (7,'林永嬅','0900111228','user_0007','00000007','2025-01-07','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (8,'高欣恬','0900111229','user_0008','00000008','2025-01-08','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (9,'魏兆祥','0900111230','user_0009','00000009','2025-01-09','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (10,'王元鼎','0900111231','user_0010','00000010','2025-01-10','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (11,'王子豪','0900111232','user_0011','00000011','2025-01-11','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (12,'陳雅婷','0900111233','user_0012','00000012','2025-01-12','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (13,'張志偉','0900111234','user_0013','00000013','2025-01-13','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (14,'李思妤','0900111235','user_0014','00000014','2025-01-14','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (15,'吳俊傑','0900111236','user_0015','00000015','2025-01-15','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (16,'楊怡君','0900111237','user_0016','00000016','2025-01-16','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (17,'劉家豪','0900111238','user_0017','00000017','2025-01-17','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (18,'趙美玲','0900111239','user_0018','00000018','2025-01-18','可使用');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (19,'黃俊廷','0900111240','user_0019','00000019','2025-01-19','已停權');
INSERT INTO user(user_id,name,telephone,account,password,register_date,status) VALUES (20,'周佩珊','0900111241','user_0020','00000020','2025-01-20','已停權');


-- Insert into manager
INSERT INTO manager(manager_id,account,password) VALUES (1,'manager_0001','00000001');
INSERT INTO manager(manager_id,account,password) VALUES (2,'manager_0002','00000002');
INSERT INTO manager(manager_id,account,password) VALUES (3,'manager_0003','00000003');
INSERT INTO manager(manager_id,account,password) VALUES (4,'manager_0004','00000004');

-- Insert into validate
INSERT INTO validate(validate_id,vendor_id,manager_id,status,validate_date,rejection_reason) VALUES (1,1,1,'可使用','2025-01-11',NULL);
INSERT INTO validate(validate_id,vendor_id,manager_id,status,validate_date,rejection_reason) VALUES (2,2,1,'可使用','2025-01-12',NULL);
INSERT INTO validate(validate_id,vendor_id,manager_id,status,validate_date,rejection_reason) VALUES (3,3,1,'可使用','2025-01-13',NULL);
INSERT INTO validate(validate_id,vendor_id,manager_id,status,validate_date,rejection_reason) VALUES (4,4,2,'可使用','2025-01-14',NULL);
INSERT INTO validate(validate_id,vendor_id,manager_id,status,validate_date,rejection_reason) VALUES (5,5,2,'可使用','2025-01-15',NULL);
INSERT INTO validate(validate_id,vendor_id,manager_id,status,validate_date,rejection_reason) VALUES (6,6,2,'可使用','2025-01-16',NULL);
INSERT INTO validate(validate_id,vendor_id,manager_id,status,validate_date,rejection_reason) VALUES (7,7,3,'可使用','2025-01-17',NULL);
INSERT INTO validate(validate_id,vendor_id,manager_id,status,validate_date,rejection_reason) VALUES (8,8,3,'可使用','2025-01-18',NULL);
INSERT INTO validate(validate_id,vendor_id,manager_id,status,validate_date,rejection_reason) VALUES (9,9,3,'可使用','2025-01-19',NULL);
INSERT INTO validate(validate_id,vendor_id,manager_id,status,validate_date,rejection_reason) VALUES (10,10,4,'可使用','2025-01-20',NULL);
INSERT INTO validate(validate_id,vendor_id,manager_id,status,validate_date,rejection_reason) VALUES (11,11,4,'待審核','2025-01-21',NULL);
INSERT INTO validate(validate_id,vendor_id,manager_id,status,validate_date,rejection_reason) VALUES (12,12,4,'可使用','2025-01-22',NULL);
INSERT INTO validate(validate_id,vendor_id,manager_id,status,validate_date,rejection_reason) VALUES (13,13,4,'已停權','2025-01-23','查無營業執照，非合法餐廳，故拒絕申請。');
INSERT INTO validate(validate_id,vendor_id,manager_id,status,validate_date,rejection_reason) VALUES (14,12,1,'已停權','2025-03-24','經營不善，已於先前倒閉。');

-- Insert into order
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (1,'自取',1,1,'到店自取','2025-05-01 12:00:00','已完成',70,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (2,'外送',2,1,'政大正門取餐處','2025-05-09 12:00:00','待接單',430,'要請老師喝的，都不需要附吸管謝謝，老師想保護海龜！',3);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (3,'自取',2,2,'到店自取','2025-05-03 12:00:00','已拒絕',55,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (4,'自取',2,3,'到店自取','2025-05-04 18:00:00','已完成',65,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (5,'外送',2,4,'政大東側門取餐處','2025-05-05 12:00:00','待送餐',65,NULL,4);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (6,'自取',2,5,'到店自取','2025-05-05 14:00:00','待取餐',50,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (7,'自取',2,6,'到店自取','2025-05-07 12:00:00','準備中',65,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (8,'自取',2,7,'到店自取','2025-05-08 20:00:00','待接單',55,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (9,'外送',2,8,'自強十舍取餐處','2025-05-01 12:00:00','已完成',65,NULL,3);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (10,'自取',2,9,'到店自取','2025-05-02 12:00:00','已完成',65,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (11,'外送',2,10,'政大西側門取餐處','2025-05-03 12:00:00','已拒絕',50,NULL,4);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (12,'自取',2,11,'到店自取','2025-05-04 18:00:00','已完成',65,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (13,'自取',2,12,'到店自取','2025-05-05 12:00:00','待取餐',55,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (14,'外送',2,13,'政大正門取餐處','2025-05-06 19:00:00','待送餐',65,NULL,3);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (15,'自取',2,14,'到店自取','2025-05-07 12:00:00','準備中',65,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (16,'自取',2,15,'到店自取','2025-05-08 20:00:00','待接單',50,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (17,'自取',2,16,'到店自取','2025-05-01 12:00:00','已完成',65,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (18,'外送',2,17,'政大東側門取餐處','2025-05-02 12:00:00','已完成',55,NULL,4);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (19,'自取',2,18,'到店自取','2025-05-03 12:00:00','已拒絕',65,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (20,'外送',3,1,'政大東側門取餐處','2025-05-10 18:00:00','待接單',1015,'配菜都不要加三色豆，我朋友們看到三色豆會想吐 :(',3);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (21,'自取',3,2,'到店自取','2025-05-05 12:00:00','待取餐',125,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (22,'自取',3,3,'到店自取','2025-05-06 19:00:00','待取餐',125,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (23,'外送',3,4,'政大西側門取餐處','2025-05-07 12:00:00','準備中',120,NULL,4);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (24,'自取',3,5,'到店自取','2025-05-08 20:00:00','待接單',130,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (25,'自取',3,6,'到店自取','2025-05-01 12:00:00','已完成',135,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (26,'自取',3,7,'到店自取','2025-05-02 12:00:00','已完成',125,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (27,'外送',3,8,'自強十舍取餐處','2025-05-03 12:00:00','已拒絕',125,NULL,3);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (28,'自取',3,9,'到店自取','2025-05-04 18:00:00','已完成',120,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (29,'外送',3,10,'政大正門取餐處','2025-05-05 12:00:00','待送餐',130,NULL,4);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (30,'自取',3,11,'到店自取','2025-05-06 19:00:00','待取餐',135,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (31,'自取',3,12,'到店自取','2025-05-07 12:00:00','準備中',125,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (32,'外送',3,13,'政大東側門取餐處','2025-05-08 20:00:00','待接單',125,NULL,3);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (33,'自取',3,14,'到店自取','2025-05-01 12:00:00','已完成',120,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (34,'自取',3,15,'到店自取','2025-05-02 12:00:00','已完成',130,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (35,'自取',3,16,'到店自取','2025-05-03 12:00:00','已拒絕',135,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (36,'外送',3,17,'政大正門取餐處','2025-05-04 18:00:00','已完成',125,NULL,4);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (37,'自取',3,18,'到店自取','2025-05-01 12:00:00','已完成',125,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (38,'外送',4,1,'政大西側門取餐處','2025-05-02 12:00:00','已完成',40,NULL,3);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (39,'自取',5,1,'到店自取','2025-05-03 12:00:00','已完成',60,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (40,'自取',6,1,'到店自取','2025-05-04 18:00:00','已完成',80,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (41,'外送',7,1,'自強十舍取餐處','2025-05-05 12:00:00','待送餐',100,NULL,4);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (42,'自取',8,1,'到店自取','2025-05-05 14:00:00','待取餐',120,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (43,'自取',9,1,'到店自取','2025-05-07 12:00:00','準備中',140,NULL,NULL);
INSERT INTO `order`(order_id,type,vendor_id,user_id,location,finish_time,status,money,note,deliverman_id) VALUES (44,'自取',10,1,'到店自取','2025-05-08 20:00:00','準備中',100,NULL,NULL);

-- Insert into orderdetail
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (1,1,1,'招牌水餃',7,10,NULL);
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (2,2,6,'檸檬春烏龍',55,1,'微糖少冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (3,2,7,'香橙春烏龍',65,2,'都無糖去冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (4,2,8,'甘蔗春烏龍',65,2,'都常溫正常甜');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (5,2,9,'焙烏龍奶茶',50,1,'無糖去冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (6,2,10,'紅茶鮮奶',65,1,'微糖少冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (7,3,6,'檸檬春烏龍',55,1,'微糖微冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (8,4,7,'香橙春烏龍',65,1,'無糖去冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (9,5,8,'甘蔗春烏龍',65,1,'微糖少冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (10,6,9,'焙烏龍奶茶',50,1,'微糖微冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (11,7,10,'紅茶鮮奶',65,1,'無糖去冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (12,8,6,'檸檬春烏龍',55,1,'微糖微冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (13,9,7,'香橙春烏龍',65,1,'正常甜度冰塊');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (14,10,8,'甘蔗春烏龍',65,1,'微糖微冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (15,11,9,'焙烏龍奶茶',50,1,'無糖去冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (16,12,10,'紅茶鮮奶',65,1,'微糖微冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (17,13,6,'檸檬春烏龍',55,1,'無糖去冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (18,14,7,'香橙春烏龍',65,1,'微糖微冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (19,15,8,'甘蔗春烏龍',65,1,'微糖微冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (20,16,9,'焙烏龍奶茶',50,1,'微糖少冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (21,17,10,'紅茶鮮奶',65,1,'無糖去冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (22,18,6,'檸檬春烏龍',55,1,'常溫，加購袋子');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (23,19,7,'香橙春烏龍',65,1,'無糖去冰');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (24,20,11,'大馬沙爹雞肉飯',125,2,'都不要油蔥，飯多');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (25,20,12,'海南雞飯',125,2,'都不要油蔥');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (26,20,13,'肉骨茶麵',120,1,'飯少');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (27,20,14,'大馬叻沙麵',130,2,'都要油蔥');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (28,20,15,'油雞腿飯',135,1,'要油蔥，飯多');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (29,21,11,'大馬沙爹雞肉飯',125,1,'不要油蔥，飯多');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (30,22,12,'海南雞飯',125,1,'不要油蔥');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (31,23,13,'肉骨茶麵',120,1,'飯少');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (32,24,14,'大馬叻沙麵',130,1,'要油蔥');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (33,25,15,'油雞腿飯',135,1,'要油蔥，飯多');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (34,26,11,'大馬沙爹雞肉飯',125,1,'不要油蔥，飯多');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (35,27,12,'海南雞飯',125,1,'不要油蔥');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (36,28,13,'肉骨茶麵',120,1,'飯少');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (37,29,14,'大馬叻沙麵',130,1,'要油蔥');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (38,30,15,'油雞腿飯',135,1,'要油蔥，飯多');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (39,31,11,'大馬沙爹雞肉飯',125,1,'不要油蔥，飯多');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (40,32,12,'海南雞飯',125,1,'不要油蔥');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (41,33,13,'肉骨茶麵',120,1,'飯少');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (42,34,14,'大馬叻沙麵',130,1,'要油蔥');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (43,35,15,'油雞腿飯',135,1,'要油蔥，飯多');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (44,36,11,'大馬沙爹雞肉飯',125,1,'不要油蔥，飯多');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (45,37,12,'海南雞飯',125,1,'不要油蔥');
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (46,38,16,'蜂蜜鬆餅',40,1,NULL);
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (47,39,21,'經典美味餐',60,1,NULL);
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (48,40,27,'小資精省餐',80,1,NULL);
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (49,41,33,'健康元氣餐',100,1,NULL);
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (50,42,39,'頂級尊享餐',120,1,NULL);
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (51,43,45,'極致滿足餐',140,1,NULL);
INSERT INTO orderdetail(orderdetail_id,order_id,product_id,name,price,quantity,customization) VALUES (52,44,48,'健康元氣餐',100,1,NULL);

-- Insert into comment
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (1,1,1,'2025-05-02 12:00:00',5,'食材新鮮，吃得出店家的用心。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (2,2,1,'2025-05-10 12:00:00',4,'主餐好吃，飲料普通。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (3,2,2,'2025-05-04 12:00:00',3,'口味不夠穩定，有時好有時差。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (4,2,3,'2025-05-05 18:00:00',5,'食材來源透明，讓人吃得安心。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (5,2,4,'2025-05-06 12:00:00',5,'很有水準，推薦給愛吃甜的朋友。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (6,2,5,'2025-05-06 14:00:00',5,'味道很講究，超出期待。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (7,2,6,'2025-05-08 12:00:00',4,'有些品項驚喜，有些則普通。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (8,2,7,'2025-05-09 20:00:00',3,'不難吃但也沒特別吸引人。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (9,2,8,'2025-05-02 12:00:00',5,'調味剛剛好，不會過鹹或過淡。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (10,2,9,'2025-05-03 12:00:00',4,'餐點整體表現不錯，值得一試。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (11,2,10,'2025-05-04 12:00:00',3,'適合填飽肚子但不會特別想再來。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (12,2,11,'2025-05-05 18:00:00',5,'餐點種類多，每次都能有新選擇。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (13,2,12,'2025-05-06 12:00:00',4,'甜點比主食更讓人印象深刻。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (14,2,13,'2025-05-07 19:00:00',5,'有特色，拍照超棒。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (15,2,14,'2025-05-08 12:00:00',3,'餐點普通，沒有太大印象。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (16,2,15,'2025-05-09 20:00:00',5,'迅速又準時，值得再訪。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (17,2,16,'2025-05-02 12:00:00',4,'準備餐稍慢但味道補救了一切。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (18,2,17,'2025-05-03 12:00:00',5,'環境乾淨整潔，取餐體驗非常舒服。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (19,2,18,'2025-05-04 12:00:00',3,'環境還可以但取餐稍擁擠。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (20,3,1,'2025-05-11 18:00:00',5,'無論是味道還是服務都值得五顆星。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (21,3,2,'2025-05-06 12:00:00',5,'口味很道地，像在家鄉吃飯一樣。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (22,3,3,'2025-05-07 19:00:00',2,'準備太久影響整體印象。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (23,3,4,'2025-05-08 12:00:00',3,'價格略高與內容不太成正比。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (24,3,5,'2025-05-09 20:00:00',4,'員工態度友善，用餐愉快。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (25,3,6,'2025-05-02 12:00:00',5,'價格實在，份量又足，超滿足。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (26,3,7,'2025-05-03 12:00:00',3,'不難吃但也沒特別吸引人。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (27,3,8,'2025-05-04 12:00:00',5,'態度非常好，餐點也很有水準。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (28,3,9,'2025-05-05 18:00:00',3,'不難吃但也沒特別吸引人。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (29,3,10,'2025-05-06 12:00:00',5,'是吃飯的好選擇。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (30,3,11,'2025-05-07 19:00:00',4,'配料豐富，視覺效果佳。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (31,3,12,'2025-05-08 12:00:00',5,'是我近期吃過最滿意的一次。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (32,3,13,'2025-05-09 20:00:00',1,'餐點冷掉了才拿到。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (33,3,14,'2025-05-02 12:00:00',4,'分量剛好，吃得剛剛好不撐。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (34,3,15,'2025-05-03 12:00:00',5,'餐點水準極高，每一道都讓人驚艷。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (35,3,16,'2025-05-04 12:00:00',4,'不錯，適合喜歡超值選擇的朋友。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (36,3,17,'2025-05-05 18:00:00',5,'主菜與配菜搭配得恰到好處。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (37,3,18,'2025-05-02 12:00:00',5,'簡直是隱藏版美食，吃完立刻想推薦給朋友。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (38,4,1,'2025-05-03 12:00:00',4,'味道有特色，但不一定每個人都喜歡。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (39,5,1,'2025-05-04 12:00:00',5,'老闆人很好，主動關心顧客需求。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (40,6,1,'2025-05-05 18:00:00',5,'員工親切，讓人有賓至如歸的感覺。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (41,7,1,'2025-05-06 12:00:00',3,'不難吃但也沒特別吸引人。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (42,8,1,'2025-05-06 14:00:00',4,'整體感覺良好，但可再提升。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (43,9,1,'2025-05-08 12:00:00',2,'份量少，吃不太飽。');
INSERT INTO comment(comment_id,vendor_id,user_id,comment_time,rating,review) VALUES (44,10,1,'2025-05-09 20:00:00',4,'CP值算高，還會再來。');

-- Insert into complain
INSERT INTO complain(complain_id,order_id,manager_id,user_id,type,complain_content,status,reply_content) VALUES (1,1,1,1,'送餐問題','送餐延遲','已回覆','您好，非常抱歉讓您有不佳的體驗，我們會檢討改善，並會發放折抵券給您作為補償。');
INSERT INTO complain(complain_id,order_id,manager_id,user_id,type,complain_content,status,reply_content) VALUES (2,12,2,11,'服務問題','態度不佳','已回覆','您好，非常抱歉讓您有不愉快的感受，我們會再與該店家溝通此問題，以提升服務品質。');
INSERT INTO complain(complain_id,order_id,manager_id,user_id,type,complain_content,status,reply_content) VALUES (3,25,2,6,'送餐問題','送錯地點','未回覆',NULL);
INSERT INTO complain(complain_id,order_id,manager_id,user_id,type,complain_content,status,reply_content) VALUES (4,42,1,1,'菜品問題','品項有缺','未回覆',NULL);



