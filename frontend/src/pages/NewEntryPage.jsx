import { useEffect, useState, useContext } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { apiFetch } from '../api/client';

export default function NewEntryPage() {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const preselectedEventId = searchParams.get('eventId') || '';

  const [boats, setBoats] = useState([]);
  const [events, setEvents] = useState([]);
  const [boatId, setBoatId] = useState('');
  const [eventId, setEventId] = useState(preselectedEventId);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user?.personId) return;
    Promise.all([
      apiFetch(`/api/boats?ownerId=${user.personId}`).then(r => r?.json()),
      apiFetch('/api/events').then(r => r?.json()),
    ])
      .then(([boatsData, eventsData]) => {
        if (boatsData) setBoats(boatsData);
        if (eventsData) setEvents(eventsData.filter(e => e.status === 'OPEN'));
      })
      .catch(() => setError('Failed to load data.'))
      .finally(() => setLoading(false));
  }, [user?.personId]);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    const res = await apiFetch('/api/entries', {
      method: 'POST',
      body: JSON.stringify({ boatId: Number(boatId), eventId: Number(eventId) }),
    });
    if (!res) return;
    if (res.status === 409) {
      setError('Entry already exists for this boat in this event.');
      return;
    }
    if (!res.ok) {
      setError('Failed to create entry.');
      return;
    }
    const entry = await res.json();
    navigate(`/entries/${entry.id}`);
  }

  if (loading) return <p>Loading...</p>;

  return (
    <div>
      <h1>New Entry</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label>Boat: </label>
          <select value={boatId} onChange={e => setBoatId(e.target.value)} required>
            <option value="">Select boat</option>
            {boats.map(b => (
              <option key={b.id} value={b.id}>{b.name}</option>
            ))}
          </select>
        </div>
        <div>
          <label>Event: </label>
          <select value={eventId} onChange={e => setEventId(e.target.value)} required>
            <option value="">Select event</option>
            {events.map(ev => (
              <option key={ev.id} value={ev.id}>{ev.name}</option>
            ))}
          </select>
        </div>
        <button type="submit">Create Entry</button>
      </form>
    </div>
  );
}
