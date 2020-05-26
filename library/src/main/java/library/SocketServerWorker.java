package library;

public class SocketServerWorker implements Runnable {
    private SocketServer socketServer;

    SocketServerWorker(SocketServer ss) {
        socketServer = ss;
    }




    public void run() {
        socketServer.start();
    }

}
