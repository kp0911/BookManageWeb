DROP TABLE IF EXISTS BOOK;
DROP TABLE IF EXISTS USERS;

-- 사용자 테이블 생성
CREATE TABLE USERS (
                       id VARCHAR(50) PRIMARY KEY,
                       name VARCHAR(50) NOT NULL,
                       grade VARCHAR(20) NOT NULL
);

CREATE TABLE BOOK (
                      id VARCHAR(50) PRIMARY KEY,
                      category VARCHAR(50),
                      title VARCHAR(100) NOT NULL,
                      rentable BOOLEAN NOT NULL,
                      rented BOOLEAN NOT NULL,
                      returnDate DATE,
                      userId VARCHAR(50),
                      FOREIGN KEY (userId) REFERENCES USERS(id)
);