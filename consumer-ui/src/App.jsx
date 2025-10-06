import { useEffect, useState } from 'react';

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

  return (
    <div className="app-container">
      <h1>Kafka 메시지 컨슈머</h1>
      <p className="description">
        컨슈머 애플리케이션이 데이터베이스에 저장한 메시지를 최신순으로 확인할 수 있습니다.
      </p>
      <div className="toolbar">
        <button className="refresh-button" onClick={loadMessages} disabled={loading}>
          {loading ? '갱신 중...' : '새로고침'}
        </button>
        <span className="status-text">{status}</span>
      </div>
      <div className="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>수신 시각</th>
              <th>메시지 내용</th>
            </tr>
          </thead>
          <tbody>
            {messages.map((item) => (
              <tr key={item.id}>
                <td>{item.id}</td>
                <td>{new Date(item.receivedAt).toLocaleString()}</td>
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
