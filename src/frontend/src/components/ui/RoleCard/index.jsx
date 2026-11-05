import React from 'react';
import './RoleCard.css';

export default function RoleCard({ role, title, description, icon: Icon, selected, onClick }) {
  return (
    <button
      type="button"
      className={`custom-role-card ${selected ? 'is-selected' : ''}`}
      onClick={() => onClick?.(role)}
    >
      <div className="role-card-icon-wrap">
        {Icon && <Icon size={24} />}
      </div>
      <div className="role-card-content">
        <h4 className="role-card-title">{title}</h4>
        {description && <p className="role-card-desc">{description}</p>}
      </div>
    </button>
  );
}

