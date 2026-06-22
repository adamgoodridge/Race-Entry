import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { apiFetch } from '../api/client';

export default function EntryDetailPage() {
  const { id } = useParams();
  const [entry, setEntry] = useState(null);
  const [boatName, setBoatName] = useState('');
  const [eventName, setEventName] = useState('');
  const [drivers, setDrivers] = useState([]);
  const [personNames, setPersonNames] = useState({});
  const [persons, setPersons] = useState([]);
  const [addForm, setAddForm] = useState({ personId: '', role: '' });
  const [addError, setAddError] = useState(null);
  const [submitError, setSubmitError] = useState(null);
  const [error, setError] = useState(null);

  async function loadDrivers() {
    const res = await apiFetch(`/api/entries/${id}/drivers`);
    if (!res) return;
    const list = await res.json();
    setDrivers(list);
    const pairs = await Promise.all(
      list.map(d =>
        apiFetch(`/api/persons/${d.personId}`)
          .then(r => r?.json())
          .then(p => p && [d.personId, `${p.firstName} ${p.lastName}`.trim()])
      )
    );
    const map = { ...personNames };
    for (const pair of pairs) {
      if (pair) map[pair[0]] = pair[1];
    }
    setPersonNames(map);
  }

  useEffect(() => {
    Promise.all([
      apiFetch(`/api/entries/${id}`).then(r => r?.json()),
      apiFetch(`/api/entries/${id}/drivers`).then(r => r?.json()),
      apiFetch('/api/persons').then(r => r?.json()),
    ])
      .then(([entryData, driversData, personsData]) => {
        if (!entryData) return;
        setEntry(entryData);
        const driverList = driversData || [];
        setDrivers(driverList);
        if (personsData) setPersons(personsData);
        return Promise.all([
          apiFetch(`/api/boats/${entryData.boatId}`).then(r => r?.json()),
          apiFetch(`/api/events/${entryData.eventId}`).then(r => r?.json()),
          Promise.all(
            driverList.map(d =>
              apiFetch(`/api/persons/${d.personId}`)
                .then(r => r?.json())
                .then(p => p && [d.personId, `${p.firstName} ${p.lastName}`.trim()])
            )
          ),
        ]);
      })
      .then(([boat, event, pairs]) => {
        if (boat) setBoatName(boat.name);
        if (event) setEventName(event.name);
        if (pairs) {
          const map = {};
          for (const pair of pairs) {
            if (pair) map[pair[0]] = pair[1];
          }
          setPersonNames(map);
        }
      })
      .catch(() => setError('Failed to load entry details.'));
  }, [id]);

  async function handleRemoveDriver(driverId) {
    const res = await apiFetch(`/api/entry-drivers/${driverId}`, { method: 'DELETE' });
    if (res?.ok) {
      setDrivers(prev => prev.filter(d => d.id !== driverId));
    }
  }

  async function handleAddDriver(e) {
    e.preventDefault();
    setAddError(null);
    const res = await apiFetch(`/api/entries/${id}/drivers`, {
      method: 'POST',
      body: JSON.stringify({ personId: Number(addForm.personId), role: addForm.role }),
    });
    if (!res) return;
    if (res.status === 409) {
      setAddError('Person already a driver on this entry.');
      return;
    }
    if (!res.ok) {
      setAddError('Failed to add driver.');
      return;
    }
    const newDriver = await res.json();
    const personRes = await apiFetch(`/api/persons/${newDriver.personId}`);
    const person = await personRes?.json();
    if (person) {
      setPersonNames(prev => ({
        ...prev,
        [newDriver.personId]: `${person.firstName} ${person.lastName}`.trim(),
      }));
    }
    setDrivers(prev => [...prev, newDriver]);
    setAddForm({ personId: '', role: '' });
  }

  async function handleSubmitEntry() {
    setSubmitError(null);
    const res = await apiFetch(`/api/entries/${id}/submit`, { method: 'POST' });
    if (!res) return;
    if (res.status === 422) {
      const body = await res.json().catch(() => null);
      setSubmitError(body?.message || 'Business rule violation.');
      return;
    }
    if (!res.ok) {
      setSubmitError('Failed to submit entry.');
      return;
    }
    const updated = await res.json();
    setEntry(updated);
  }

  if (error) return <p>{error}</p>;
  if (!entry) return <p>Loading...</p>;

  return (
    <div>
      <h1>Entry Detail</h1>
      <p>Boat: {boatName || `#${entry.boatId}`}</p>
      <p>Event: {eventName || `#${entry.eventId}`}</p>
      <p>
        Status:{' '}
        <span style={{ color: entry.status === 'SUBMITTED' ? 'green' : 'grey' }}>
          {entry.status}
        </span>
      </p>

      {entry.status !== 'SUBMITTED' && (
        <div>
          <button onClick={handleSubmitEntry}>Submit Entry</button>
          {submitError && <span style={{ color: 'red' }}> {submitError}</span>}
        </div>
      )}

      <h2>Drivers</h2>
      {drivers.length === 0 ? (
        <p>No drivers yet.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Role</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {drivers.map(d => (
              <tr key={d.id}>
                <td>{personNames[d.personId] ?? `Person #${d.personId}`}</td>
                <td>{d.role || '—'}</td>
                <td>
                  <button onClick={() => handleRemoveDriver(d.id)}>Remove</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <h2>Add Driver</h2>
      {addError && <p style={{ color: 'red' }}>{addError}</p>}
      <form onSubmit={handleAddDriver}>
        <select
          value={addForm.personId}
          onChange={e => setAddForm(f => ({ ...f, personId: e.target.value }))}
          required
        >
          <option value="">Select person</option>
          {persons.map(p => (
            <option key={p.id} value={p.id}>
              {`${p.firstName} ${p.lastName}`.trim()}
            </option>
          ))}
        </select>
        {' '}
        <input
          placeholder="Role (optional)"
          value={addForm.role}
          onChange={e => setAddForm(f => ({ ...f, role: e.target.value }))}
        />
        {' '}
        <button type="submit">Add Driver</button>
      </form>
    </div>
  );
}
