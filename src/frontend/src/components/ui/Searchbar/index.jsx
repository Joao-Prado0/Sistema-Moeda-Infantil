import React from 'react';
import './Searchbar.css';
import { Search, X } from 'lucide-react';

export default function Searchbar({ value = '', onChange, placeholder = 'Pesquisar...', onClear, className = '' }) {
  return (
    <div className={`custom-searchbar ${className}`}>
      <Search size={18} className="searchbar-icon" />
      <input
        type="text"
        value={value}
        onChange={(e) => onChange?.(e.target.value)}
        placeholder={placeholder}
        className="searchbar-input"
      />
      {value && (
        <button
          type="button"
          className="searchbar-clear"
          onClick={() => {
            onChange?.('');
            onClear?.();
          }}
          aria-label="Limpar busca"
        >
          <X size={16} />
        </button>
      )}
    </div>
  );
}

