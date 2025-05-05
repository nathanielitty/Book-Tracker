import React, { useContext } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { Shelf } from '../types';

const NavBar: React.FC = () => {
  const { isAuthenticated, logout } = useContext(AuthContext);
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
  };

  // Format shelf name for display
  const formatShelfName = (shelf: string) => {
    return shelf.replace(/_/g, ' ').toLowerCase()
      .split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  };

  // Check if a link is active
  const isActive = (path: string) => {
    return location.pathname === path ? 'active-link' : '';
  };

  return (
    <nav className="navbar">
      <div className="nav-brand">
        <Link to="/" className={isActive('/')}>
          📚 Book Tracker
        </Link>
      </div>

      <div className="nav-links">
        <Link to="/search" className={isActive('/search')}>Search</Link>
        
        {isAuthenticated && (
          <>
            {/* Render links for each shelf type */}
            {Object.values(Shelf).map(shelf => (
              <Link 
                key={shelf}
                to={`/shelf/${shelf}`} 
                className={isActive(`/shelf/${shelf}`)}
              >
                {formatShelfName(shelf)}
              </Link>
            ))}
            
            <Link to="/dashboard" className={isActive('/dashboard')}>
              Recommendations
            </Link>
          </>
        )}
      </div>
      
      <div className="nav-auth">
        {isAuthenticated ? (
          <button onClick={handleLogout} className="logout-btn">Logout</button>
        ) : (
          <>
            <Link to="/login" className={isActive('/login')}>Login</Link>
            <Link to="/register" className={isActive('/register')}>Register</Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default NavBar;