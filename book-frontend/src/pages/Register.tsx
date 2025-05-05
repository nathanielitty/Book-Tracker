import React, { useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';

const Register: React.FC = () => {
  const { register } = useContext(AuthContext);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await register(username, email, password);
    } catch (err: unknown) {
      let errorMessage = 'Registration failed';
      if (typeof err === 'object' && err !== null && 'response' in err) {
        const response = (err as { response?: { data?: unknown } }).response;
        if (response && typeof response.data === 'string') {
          errorMessage = response.data;
        } else if (response && typeof response.data === 'object' && response.data !== null && 'message' in response.data && typeof response.data.message === 'string') {
          // Handle cases where the error message is nested, e.g., { message: 'Error details' }
          errorMessage = response.data.message;
        }
      } else if (err instanceof Error) {
        errorMessage = err.message;
      }
      setError(errorMessage);
    }
  };

  return (
    <div className="page auth-page">
      <h2>Register</h2>
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
          type="email"
          placeholder="Email"
          value={email}
          onChange={e => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={e => setPassword(e.target.value)}
          required
        />
        <button type="submit">Register</button>
      </form>
    </div>
  );
};

export default Register;