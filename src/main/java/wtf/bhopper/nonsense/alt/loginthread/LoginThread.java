package wtf.bhopper.nonsense.alt.loginthread;

import wtf.bhopper.nonsense.alt.Alt;
import wtf.bhopper.nonsense.alt.mslogin.LoginData;

import java.util.function.Consumer;

public abstract class LoginThread extends Thread {

    protected Alt account;
    protected final Consumer<LoginData> loginDataCallback;
    protected final Consumer<Exception> errorCallback;

    public LoginThread(Consumer<LoginData> loginDataCallback, Consumer<Exception> errorCallback) {
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

}
