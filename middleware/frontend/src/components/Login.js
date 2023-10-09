import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
// eslint-disable-next-line
import UseToken from "./UseToken";
import { MDBIcon } from "mdbreact";
import "./config";
import "../assets/scss/login.scss";

export default function Login({ setToken }) {
  const [username, setUserName] = useState();
  const [password, setPassword] = useState();
  const [loginStatus, setLoginStatus] = useState();
  const [isSubmitted, setIsSubmitted] = useState();

  const loginUser = async (credentials) => {
    const crd = Buffer.from(
      credentials.username + ":" + credentials.password
    ).toString("base64");
    const url = global.config.url.security;
    try {
      return fetch(url, {
        method: "POST",
        headers: {
          Authorization: "Basic " + crd,
        },
        body: JSON.stringify(credentials),
      })
        .then((response) => response)
        .then((data) => handleContent(data, crd));
      //.then(data => data.ok? crd : data.ok);
    } catch (error) {
      console.log("error", error);
      return false;
    }
  };

  const handleContent = (data, crd) => {
    const token = data.ok ? crd : data.ok;
    if (token) {
      setToken(token);
    }
    setLoginStatus(token ? false : true);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    setIsSubmitted(true);
  };

  useEffect(() => {
    const fetchData = (async) => {
      loginUser({ username, password });
    };
    if (isSubmitted) {
      fetchData();
    }
    return () => {
      setIsSubmitted(false);
    };
    // eslint-disable-next-line
  }, [isSubmitted]);

  return (
    <section className="ftco-section">
      <div className="container">
        <div className="row justify-content-center">
          <div className="col-md-6 text-center mb-5">
            <h2 className="heading-section">PMA Connector</h2>
          </div>
        </div>
        <div className="row justify-content-center">
          <div className="col-md-7 col-lg-6">
            <div className="login-wrap p-5 p-md-6">
              <div className="icon d-flex align-items-center justify-content-center">
                <MDBIcon far icon="user" />
              </div>
              <h3 className="text-center mb-4 login-header">LogIn</h3>
              <form action="#" className="login-form" onSubmit={handleSubmit}>
                <div className="form-group">
                  <input
                    type="text"
                    className="form-control rounded-left"
                    placeholder="Username"
                    required=""
                    onChange={(e) => setUserName(e.target.value)}
                  />
                </div>
                <div className="form-group d-flex">
                  <input
                    type="password"
                    className="form-control rounded-left"
                    placeholder="Password"
                    required=""
                    onChange={(e) => setPassword(e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <button
                    type="submit"
                    className="form-control btn btn-primary rounded submit px-3"
                  >
                    Login
                  </button>
                </div>
                <div className="form-group">
                  {loginStatus ? (
                    <div className="login-status text-center">
                      <p>Access Denied </p>
                    </div>
                  ) : (
                    ""
                  )}
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

Login.propTypes = {
  setToken: PropTypes.func.isRequired,
};
