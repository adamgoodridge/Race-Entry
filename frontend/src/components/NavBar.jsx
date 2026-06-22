import { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

export default function NavBar() {
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <nav>
      <Link to="/events">Events</Link>
      {' | '}
      <Link to="/my-boats">My Boats</Link>
      {user?.role === 'ROLE_ADMIN' && (
        <>
          {' | '}
          <Link to="/admin/boat-classes">Boat Classes</Link>
          {' | '}
          <Link to="/admin/events">Admin Events</Link>
        </>
      )}
      {' | '}
      <button onClick={handleLogout}>Logout</button>
    </nav>
  );
}
