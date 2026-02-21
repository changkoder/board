import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { postApi } from '../api/posts';
import { reportApi } from '../api/reports';
import { adminApi } from '../api/admin';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import CommentSection from '../components/CommentSection';
import ReportModal from '../components/ReportModal';

export default function PostDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const { showToast } = useToast();
  const [post, setPost] = useState(null);
  const [bookmarked, setBookmarked] = useState(false);
  const [loading, setLoading] = useState(true);
  const [showReportModal, setShowReportModal] = useState(false);

  useEffect(() => {
    postApi
      .getById(id)
      .then((res) => {
        setPost(res.data.data);
        setBookmarked(res.data.data.bookmarked);
      })
      .catch(() => navigate('/'))
      .finally(() => setLoading(false));
  }, [id, navigate]);

  const handleDelete = async () => {
    if (!window.confirm('정말 삭제하시겠습니까?')) return;
    try {
      await postApi.delete(id);
      showToast('게시글이 삭제되었습니다.', 'success');
      navigate('/');
    } catch (err) {
      showToast(err.response?.data?.message || '삭제에 실패했습니다.', 'error');
    }
  };

  const handleLike = async () => {
    try {
      await postApi.toggleLike(id);
      const res = await postApi.getById(id);
      setPost(res.data.data);
      setBookmarked(res.data.data.bookmarked);
    } catch (err) {
      showToast(err.response?.data?.message || '좋아요 처리에 실패했습니다.', 'error');
    }
  };

  const handleBookmark = async () => {
    try {
      const res = await postApi.toggleBookmark(id);
      setBookmarked(res.data.data);
    } catch (err) {
      showToast(err.response?.data?.message || '북마크 처리에 실패했습니다.', 'error');
    }
  };

  const handleReport = async (reason) => {
    try {
      await reportApi.reportPost(id, reason);
      showToast('신고가 접수되었습니다.', 'success');
      setShowReportModal(false);
    } catch (err) {
      showToast(err.response?.data?.message || '신고에 실패했습니다.', 'error');
    }
  };

  if (loading) return <div className="loading">로딩 중...</div>;
  if (!post) return null;

  const isAuthor = user && user.id === post.authorId;
  const isAdmin = user && user.role === 'ADMIN';

  const handleAdminHide = async () => {
    if (!window.confirm('[관리자] 이 게시글을 숨기시겠습니까?')) return;
    try {
      await adminApi.hidePost(id);
      showToast('게시글이 숨김 처리되었습니다.', 'success');
      navigate('/');
    } catch (err) {
      showToast(err.response?.data?.message || '숨김 처리에 실패했습니다.', 'error');
    }
  };

  const handleAdminDelete = async () => {
    if (!window.confirm('[관리자] 이 게시글을 영구 삭제하시겠습니까?')) return;
    try {
      await adminApi.deletePost(id);
      showToast('게시글이 삭제되었습니다.', 'success');
      navigate('/');
    } catch (err) {
      showToast(err.response?.data?.message || '삭제에 실패했습니다.', 'error');
    }
  };

  const handleBlockUser = async () => {
    if (!window.confirm(`[관리자] ${post.authorNickname} 사용자를 차단하시겠습니까?`)) return;
    try {
      await adminApi.blockUser(post.authorId);
      showToast('사용자가 차단되었습니다.', 'success');
    } catch (err) {
      showToast(err.response?.data?.message || '차단에 실패했습니다.', 'error');
    }
  };

  return (
    <div className="page">
      <article className="post-detail">
        <div className="post-header">
          <h1>{post.title}</h1>
          <div className="post-meta">
            <span>{post.authorNickname}</span>
            <span>{new Date(post.createdAt).toLocaleString()}</span>
            <span>조회 {post.viewCount}</span>
            <span>좋아요 {post.likeCount}</span>
          </div>
          {post.categoryName && <span className="badge">{post.categoryName}</span>}
        </div>

        <div className="post-content">{post.content}</div>

        <div className="post-actions">
          {isAuthenticated && (
            <>
              <button onClick={handleLike} className="btn">
                좋아요 {post.likeCount}
              </button>
              <button onClick={handleBookmark} className={`btn ${bookmarked ? 'btn-primary' : ''}`}>
                {bookmarked ? '북마크 해제' : '북마크'}
              </button>
            </>
          )}

          {isAuthor && (
            <>
              <Link to={`/posts/${id}/edit`} className="btn">
                수정
              </Link>
              <button onClick={handleDelete} className="btn btn-danger">
                삭제
              </button>
            </>
          )}

          {isAuthenticated && !isAuthor && (
            <button onClick={() => setShowReportModal(true)} className="btn btn-text-danger">
              신고
            </button>
          )}

          {isAdmin && !isAuthor && (
            <>
              <button onClick={handleAdminHide} className="btn">
                숨기기
              </button>
              <button onClick={handleAdminDelete} className="btn btn-danger">
                삭제
              </button>
              <button onClick={handleBlockUser} className="btn btn-danger">
                사용자 차단
              </button>
            </>
          )}
        </div>
      </article>

      <CommentSection postId={id} />

      <Link to="/" className="btn" style={{ marginTop: '16px', display: 'inline-block' }}>
        목록으로
      </Link>

      {showReportModal && (
        <ReportModal
          onSubmit={handleReport}
          onClose={() => setShowReportModal(false)}
        />
      )}
    </div>
  );
}
