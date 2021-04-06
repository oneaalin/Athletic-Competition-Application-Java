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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.List;

public class ClientRpcReflectionWorker implements Runnable , IObserver {
    private IService server ;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    private volatile boolean connected;

    public ClientRpcReflectionWorker(IService server , Socket connection){
        this.server = server;
        this.connection = connection;
        try{
            output =  new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void run(){
        while(connected){
            try{
                Object request = input.readObject();
                Response response = handleRequest((Request)request);
                if(response != null){
                    sendResponse(response);
                }
            } catch (IOException e){
                e.printStackTrace();
            } catch (ClassNotFoundException e){
                e.printStackTrace();
            }
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        try{
            input.close();
            output.close();
            connection.close();
        } catch (IOException e){
            System.out.println("Error " + e);
        }
    }

    public void updateChildren(ChildDTO child,List<ChallengeDTO> challenges){
        UpdateDTO update = new UpdateDTO(child,challenges);
        Response response = new Response.Builder().type(ResponseType.NEW_CHILD).data(update).build();
        try{
            sendResponse(response);
        } catch (IOException e){
            throw new ValidationException("Updating error " + e);
        }
    }

    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();

    private Response handleRequest(Request request){
        Response response = null;
        String handlerName = "handle"+(request).type();
        try{
            Method method = this.getClass().getDeclaredMethod(handlerName,Request.class);
            response=(Response)method.invoke(this,request);
        } catch (NoSuchMethodException e){
            e.printStackTrace();
        } catch (InvocationTargetException e){
            e.printStackTrace();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }

        return response;
    }

    private Response handleLOGIN(Request request){
        Employee employee = (Employee)request.data();
        try{
            Employee employee1 = server.loginEmployee(employee.getUsername(), employee.getPassword(), this);
            return new Response.Builder().type(ResponseType.LOGGED).data(employee1).build();
        } catch (ValidationException e){
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGOUT(Request request){
        Employee employee = (Employee)request.data();
        try{
            server.logout(employee,this);
            connected = false;
            return okResponse;
        } catch (ValidationException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleREGISTER(Request request){
        Employee employee = (Employee) request.data();
        try{
            Employee employee1 = server.registerEmployee(employee.getUsername(), employee.getPassword());
            return new Response.Builder().type(ResponseType.REGISTERED).data(employee1).build();
        } catch (ValidationException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
    private Response handleGET_CHALLENGES(Request request){
        try{
            List<ChallengeDTO> challenges = server.getAllChallenges();
            return new Response.Builder().type(ResponseType.GET_CHALLENGES).data(challenges).build();
        } catch (ValidationException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_CHILDREN(Request request){
        try{
            List<ChildDTO> children = server.getAllChildren();
            return new Response.Builder().type(ResponseType.GET_CHILDREN).data(children).build();
        } catch (ValidationException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleREGISTER_CHILD(Request request){
        RegisterDTO rdto = (RegisterDTO)request.data();
        try{
            Child child = server.registerChild(rdto.getName(), rdto.getAge(), rdto.getChallenge1(), rdto.getChallenge2());
            return new Response.Builder().type(ResponseType.REGISTERED_CHILD).data(child).build();
        } catch (ValidationException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_CHALLENGE_BY_PROPERTIES(Request request){
        Challenge challenge = (Challenge)request.data();
        try{
            Challenge challenge1 = server.getChallengeByProperties(challenge.getMinimumAge(), challenge.getMaximumAge(), challenge.getName());
            return new Response.Builder().type(ResponseType.GET_CHALLENGE).data(challenge1).build();
        } catch (ValidationException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_CHILDREN_BY_ID(Request request){
        Long id = (Long)request.data();
        try{
            List<Child> children = server.getChildrenById(id);
            return new Response.Builder().type(ResponseType.GET_CHILDREN_BY_ID).data(children).build();
        }catch (ValidationException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private void sendResponse(Response response) throws IOException {
        output.writeObject(response);
        output.flush();
    }

}
