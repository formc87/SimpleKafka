import { useState } from 'react';

/**
 * 간단한 Kafka 프로듀서 제어 화면.
 * 사용자가 메시지를 입력하고 버튼을 클릭하면 백엔드 REST API를 호출한다.
 */
function App() {
  /** 입력한 메시지 상태 */
  const [message, setMessage] = useState('');
  /** API 호출 후 사용자에게 보여줄 상태 메시지 */
  const [status, setStatus] = useState('');
  /** 버튼 중복 클릭을 막기 위한 로딩 상태 */
  const [loading, setLoading] = useState(false);

  /**
   * REST API를 호출해 메시지를 전송한다.
   */
  const handlePublish = async () => {
    if (!message.trim()) {
      setStatus('메시지를 입력해 주세요.');
      return;
    }

    const baseUrl = import.meta.env.VITE_PRODUCER_API_BASE ?? 'http://localhost:8080';

    setLoading(true);
    setStatus('메시지를 전송 중입니다...');

    try {
      const response = await fetch(`${baseUrl}/publish`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message })
      });

      if (!response.ok) {
        throw new Error('서버에서 오류가 발생했습니다.');
      }

      setStatus('카프카로 메시지를 전송했습니다!');
      setMessage('');
    } catch (error) {
      console.error('발행 실패', error);
      setStatus('발행에 실패했습니다. 서버 로그를 확인해 주세요.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app-container">
      <h1>Kafka 메시지 프로듀서</h1>
      <p className="description">
        아래 입력창에 메시지를 작성하고 <strong>발행하기</strong> 버튼을 누르면 REST API를 통해 Kafka 토픽으로 전송됩니다.
      </p>
      <textarea
        className="message-input"
        placeholder="보낼 메시지를 입력하세요"
        value={message}
        onChange={(event) => setMessage(event.target.value)}
        rows={6}
      />
      <button className="publish-button" onClick={handlePublish} disabled={loading}>
        {loading ? '발행 중...' : '발행하기'}
      </button>
      {status && <p className="status-text">{status}</p>}
    </div>
  );
}

export default App;
