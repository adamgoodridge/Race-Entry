import { useEffect, useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { apiFetch } from '../api/client';

export default function MyBoatsPage() {
  const { user } = useContext(AuthContext);
  const [boats, setBoats] = useState([]);
  const [boatClasses, setBoatClasses] = useState([]);
  const [error, setError] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [editForm, setEditForm] = useState({ name: '', sailNumber: '', boatClassId: '' });
  const [addForm, setAddForm] = useState({ name: '', sailNumber: '', boatClassId: '' });

  useEffect(() => {
    if (!user?.personId) return;
    Promise.all([
      apiFetch(`/api/boats?ownerId=${user.personId}`).then(r => r?.json()),
      apiFetch('/api/boat-classes').then(r => r?.json()),
    ])
      .then(([boatsData, classesData]) => {
        if (boatsData) setBoats(boatsData);
        if (classesData) setBoatClasses(classesData);
      })
      .catch(() => setError('Failed to load data.'));
  }, [user?.personId]);

  function classNameById(id) {
    return boatClasses.find(c => c.id === id)?.name ?? id;
  }

  async function handleAdd(e) {
    e.preventDefault();
    const res = await apiFetch('/api/boats', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: addForm.name,
        sailNumber: addForm.sailNumber,
        boatClassId: Number(addForm.boatClassId),
        ownerId: user.personId,
      }),
    });
    if (!res?.ok) return;
    const boat = await res.json();
    setBoats(prev => [...prev, boat]);
    setAddForm({ name: '', sailNumber: '', boatClassId: '' });
  }

  function startEdit(boat) {
    setEditingId(boat.id);
    setEditForm({ name: boat.name, sailNumber: boat.sailNumber, boatClassId: boat.boatClassId ?? '' });
  }

  async function handleEdit(e, boatId) {
    e.preventDefault();
    const res = await apiFetch(`/api/boats/${boatId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: editForm.name,
        sailNumber: editForm.sailNumber,
        boatClassId: Number(editForm.boatClassId),
      }),
    });
    if (!res?.ok) return;
    const updated = await res.json();
    setBoats(prev => prev.map(b => b.id === boatId ? updated : b));
    setEditingId(null);
  }

  async function handleDelete(boatId) {
    if (!confirm('Delete this boat?')) return;
    const res = await apiFetch(`/api/boats/${boatId}`, { method: 'DELETE' });
    if (res?.ok) {
      setBoats(prev => prev.filter(b => b.id !== boatId));
    }
  }

  if (error) return <p>{error}</p>;

  return (
    <div>
      <h1>My Boats</h1>
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Sail Number</th>
            <th>Class</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {boats.map(boat =>
            editingId === boat.id ? (
              <tr key={boat.id}>
                <td colSpan={4}>
                  <form onSubmit={e => handleEdit(e, boat.id)}>
                    <input
                      value={editForm.name}
                      onChange={e => setEditForm(f => ({ ...f, name: e.target.value }))}
                      required
                    />
                    <input
                      value={editForm.sailNumber}
                      onChange={e => setEditForm(f => ({ ...f, sailNumber: e.target.value }))}
                      required
                    />
                    <select
                      value={editForm.boatClassId}
                      onChange={e => setEditForm(f => ({ ...f, boatClassId: e.target.value }))}
                      required
                    >
                      <option value="">Select class</option>
                      {boatClasses.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                    </select>
                    <button type="submit">Save</button>
                    <button type="button" onClick={() => setEditingId(null)}>Cancel</button>
                  </form>
                </td>
              </tr>
            ) : (
              <tr key={boat.id}>
                <td>{boat.name}</td>
                <td>{boat.sailNumber}</td>
                <td>{classNameById(boat.boatClassId)}</td>
                <td>
                  <button onClick={() => startEdit(boat)}>Edit</button>
                  {' '}
                  <button onClick={() => handleDelete(boat.id)}>Delete</button>
                </td>
              </tr>
            )
          )}
        </tbody>
      </table>

      <h2>Add Boat</h2>
      <form onSubmit={handleAdd}>
        <input
          placeholder="Name"
          value={addForm.name}
          onChange={e => setAddForm(f => ({ ...f, name: e.target.value }))}
          required
        />
        {' '}
        <input
          placeholder="Sail number"
          value={addForm.sailNumber}
          onChange={e => setAddForm(f => ({ ...f, sailNumber: e.target.value }))}
          required
        />
        {' '}
        <select
          value={addForm.boatClassId}
          onChange={e => setAddForm(f => ({ ...f, boatClassId: e.target.value }))}
          required
        >
          <option value="">Select class</option>
          {boatClasses.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
        </select>
        {' '}
        <button type="submit">Add Boat</button>
      </form>
    </div>
  );
}
