import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { postApi } from '../api/posts';
import { imageApi } from '../api/images';
import { useToast } from '../contexts/ToastContext';
import { useAuth } from '../contexts/AuthContext';

const BASE_CATEGORIES = [
  { id: 2, name: '자유' },
  { id: 3, name: '질문' },
  { id: 4, name: '정보공유' },
];

export default function PostCreatePage() {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [imageUrls, setImageUrls] = useState([]);
  const [uploading, setUploading] = useState(false);
  const navigate = useNavigate();
  const { showToast } = useToast();
  const { user } = useAuth();

  // 관리자는 공지 카테고리 포함
  const categories =
    user?.role === 'ADMIN'
      ? [{ id: 1, name: '공지' }, ...BASE_CATEGORIES]
      : BASE_CATEGORIES;

  const handleImageUpload = async (e) => {
    const files = Array.from(e.target.files);
    if (files.length === 0) return;

    setUploading(true);
    try {
      const res = await imageApi.upload(files);
      const urls = res.data.data.map((img) => img.imageUrl);
      setImageUrls((prev) => [...prev, ...urls]);
    } catch (err) {
      showToast(err.response?.data?.message || '이미지 업로드에 실패했습니다.', 'error');
    } finally {
      setUploading(false);
      e.target.value = '';
    }
  };

  const handleRemoveImage = (index) => {
    setImageUrls((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const res = await postApi.create(title, content, Number(categoryId), imageUrls);
      navigate(`/posts/${res.data.data.id}`);
    } catch (err) {
      showToast(err.response?.data?.message || '게시글 작성에 실패했습니다.', 'error');
    }
  };

  return (
    <div className="page">
      <h1>게시글 작성</h1>

      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label htmlFor="categoryId">카테고리</label>
          <select
            id="categoryId"
            value={categoryId}
            onChange={(e) => setCategoryId(e.target.value)}
            required
          >
            <option value="">카테고리 선택</option>
            {categories.map((cat) => (
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

        <div className="form-group">
          <label>이미지</label>
          <input
            type="file"
            accept="image/*"
            multiple
            onChange={handleImageUpload}
            disabled={uploading}
            className="file-input"
          />
          {uploading && <small>업로드 중...</small>}

          {imageUrls.length > 0 && (
            <div className="image-preview-list">
              {imageUrls.map((url, index) => (
                <div key={index} className="image-preview-item">
                  <img src={url} alt={`첨부 이미지 ${index + 1}`} />
                  <button
                    type="button"
                    onClick={() => handleRemoveImage(index)}
                    className="image-remove-btn"
                  >
                    &times;
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="form-actions">
          <button type="submit" className="btn btn-primary">
            작성
          </button>
          <button type="button" onClick={() => navigate(-1)} className="btn">
            취소
          </button>
        </div>
      </form>
    </div>
  );
}
