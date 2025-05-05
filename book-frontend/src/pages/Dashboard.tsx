import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { Recommendation } from '../types';
import '../App.css';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';

const Dashboard: React.FC = () => {
  const [recommendations, setRecommendations] = useState<Recommendation[]>([]);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    fetchRecommendations();
  }, []);

  const fetchRecommendations = async () => {
    setIsLoading(true);
    try {
      const res = await api.get<Recommendation[]>('/recommendations');
      setRecommendations(res.data);
    } catch (err) {
      setError('Failed to load recommendations');
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) return <div className="loading">Loading recommendations...</div>;

  return (
    <div className="page dashboard-page">
      <h2>Your Book Recommendations</h2>
      {error && <p className="error">{error}</p>}
      
      {recommendations.length === 0 && !isLoading ? (
        <p className="no-recommendations">
          No recommendations available yet. Try rating more books to get personalized recommendations.
        </p>
      ) : (
        <>
          <div className="recommendation-list">
            {recommendations.map(rec => (
              <div key={rec.externalId} className="recommendation-card">
                {rec.thumbnailUrl && (
                  <div className="book-cover">
                    <img src={rec.thumbnailUrl} alt={rec.title} />
                  </div>
                )}
                <div className="recommendation-info">
                  <h3 className="book-title">{rec.title}</h3>
                  <div className="recommendation-score">
                    Score: <span className="score-value">{rec.score.toFixed(1)}</span>
                  </div>
                  <div className="recommendation-date">
                    Computed: {new Date(rec.computedAt).toLocaleDateString()}
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div className="recommendation-chart">
            <h3>Top Recommendations</h3>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart 
                data={recommendations.slice(0, 10).map(r => ({ 
                  name: r.title.length > 20 ? r.title.substring(0, 20) + '...' : r.title, 
                  score: +r.score.toFixed(2) 
                }))}
              >
                <XAxis dataKey="name" />
                <YAxis domain={[0, 5]} />
                <Tooltip />
                <Bar dataKey="score" fill="#8884d8" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </>
      )}
    </div>
  );
};

export default Dashboard;