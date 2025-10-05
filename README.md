# Spring Boot 환경에서 Kafka 실습 예제

이 저장소는 README의 가이드에 따라 Kafka 환경을 학습할 수 있도록 두 개의 스프링 부트 모듈(이벤트 프로듀서, 이벤트 컨슈머)과 Kafka/Zookeeper를 실행하는 Docker Compose 파일을 제공합니다.

## 1. Kafka 및 환경 세팅

- `docker-compose.yml` 파일을 이용해 Kafka와 Zookeeper 컨테이너를 실행합니다.
  ```bash
  docker compose up -d
  ```
- Java 17 이상과 Maven이 설치된 환경에서 프로젝트를 빌드합니다.

## 2. 모듈 구조

| 모듈 | 설명 |
| --- | --- |
| `event-producer` | REST API(`/publish`)를 통해 메시지를 Kafka로 전송하고, 설정에 따라 자동 발행 기능을 제공합니다. |
| `event-consumer` | Kafka 토픽을 구독하여 메시지를 수신하고, H2 데이터베이스에 저장한 뒤 `/messages` API로 최근 메시지를 제공합니다. |
| `producer-ui` | 프로듀서 REST API를 호출하여 메시지를 전송하는 React 기반 웹 클라이언트입니다. |
| `consumer-ui` | 컨슈머 REST API로부터 메시지 목록을 읽어오는 React 기반 웹 클라이언트입니다. |

각 모듈은 독립적으로 실행 가능한 Spring Boot 애플리케이션이며, 공통 토픽 이름과 Kafka 접속 정보를 `application.yml`에서 공유합니다.

## 3. 빌드 및 실행

```bash
mvn clean package
```

### 이벤트 프로듀서 서버 실행
```bash
cd event-producer
mvn spring-boot:run
```

### 이벤트 컨슈머 서버 실행
```bash
cd event-consumer
mvn spring-boot:run
```

### 프로듀서 UI 실행
```bash
cd producer-ui
npm install
npm run dev
```
- 브라우저에서 `http://localhost:5173`으로 접속해 메시지를 작성하고 **발행하기** 버튼을 클릭하면 `event-producer` REST API(`/publish`)가 호출됩니다.

### 컨슈머 UI 실행
```bash
cd consumer-ui
npm install
npm run dev
```
- 브라우저에서 `http://localhost:5174`에 접속하면 컨슈머가 데이터베이스에 적재한 메시지를 실시간으로 조회할 수 있습니다.

## 4. API 사용 예시

- **수동 발행:**
  ```bash
  curl -X POST \
       -H "Content-Type: application/json" \
       -d '{"message": "Hello Kafka"}' \
       http://localhost:8080/publish
  ```
- **수신 메시지 조회:**
  ```bash
  curl http://localhost:8081/messages
  ```
  - 컨슈머는 수신한 메시지를 H2 메모리 데이터베이스에 저장하므로, 애플리케이션을 재시작하면 데이터가 초기화됩니다.

## 5. 자동 발행 기능

`event-producer/src/main/resources/application.yml`에서 `demo.auto-publish.enabled` 값을 `true`로 변경하면 설정된 간격(`interval`)마다 메시지가 자동 전송됩니다.

## 6. 테스트

모듈 별 기본 컨텍스트 로딩 테스트가 포함되어 있으며, Kafka 인스턴스 없이도 실행될 수 있도록 테스트 전용 설정이 추가되어 있습니다.
