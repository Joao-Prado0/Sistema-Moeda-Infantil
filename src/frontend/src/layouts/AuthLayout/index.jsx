import React from 'react';
import './AuthLayout.css';
import LogoMark from '../../components/ui/LogoMark';

export default function AuthLayout({ title, subtitle, children, footer }) {
  return (
    <div className="auth-layout">
      <div className="auth-card-container">
        <header className="auth-header">
          <LogoMark className="auth-logo" />
          {title && <h2 className="auth-title">{title}</h2>}
          {subtitle && <p className="auth-subtitle">{subtitle}</p>}
        </header>

        <main className="auth-main-content">{children}</main>

        {footer && <footer className="auth-footer">{footer}</footer>}
      </div>
    </div>
  );
}

