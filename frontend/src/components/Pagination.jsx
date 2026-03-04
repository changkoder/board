export default function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null;

  const maxVisible = 5;
  let start = Math.max(0, page - Math.floor(maxVisible / 2));
  let end = start + maxVisible;
  if (end > totalPages) {
    end = totalPages;
    start = Math.max(0, end - maxVisible);
  }
  const pages = [];
  for (let i = start; i < end; i++) {
    pages.push(i);
  }

  return (
    <div className="pagination">
      <button onClick={() => onPageChange(0)} disabled={page === 0}>
        &laquo;
      </button>
      <button onClick={() => onPageChange(page - 1)} disabled={page === 0}>
        &lsaquo;
      </button>
      {pages.map((p) => (
        <button
          key={p}
          onClick={() => onPageChange(p)}
          className={page === p ? 'page-active' : ''}
        >
          {p + 1}
        </button>
      ))}
      <button onClick={() => onPageChange(page + 1)} disabled={page >= totalPages - 1}>
        &rsaquo;
      </button>
      <button onClick={() => onPageChange(totalPages - 1)} disabled={page >= totalPages - 1}>
        &raquo;
      </button>
    </div>
  );
}
