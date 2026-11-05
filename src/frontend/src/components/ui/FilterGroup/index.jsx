import React from 'react';
import './FilterGroup.css';

export default function FilterGroup({ label, children, className = '' }) {
  return (
    <div className={`custom-filter-group ${className}`}>
      {label && <span className="filter-group-label">{label}</span>}
      <div className="filter-group-content">{children}</div>
    </div>
  );
}

