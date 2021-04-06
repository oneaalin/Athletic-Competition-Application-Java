package chat.service;

import chat.model.*;

import java.util.List;

public interface IService {
    Employee loginEmployee(String username , String password,IObserver client);
    Employee registerEmployee(String username,String password);
    List<ChallengeDTO> getAllChallenges();
    List<ChildDTO> getAllChildren();
    Child registerChild(String name, int age, String challenge1, String challenge2);
    Challenge getChallengeByProperties(int minimumAge, int maximumAge , String name);
    List<Child> getChildrenById(Long cid);
    void logout(Employee employee,IObserver client);
}
