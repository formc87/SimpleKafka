# Spring Boot 환경에서 Kafka 실습 예제

이 저장소는 README의 가이드에 따라 Kafka 환경을 학습할 수 있도록 두 개의 스프링 부트 모듈(이벤트 프로듀서, 이벤트 컨슈머)과 Kafka/Zookeeper, React 기반 대시보드를 제공합니다. Docker Compose를 통해 다중 브로커(3노드) Kafka 클러스터를 기동하고 각 브로커에 메시지가 어떻게 분산되는지 UI에서 확인할 수 있습니다.

## 1. Kafka 및 환경 세팅

- `docker-compose.yml` 파일로 Zookeeper 1대와 Kafka 브로커 3대를 실행합니다.
  ```bash
  docker compose up -d
  ```
  - 브로커는 `localhost:9092`, `localhost:9093`, `localhost:9094` 포트에 매핑되며 내부적으로는 각각 `kafka1:19092`, `kafka2:19093`, `kafka3:19094` 주소로 통신합니다.
  - Compose 파일은 기본 복제 계수와 파티션 수를 3으로 맞춰 두었으므로, 단일 토픽이라도 세 브로커에 골고루 분산됩니다.
- Java 17 이상과 Maven이 설치된 환경에서 프로젝트를 빌드합니다.
  ```bash
  mvn clean package
  ```

## 2. 모듈 구조

| 모듈 | 설명 |
| --- | --- |
| `event-producer` | REST API(`/publish`)를 통해 메시지를 Kafka로 전송하고, 설정에 따라 자동 발행 기능을 제공합니다. |
| `event-consumer` | Kafka 토픽을 구독하여 메시지를 수신하고, H2 데이터베이스에 저장한 뒤 `/messages` API로 최근 메시지를 제공합니다. 저장 시 토픽/파티션/오프셋과 리더 브로커 정보를 함께 기록합니다. |
| `producer-ui` | 프로듀서 REST API를 호출하여 메시지를 전송하는 React 기반 웹 클라이언트입니다. |
| `consumer-ui` | 컨슈머 REST API로부터 메시지 목록을 읽어오고, 브로커별 수신 현황을 시각화하는 React 기반 웹 클라이언트입니다. |

각 모듈은 독립적으로 실행 가능한 Spring Boot 애플리케이션이며, 공통 토픽 이름과 Kafka 접속 정보를 `application.yml`에서 공유합니다. 기본 토픽은 3개의 파티션과 3중 복제를 사용하도록 자동 생성됩니다.

## 3. 애플리케이션 실행

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
- 브라우저에서 `http://localhost:5173`에 접속해 메시지를 작성하고 **발행하기** 버튼을 클릭하면 `event-producer` REST API(`/publish`)가 호출됩니다.

### 컨슈머 UI 실행
```bash
cd consumer-ui
npm install
npm run dev
```
- 브라우저에서 `http://localhost:5174`에 접속하면 컨슈머가 데이터베이스에 적재한 메시지를 확인할 수 있습니다.
- 목록 상단에는 최근 메시지를 리더로 전달한 브로커별 수신 건수가 요약되므로, 3개의 브로커가 어떻게 역할을 나누고 있는지 한눈에 파악할 수 있습니다.

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
  - 응답에는 `topic`, `partition`, `offset`, `leaderBrokerId`, `leaderHost`, `leaderPort` 정보가 포함되어 메시지가 어떤 브로커를 통해 전달되었는지 확인할 수 있습니다.
  - 컨슈머는 수신한 메시지를 H2 메모리 데이터베이스에 저장하므로, 애플리케이션을 재시작하면 데이터가 초기화됩니다.

## 5. 자동 발행 기능

`event-producer/src/main/resources/application.yml`에서 `demo.auto-publish.enabled` 값을 `true`로 변경하면 설정된 간격(`interval`)마다 메시지가 자동 전송됩니다. 다중 브로커 구성이므로 자동 발행 메시지 역시 파티션 별로 브로커가 분산 처리하는 모습을 UI로 확인할 수 있습니다.

## 6. 테스트

모듈 별 기본 컨텍스트 로딩 테스트가 포함되어 있으며, Kafka 인스턴스 없이도 실행될 수 있도록 테스트 전용 설정이 추가되어 있습니다.

## 7. SAML 기반 SSO 실습

`sso-portal` 모듈과 `event-producer` 모듈을 활용해 간단한 SAML SSO 흐름을 체험할 수 있습니다.

1. **포털 실행**
   ```bash
   cd sso-portal
   mvn spring-boot:run
   ```
   - 서버는 `http://localhost:8085`에서 구동되며 회원가입과 로그인, 프로듀서 사이트로 이동하는 버튼을 제공합니다.

2. **프로듀서 애플리케이션 실행**
   ```bash
   cd event-producer
   mvn spring-boot:run
   ```
   - 포털에서 로그인한 뒤 `프로듀서 사이트로 이동` 버튼을 누르면 SAML Response가 생성되어 `/saml/acs` 엔드포인트로 전달되고, 세션이 설정됩니다.
   - 성공적으로 연동되면 프로듀서 페이지 상단에 `홍길동님 환영합니다`와 같은 인사 메시지가 표시됩니다.

> 본 예제는 학습 목적의 단순화된 SAML 흐름으로, 서명/암호화 등의 보안 검증 절차는 포함되어 있지 않습니다.
