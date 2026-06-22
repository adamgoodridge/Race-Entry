import { useEffect, useState } from 'react';
import { apiFetch } from '../api/client';

export default function AdminEventsPage() {
  const [events, setEvents] = useState([]);
  const [error, setError] = useState(null);
  const [addName, setAddName] = useState('');
  const [addError, setAddError] = useState(null);

  useEffect(() => {
    apiFetch('/api/events')
      .then(r => r?.json())
      .then(data => { if (data) setEvents(data); })
      .catch(() => setError('Failed to load events.'));
  }, []);

  async function handleClose(id) {
    const res = await apiFetch(`/api/events/${id}/close`, { method: 'PUT' });
    if (res?.ok) {
      const updated = await res.json();
      setEvents(prev => prev.map(e => e.id === id ? updated : e));
    }
  }

  async function handleDelete(id) {
    if (!confirm('Delete this event?')) return;
    const res = await apiFetch(`/api/events/${id}`, { method: 'DELETE' });
    if (res?.ok) {
      setEvents(prev => prev.filter(e => e.id !== id));
    }
  }

  async function handleAdd(e) {
    e.preventDefault();
    setAddError(null);
    const res = await apiFetch('/api/events', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: addName }),
    });
    if (!res?.ok) {
      setAddError('Failed to create event.');
      return;
    }
    const created = await res.json();
    setEvents(prev => [...prev, created]);
    setAddName('');
  }

  if (error) return <p>{error}</p>;

  return (
    <div>
      <h1>Admin Events</h1>
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {events.map(ev => (
            <tr key={ev.id}>
              <td>{ev.name}</td>
              <td>
                <span style={{ color: ev.status === 'OPEN' ? 'green' : 'grey' }}>
                  {ev.status}
                </span>
              </td>
              <td>
                {ev.status === 'OPEN' && (
                  <button onClick={() => handleClose(ev.id)}>Close</button>
                )}
                {' '}
                <button onClick={() => handleDelete(ev.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <h2>Create Event</h2>
      <form onSubmit={handleAdd}>
        <input
          placeholder="Name"
          value={addName}
          onChange={e => setAddName(e.target.value)}
          required
        />
        {' '}
        <button type="submit">Create</button>
      </form>
      {addError && <p>{addError}</p>}
    </div>
  );
}
