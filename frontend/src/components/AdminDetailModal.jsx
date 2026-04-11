import { useEffect, useState } from 'react';
import { adminApi } from '../api/admin';

export default function AdminDetailModal({ type, targetId, onClose, onRestore, onDelete }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(false);

    const request = type === 'post'
      ? adminApi.getPostDetail(targetId)
      : adminApi.getCommentDetail(targetId);

    request
      .then((res) => {
        if (!cancelled) {
          setData(res.data.data);
        }
      })
      .catch(() => {
        if (!cancelled) {
          setError(true);
        }
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false);
        }
      });

    return () => {
      cancelled = true;
    };
  }, [type, targetId]);

  const handleRestore = async () => {
    if (!window.confirm('복원하시겠습니까?')) return;
    await onRestore(targetId);
  };

  const handleDelete = async () => {
    if (!window.confirm('정말 삭제하시겠습니까?')) return;
    await onDelete(targetId);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal modal-lg" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>{type === 'post' ? '숨김 게시글 상세' : '숨김 댓글 상세'}</h3>
          <button onClick={onClose} className="modal-close" aria-label="닫기">
            ×
          </button>
        </div>

        {loading ? (
          <div className="loading">로딩 중...</div>
        ) : error ? (
          <div className="error-state">
            <p>데이터를 불러올 수 없습니다.</p>
          </div>
        ) : data ? (
          <>
            {type === 'post' ? (
              <div className="admin-detail-body">
                <div className="admin-detail-section">
                  <h4>{data.title}</h4>
                  <div className="admin-detail-meta">
                    <span>{data.categoryName}</span>
                    <span>·</span>
                    <span>{data.authorNickname}</span>
                    <span>·</span>
                    <span>{new Date(data.createdAt).toLocaleString()}</span>
                  </div>
                  <div className="admin-detail-content">
                    {data.content}
                  </div>
                  {data.imageUrls && data.imageUrls.length > 0 && (
                    <div className="admin-detail-images">
                      {data.imageUrls.map((url, idx) => (
                        <img key={idx} src={url} alt="" />
                      ))}
                    </div>
                  )}
                </div>
              </div>
            ) : (
              <div className="admin-detail-body">
                <div className="admin-detail-section">
                  <div className="admin-detail-meta">
                    <span>{data.authorNickname}</span>
                    <span>·</span>
                    <span>{new Date(data.createdAt).toLocaleString()}</span>
                  </div>
                  <div className="admin-detail-content">{data.content}</div>
                </div>
              </div>
            )}

            <div className="admin-detail-section">
              <div className="admin-detail-label">
                신고 내역 ({data.reports ? data.reports.length : 0}건)
              </div>
              {data.reports && data.reports.length > 0 ? (
                <ul className="admin-detail-reports">
                  {data.reports.map((report) => (
                    <li key={report.id}>
                      <span className="admin-detail-reporter">{report.reporterNickname}</span>
                      <span> · </span>
                      <span className="admin-detail-reason">{report.reasonLabel}</span>
                      <span> · </span>
                      <span className="admin-detail-date">
                        {new Date(report.createdAt).toLocaleString()}
                      </span>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="empty">신고 내역이 없습니다.</p>
              )}
            </div>

            <div className="modal-actions">
              <button onClick={handleRestore} className="btn btn-primary">
                복원
              </button>
              <button onClick={handleDelete} className="btn btn-danger">
                삭제
              </button>
              <button onClick={onClose} className="btn">
                닫기
              </button>
            </div>
          </>
        ) : null}
      </div>
    </div>
  );
}
