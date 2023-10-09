import React, { Component } from "react";

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.min";
import Row from "react-bootstrap/Row";
// eslint-disable-next-line
import Navbar from "react-bootstrap/Navbar";
// eslint-disable-next-line
import Nav from "react-bootstrap/Nav";

import { Route, BrowserRouter, Switch } from "react-router-dom";

import RouteConfig from "./RouteConfig";
import SideBar from "./SideBar";

class Template extends Component {
  // eslint-disable-next-line
  constructor(props) {
    super(props);
    this.state = {
      active: false,
    };
  }
  render() {
    let rowClassName = "nm";
    if (this.state.active) {
      rowClassName += " hideSideBar";
    }
    return (
      <div>
        <BrowserRouter>
          <Row className={rowClassName}>
            <SideBar />

            <div className="main-content">
              <div className="content">
                <Switch>
                  {RouteConfig.map((route, i) => (
                    <Route key={route} {...route} />
                  ))}
                </Switch>
              </div>
            </div>
          </Row>
        </BrowserRouter>
      </div>
    );
  }
}

export default Template;
