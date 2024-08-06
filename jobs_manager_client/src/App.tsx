import './App.css';

import { Routes, Route, BrowserRouter } from "react-router-dom";

import LoginPage from './pages/LoginPage';
import AuthProvider from './authentication/AuthContext';
import WelcomePage from './pages/WelcomePage';
import ProjectListPage from './pages/ProjectsListPage';
import ProjectPage from './pages/ProjectPage';
import JobNodePage from './pages/JobNodePage';
import UsersManagementPage from './pages/UsersManagementPage';
import Header from './components/Header';


function App() {
  return (
    <div className="App">
      <AuthProvider>
      
        <BrowserRouter>
          <Header/>
          <Routes>
            <Route path={"/login"} Component={LoginPage}/>
          
            <Route path={"/"} Component={WelcomePage}/>

            <Route path={"/projects"} Component={ProjectListPage}/>

            <Route path={"/projects/:projectId"} Component={ProjectPage}/>

            <Route path={"/projects/:projectId/job_nodes/:jobNodeId"} Component={JobNodePage}/>

            <Route path={"/users"} Component={UsersManagementPage}/>
            
          </Routes>
        </BrowserRouter>
      </AuthProvider>
      
    </div>
  );
}

export default App;
