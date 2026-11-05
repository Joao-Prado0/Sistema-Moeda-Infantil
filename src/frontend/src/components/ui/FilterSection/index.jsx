import React from 'react';
import './FilterSection.css';

export default function FilterSection({ title, children, className = '' }) {
  return (
    <section className={`custom-filter-section ${className}`}>
      {title && <h5 className="filter-section-title">{title}</h5>}
      <div className="filter-section-body">{children}</div>
    </section>
  );
}

