package chat.service;

import chat.model.ChallengeDTO;
import chat.model.ChildDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IObserver extends Remote {
    void updateChildren(ChildDTO childDTO,List<ChallengeDTO> challenges) throws RemoteException;
}
