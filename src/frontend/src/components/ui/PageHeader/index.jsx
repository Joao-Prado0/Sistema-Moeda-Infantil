import React from 'react';
import './PageHeader.css';

export default function PageHeader({ title, subtitle, actions, children }) {
  return (
    <div className="custom-page-header">
      <div className="page-header-text">
        {title && <h1 className="page-header-title">{title}</h1>}
        {subtitle && <p className="page-header-subtitle">{subtitle}</p>}
      </div>
      {(actions || children) && (
        <div className="page-header-actions">
          {actions}
          {children}
        </div>
      )}
    </div>
  );
}

