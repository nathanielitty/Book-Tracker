import React, { useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';

const Login: React.FC = () => {
  const { login } = useContext(AuthContext);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await login(username, password);
    } catch (err: unknown) {
      if (
        err instanceof Error &&
        (err as { response?: { data?: string } }).response?.data
      ) {
        setError((err as { response?: { data?: string } }).response?.data || 'An unknown error occurred');
      } else {
        setError('Login failed');
      }
    }
  };

  return (
    <div className="page auth-page">
      <h2>Login</h2>
      {error && <p className="error">{error}</p>}
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={e => setUsername(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={e => setPassword(e.target.value)}
          required
        />
        <button type="submit">Login</button>
      </form>
    </div>
  );
};

export default Login;