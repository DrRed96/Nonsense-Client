package wtf.bhopper.nonsense.alt.loginthread;

import wtf.bhopper.nonsense.alt.mslogin.LoginData;

@FunctionalInterface
public interface LoginDataCallback {
    void accept(LoginData loginData);
}
