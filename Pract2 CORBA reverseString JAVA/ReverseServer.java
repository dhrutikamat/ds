// ReverseServer.java
import ReverseModule.Reverse;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;

class ReverseServer {
    public static void main(String[] args) {
        try {
            // Initialize the ORB
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(args, null);

            // Initialize the portable object adaptor (BOA/POA) connects client request using object reference
            // Uses orb method as resolve_initial_references
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            // Creating an object of ReverseImpl class
            ReverseImpl rvr = new ReverseImpl();

            // Server consists of 2 classes, servant and server. The servant is the subclass of ReversePOA which is generated by the idlj compiler
            // The servant ReverseImpl is the implementation of the ReverseModule idl interface
            // Get the object reference from the servant class
            // Use root POA class and its method servant_to_reference
            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(rvr);
            System.out.println("Step1");

            // Helper class provides narrow method that casts CORBA object reference (ref) into the Java interface
            Reverse h_ref = ReverseModule.ReverseHelper.narrow(ref);
            System.out.println("Step2");

            // ORB layer uses resolve_initial_references method to take initial reference as NameService
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

            // Register new object in the naming context under the Reverse
            System.out.println("Step3");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            System.out.println("Step4");

            String name = "Reverse";
            NameComponent path[] = ncRef.to_name(name);
            ncRef.rebind(path, h_ref);

            // Server runs and waits for invocations of the new object from the client
            System.out.println("Reverse Server reading and waiting.");
            orb.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
