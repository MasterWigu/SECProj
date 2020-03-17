package server;

import library.ICommLib;

import java.rmi.registry.*;

public class DPASServer {
    public static void main(String args[]){
        int registryPort = 8000;
        System.out.println("Main OK");
        try{
            ICommLib aDPASService = new DPASService();
            System.out.println("After create");

            Registry reg = LocateRegistry.createRegistry(registryPort);
            reg.rebind("DPASService", aDPASService);

            // A more realistic would be having an autonomous RMI Registry
            // available at the default port
            // (implies defining a 'codebase' to allow the RMI Registry
            // to remotely obtain the interfaces for the
            // objects that will be registered):
            //
            // Naming.rebind("ShapeList", aShapelist);

            System.out.println("DPAS server ready");

            System.out.println("Awaiting connections");
            System.out.println("Press enter to shutdown");
            System.in.read();
            System.exit(0);

        }catch(Exception e) {
            System.out.println("DPAS server main " + e.getMessage());
        }
    }
}