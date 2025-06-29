//import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { ThemeProvider } from './components/ThemeProvider'
import NavBar from './components/NavBar'
import Footer from './components/Footer'
import PrivateRoute from './components/PrivateRoute'
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import Search from './pages/Search'
import Shelf from './pages/Shelf'
import Dashboard from './pages/Dashboard'
import BypassPage from './pages/BypassPage'
import BookDetails from './pages/BookDetails'
import './App.css'

function App() {
  return (
    <ThemeProvider defaultTheme="dark" storageKey="booktracker-theme">
      <Router>
        <AuthProvider>
          <div className="app-container min-h-screen bg-gray-900 text-white">
            <NavBar />
            <main className="main-content">
              <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/bypass" element={<BypassPage />} />
                <Route path="/search" element={<PrivateRoute><Search /></PrivateRoute>} />
                <Route path="/shelf/:shelf" element={<PrivateRoute><Shelf /></PrivateRoute>} />
                <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
                <Route path="/book/:id" element={<BookDetails />} />
                <Route path="*" element={<Navigate to="/" />} />
              </Routes>
            </main>
            <Footer />
          </div>
        </AuthProvider>
      </Router>
    </ThemeProvider>
  )
}

export default App
