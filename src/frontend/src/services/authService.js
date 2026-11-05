import authApi from '../api/auth';

const STORAGE_KEY = 'usuario_logado';

export const authService = {
  login: async (credenciais) => {
    const usuario = await authApi.login(credenciais);
    if (usuario) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(usuario));
    }
    return usuario;
  },

  logout: () => {
    localStorage.removeItem(STORAGE_KEY);
  },

  getUsuarioLogado: () => {
    try {
      const data = localStorage.getItem(STORAGE_KEY);
      return data ? JSON.parse(data) : null;
    } catch {
      return null;
    }
  },

  isAuthenticated: () => {
    return !!localStorage.getItem(STORAGE_KEY);
  },
};

export default authService;

