import { BookOpen, Target, Search, Smartphone, TrendingUp, Users, Shield, Sparkles } from "lucide-react"
import Link from "next/link"
import { Button } from "@/components/ui/button"

export default function Home() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-900 to-gray-800 text-white">
      {/* Hero Section */}
      <div className="relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-blue-900/20 via-gray-900 to-purple-900/20"></div>
        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <div>
              <h1 className="text-5xl md:text-6xl font-bold mb-6 bg-gradient-to-r from-blue-400 to-purple-500 bg-clip-text text-transparent">
                BookTracker
              </h1>
              <p className="text-2xl text-gray-300 mb-6">Track, rate, and discover your next favorite book</p>
              <p className="text-lg text-gray-400 mb-8 leading-relaxed">
                A modern, MyAnimeList-inspired book tracking application that helps you organize your reading life. Keep
                track of books you've read, want to read, and are currently reading. Get personalized recommendations
                and discover new books tailored to your taste.
              </p>
              <div className="flex flex-col sm:flex-row gap-4">
                <Button
                  asChild
                  className="bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white px-8 py-6 h-auto font-semibold rounded-lg transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5"
                >
                  <Link href="/register">
                    <Sparkles className="mr-2 h-5 w-5" />
                    Get Started
                  </Link>
                </Button>
                <Button
                  asChild
                  variant="outline"
                  className="bg-gray-800 text-white border-gray-700 px-8 py-6 h-auto font-semibold rounded-lg hover:bg-gray-700 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5"
                >
                  <Link href="/login">
                    <BookOpen className="mr-2 h-5 w-5" />
                    Login
                  </Link>
                </Button>
              </div>
            </div>

            <div className="relative">
              <div className="flex items-center justify-center">
                <div className="relative">
                  <div className="grid grid-cols-3 gap-4 rotate-12 transform">
                    {["📚", "📖", "📕", "📗", "📘", "📙"].map((emoji, index) => (
                      <div
                        key={index}
                        className="w-16 h-16 bg-gradient-to-br from-gray-800 to-gray-700 rounded-lg flex items-center justify-center text-3xl shadow-lg animate-pulse"
                        style={{ animationDelay: `${index * 0.2}s` }}
                      >
                        {emoji}
                      </div>
                    ))}
                  </div>
                  <div className="absolute -inset-4 bg-gradient-to-r from-blue-500/20 to-purple-500/20 rounded-xl blur-xl"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Features Section */}
      <div className="py-24 bg-gray-800/50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-white">Why BookTracker?</h2>
            <p className="text-xl text-gray-400">Everything you need to manage your reading journey</p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
            <div className="group">
              <div className="bg-gray-800 rounded-xl p-8 text-center hover:bg-gray-700 transition-all duration-300 shadow-lg hover:shadow-xl transform hover:-translate-y-2 border border-gray-700">
                <div className="w-16 h-16 bg-gradient-to-br from-blue-500 to-blue-600 rounded-lg flex items-center justify-center mx-auto mb-6 group-hover:scale-110 transition-transform duration-300">
                  <TrendingUp className="h-6 w-6 text-white" />
                </div>
                <h3 className="text-xl font-semibold mb-4 text-white">Track Your Progress</h3>
                <p className="text-gray-400 leading-relaxed">
                  Keep detailed records of your reading journey with custom shelves and ratings
                </p>
              </div>
            </div>

            <div className="group">
              <div className="bg-gray-800 rounded-xl p-8 text-center hover:bg-gray-700 transition-all duration-300 shadow-lg hover:shadow-xl transform hover:-translate-y-2 border border-gray-700">
                <div className="w-16 h-16 bg-gradient-to-br from-purple-500 to-purple-600 rounded-lg flex items-center justify-center mx-auto mb-6 group-hover:scale-110 transition-transform duration-300">
                  <Target className="h-6 w-6 text-white" />
                </div>
                <h3 className="text-xl font-semibold mb-4 text-white">Set Reading Goals</h3>
                <p className="text-gray-400 leading-relaxed">
                  Challenge yourself with annual reading goals and track your progress
                </p>
              </div>
            </div>

            <div className="group">
              <div className="bg-gray-800 rounded-xl p-8 text-center hover:bg-gray-700 transition-all duration-300 shadow-lg hover:shadow-xl transform hover:-translate-y-2 border border-gray-700">
                <div className="w-16 h-16 bg-gradient-to-br from-green-500 to-green-600 rounded-lg flex items-center justify-center mx-auto mb-6 group-hover:scale-110 transition-transform duration-300">
                  <Search className="h-6 w-6 text-white" />
                </div>
                <h3 className="text-xl font-semibold mb-4 text-white">Discover New Books</h3>
                <p className="text-gray-400 leading-relaxed">
                  Get personalized recommendations based on your reading preferences
                </p>
              </div>
            </div>

            <div className="group">
              <div className="bg-gray-800 rounded-xl p-8 text-center hover:bg-gray-700 transition-all duration-300 shadow-lg hover:shadow-xl transform hover:-translate-y-2 border border-gray-700">
                <div className="w-16 h-16 bg-gradient-to-br from-orange-500 to-orange-600 rounded-lg flex items-center justify-center mx-auto mb-6 group-hover:scale-110 transition-transform duration-300">
                  <Smartphone className="h-6 w-6 text-white" />
                </div>
                <h3 className="text-xl font-semibold mb-4 text-white">Modern Interface</h3>
                <p className="text-gray-400 leading-relaxed">
                  Enjoy a beautiful, responsive design inspired by popular tracking platforms
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Stats Section */}
      <div className="py-24 bg-gray-900">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid md:grid-cols-3 gap-8 text-center">
            <div className="group">
              <div className="flex items-center justify-center mb-4">
                <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-purple-500 rounded-lg flex items-center justify-center">
                  <Users className="h-6 w-6 text-white" />
                </div>
              </div>
              <div className="text-4xl font-bold text-white mb-2">10,000+</div>
              <div className="text-gray-400">Active Readers</div>
            </div>
            <div className="group">
              <div className="flex items-center justify-center mb-4">
                <div className="w-12 h-12 bg-gradient-to-br from-green-500 to-blue-500 rounded-lg flex items-center justify-center">
                  <BookOpen className="h-6 w-6 text-white" />
                </div>
              </div>
              <div className="text-4xl font-bold text-white mb-2">1M+</div>
              <div className="text-gray-400">Books Tracked</div>
            </div>
            <div className="group">
              <div className="flex items-center justify-center mb-4">
                <div className="w-12 h-12 bg-gradient-to-br from-purple-500 to-pink-500 rounded-lg flex items-center justify-center">
                  <Shield className="h-6 w-6 text-white" />
                </div>
              </div>
              <div className="text-4xl font-bold text-white mb-2">99.9%</div>
              <div className="text-gray-400">Uptime</div>
            </div>
          </div>
        </div>
      </div>

      {/* CTA Section */}
      <div className="py-24 bg-gradient-to-br from-blue-900/20 via-gray-900 to-purple-900/20">
        <div className="max-w-4xl mx-auto text-center px-4 sm:px-6 lg:px-8">
          <h2 className="text-3xl md:text-4xl font-bold mb-6 text-white">Ready to start your reading journey?</h2>
          <p className="text-xl text-gray-300 mb-8">
            Join thousands of readers who trust BookTracker to organize their library
          </p>
          <Button
            asChild
            className="bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white px-10 py-7 h-auto font-semibold rounded-lg transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 text-lg"
          >
            <Link href="/register">
              <Sparkles className="mr-3 h-6 w-6" />
              Sign Up Now
            </Link>
          </Button>
        </div>
      </div>
    </div>
  )
}