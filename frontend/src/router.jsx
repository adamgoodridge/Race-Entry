import { BrowserRouter, Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { useContext } from 'react';
import { AuthContext } from './context/AuthContext';
import NavBar from './components/NavBar';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import EventListPage from './pages/EventListPage';
import EventDetailPage from './pages/EventDetailPage';
import MyBoatsPage from './pages/MyBoatsPage';
import NewEntryPage from './pages/NewEntryPage';
import EntryDetailPage from './pages/EntryDetailPage';
import AdminBoatClassesPage from './pages/AdminBoatClassesPage';
import AdminEventsPage from './pages/AdminEventsPage';

function ProtectedLayout() {
  const { token } = useContext(AuthContext);
  if (!token) return <Navigate to="/login" replace />;
  return (
    <>
      <NavBar />
      <Outlet />
    </>
  );
}

function AdminLayout() {
  const { token, user } = useContext(AuthContext);
  if (!token) return <Navigate to="/login" replace />;
  if (user?.role !== 'ROLE_ADMIN') return <Navigate to="/events" replace />;
  return (
    <>
      <NavBar />
      <Outlet />
    </>
  );
}

export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route element={<ProtectedLayout />}>
          <Route path="/events" element={<EventListPage />} />
          <Route path="/events/:id" element={<EventDetailPage />} />
          <Route path="/my-boats" element={<MyBoatsPage />} />
          <Route path="/entries/new" element={<NewEntryPage />} />
          <Route path="/entries/:id" element={<EntryDetailPage />} />
        </Route>
        <Route element={<AdminLayout />}>
          <Route path="/admin/boat-classes" element={<AdminBoatClassesPage />} />
          <Route path="/admin/events" element={<AdminEventsPage />} />
        </Route>
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
