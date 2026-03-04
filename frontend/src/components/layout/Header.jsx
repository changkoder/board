import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { useNotification } from '../../contexts/NotificationContext';

export default function Header() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const { unreadCount } = useNotification();

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  return (
    <header className="header">
      <div className="header-inner">
        <Link to="/" className="logo">
          Board
        </Link>

        <nav className="nav">
          {isAuthenticated ? (
            <>
              <Link to="/posts/new" className="nav-link">
                글쓰기
              </Link>
              <Link to="/notifications" className="nav-link nav-notification">
                알림
                {unreadCount > 0 && (
                  <span className="notification-badge">{unreadCount > 99 ? '99+' : unreadCount}</span>
                )}
              </Link>
              <Link to="/mypage" className="nav-link nav-profile">
                {user.profileImg ? (
                  <img src={user.profileImg} alt={user.nickname} className="nav-profile-img" />
                ) : (
                  <span className="nav-profile-placeholder">{user.nickname.charAt(0)}</span>
                )}
                {user.nickname}
              </Link>
              {user.role === 'ADMIN' && (
                <Link to="/admin" className="nav-link">
                  관리자페이지
                </Link>
              )}
              <button onClick={handleLogout} className="nav-link btn-link">
                로그아웃
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-link">
                로그인
              </Link>
              <Link to="/signup" className="nav-link">
                회원가입
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
