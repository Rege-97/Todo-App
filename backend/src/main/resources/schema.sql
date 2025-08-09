-- == 회원 테이블 ==
-- 앱 사용자의 기본 정보를 저장합니다.
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 사용자의 고유 식별자 (자동 증가)
    email VARCHAR(120) NOT NULL UNIQUE, -- 로그인 ID로 사용될 이메일 (중복 불가)
    password VARCHAR(255) NOT NULL, -- BCrypt로 암호화된 비밀번호
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 계정 생성일
    );

-- == 투두(할 일) 테이블 ==
-- 각 사용자가 생성한 할 일 목록을 저장합니다.
CREATE TABLE IF NOT EXISTS todos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 할 일 항목의 고유 식별자 (자동 증가)
    user_id BIGINT NOT NULL, -- 이 할 일이 어떤 사용자의 것인지 알려주는 외래 키
    title VARCHAR(255) NOT NULL, -- 할 일의 내용 (예: "스프링 부트 공부하기")
    status VARCHAR(20) NOT NULL DEFAULT 'TODO', -- 할 일의 완료 여부 (기본값은 '미완료'인 false)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 할 일 생성일
    completed_at TIMESTAMP NULL, -- 완료일
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- == 리프레시 토큰 테이블 ==
-- 발급된 리프레시 토큰을 저장하여 추적하고 관리합니다.
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 고유 식별자
    user_id BIGINT NOT NULL UNIQUE, -- 어떤 사용자의 토큰인지 (한 사용자당 하나의 리프레시 토큰만 가짐)
    token VARCHAR(255) NOT NULL, -- 발급된 리프레시 토큰 문자열
    expiry_date TIMESTAMP NOT NULL, -- 토큰 만료일시
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );