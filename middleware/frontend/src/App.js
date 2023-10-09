import React from "react";
import { BrowserRouter, Route, Switch } from "react-router-dom";
import Login from "./components/Login";
import UseToken from "./components/UseToken";
import Template from "./components/Template";

function App() {
  const { token, setToken } = UseToken();
  if (!token) {
    return (
      <>
        <div className="wrapper">
          <Login setToken={setToken} />
        </div>
      </>
    );
  }
  return (
    <>
      <div className="wrapper">
        <BrowserRouter>
          <Switch>
            <Route path="/" component={Template} />
          </Switch>
        </BrowserRouter>
      </div>
    </>
  );
}

export default App;
