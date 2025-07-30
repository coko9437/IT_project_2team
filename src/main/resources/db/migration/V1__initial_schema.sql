-- src/main/resources/db/migration/V1__initial_schema.sql

-- lunch_matches 테이블 생성
CREATE TABLE IF NOT EXISTS lunch_matches (
                                             rno BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50),
    category VARCHAR(100),
    latitude DOUBLE,
    longitude DOUBLE,
    rating DOUBLE,
    price_level VARCHAR(50),
    operating_hours VARCHAR(255),
    reg_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    mod_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- 초기 데이터 삽입 (선택 사항)
INSERT INTO lunch_matches (name, address, phone_number, category, latitude, longitude, rating, price_level, operating_hours) VALUES
                                                                                                                                 ('부산맛집1', '부산시 해운대구', '051-111-1111', '한식', 35.16, 129.15, 4.2, '보통', '매일 11:00-21:00'),
                                                                                                                                 ('부산맛집2', '부산시 서면', '051-222-2222', '일식', 35.15, 129.06, 4.8, '비쌈', '평일 10:00-22:00'),
                                                                                                                                 ('부산맛집3', '부산시 남포동', '051-333-3333', '중식', 35.10, 129.03, 3.5, '보통', '주말 12:00-20:00');