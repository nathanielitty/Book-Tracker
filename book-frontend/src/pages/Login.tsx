import React, { useState, useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const Login: React.FC = () => {
  const { login } = useContext(AuthContext);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');
    
    try {
      await login(username, password);
    } catch (err: unknown) {
      if (
        err instanceof Error &&
        (err as { response?: { data?: string } }).response?.data
      ) {
        setError((err as { response?: { data?: string } }).response?.data || 'An unknown error occurred');
      } else {
        setError('Login failed. Please check your credentials.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="page">
      <div className="auth-page">
        <h2>Welcome Back</h2>
        <p style={{ textAlign: 'center', color: 'var(--mal-gray-600)', marginBottom: '2rem' }}>
          Sign in to your BookTracker account
        </p>
        
        {error && <div className="error">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Username</label>
            <input
              type="text"
              className="form-input"
              placeholder="Enter your username"
              value={username}
              onChange={e => setUsername(e.target.value)}
              required
              disabled={isLoading}
            />
          </div>
          
          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-input"
              placeholder="Enter your password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
              disabled={isLoading}
            />
          </div>
          
          <button type="submit" disabled={isLoading}>
            {isLoading ? 'Signing In...' : 'Sign In'}
          </button>
        </form>
        
        <div className="auth-link">
          Don't have an account? <Link to="/register">Sign up here</Link>
        </div>
      </div>
    </div>
  );
};

export default Login;