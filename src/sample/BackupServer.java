package sample;

import com.healthmarketscience.rmiio.*;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;

import javax.swing.plaf.metal.MetalIconFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;


public class BackupServer extends UnicastRemoteObject implements FileInterface, Serializable{

    private SavedFilesList savedFilesList;
    private File file = null;
    private Path path = null;

    public BackupServer(String ip,int port) throws RemoteException{
        super(Registry.REGISTRY_PORT);
        savedFilesList = new SavedFilesList();
        path = Paths.get("C:\\Users\\Bartłomiej\\Desktop\\Saved Files");

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                //fail to create directory
                e.printStackTrace();
            }
        }

        try{
            LocateRegistry.createRegistry(port);
            Naming.rebind("rmi://" + ip + ":"+ port +"/BackupServer", this);
            System.err.println("Server is created on port: " + port);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void sendFile(RemoteInputStream ris) throws IOException, RemoteException{
        InputStream input = null;
        try{
            input = RemoteInputStreamClient.wrap(ris);
            writeToFile(input);
        }

        catch (Exception e){
            e.printStackTrace();
        }

    }


    public void writeToFile(InputStream stream) throws IOException, RemoteException {
        FileOutputStream output = null;

        try {
            file = File.createTempFile("data", ".mp4", new File("D:\\"));
            output = new FileOutputStream(file);
            int chunk = 4096;
            byte [] result = new byte[chunk];

            int readBytes = 0;
            do {
                readBytes = stream.read(result);
                if (readBytes > 0)
                    output.write(result, 0, readBytes);
                System.out.println("Zapisuje...");
            } while(readBytes != -1);
            System.out.println(file.length());
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(output != null){
                output.close();
                System.out.println("Zamykam strumień...");
            }


        }
    }


    public RemoteInputStream passAStream(String filepath) throws RemoteException{
        SimpleRemoteInputStream input = null;
        try{
            input = new SimpleRemoteInputStream(new FileInputStream(filepath));
        }

        catch (Exception e){
            e.printStackTrace();
        }
        return input.export();
    }

    @Override
    public boolean checkFileOnServer(String nameOfFile) throws RemoteException {
        if(savedFilesList.fileOnList(nameOfFile))
            return true;
        else
            return false;
    }

    @Override
    public Properties getListOfSavedFiles() throws RemoteException {
        return savedFilesList.getFilesList();
    }

    private void saveFileInFolder(){
        new File(file,path.toString());
    }

}

