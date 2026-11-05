import React from 'react';
import './BackButton.css';
import { ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function BackButton({ to, onClick, label = 'Voltar', className = '' }) {
  const navigate = useNavigate();

  const handleClick = (e) => {
    if (onClick) {
      onClick(e);
      return;
    }
    if (to) {
      navigate(to);
    } else {
      navigate(-1);
    }
  };

  return (
    <button type="button" className={`btn-back ${className}`} onClick={handleClick}>
      <ArrowLeft size={18} />
      <span>{label}</span>
    </button>
  );
}

