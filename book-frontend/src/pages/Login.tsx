import React, { useState, useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { BookOpen, Lock, User, LogIn } from 'lucide-react';

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
      console.error('Login error:', err);
      if (
        err instanceof Error &&
        (err as { response?: { data?: string | { message?: string; error?: string } } }).response?.data
      ) {
        const errorData = (err as { response?: { data?: string | { message?: string; error?: string } } }).response?.data;
        if (typeof errorData === 'string') {
          setError(errorData);
        } else if (typeof errorData === 'object' && errorData) {
          setError(errorData.message || errorData.error || 'Server error occurred');
        } else {
          setError('An unknown error occurred');
        }
      } else {
        setError('Login failed. Please check your credentials.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-900 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <div className="flex justify-center mb-6">
            <div className="w-16 h-16 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
              <BookOpen size={24} className="text-white" />
            </div>
          </div>
          <h2 className="text-3xl font-bold text-white mb-2">Welcome Back</h2>
          <p className="text-gray-400">
            Sign in to your BookTracker account
          </p>
        </div>
        
        <div className="bg-gray-800 rounded-xl shadow-xl p-8 border border-gray-700">
          {error && (
            <div className="mb-6 p-4 bg-red-900/50 border border-red-500 rounded-lg">
              <p className="text-red-200 text-sm">{error}</p>
            </div>
          )}
          
          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label htmlFor="username" className="block text-sm font-medium text-gray-300 mb-2">
                Username
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <User size={18} className="text-gray-400" />
                </div>
                <input
                  id="username"
                  type="text"
                  className="w-full pl-10 pr-4 py-3 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                  placeholder="Enter your username"
                  value={username}
                  onChange={e => setUsername(e.target.value)}
                  required
                  disabled={isLoading}
                />
              </div>
            </div>
            
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-300 mb-2">
                Password
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Lock size={18} className="text-gray-400" />
                </div>
                <input
                  id="password"
                  type="password"
                  className="w-full pl-10 pr-4 py-3 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                  placeholder="Enter your password"
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  required
                  disabled={isLoading}
                />
              </div>
            </div>
            
            <button 
              type="submit" 
              disabled={isLoading}
              className="w-full flex items-center justify-center py-3 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200"
            >
              {isLoading ? (
                <div className="flex items-center">
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                  Signing In...
                </div>
              ) : (
                <div className="flex items-center">
                  <LogIn size={18} className="mr-2" />
                  Sign In
                </div>
              )}
            </button>
          </form>
        </div>
        
        <div className="text-center">
          <p className="text-gray-400">
            Don't have an account?{' '}
            <Link 
              to="/register" 
              className="text-blue-400 hover:text-blue-300 font-medium transition-colors"
            >
              Sign up here
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;