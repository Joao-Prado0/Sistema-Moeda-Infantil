import { request } from '../services/api';

export const authApi = {
  login: (credenciais) =>
    request('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credenciais),
    }),
};

export default authApi;

