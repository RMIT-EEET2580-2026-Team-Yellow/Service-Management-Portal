import { useState } from 'react'
import AppRouter from "./common/router/AppRouter";
import './App.css'
import Navbar from './components/NavBar';

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
