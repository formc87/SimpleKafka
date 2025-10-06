import { useEffect, useState } from 'react';

/**
 * 간단한 Kafka 프로듀서 제어 화면.
 * 사용자가 메시지를 입력하고 버튼을 클릭하면 백엔드 REST API를 호출한다.
 */
function App() {
  /** 인증된 사용자 정보 */
  const [user, setUser] = useState(() => {
    const params = new URLSearchParams(window.location.search);
    const displayName = params.get('displayName');
    if (!displayName) {
      return null;
    }
    return {
      displayName,
      email: params.get('email') ?? ''
    };
  });
  /** 입력한 메시지 상태 */
  const [message, setMessage] = useState('');
  /** API 호출 후 사용자에게 보여줄 상태 메시지 */
  const [status, setStatus] = useState('');
  /** 버튼 중복 클릭을 막기 위한 로딩 상태 */
  const [loading, setLoading] = useState(false);
  const [checkingSession, setCheckingSession] = useState(true);

  useEffect(() => {
    if (window.location.search) {
      const url = new URL(window.location.href);
      url.search = '';
      window.history.replaceState({}, '', url.toString());
    }

    const fetchSession = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/session', {
          method: 'GET',
          credentials: 'include'
        });

        if (response.ok) {
          const sessionInfo = await response.json();
          if (sessionInfo.authenticated) {
            setUser({
              displayName: sessionInfo.displayName,
              email: sessionInfo.email ?? ''
            });
          } else {
            window.location.href = 'http://localhost:8085/login';
          }
        } else if (response.status === 401) {
          window.location.href = 'http://localhost:8085/login';
        } else {
          setStatus('세션 정보를 확인할 수 없습니다. 잠시 후 다시 시도해 주세요.');
        }
      } catch (error) {
        console.error('세션 확인 실패', error);
        setStatus('네트워크 오류로 세션을 확인할 수 없습니다.');
      } finally {
        setCheckingSession(false);
      }
    };

    fetchSession();
  }, []);

  const isAuthenticated = Boolean(user?.displayName);

  const handleLogout = async () => {
    try {
      await fetch('http://localhost:8080/logout', {
        method: 'GET',
        mode: 'cors',
        credentials: 'include'
      });
    } catch (error) {
      console.error('로그아웃 요청 실패', error);
    } finally {
      const form = document.createElement('form');
      form.method = 'post';
      form.action = 'http://localhost:8085/logout';
      document.body.appendChild(form);
      form.submit();
    }
  };

  /**
   * REST API를 호출해 메시지를 전송한다.
   */
  const handlePublish = async () => {
    if (!isAuthenticated) {
      setStatus('로그인 상태에서만 메시지를 발행할 수 있습니다.');
      return;
    }

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
      <header className="welcome-header">
        <div>
          <h1>
            {isAuthenticated
              ? `${user.displayName}님 환영합니다!`
              : checkingSession
                ? '세션을 확인하는 중입니다'
                : '로그인이 필요합니다'}
          </h1>
          {isAuthenticated && <p className="user-email">{user.email}</p>}
          {!isAuthenticated && !checkingSession && (
            <p className="user-email">SSO 포털에서 로그인한 후 다시 접속해 주세요.</p>
          )}
        </div>
        <button className="logout-button" type="button" onClick={handleLogout}>
          로그아웃
        </button>
      </header>

      <section className="card">
        <h2>Kafka 메시지 프로듀서</h2>
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
      </section>
    </div>
  );
}

export default App;
