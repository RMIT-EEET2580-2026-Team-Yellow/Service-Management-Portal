import { useState } from 'react'
import AppRouter from "./common/router/AppRouter";
import Navbar from './components/reusable/NavBar';
import './App.css'

function App() {
  return (
    <div>
      <div>
        <Navbar />
        <AppRouter />
      </div>
    </div>
  );
}

export default App;
