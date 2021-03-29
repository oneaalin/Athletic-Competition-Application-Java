package service;

import model.*;
import repository.IChallengeRepository;
import repository.IChildRepository;
import repository.IEmployeesRepository;
import repository.IEntriesRepository;
import validator.ValidationException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service {
    private IChildRepository childRepo;
    private IChallengeRepository challengeRepo;
    private IEmployeesRepository employeesRepo;
    private IEntriesRepository entriesRepo;

    public Service(IChildRepository childRepo, IChallengeRepository challengeRepo, IEmployeesRepository employeesRepo, IEntriesRepository entriesRepo) {
        this.childRepo = childRepo;
        this.challengeRepo = challengeRepo;
        this.employeesRepo = employeesRepo;
        this.entriesRepo = entriesRepo;
    }

    public String hashPassword(String password){
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

    public Employee loginEmployee(String username , String password){
        Employee employee = employeesRepo.findByUsername(username);
        if(employee != null){
            String passwordSalt = hashPassword(password);
            if(passwordSalt.equals(employee.getPassword())){
                return employee;
            }
        }
        return null;
    }

    public Employee registerEmployee(String username,String password){
        String passwordSalt = hashPassword(password);
        Employee employee = employeesRepo.save(new Employee(username,passwordSalt));
        if(employee == null){
            return null;
        }
        return employee;
    }

    public List<ChallengeDTO> getAllChallenges(){
        List<Challenge> challenges = StreamSupport.stream(challengeRepo.findAll().spliterator(),false).sorted(Comparator.comparingInt(Challenge::getMinimumAge)).collect(Collectors.toList());
        List<ChallengeDTO> challengesDTO = new ArrayList<>();
        for(Challenge challenge : challenges){
            challengesDTO.add(new ChallengeDTO(challenge.getMinimumAge(),challenge.getMaximumAge(),challenge.getName(),entriesRepo.findChildNumber(challenge.getId())));
        }
        return challengesDTO;
    }

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

        return added;

    }

    public Challenge getChallengeByProperties(int minimumAge, int maximumAge , String name){
        return challengeRepo.findByProperties(minimumAge,maximumAge,name);
    }

    public List<Child> getChildrenById(Long cid){
        List<Child> children = new ArrayList<>();
        entriesRepo.findAll().forEach((entry)->{
            if(entry.getId().getRight() == cid)
                children.add(childRepo.findOne(entry.getId().getLeft()));
        });
        return children;
    }
}
