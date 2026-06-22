export async function apiFetch(path, options = {}) {
  const token = localStorage.getItem('token');
  const headers = { 'Content-Type': 'application/json', ...options.headers };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  const response = await fetch(path, { ...options, headers });
  if (response.status === 401) {
    localStorage.removeItem('token');
    window.location.href = '/login';
    return null;
  }
  return response;
}
