import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { commentApi } from '../api/comments';
import { reportApi } from '../api/reports';
import { authApi } from '../api/auth';
import ReportModal from './ReportModal';
import { adminApi } from '../api/admin';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';

function MentionText({ content }) {
  const navigate = useNavigate();
  const { showToast } = useToast();

  const handleMentionClick = async (nickname) => {
    try {
      const res = await authApi.getUserByNickname(nickname);
      navigate(`/users/${res.data.data.id}`);
    } catch {
      showToast('존재하지 않는 사용자입니다.', 'error');
    }
  };

  const parts = [];
  const regex = /@(\S+)/g;
  let lastIndex = 0;
  let match;

  while ((match = regex.exec(content)) !== null) {
    if (match.index > lastIndex) {
      parts.push(content.slice(lastIndex, match.index));
    }
    const nickname = match[1];
    parts.push(
      <span
        key={match.index}
        className="mention-text"
        onClick={() => handleMentionClick(nickname)}
      >
        @{nickname}
      </span>
    );
    lastIndex = regex.lastIndex;
  }

  if (lastIndex < content.length) {
    parts.push(content.slice(lastIndex));
  }

  return <>{parts}</>;
}

export default function CommentSection({ postId }) {
  const { user, isAuthenticated } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [replyTo, setReplyTo] = useState(null);
  const [replyContent, setReplyContent] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [editContent, setEditContent] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [reportTarget, setReportTarget] = useState(null);
  const [collapsedReplies, setCollapsedReplies] = useState({});

  const fetchComments = () => {
    commentApi
      .getByPostId(postId)
      .then((res) => {
        setComments(res.data.data);
        setError(false);
      })
      .catch(() => {
        setComments([]);
        setError(true);
      })
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

  const toggleReplies = (commentId) => {
    setCollapsedReplies((prev) => ({ ...prev, [commentId]: !prev[commentId] }));
  };

  const isAdmin = user && user.role === 'ADMIN';

  const renderComment = (comment, isChild = false, rootParentId = null) => (
    <div key={comment.id} className={`comment ${isChild ? 'comment-child' : ''}`}>
      <div className="comment-header">
        <span className="inline-avatar inline-avatar-sm">
          {comment.authorProfileImg ? (
            <img src={comment.authorProfileImg} alt="" />
          ) : (
            <span className="inline-avatar-placeholder">{comment.authorNickname === '(탈퇴한 사용자)' ? '?' : comment.authorNickname?.charAt(0)}</span>
          )}
        </span>
        <strong
          className={comment.authorNickname !== '(탈퇴한 사용자)' ? 'nickname-link' : ''}
          onClick={comment.authorNickname !== '(탈퇴한 사용자)' ? () => navigate(`/users/${comment.authorId}`) : undefined}
        >
          {comment.authorNickname}
        </strong>
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
        <p className="comment-body">
          <MentionText content={comment.content} />
        </p>
      )}

      <div className="comment-actions">
        {isAuthenticated && (
          <>
            <button onClick={() => handleLike(comment.id)} className="btn-text">
              좋아요 {comment.likeCount}
            </button>
            <button
              onClick={() => {
                if (replyTo === comment.id) {
                  setReplyTo(null);
                  setReplyContent('');
                } else {
                  setReplyTo(comment.id);
                  setReplyContent(isChild ? `@${comment.authorNickname} ` : '');
                }
              }}
              className="btn-text"
            >
              답글
            </button>
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

      {/* 답글 폼 - 해당 댓글 바로 아래에 표시 */}
      {replyTo === comment.id && (
        <form
          onSubmit={(e) => handleReply(e, isChild ? (rootParentId || comment.id) : comment.id)}
          className="reply-form"
        >
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
      {!isChild && comment.children && comment.children.length > 0 && (
        <>
          <button
            onClick={() => toggleReplies(comment.id)}
            className="btn-text reply-toggle"
          >
            {collapsedReplies[comment.id]
              ? `답글 ${comment.children.length}개 보기`
              : `답글 접기`}
          </button>
          {!collapsedReplies[comment.id] &&
            comment.children.map((child) => renderComment(child, true, comment.id))}
        </>
      )}
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
        {error ? (
          <div className="error-state">
            <p>댓글을 불러올 수 없습니다.</p>
            <button onClick={fetchComments} className="btn btn-sm" style={{ marginTop: '8px' }}>다시 시도</button>
          </div>
        ) : comments.length === 0 ? (
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
