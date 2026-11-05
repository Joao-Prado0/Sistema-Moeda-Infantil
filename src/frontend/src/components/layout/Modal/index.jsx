import './Modal.css';

export default function Modal({ isOpen, onClose, title, children, maxWidth = '560px' }) {
  if (!isOpen) return null;

  return (
    <div className="custom-modal-backdrop" onClick={onClose} role="dialog" aria-modal="true">
      <div
        className="custom-modal-content"
        style={{ maxWidth }}
        onClick={(e) => e.stopPropagation()}
      >
        {title && (
          <header className="custom-modal-header">
            <h3>{title}</h3>
            <button type="button" className="custom-modal-close" onClick={onClose} aria-label="Fechar">
              &times;
            </button>
          </header>
        )}
        <div className="custom-modal-body">{children}</div>
      </div>
    </div>
  );
}

