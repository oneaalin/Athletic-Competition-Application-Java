package chat.server;

import chat.model.*;
import chat.persistence.IChallengeRepository;
import chat.persistence.IChildRepository;
import chat.persistence.IEmployeesRepository;
import chat.persistence.IEntriesRepository;
import chat.service.IObserver;
import chat.service.IService;
import chat.service.ValidationException;

import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service implements IService {

    private IChallengeRepository challengeRepo;
    private IChildRepository childRepo;
    private IEmployeesRepository employeesRepo;
    private IEntriesRepository entriesRepo;
    private Map<Long, IObserver> loggedClients;

    public Service(IChallengeRepository challengeRepo, IChildRepository childRepo, IEmployeesRepository employeesRepo, IEntriesRepository entriesRepo) {
        this.challengeRepo = challengeRepo;
        this.childRepo = childRepo;
        this.employeesRepo = employeesRepo;
        this.entriesRepo = entriesRepo;
        loggedClients = new ConcurrentHashMap<>();
    }

    private String hashPassword(String password){
        String passwordToHash=password;
        String generatedPassword=null;
        try{
            MessageDigest md=MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes=md.digest();
            StringBuilder sb=new StringBuilder();
            for (int i=0; i<bytes.length; i++){
                sb.append(Integer.toString((bytes[i]&0xff)+0x100,32).substring(1));
            }
            generatedPassword=sb.toString();
            return generatedPassword;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public synchronized Employee loginEmployee(String username , String password,IObserver client){
        Employee employee = employeesRepo.findByUsername(username);
        if(employee != null){
            String passwordSalt = hashPassword(password);
            if(passwordSalt.equals(employee.getPassword())){
                if(loggedClients.get(employee.getId()) != null)
                    throw new ValidationException("User already logged in ! ");
                loggedClients.put(employee.getId(),client);
                return employee;
            }
        }
        return null;
    }

    @Override
    public synchronized void logout(Employee employee, IObserver client){
        IObserver localClient=loggedClients.remove(employee.getId());
        if (localClient==null)
            throw new ValidationException("User "+employee.getId()+" is not logged in.");
    }

    @Override
    public synchronized Employee registerEmployee(String username,String password){
        String passwordSalt = hashPassword(password);
        Employee employee = employeesRepo.save(new Employee(username,passwordSalt));
        if(employee == null){
            return null;
        }
        return employee;
    }

    @Override
    public List<ChallengeDTO> getAllChallenges(){
        List<Challenge> challenges = StreamSupport.stream(challengeRepo.findAll().spliterator(),false).sorted(Comparator.comparingInt(Challenge::getMinimumAge)).collect(Collectors.toList());
        List<ChallengeDTO> challengesDTO = new ArrayList<>();
        for(Challenge challenge : challenges){
            challengesDTO.add(new ChallengeDTO(challenge.getMinimumAge(),challenge.getMaximumAge(),challenge.getName(),entriesRepo.findChildNumber(challenge.getId())));
        }
        return challengesDTO;
    }

    @Override
    public List<ChildDTO> getAllChildren(){
        Iterable<Child> children = childRepo.findAll();
        List<ChildDTO> childrenDTO = new ArrayList<>();
        for(Child child : children){
            childrenDTO.add(new ChildDTO(child.getName(),child.getAge(),entriesRepo.findChallengeNumber(child.getId())));
        }
        return childrenDTO;
    }

    private Tuple<Integer,Integer> createAgeInterval(Integer age){
        Iterable<Challenge> challenges = challengeRepo.findAll();
        for(Challenge challenge :challenges){
            if(challenge.getMinimumAge() <= age && challenge.getMaximumAge() >= age){
                return new Tuple<>(challenge.getMinimumAge(),challenge.getMaximumAge());
            }
        }
        return null;
    }

    @Override
    public Child registerChild(String name,int age,String challenge1,String challenge2){

        if(challenge1.equals(challenge2)){
            throw new ValidationException("Participantul nu poate fi inscris de 2 ori la aceeasi proba ! ");
        }

        Child child = new Child(name,age);

        if(childRepo.findByProperties(name,age) != null) {

            Child foundChild = childRepo.findByProperties(name, age);
            if (entriesRepo.findChallengeNumber(foundChild.getId()) == 1 && challenge2 != "") {
                throw new ValidationException("Participantul mai poate fi inscris la o singura proba ! ");
            }else if (entriesRepo.findChallengeNumber(foundChild.getId()) == 2){
                throw new ValidationException("Participantul nu mai poate fi inscris la nici o proba ! ");
            }

            Tuple<Integer,Integer> ageInterval = createAgeInterval(age);
            Challenge challengeFound1 = challengeRepo.findByProperties(ageInterval.getLeft(),ageInterval.getRight(),challenge1);

            if(challengeFound1 != null && entriesRepo.exists(new Tuple<>(foundChild.getId(), challengeFound1.getId()))){
                throw new ValidationException("Participantul nu mai poate fi inscris la aceeasi proba ! ");
            }

            Entry entry = new Entry(LocalDateTime.now(),foundChild,challengeFound1);
            if(challengeFound1 != null){
                entry.setId(new Tuple<>(foundChild.getId(),challengeFound1.getId()));
                if(entriesRepo.save(entry) != null)
                    return foundChild;
            }
            notifyRegisterChild(foundChild);
            return null;
        }

        Child added = childRepo.save(child);
        Child foundChild = childRepo.findByProperties(name,age);

        if(challenge1 != ""){
            Tuple<Integer,Integer> ageInterval = createAgeInterval(age);
            Challenge foundChallenge1 = challengeRepo.findByProperties(ageInterval.getLeft(),ageInterval.getRight(),challenge1);
            Entry entry = new Entry(LocalDateTime.now(),foundChild,foundChallenge1);
            if(foundChallenge1 != null){
                entry.setId(new Tuple<>(foundChild.getId(),foundChallenge1.getId()));
                if(entriesRepo.save(entry) != null)
                    return foundChild;
            }
        }

        if(challenge2 != ""){
            Tuple<Integer,Integer> ageInterval = createAgeInterval(age);
            Challenge foundChallenge2 = challengeRepo.findByProperties(ageInterval.getLeft(),ageInterval.getRight(),challenge2);
            Entry entry = new Entry(LocalDateTime.now(),foundChild,foundChallenge2);
            if(foundChallenge2 != null){
                entry.setId(new Tuple<>(foundChild.getId(),foundChallenge2.getId()));
                if(entriesRepo.save(entry) != null)
                    return foundChild;
            }
        }

        if(added == null)
            notifyRegisterChild(foundChild);
        return added;

    }

    private final int defaultThreadsNo=5;
    private void notifyRegisterChild(Child child){

        ChildDTO childDTO = new ChildDTO(child.getName(),child.getAge(),entriesRepo.findChallengeNumber(child.getId()));
        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for (Map.Entry<Long,IObserver> entry : loggedClients.entrySet()) {
            executor.execute(() -> {
                try {
                    entry.getValue().updateChildren(childDTO,getAllChallenges());
                } catch(ValidationException | RemoteException e){
                    e.printStackTrace();
                }
            });

        }
        executor.shutdown();

    }

    @Override
    public Challenge getChallengeByProperties(int minimumAge, int maximumAge , String name){
        return challengeRepo.findByProperties(minimumAge,maximumAge,name);
    }

    @Override
    public List<Child> getChildrenById(Long cid){
        List<Child> children = new ArrayList<>();
        entriesRepo.findAll().forEach((entry)->{
            if(entry.getId().getRight().equals(cid))
                children.add(childRepo.findOne(entry.getId().getLeft()));
        });
        return children;
    }

}
