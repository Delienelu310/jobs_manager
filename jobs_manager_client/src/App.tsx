import './App.css';

import { Routes, Route, BrowserRouter } from "react-router-dom";

import LoginPage from './pages/LoginPage';
import AuthProvider from './authentication/AuthContext';
import WelcomePage from './pages/WelcomePage';


function App() {
  return (
    <div className="App">
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path={"/login"} Component={LoginPage}/>
          
            <Route path={"/"} Component={WelcomePage}/>
          </Routes>
        </BrowserRouter>
      </AuthProvider>
      
    </div>
  );
}

export default App;
