import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();
  const { showToast } = useToast();

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await login(email, password);
      navigate('/');
    } catch (err) {
      showToast(err.response?.data?.message || '로그인에 실패했습니다.', 'error');
    }
  };

  return (
    <div className="page auth-page">
      <h1>로그인</h1>

      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label htmlFor="email">이메일</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="password">비밀번호</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        <button type="submit" className="btn btn-primary">
          로그인
        </button>

        <p className="form-footer">
          계정이 없으신가요? <Link to="/signup">회원가입</Link>
        </p>
      </form>
    </div>
  );
}
