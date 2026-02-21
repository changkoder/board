import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { postApi } from '../api/posts';

const CATEGORIES = [
  { id: 1, name: '자유' },
  { id: 2, name: '질문' },
  { id: 3, name: '정보공유' },
];

export default function PostEditPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    postApi
      .getById(id)
      .then((res) => {
        const post = res.data.data;
        setTitle(post.title);
        setContent(post.content);
      })
      .catch(() => navigate('/'))
      .finally(() => setLoading(false));
  }, [id, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      await postApi.update(id, title, content, Number(categoryId));
      navigate(`/posts/${id}`);
    } catch (err) {
      setError(err.response?.data?.message || '게시글 수정에 실패했습니다.');
    }
  };

  if (loading) return <div className="loading">로딩 중...</div>;

  return (
    <div className="page">
      <h1>게시글 수정</h1>

      <form onSubmit={handleSubmit} className="form">
        {error && <div className="error-message">{error}</div>}

        <div className="form-group">
          <label htmlFor="categoryId">카테고리</label>
          <select
            id="categoryId"
            value={categoryId}
            onChange={(e) => setCategoryId(e.target.value)}
            required
          >
            <option value="">카테고리 선택</option>
            {CATEGORIES.map((cat) => (
              <option key={cat.id} value={cat.id}>
                {cat.name}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="title">제목</label>
          <input
            id="title"
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="content">내용</label>
          <textarea
            id="content"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            rows={15}
            required
          />
        </div>

        <div className="form-actions">
          <button type="submit" className="btn btn-primary">
            수정
          </button>
          <button type="button" onClick={() => navigate(-1)} className="btn">
            취소
          </button>
        </div>
      </form>
    </div>
  );
}
