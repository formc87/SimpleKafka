import { useEffect, useMemo, useState } from 'react';

/**
 * Kafka 컨슈머가 적재한 메시지를 테이블로 보여주는 화면.
 */
function App() {
  /** 백엔드에서 받아온 메시지 목록 */
  const [messages, setMessages] = useState([]);
  /** 사용자에게 보여줄 상태 문구 */
  const [status, setStatus] = useState('메시지를 불러오는 중입니다...');
  /** 자동 갱신 중 로딩 여부 */
  const [loading, setLoading] = useState(true);

  const baseUrl = import.meta.env.VITE_CONSUMER_API_BASE ?? 'http://localhost:8081';

  /**
   * 메시지를 백엔드에서 가져오는 함수.
   */
  const loadMessages = async () => {
    setLoading(true);
    try {
      const response = await fetch(`${baseUrl}/messages`);
      if (!response.ok) {
        throw new Error('API 호출 실패');
      }
      const data = await response.json();
      setMessages(data);
      setStatus(data.length ? `${data.length}개의 메시지를 불러왔습니다.` : '아직 수신된 메시지가 없습니다.');
    } catch (error) {
      console.error('메시지 조회 실패', error);
      setStatus('메시지 조회에 실패했습니다. 서버 상태를 확인해 주세요.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // 초기 로딩 수행
    loadMessages();
    // 5초마다 자동 새로고침
    const interval = setInterval(loadMessages, 5000);
    return () => clearInterval(interval);
  }, []);

  /**
   * 브로커별 메시지 수신 현황을 계산한다.
   */
  const brokerSummary = useMemo(() => {
    const grouped = new Map();
    messages.forEach((item) => {
      const key = item.leaderBrokerId ?? 'unknown';
      const current = grouped.get(key) ?? {
        brokerId: item.leaderBrokerId,
        host: item.leaderHost,
        port: item.leaderPort,
        count: 0,
      };
      current.count += 1;
      current.host = current.host ?? item.leaderHost;
      current.port = current.port ?? item.leaderPort;
      grouped.set(key, current);
    });
    return Array.from(grouped.values());
  }, [messages]);

  return (
    <div className="app-container">
      <h1>Kafka 메시지 컨슈머</h1>
      <p className="description">
        컨슈머 애플리케이션이 데이터베이스에 저장한 메시지를 최신순으로 확인하고, 어떤 브로커가 리더로 전달했는지도 함께 살펴볼 수 있습니다.
      </p>
      <div className="toolbar">
        <button className="refresh-button" onClick={loadMessages} disabled={loading}>
          {loading ? '갱신 중...' : '새로고침'}
        </button>
        <span className="status-text">{status}</span>
      </div>

      {brokerSummary.length > 0 && (
        <div className="summary-card">
          <h2>브로커별 수신 현황</h2>
          <table className="summary-table">
            <thead>
              <tr>
                <th>브로커 ID</th>
                <th>호스트</th>
                <th>수신 건수</th>
              </tr>
            </thead>
            <tbody>
              {brokerSummary.map((item, index) => (
                <tr key={item.brokerId ?? `unknown-${index}`}>
                  <td>{item.brokerId ?? '확인 실패'}</td>
                  <td>{item.host ? `${item.host}${item.port ? `:${item.port}` : ''}` : '확인 실패'}</td>
                  <td>{item.count}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div className="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>수신 시각</th>
              <th>토픽</th>
              <th>파티션</th>
              <th>오프셋</th>
              <th>리더 브로커</th>
              <th>메시지 내용</th>
            </tr>
          </thead>
          <tbody>
            {messages.map((item) => (
              <tr key={item.id}>
                <td>{item.id}</td>
                <td>{new Date(item.receivedAt).toLocaleString()}</td>
                <td>{item.topic}</td>
                <td>{item.partition}</td>
                <td>{item.offset}</td>
                <td>
                  {item.leaderBrokerId != null
                    ? `${item.leaderBrokerId} (${item.leaderHost ?? 'host 미상'}${item.leaderPort ? `:${item.leaderPort}` : ''})`
                    : '확인 실패'}
                </td>
                <td className="payload-cell">{item.payload}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default App;
