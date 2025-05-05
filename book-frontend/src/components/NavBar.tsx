import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const NavBar: React.FC = () => {
  const { isAuthenticated, logout } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <Link to="/search">Search</Link>
      {isAuthenticated && (
        <>
          <Link to="/shelf/WANT_TO_READ">Want to Read</Link>
          <Link to="/shelf/CURRENTLY_READING">Currently Reading</Link>
          <Link to="/shelf/COMPLETED">Completed</Link>
          <Link to="/dashboard">Recommendations</Link>
          <button onClick={handleLogout}>Logout</button>
        </>
      )}
      {!isAuthenticated && (
        <>
          <Link to="/login">Login</Link>
          <Link to="/register">Register</Link>
        </>
      )}
    </nav>
  );
};

export default NavBar;