export const HTTP_CONFIG = {
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  
  getAuthHeaders: (): HeadersInit => {
    const token = localStorage.getItem('accessToken');
    return {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    };
  }
};