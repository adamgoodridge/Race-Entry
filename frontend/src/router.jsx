import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useContext } from 'react';
import { AuthContext } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import EventListPage from './pages/EventListPage';
import EventDetailPage from './pages/EventDetailPage';
import MyBoatsPage from './pages/MyBoatsPage';
import NewEntryPage from './pages/NewEntryPage';
import EntryDetailPage from './pages/EntryDetailPage';
import AdminBoatClassesPage from './pages/AdminBoatClassesPage';
import AdminEventsPage from './pages/AdminEventsPage';

function ProtectedRoute({ children }) {
  const { token } = useContext(AuthContext);
  return token ? children : <Navigate to="/login" replace />;
}

function AdminRoute({ children }) {
  const { token, user } = useContext(AuthContext);
  if (!token) return <Navigate to="/login" replace />;
  if (user?.role !== 'ROLE_ADMIN') return <Navigate to="/events" replace />;
  return children;
}

export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/events" element={<ProtectedRoute><EventListPage /></ProtectedRoute>} />
        <Route path="/events/:id" element={<ProtectedRoute><EventDetailPage /></ProtectedRoute>} />
        <Route path="/my-boats" element={<ProtectedRoute><MyBoatsPage /></ProtectedRoute>} />
        <Route path="/entries/new" element={<ProtectedRoute><NewEntryPage /></ProtectedRoute>} />
        <Route path="/entries/:id" element={<ProtectedRoute><EntryDetailPage /></ProtectedRoute>} />
        <Route path="/admin/boat-classes" element={<AdminRoute><AdminBoatClassesPage /></AdminRoute>} />
        <Route path="/admin/events" element={<AdminRoute><AdminEventsPage /></AdminRoute>} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
