import React from 'react';
import './CardNavOptions.css';

export default function CardNavOptions({ options = [], activeId, onSelect }) {
  return (
    <div className="card-nav-options">
      {options.map((option) => {
        const Icon = option.icon;
        const isActive = activeId === option.id;
        return (
          <button
            key={option.id}
            type="button"
            className={`card-nav-item ${isActive ? 'is-active' : ''}`}
            onClick={() => onSelect?.(option.id)}
          >
            {Icon && <Icon size={20} />}
            <span>{option.label}</span>
            {option.count !== undefined && (
              <span className="card-nav-count">{option.count}</span>
            )}
          </button>
        );
      })}
    </div>
  );
}

