import React from 'react';
import './FilterDrawer.css';
import { X } from 'lucide-react';

export default function FilterDrawer({ isOpen, onClose, title = 'Filtros', children, onClear, onApply }) {
  if (!isOpen) return null;

  return (
    <div className="filter-drawer-backdrop" onClick={onClose}>
      <aside className="filter-drawer" onClick={(e) => e.stopPropagation()}>
        <header className="filter-drawer-header">
          <h3>{title}</h3>
          <button type="button" className="btn-close" onClick={onClose} aria-label="Fechar">
            <X size={20} />
          </button>
        </header>

        <div className="filter-drawer-body">
          {children}
        </div>

        {(onClear || onApply) && (
          <footer className="filter-drawer-footer">
            {onClear && (
              <button type="button" className="btn-filter-clear" onClick={onClear}>
                Limpar
              </button>
            )}
            {onApply && (
              <button type="button" className="btn-filter-apply" onClick={onApply}>
                Aplicar
              </button>
            )}
          </footer>
        )}
      </aside>
    </div>
  );
}

