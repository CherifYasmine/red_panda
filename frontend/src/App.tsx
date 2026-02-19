/**
 * Main App Component
 * 
 * This is the root component that will be filled with routing in Step 15.
 * For now, we're just testing that Tailwind CSS is loaded correctly.
 */

function App() {
  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto p-4">
        <h1 className="text-4xl font-bold text-blue-600 mb-4">Maplewood High School</h1>
        <p className="text-gray-600 text-lg mb-8">Course Planning System</p>
        
        {/* Test Tailwind is loaded */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-semibold mb-2">✅ Styles Loaded</h2>
            <p className="text-gray-600">Tailwind CSS is working!</p>
          </div>
          
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-semibold mb-2">📂 Structure Ready</h2>
            <p className="text-gray-600">Project directories created</p>
          </div>
          
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-semibold mb-2">🔧 Ready to Build</h2>
            <p className="text-gray-600">Step 1 complete!</p>
          </div>
        </div>
        
        {/* Test button styles */}
        <div className="flex gap-2 flex-wrap">
          <button className="inline-flex items-center justify-center px-4 py-2 rounded-lg font-medium transition-colors cursor-pointer text-white hover:opacity-90 bg-blue-600">Primary Button</button>
          <button className="inline-flex items-center justify-center px-4 py-2 rounded-lg font-medium transition-colors cursor-pointer text-white hover:opacity-90 bg-emerald-600">Secondary Button</button>
          <button className="inline-flex items-center justify-center px-4 py-2 rounded-lg font-medium transition-colors cursor-pointer text-white hover:opacity-90 bg-green-700">Success Button</button>
          <button className="inline-flex items-center justify-center px-4 py-2 rounded-lg font-medium transition-colors cursor-pointer text-white hover:opacity-90 bg-red-900">Danger Button</button>
        </div>
      </div>
    </div>
  )
}

export default App
