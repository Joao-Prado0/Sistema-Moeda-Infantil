import VMasker from 'vanilla-masker';

export const mascaras = {
  cpf: (valor) => VMasker.toPattern(valor, '999.999.999-99'),
  cnpj: (valor) => VMasker.toPattern(valor, '99.999.999/9999-99'),
  cep: (valor) => VMasker.toPattern(valor, '99999-999'),
};

const formatadorMoedas = new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 2 });

export function formatarMoedas(valor) {
  return formatadorMoedas.format(Number(valor) || 0);
}

export function primeiroNome(nome = '') {
  return nome.trim().split(/\s+/)[0] || '';
}

export function iniciais(nome = '') {
  const partes = nome.trim().split(/\s+/).filter(Boolean);
  if (partes.length === 0) return '?';
  const ultima = partes.length > 1 ? partes[partes.length - 1][0] : '';
  return (partes[0][0] + ultima).toUpperCase();
}

// O backend envia LocalDateTime como "2026-10-21T13:44:53.652147"
export function membroDesde(dataCriacao) {
  if (!dataCriacao) return null;
  const [ano, mes] = Array.isArray(dataCriacao)
    ? dataCriacao
    : String(dataCriacao).split('-').map(Number);
  if (!ano || !mes) return null;
  return new Date(ano, mes - 1).toLocaleDateString('pt-BR', { month: 'long', year: 'numeric' });
}

// Retorna um nível de 1 (fraca) a 4 (forte)
export function forcaSenha(senha = '') {
  let pontos = 0;
  if (senha.length >= 6) pontos++;
  if (senha.length >= 10) pontos++;
  if (/[a-z]/.test(senha) && /[A-Z]/.test(senha)) pontos++;
  if (/\d/.test(senha) && /[^A-Za-z0-9]/.test(senha)) pontos++;
  return Math.max(1, pontos);
}
