package wtf.bhopper.nonsense.alt.loginthread;

import wtf.bhopper.nonsense.alt.Alt;
import wtf.bhopper.nonsense.alt.mslogin.LoginData;
import wtf.bhopper.nonsense.util.misc.ErrorCallback;

public abstract class LoginThread extends Thread {

    protected Alt account;
    protected final LoginDataCallback loginDataCallback;
    protected final ErrorCallback errorCallback;

    public LoginThread(LoginDataCallback loginDataCallback, ErrorCallback errorCallback) {
        this.loginDataCallback = loginDataCallback;
        this.errorCallback = errorCallback;
        this.setName("Account login thread");
    }

    abstract void execute();

    abstract void finish();

    @Override
    public void run() {
        this.execute();
    }

    @Override
    public void interrupt() {
        this.finish();
        super.interrupt();
    }

    public interface AccountLoginCallback {
        void accept(LoginData loginData);
    }

}
