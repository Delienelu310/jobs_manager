import './App.css';

import { Routes, Route, BrowserRouter } from "react-router-dom";

import LoginPage from './pages/LoginPage';
import AuthProvider from './authentication/AuthContext';
import WelcomePage from './pages/WelcomePage';
import ProjectListPage from './pages/ProjectsListPage';
import ProjectPage from './pages/ProjectPage';


function App() {
  return (
    <div className="App">
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path={"/login"} Component={LoginPage}/>
          
            <Route path={"/"} Component={WelcomePage}/>

            <Route path={"/projects"} Component={ProjectListPage}/>

            <Route path={"/projects/:projectId"} Component={ProjectPage}/>
          </Routes>
        </BrowserRouter>
      </AuthProvider>
      
    </div>
  );
}

export default App;
