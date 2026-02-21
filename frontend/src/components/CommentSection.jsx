import { useState, useEffect } from 'react';
import { commentApi } from '../api/comments';
import { reportApi } from '../api/reports';
import ReportModal from './ReportModal';
import { adminApi } from '../api/admin';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';

export default function CommentSection({ postId }) {
  const { user, isAuthenticated } = useAuth();
  const { showToast } = useToast();
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [replyTo, setReplyTo] = useState(null);
  const [replyContent, setReplyContent] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [editContent, setEditContent] = useState('');
  const [loading, setLoading] = useState(true);
  const [reportTarget, setReportTarget] = useState(null);

  const fetchComments = () => {
    commentApi
      .getByPostId(postId)
      .then((res) => setComments(res.data.data))
      .catch(() => setComments([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchComments();
  }, [postId]);

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;
    try {
      await commentApi.create(postId, newComment);
      setNewComment('');
      fetchComments();
    } catch (err) {
      showToast(err.response?.data?.message || '댓글 작성에 실패했습니다.', 'error');
    }
  };

  const handleReply = async (e, parentId) => {
    e.preventDefault();
    if (!replyContent.trim()) return;
    try {
      await commentApi.create(postId, replyContent, parentId);
      setReplyTo(null);
      setReplyContent('');
      fetchComments();
    } catch (err) {
      showToast(err.response?.data?.message || '답글 작성에 실패했습니다.', 'error');
    }
  };

  const handleUpdate = async (commentId) => {
    if (!editContent.trim()) return;
    try {
      await commentApi.update(commentId, editContent);
      setEditingId(null);
      setEditContent('');
      fetchComments();
    } catch (err) {
      showToast(err.response?.data?.message || '댓글 수정에 실패했습니다.', 'error');
    }
  };

  const handleDelete = async (commentId) => {
    if (!window.confirm('댓글을 삭제하시겠습니까?')) return;
    try {
      await commentApi.delete(commentId);
      fetchComments();
    } catch (err) {
      showToast(err.response?.data?.message || '댓글 삭제에 실패했습니다.', 'error');
    }
  };

  const handleLike = async (commentId) => {
    try {
      await commentApi.toggleLike(commentId);
      fetchComments();
    } catch (err) {
      showToast(err.response?.data?.message || '좋아요 처리에 실패했습니다.', 'error');
    }
  };

  const handleReport = async (reason) => {
    if (!reportTarget) return;
    try {
      await reportApi.reportComment(reportTarget, reason);
      showToast('신고가 접수되었습니다.', 'success');
      setReportTarget(null);
    } catch (err) {
      showToast(err.response?.data?.message || '신고에 실패했습니다.', 'error');
    }
  };

  const handleAdminHide = async (commentId) => {
    if (!window.confirm('[관리자] 이 댓글을 숨기시겠습니까?')) return;
    try {
      await adminApi.hideComment(commentId);
      fetchComments();
    } catch (err) {
      showToast(err.response?.data?.message || '숨김 처리에 실패했습니다.', 'error');
    }
  };

  const handleAdminDelete = async (commentId) => {
    if (!window.confirm('[관리자] 이 댓글을 영구 삭제하시겠습니까?')) return;
    try {
      await adminApi.deleteComment(commentId);
      fetchComments();
    } catch (err) {
      showToast(err.response?.data?.message || '삭제에 실패했습니다.', 'error');
    }
  };

  const handleBlockUser = async (authorId, authorNickname) => {
    if (!window.confirm(`[관리자] ${authorNickname} 사용자를 차단하시겠습니까?`)) return;
    try {
      await adminApi.blockUser(authorId);
      showToast('사용자가 차단되었습니다.', 'success');
    } catch (err) {
      showToast(err.response?.data?.message || '차단에 실패했습니다.', 'error');
    }
  };

  const isAdmin = user && user.role === 'ADMIN';

  const renderComment = (comment, isChild = false) => (
    <div key={comment.id} className={`comment ${isChild ? 'comment-child' : ''}`}>
      <div className="comment-header">
        <strong>{comment.authorNickname}</strong>
        <span className="comment-date">
          {new Date(comment.createdAt).toLocaleString()}
        </span>
      </div>

      {editingId === comment.id ? (
        <div className="comment-edit">
          <textarea
            value={editContent}
            onChange={(e) => setEditContent(e.target.value)}
            rows={3}
          />
          <div className="comment-edit-actions">
            <button onClick={() => handleUpdate(comment.id)} className="btn btn-sm btn-primary">
              저장
            </button>
            <button onClick={() => setEditingId(null)} className="btn btn-sm">
              취소
            </button>
          </div>
        </div>
      ) : (
        <p className="comment-body">{comment.content}</p>
      )}

      <div className="comment-actions">
        {isAuthenticated && (
          <>
            <button onClick={() => handleLike(comment.id)} className="btn-text">
              좋아요 {comment.likeCount}
            </button>
            {!isChild && (
              <button
                onClick={() => {
                  setReplyTo(replyTo === comment.id ? null : comment.id);
                  setReplyContent('');
                }}
                className="btn-text"
              >
                답글
              </button>
            )}
            {user && user.id === comment.authorId && (
              <>
                <button
                  onClick={() => {
                    setEditingId(comment.id);
                    setEditContent(comment.content);
                  }}
                  className="btn-text"
                >
                  수정
                </button>
                <button onClick={() => handleDelete(comment.id)} className="btn-text text-danger">
                  삭제
                </button>
              </>
            )}
            {user && user.id !== comment.authorId && (
              <button onClick={() => setReportTarget(comment.id)} className="btn-text text-danger">
                신고
              </button>
            )}
            {isAdmin && user.id !== comment.authorId && (
              <>
                <button onClick={() => handleAdminHide(comment.id)} className="btn-text">
                  숨기기
                </button>
                <button onClick={() => handleAdminDelete(comment.id)} className="btn-text text-danger">
                  삭제
                </button>
                <button onClick={() => handleBlockUser(comment.authorId, comment.authorNickname)} className="btn-text text-danger">
                  차단
                </button>
              </>
            )}
          </>
        )}
      </div>

      {/* 답글 폼 */}
      {replyTo === comment.id && (
        <form onSubmit={(e) => handleReply(e, comment.id)} className="reply-form">
          <textarea
            value={replyContent}
            onChange={(e) => setReplyContent(e.target.value)}
            placeholder="답글을 입력하세요"
            rows={2}
            required
          />
          <div className="comment-edit-actions">
            <button type="submit" className="btn btn-sm btn-primary">
              답글 작성
            </button>
            <button type="button" onClick={() => setReplyTo(null)} className="btn btn-sm">
              취소
            </button>
          </div>
        </form>
      )}

      {/* 대댓글 */}
      {comment.children &&
        comment.children.map((child) => renderComment(child, true))}
    </div>
  );

  if (loading) return <div className="loading">댓글 로딩 중...</div>;

  return (
    <div className="comment-section">
      <h2>댓글 {comments.length}개</h2>

      {/* 댓글 작성 폼 */}
      {isAuthenticated ? (
        <form onSubmit={handleCreate} className="comment-form">
          <textarea
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            placeholder="댓글을 입력하세요"
            rows={3}
            required
          />
          <button type="submit" className="btn btn-primary">
            댓글 작성
          </button>
        </form>
      ) : (
        <p className="comment-login-notice">댓글을 작성하려면 로그인이 필요합니다.</p>
      )}

      {/* 댓글 목록 */}
      <div className="comment-list-detail">
        {comments.length === 0 ? (
          <p className="empty">댓글이 없습니다.</p>
        ) : (
          comments.map((comment) => renderComment(comment))
        )}
      </div>

      {reportTarget && (
        <ReportModal
          onSubmit={handleReport}
          onClose={() => setReportTarget(null)}
        />
      )}
    </div>
  );
}
