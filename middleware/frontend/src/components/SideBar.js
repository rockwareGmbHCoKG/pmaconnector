import React, { Component } from "react";
import { MDBIcon } from "mdbreact";
import "../assets/scss/sidebar.scss";
import SignOut from "./SignOut";
import $ from "jquery";

export default class SideBar extends Component {
  constructor(props) {
    super(props);
    this.handleSidebarCollapse = this.handleSidebarCollapse.bind(this);
    this.state = {
      active: false,
    };
  }
  handleSidebarCollapse() {
    this.setState({ active: !this.state.active });
    $("#sidebar").parents(".row").toggleClass("sideBarActive");
  }

  render() {
    return (
      <nav id="sidebar" className={this.state.active ? "active" : ""}>
        <div className="custom-menu">
          <button
            type="button"
            id="sidebarCollapse"
            className="btn btn-sidebar"
            onClick={this.handleSidebarCollapse}
          >
            <MDBIcon icon="bars" />
            <span className="sr-only">Toggle Menu</span>
          </button>
        </div>
        <div className="p-4 pt-5">
          <h1>
            <a href="index.html" className="logo">
              PMA-Connector
            </a>
          </h1>
          <ul className="list-unstyled components mb-5">
            <li>
              <a href="/">All campaigns</a>
            </li>
            <li>
              <a href="/executions">Execution details</a>
            </li>
            <li>
              <a href="/errors">Errors</a>
            </li>
            <li>
              <a href="/transferred-recipients">Transferred recipients</a>
            </li>
          </ul>
          <div className="mb-5">
            <SignOut />
          </div>
          <div className="footer">
            <p>
              <script>document.write(new Date().getFullYear());</script>
            </p>
          </div>
        </div>
      </nav>
    );
  }
}
