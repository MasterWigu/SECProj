package library;

public class SocketServerWorker implements Runnable {
    private SocketServer socketServer;

    public SocketServerWorker(SocketServer ss) {
        socketServer = ss;
    }




    public void run() {
        socketServer.start();
    }

}
