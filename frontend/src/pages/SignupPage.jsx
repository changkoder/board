import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../api/auth';
import { useToast } from '../contexts/ToastContext';

export default function SignupPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const navigate = useNavigate();
  const { showToast } = useToast();

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await authApi.signup(email, password, nickname);
      showToast('회원가입이 완료되었습니다.', 'success');
      navigate('/login');
    } catch (err) {
      showToast(err.response?.data?.message || '회원가입에 실패했습니다.', 'error');
    }
  };

  return (
    <div className="page auth-page">
      <h1>회원가입</h1>

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
            minLength={8}
            maxLength={20}
            required
          />
          <small>8~20자</small>
        </div>

        <div className="form-group">
          <label htmlFor="nickname">닉네임</label>
          <input
            id="nickname"
            type="text"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            minLength={2}
            maxLength={20}
            required
          />
          <small>2~20자</small>
        </div>

        <button type="submit" className="btn btn-primary">
          회원가입
        </button>

        <p className="form-footer">
          이미 계정이 있으신가요? <Link to="/login">로그인</Link>
        </p>
      </form>
    </div>
  );
}
