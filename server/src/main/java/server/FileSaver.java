package server;

import server.DataSerializables.GeneralBoardData;
import server.DataSerializables.PersonalBoardsData;
import server.DataSerializables.UsersData;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

class FileSaver {
    private static FileSaver fileSaver = null;

    private final Object usersFileLock;
    private final Object generalBoardFileLock;
    private final Object personalBoardsFileLock;
    private String location;
    private int id;

    static FileSaver getInstance(String location, int id){
        if(fileSaver == null)
            fileSaver = new FileSaver(location, id);
        return fileSaver;
    }

    private FileSaver(String loc, int id) {
        usersFileLock = new Object();
        generalBoardFileLock = new Object();
        personalBoardsFileLock = new Object();
        location = loc;
        this.id = id;
    }



    void writeGeneralBoard(GeneralBoardData generalBoardData) {
        synchronized (generalBoardFileLock) {
            boolean tryAgain = true;

            while (tryAgain) {
                try {
                    //Saving of object in a file
                    FileOutputStream file = new FileOutputStream(location+"TempGeneralBoard"+id);
                    ObjectOutputStream out = new ObjectOutputStream(file);

                    // Method for serialization of object
                    out.writeObject(generalBoardData);
                    out.close();
                    file.close();

                    tryAgain = false;
                } catch (IOException ex) {
                    System.out.println("IOException is caught");
                    tryAgain = true;
                }
            }
            Path source = Paths.get(location+"TempGeneralBoard"+id);
            Path dest = Paths.get(location+"GeneralBoard"+id);

            try {
                Files.move(source, dest, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    GeneralBoardData readGeneralBoard() {
        synchronized (generalBoardFileLock) {
            GeneralBoardData generalBoardData = new GeneralBoardData();

            try {
                //Saving of object in a file
                FileInputStream file = new FileInputStream(location+"GeneralBoard"+id);
                ObjectInputStream in = new ObjectInputStream(file);

                // Method for serialization of object
                generalBoardData = (GeneralBoardData) in.readObject();
                in.close();
                file.close();

                System.out.println("Read general board done");
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("No previous general board file to read from");
                //ex.printStackTrace();
            }

            return generalBoardData;
        }
    }

    void writePersonalBoards(PersonalBoardsData personalBoardsData) {
        synchronized (personalBoardsFileLock) {
            boolean tryAgain = true;

            while (tryAgain) {
                try {
                    //Saving of object in a file
                    FileOutputStream file = new FileOutputStream(location+"TempPersonalBoards"+id);
                    ObjectOutputStream out = new ObjectOutputStream(file);

                    // Method for serialization of object
                    out.writeObject(personalBoardsData);
                    out.close();
                    file.close();

                    tryAgain = false;
                } catch (IOException ex) {
                    System.out.println("IOException is caught");
                    tryAgain = true;
                }
            }
            Path source = Paths.get(location+"TempPersonalBoards"+id);
            Path dest = Paths.get(location+"PersonalBoards"+id);

            try {
                Files.move(source, dest, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    PersonalBoardsData readPersonalBoards() {
        synchronized (personalBoardsFileLock) {
            PersonalBoardsData personalBoardsData = new PersonalBoardsData();

            try {
                //Saving of object in a file
                FileInputStream file = new FileInputStream(location+"PersonalBoards"+id);
                ObjectInputStream in = new ObjectInputStream(file);

                // Method for serialization of object
                personalBoardsData = (PersonalBoardsData) in.readObject();
                in.close();
                file.close();

                System.out.println("Read personal boards done");
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("No previous personal boards file to read from");
                //ex.printStackTrace();
            }

            return personalBoardsData;
        }
    }


    void writeUsers(UsersData users) {
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


    UsersData readUsers() {
        synchronized (usersFileLock) {
            UsersData users = new UsersData();

            try {
                //Saving of object in a file
                FileInputStream file = new FileInputStream(location+"UserList"+id);
                ObjectInputStream in = new ObjectInputStream(file);

                // Method for serialization of object
                users = (UsersData) in.readObject();
                in.close();
                file.close();
                System.out.println("Read users done");
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("No previous users file to read from");
                //ex.printStackTrace();
            }
            return users;
        }
    }
}
