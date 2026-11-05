import { useId } from 'react';

// Moeda da marca: face dourada com a estrela do mérito
export default function LogoMark({ className }) {
  const id = useId().replace(/[^a-zA-Z0-9_-]/g, '');

  return (
    <svg viewBox="0 0 48 48" className={className} aria-hidden="true" focusable="false">
      <defs>
        <linearGradient id={`${id}-face`} x1="8" y1="4" x2="40" y2="44" gradientUnits="userSpaceOnUse">
          <stop offset="0" stopColor="#FFE08A" />
          <stop offset="0.45" stopColor="#FFC531" />
          <stop offset="1" stopColor="#E09400" />
        </linearGradient>
        <linearGradient id={`${id}-rim`} x1="40" y1="6" x2="8" y2="42" gradientUnits="userSpaceOnUse">
          <stop offset="0" stopColor="#FFF4C9" />
          <stop offset="1" stopColor="#B87700" />
        </linearGradient>
      </defs>
      <circle cx="24" cy="24" r="22" fill={`url(#${id}-face)`} />
      <circle cx="24" cy="24" r="22" fill="none" stroke="#B87700" strokeOpacity="0.35" />
      <circle cx="24" cy="24" r="17" fill="none" stroke={`url(#${id}-rim)`} strokeWidth="1.6" />
      <path
        d="M24 14.5L26.47 21.1L33.51 21.41L27.99 25.8L29.88 32.59L24 28.7L18.12 32.59L20.01 25.8L14.49 21.41L21.53 21.1Z"
        fill="#1D1757"
        stroke="#1D1757"
        strokeWidth="1.2"
        strokeLinejoin="round"
      />
      <path
        d="M11.5 17.5A14.5 14.5 0 0 1 19.5 9.9"
        fill="none"
        stroke="#fff"
        strokeOpacity="0.6"
        strokeWidth="2"
        strokeLinecap="round"
      />
    </svg>
  );
}
