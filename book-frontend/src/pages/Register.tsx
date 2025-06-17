import React, { useState, useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const Register: React.FC = () => {
  const { register } = useContext(AuthContext);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    if (password !== confirmPassword) {
      setError('Passwords do not match');
      setIsLoading(false);
      return;
    }

    if (password.length < 6) {
      setError('Password must be at least 6 characters long');
      setIsLoading(false);
      return;
    }

    try {
      await register(username, email, password);
    } catch (err: unknown) {
      let errorMessage = 'Registration failed. Please try again.';
      if (typeof err === 'object' && err !== null && 'response' in err) {
        const response = (err as { response?: { data?: unknown } }).response;
        if (response && typeof response.data === 'string') {
          errorMessage = response.data;
        } else if (response && typeof response.data === 'object' && response.data !== null && 'message' in response.data && typeof response.data.message === 'string') {
          errorMessage = response.data.message;
        }
      } else if (err instanceof Error) {
        errorMessage = err.message;
      }
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="page">
      <div className="auth-page">
        <h2>Join BookTracker</h2>
        <p style={{ textAlign: 'center', color: 'var(--mal-gray-600)', marginBottom: '2rem' }}>
          Create your account and start tracking your reading journey
        </p>
        
        {error && <div className="error">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Username</label>
            <input
              type="text"
              className="form-input"
              placeholder="Choose a username"
              value={username}
              onChange={e => setUsername(e.target.value)}
              required
              disabled={isLoading}
              minLength={3}
            />
          </div>
          
          <div className="form-group">
            <label className="form-label">Email</label>
            <input
              type="email"
              className="form-input"
              placeholder="Enter your email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
              disabled={isLoading}
            />
          </div>
          
          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-input"
              placeholder="Choose a strong password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
              disabled={isLoading}
              minLength={6}
            />
          </div>
          
          <div className="form-group">
            <label className="form-label">Confirm Password</label>
            <input
              type="password"
              className="form-input"
              placeholder="Confirm your password"
              value={confirmPassword}
              onChange={e => setConfirmPassword(e.target.value)}
              required
              disabled={isLoading}
              minLength={6}
            />
          </div>
          
          <button type="submit" disabled={isLoading}>
            {isLoading ? 'Creating Account...' : 'Create Account'}
          </button>
        </form>
        
        <div className="auth-link">
          Already have an account? <Link to="/login">Sign in here</Link>
        </div>
      </div>
    </div>
  );
};

export default Register;
