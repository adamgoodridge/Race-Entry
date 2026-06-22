import { AuthProvider } from './context/AuthContext';
import AppRouter from './router';

export default function App() {
  return (
    <AuthProvider>
      <AppRouter />
    </AuthProvider>
  );
}
