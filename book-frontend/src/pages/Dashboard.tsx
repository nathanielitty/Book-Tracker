import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import '../App.css';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';

type RecDto = {
  externalId: string;
  title: string;
  thumbnailUrl: string;
  score: number;
  computedAt: string;
};

const Dashboard: React.FC = () => {
  const [recs, setRecs] = useState<RecDto[]>([]);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get<RecDto[]>('/recommendations')
      .then(res => setRecs(res.data))
      .catch(() => setError('Failed to load recommendations'));
  }, []);

  if (error) return <p className="error">{error}</p>;

  return (
    <div className="page">
      <h2>Recommendations</h2>
      <div className="rec-list">
        {recs.map(r => (
          <div key={r.externalId} className="rec-card">
            {r.thumbnailUrl && <img src={r.thumbnailUrl} alt={r.title} />}
            <p>{r.title}</p>
          </div>
        ))}
      </div>
      <h3>Top Scores</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={recs.slice(0, 10).map(r => ({ name: r.title.substring(0,10)+'...', score: r.score }))}>
          <XAxis dataKey="name" />
          <YAxis />
          <Tooltip />
          <Bar dataKey="score" fill="#8884d8" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default Dashboard;