import { useEffect, useState } from 'react';
import { apiFetch } from '../api/client';

export default function AdminBoatClassesPage() {
  const [boatClasses, setBoatClasses] = useState([]);
  const [error, setError] = useState(null);
  const [addName, setAddName] = useState('');
  const [addError, setAddError] = useState(null);

  useEffect(() => {
    apiFetch('/api/boat-classes')
      .then(r => r?.json())
      .then(data => { if (data) setBoatClasses(data); })
      .catch(() => setError('Failed to load boat classes.'));
  }, []);

  async function handleAdd(e) {
    e.preventDefault();
    setAddError(null);
    const res = await apiFetch('/api/boat-classes', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: addName }),
    });
    if (!res?.ok) {
      setAddError('Failed to add boat class.');
      return;
    }
    const created = await res.json();
    setBoatClasses(prev => [...prev, created]);
    setAddName('');
  }

  async function handleDelete(id) {
    if (!confirm('Delete this boat class?')) return;
    const res = await apiFetch(`/api/boat-classes/${id}`, { method: 'DELETE' });
    if (res?.ok) {
      setBoatClasses(prev => prev.filter(c => c.id !== id));
    }
  }

  if (error) return <p>{error}</p>;

  return (
    <div>
      <h1>Boat Classes</h1>
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {boatClasses.map(c => (
            <tr key={c.id}>
              <td>{c.name}</td>
              <td>
                <button onClick={() => handleDelete(c.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <h2>Add Boat Class</h2>
      <form onSubmit={handleAdd}>
        <input
          placeholder="Name"
          value={addName}
          onChange={e => setAddName(e.target.value)}
          required
        />
        {' '}
        <button type="submit">Add</button>
      </form>
      {addError && <p>{addError}</p>}
    </div>
  );
}
