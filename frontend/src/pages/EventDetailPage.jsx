import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { apiFetch } from '../api/client';

export default function EventDetailPage() {
  const { id } = useParams();
  const [event, setEvent] = useState(null);
  const [entries, setEntries] = useState([]);
  const [boatNames, setBoatNames] = useState({});
  const [error, setError] = useState(null);

  useEffect(() => {
    Promise.all([
      apiFetch(`/api/events/${id}`).then(r => r && r.json()),
      apiFetch(`/api/entries/event/${id}`).then(r => r && r.json()),
    ])
      .then(([eventData, entriesData]) => {
        if (eventData) setEvent(eventData);
        const list = entriesData || [];
        setEntries(list);
        return Promise.all(
          list.map(entry =>
            apiFetch(`/api/boats/${entry.boatId}`)
              .then(r => r && r.json())
              .then(boat => boat && [entry.boatId, boat.name])
          )
        );
      })
      .then(pairs => {
        const map = {};
        for (const pair of pairs) {
          if (pair) map[pair[0]] = pair[1];
        }
        setBoatNames(map);
      })
      .catch(() => setError('Failed to load event details.'));
  }, [id]);

  if (error) return <p>{error}</p>;
  if (!event) return <p>Loading...</p>;

  return (
    <div>
      <h1>{event.name}</h1>
      <p>
        Status:{' '}
        <span style={{ color: event.status === 'OPEN' ? 'green' : 'grey' }}>
          {event.status}
        </span>
      </p>

      {event.status === 'OPEN' && (
        <Link to={`/entries/new?eventId=${id}`}>Enter this event</Link>
      )}

      <h2>Entries</h2>
      {entries.length === 0 ? (
        <p>No entries yet.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Boat</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {entries.map(entry => (
              <tr key={entry.id}>
                <td>{boatNames[entry.boatId] ?? `Boat #${entry.boatId}`}</td>
                <td>{entry.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
