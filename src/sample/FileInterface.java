package sample;

import com.healthmarketscience.rmiio.*;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Properties;

public interface FileInterface extends Remote{
    public void sendFile(RemoteInputStream ris) throws RemoteException, IOException;
    public void writeToFile(InputStream stream) throws IOException, RemoteException;
    public RemoteInputStream passAStream(String filename) throws  RemoteException;

    public boolean checkFileOnServer(String nameOfFile) throws RemoteException;
    public Properties getListOfSavedFiles() throws RemoteException;

}
