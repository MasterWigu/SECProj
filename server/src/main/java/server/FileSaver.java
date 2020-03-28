package server;

import commonClasses.Announcement;
import commonClasses.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

class FileSaver {
    private static FileSaver fileSaver = null;

    private final Object usersFileLock;
    private final Object announcementsFileLock;

    public static FileSaver getInstance(){
        if(fileSaver == null)
            fileSaver = new FileSaver();
        return fileSaver;
    }

    private FileSaver() {
        usersFileLock = new Object();
        announcementsFileLock = new Object();
    }


    void writeAnnouncements(List<Announcement> announcements) {
        // Serialization

        synchronized (announcementsFileLock) {
            boolean tryAgain = true;

            while (tryAgain) {
                try {
                    //Saving of object in a file
                    FileOutputStream file = new FileOutputStream("TempAnnouncementList");
                    ObjectOutputStream out = new ObjectOutputStream(file);

                    // Method for serialization of object
                    out.writeObject(announcements);
                    out.close();
                    file.close();

                    tryAgain = false;
                } catch (IOException ex) {
                    System.out.println("IOException is caught");
                    tryAgain = true;
                }
            }
            Path source = Paths.get("TempAnnouncementList");
            Path dest = Paths.get("AnnouncementList");

            try {
                Files.move(source, dest, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    List<Announcement> readAnnouncements() {
        // Serialization
        List<Announcement> anns = new ArrayList<>();
        synchronized (announcementsFileLock) {
            boolean tryAgain = true;

            while (tryAgain) {
                try {
                    //Saving of object in a file
                    FileInputStream file = new FileInputStream("AnnouncementList");
                    ObjectInputStream in = new ObjectInputStream(file);

                    // Method for serialization of object
                    anns = (List<Announcement>) in.readObject();
                    in.close();
                    file.close();

                    System.out.println("Read announcements done");
                    tryAgain = false;
                } catch (IOException | ClassNotFoundException ex) {
                    System.out.println("IOException is caught");
                    ex.printStackTrace();
                    tryAgain = false;
                }
            }
        }
        return anns;
    }

    void writeUsers(List<User> users) {
        // Serialization

        synchronized (usersFileLock) {
            boolean tryAgain = true;

            while (tryAgain) {
                try {
                    //Saving of object in a file
                    FileOutputStream file = new FileOutputStream("TempUserList");
                    ObjectOutputStream out = new ObjectOutputStream(file);

                    // Method for serialization of object
                    out.writeObject(users);
                    out.close();
                    file.close();

                    tryAgain = false;
                } catch (IOException ex) {
                    System.out.println("IOException is caught");
                    tryAgain = true;
                }
            }

            Path source = Paths.get("TempUserList");
            Path dest = Paths.get("UserList");

            try {
                Files.move(source, dest, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    List<User> readUsers() {
        // Serialization
        List<User> users = new ArrayList<>();

        synchronized (announcementsFileLock) {
            boolean tryAgain = true;

            while (tryAgain) {
                try {
                    //Saving of object in a file
                    FileInputStream file = new FileInputStream("UserList");
                    ObjectInputStream in = new ObjectInputStream(file);

                    // Method for serialization of object
                    users = (List<User>) in.readObject();
                    in.close();
                    file.close();
                    System.out.println("Read users done");
                    tryAgain = false;
                } catch (IOException | ClassNotFoundException ex) {
                    System.out.println("IOException is caught");
                    ex.printStackTrace();
                    tryAgain = false;
                }
            }
        }
        return users;
    }
}
