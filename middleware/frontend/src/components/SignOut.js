import React from "react";
import { MDBIcon } from "mdbreact";
// styles for this component is taken from sidebar component
export default function SignOut() {
  const removeToken = () => {
    localStorage.removeItem("token");
    window.location.reload(false);
  };
  return (
    <button className="btn signout-btn" onClick={removeToken}>
      SignOut <MDBIcon icon="sign-out-alt" />
    </button>
  );
}
