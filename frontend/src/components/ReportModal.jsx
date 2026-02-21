import { useState } from 'react';

const REPORT_REASONS = [
  { value: 'SPAM', label: '스팸/광고' },
  { value: 'ABUSE', label: '욕설/비하' },
  { value: 'INAPPROPRIATE', label: '부적절한 내용' },
  { value: 'FALSE_INFO', label: '허위 정보' },
  { value: 'OTHER', label: '기타' },
];

export default function ReportModal({ onSubmit, onClose }) {
  const [selected, setSelected] = useState('');

  const handleSubmit = () => {
    if (!selected) {
      alert('신고 사유를 선택해주세요.');
      return;
    }
    onSubmit(selected);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h3>신고 사유 선택</h3>
        <div className="report-reasons">
          {REPORT_REASONS.map((reason) => (
            <label key={reason.value} className="report-reason-item">
              <input
                type="radio"
                name="reportReason"
                value={reason.value}
                checked={selected === reason.value}
                onChange={(e) => setSelected(e.target.value)}
              />
              <span>{reason.label}</span>
            </label>
          ))}
        </div>
        <div className="modal-actions">
          <button onClick={handleSubmit} className="btn btn-danger" disabled={!selected}>
            신고하기
          </button>
          <button onClick={onClose} className="btn">
            취소
          </button>
        </div>
      </div>
    </div>
  );
}
