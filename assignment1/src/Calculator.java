import java.rmi.Remote;
import java.rmi.RemoteException;

// method abstraction for remote calculator
public interface Calculator extends Remote{
    // get value from client
    //int getValue() throws RemoteException;

    // pushes an integer value onto the server's stack
    void pushValue(int value) throws RemoteException;

    // get the operation from the client
    void pushOperation(String operator) throws RemoteException;

    // popping the value from stack
    int pop() throws RemoteException;

    // boolean to check if the stack is empty
    boolean isEmpty() throws RemoteException;

    // delay the pop operation
    int delayPop(int millis) throws RemoteException;
}
