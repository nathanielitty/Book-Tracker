import React from 'react';
import { Link } from 'react-router-dom';
import { BookOpen, ArrowLeft } from 'lucide-react';

const BypassPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-gray-900 text-white flex items-center justify-center">
      <div className="max-w-md w-full text-center space-y-6">
        <div className="flex justify-center mb-6">
          <div className="w-16 h-16 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
            <BookOpen size={24} className="text-white" />
          </div>
        </div>
        
        <h1 className="text-3xl font-bold text-white mb-4">Development Access</h1>
        <p className="text-gray-400 mb-8">
          This is a development bypass page for testing purposes.
        </p>
        
        <div className="space-y-4">
          <Link 
            to="/dashboard" 
            className="block w-full bg-gradient-to-r from-blue-600 to-purple-600 text-white py-3 px-6 rounded-lg font-medium hover:from-blue-700 hover:to-purple-700 transition-all duration-200"
          >
            Access Dashboard
          </Link>
          <Link 
            to="/" 
            className="flex items-center justify-center w-full bg-gray-800 text-white py-3 px-6 rounded-lg font-medium hover:bg-gray-700 transition-colors border border-gray-700"
          >
            <ArrowLeft size={16} className="mr-2" />
            Back to Home
          </Link>
        </div>
      </div>
    </div>
  );
};

export default BypassPage;
