import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public class CalculatorServer {
    public static void main(String[] args) {
        try {
            // create and export the remote object
            Calculator calculator = new CalculatorImplementation();

            //start RMI registry on port 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // bind the server calculator server object to the name "calc" in the registry
            registry.rebind("calc", calculator);
            System.out.println("Calculator Server running ...");

        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
