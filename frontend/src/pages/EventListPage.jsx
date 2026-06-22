import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiFetch } from '../api/client';

export default function EventListPage() {
  const [events, setEvents] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    apiFetch('/api/events')
      .then(r => r && r.json())
      .then(data => data && setEvents(data))
      .catch(() => setError('Failed to load events.'));
  }, []);

  if (error) return <p>{error}</p>;

  return (
    <div>
      <h1>Events</h1>
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {events.map(event => (
            <tr key={event.id}>
              <td>{event.name}</td>
              <td>
                <span style={{ color: event.status === 'OPEN' ? 'green' : 'grey' }}>
                  {event.status}
                </span>
              </td>
              <td>
                <Link to={`/events/${event.id}`}>View</Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
