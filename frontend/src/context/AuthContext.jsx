import { createContext, useState, useCallback } from 'react';

export const AuthContext = createContext(null);

function decodeToken(token) {
  try {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(payload));
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token'));
  const [claims, setClaims] = useState(() => {
    const stored = localStorage.getItem('token');
    return stored ? decodeToken(stored) : null;
  });

  const login = useCallback((newToken) => {
    localStorage.setItem('token', newToken);
    setToken(newToken);
    setClaims(decodeToken(newToken));
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('token');
    setToken(null);
    setClaims(null);
  }, []);

  const user = claims ? { username: claims.sub, role: claims.role } : null;

  return (
    <AuthContext.Provider value={{ token, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
