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
    private String location;
    private int id;

    public static FileSaver getInstance(String location, int id){
        if(fileSaver == null)
            fileSaver = new FileSaver(location, id);
        return fileSaver;
    }

    private FileSaver(String loc, int id) {
        usersFileLock = new Object();
        announcementsFileLock = new Object();
        location = loc;
        id = id;
    }


    void writeAnnouncements(List<Announcement> announcements) {
        // Serialization

        synchronized (announcementsFileLock) {
            boolean tryAgain = true;

            while (tryAgain) {
                try {
                    //Saving of object in a file
                    FileOutputStream file = new FileOutputStream(location+"TempAnnouncementList"+id);
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
            Path source = Paths.get(location+"TempAnnouncementList"+id);
            Path dest = Paths.get(location+"AnnouncementList"+id);

            try {
                Files.move(source, dest, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    List<Announcement> readAnnouncements() {
        // Serialization

        synchronized (announcementsFileLock) {
            List<Announcement> anns = new ArrayList<>();
            boolean tryAgain = true;

            while (tryAgain) {
                try {
                    //Saving of object in a file
                    FileInputStream file = new FileInputStream(location+"AnnouncementList"+id);
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
            return anns;
        }
    }

    void writeUsers(List<User> users) {
        // Serialization

        synchronized (usersFileLock) {
            boolean tryAgain = true;

            while (tryAgain) {
                try {
                    //Saving of object in a file
                    FileOutputStream file = new FileOutputStream(location+"TempUserList"+id);
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

            Path source = Paths.get(location+"TempUserList"+id);
            Path dest = Paths.get(location+"UserList"+id);

            try {
                Files.move(source, dest, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    List<User> readUsers() {
        // Serialization


        synchronized (announcementsFileLock) {
            List<User> users = new ArrayList<>();
            boolean tryAgain = true;

            while (tryAgain) {
                try {
                    //Saving of object in a file
                    FileInputStream file = new FileInputStream(location+"UserList"+id);
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
            return users;
        }
    }
}
