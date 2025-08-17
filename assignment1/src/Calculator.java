import java.rmi.Remote;
import java.rmi.RemoteException;

// method abstraction for remote calculator
public interface Calculator extends Remote{
    // get value from client
    int getValue() throws RemoteException;

    // push value from client onto the stack
    void pushValue() throws RemoteException;

    // get the operation from the client
    void pushOperation(String operation) throws RemoteException;

    // popping the value from stack
    int popValue() throws RemoteException;

    // boolean to check if the stack is empty
    boolean isEmpty() throws RemoteException;

    // delay the pop operation
    float delayPop(int millis) throws RemoteException;
}
