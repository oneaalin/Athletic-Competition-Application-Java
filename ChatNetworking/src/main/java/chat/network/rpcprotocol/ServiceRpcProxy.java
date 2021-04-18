package chat.network.rpcprotocol;

import chat.model.*;
import chat.network.dtos.RegisterDTO;
import chat.network.dtos.UpdateDTO;
import chat.service.IObserver;
import chat.service.IService;
import chat.service.ValidationException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServiceRpcProxy implements IService {
    private String host;
    private int port;

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ServiceRpcProxy(String host,int port){
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<Response>();
    }

    @Override
    public Employee loginEmployee(String username, String password, IObserver client) {
        initializeConnection();
        Employee employee = new Employee(username,password);
        Request request = new Request.Builder().type(RequestType.LOGIN).data(employee).build();
        sendRequest(request);
        Response response = readResponse();
        if(response.type() == ResponseType.LOGGED){
            this.client = client;
           // return (Employee) response.data();
        }
        if(response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            closeConnection();
            throw new ValidationException(error);
        }
        return (Employee) response.data();
    }

    @Override
    public void logout(Employee employee, IObserver client) {
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(employee).build();
        sendRequest(request);
        Response response = readResponse();
        closeConnection();
        if(response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            throw new ValidationException(error);
        }
    }

    @Override
    public Employee registerEmployee(String username, String password) {
        initializeConnection();
        Employee employee = new Employee(username, password);
        Request request = new Request.Builder().type(RequestType.REGISTER).data(employee).build();
        sendRequest(request);
        Response response = readResponse();
        if(response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            closeConnection();
            throw new ValidationException(error);
        }
        closeConnection();
        return (Employee) response.data();
    }

    @Override
    public List<ChallengeDTO> getAllChallenges() {
        Request request = new Request.Builder().type(RequestType.GET_CHALLENGES).data(null).build();
        sendRequest(request);
        Response response = readResponse();
        if(response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            throw new ValidationException(error);
        }
        return (List<ChallengeDTO>) response.data();
    }

    @Override
    public List<ChildDTO> getAllChildren() {
        Request request = new Request.Builder().type(RequestType.GET_CHILDREN).data(null).build();
        sendRequest(request);
        Response response = readResponse();
        if(response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            throw new ValidationException(error);
        }
        return (List<ChildDTO>) response.data();
    }

    @Override
    public Child registerChild(String name, int age, String challenge1, String challenge2) {
        RegisterDTO rdto = new RegisterDTO(name,age,challenge1,challenge2);
        Request request = new Request.Builder().type(RequestType.REGISTER_CHILD).data(rdto).build();
        sendRequest(request);
        Response response = readResponse();
        if(response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            throw new ValidationException(error);
        }
        return (Child) response.data();
    }

    @Override
    public Challenge getChallengeByProperties(int minimumAge, int maximumAge, String name) {
        Challenge challenge = new Challenge(minimumAge,maximumAge,name);
        Request request = new Request.Builder().type(RequestType.GET_CHALLENGE_BY_PROPERTIES).data(challenge).build();
        sendRequest(request);
        Response response = readResponse();
        if(response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            throw new ValidationException(error);
        }
        return (Challenge) response.data();
    }

    @Override
    public List<Child> getChildrenById(Long cid) {
        Request request = new Request.Builder().type(RequestType.GET_CHILDREN_BY_ID).data(cid).build();
        sendRequest(request);
        Response response = readResponse();
        if(response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            throw new ValidationException(error);
        }
        return (List<Child>) response.data();
    }

    private void closeConnection(){
        finished = true;
        try{
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void sendRequest(Request request){
        try{
            output.writeObject(request);
            output.flush();
        }catch (IOException e){
            throw new ValidationException("Error sending object " + e);
        }
    }

    private Response readResponse(){
        Response response = null;
        try{
            response = qresponses.take();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection(){
        try{
            connection = new Socket(host,port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void startReader(){
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private void handleUpdate(Response response){
        if(response.type() == ResponseType.NEW_CHILD){
            UpdateDTO update = (UpdateDTO) response.data();
            ChildDTO child = update.getChildDTO();
            List<ChallengeDTO> challenges = update.getChallenges();
            try{
                client.updateChildren(child,challenges);
            }catch (ValidationException | RemoteException e){
                e.printStackTrace();
            }
        }
    }

    private boolean isUpdate(Response response){
        return response.type() == ResponseType.NEW_CHILD;
    }

    private class ReaderThread implements Runnable{
        @Override
        public void run() {
            while(!finished){
                try{
                    Object response = input.readObject();
                    if(isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }else{
                        try{
                            qresponses.put((Response) response);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }catch (IOException e){
                    System.out.println("Reading error " + e);
                }catch ( ClassNotFoundException e){
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
