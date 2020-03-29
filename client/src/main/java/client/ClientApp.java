package client;

public class ClientApp {

    public static void main(String[] args) {
        try {
            String b = args[0];
            int a = Integer.parseInt(b);
            Client c = new Client(a);
            c.login();
            c.work();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
