# Spring Boot 환경에서 Kafka 실습 프롬프트

## 1. Kafka 및 환경 세팅

- Docker를 활용하여 Kafka 및 Zookeeper 컨테이너 생성
  - Kafka와 Zookeeper 도커 이미지를 다운로드 받고 실행

- Spring Boot 프로젝트 생성
  - Maven, Java 17 이상, Lombok, Spring Web, Spring for Kafka 포함
  - 프로젝트 이름 예: kafka-demo

## 2. 모듈화 구조 구성

### 서버 1: 이벤트 발생 서버
- Spring Boot REST API로 이벤트 수동/자동 생성
- 예: `/publish` 엔드포인트 호출로 메시지를 Kafka에 전송

### 서버 2: Kafka 연동 서버
- Kafka 메시지 구독 Consumer 구현
- 수신된 메시지 콘솔 출력 또는 간단 UI에서 확인 가능

### 실행 방식
- 두 서버를 IDE 내 모듈 또는 별도 프로젝트로 구성하여 각각 실행
- Docker Compose로 Kafka와 두 서버 동시 실행 스크립트 작성 가능

## 3. Kafka와 Spring Boot 연동

- `spring-kafka` 의존성 추가 및 설정
- Kafka 서버 주소 (`localhost:9092`) 설정
- Producer, Consumer 설정 bean 작성

## 4. 실습 단계

1. Docker로 Kafka와 Zookeeper 컨테이너 실행
2. Spring Boot 프로젝트에서 Kafka 의존성 추가 및 설정
3. 서버 1의 `/publish` 엔드포인트 호출해 메시지 전송
4. 서버 2에서 메시지 로그 출력 확인
5. UI 또는 터미널로 메시지 흐름 육안 확인 가능

---
