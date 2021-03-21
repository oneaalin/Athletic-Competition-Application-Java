import model.*;
import repository.*;


import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Properties;

public class App {

    public static void main(String[] args) {

        Properties props = new Properties();
        try {
            props.load(new FileReader("bd.properties"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config " + e);
        }

        IChildRepository childRepo = new ChildRepository(props);

        /*
        //childRepo.save(new Child("Alin",12));
        System.out.println("Toti copiii din db");
        //childRepo.delete(7l);
        //childRepo.delete(8l);
        //Child child=new Child("Adrian",11);
        //child.setId(1l);
        //childRepo.update(child);
        childRepo.findAll().forEach((Child element)->{
            System.out.println(element);
        });
        //System.out.println(childRepo.findOne(3l));

        //childRepo.delete(3l);

        System.out.println(childRepo.exists(3l));

        System.out.println(childRepo.count());*/

        IChallengeRepository challengeRepo = new ChallengeRepository(props);
        /*
        //challengeRepo.save(new Challenge(6,8,"6-8"));
        //challengeRepo.delete(3l);

        challengeRepo.findAll().forEach((Challenge element)->{
            System.out.println(element);
        });

        System.out.println(challengeRepo.findOne(2l));

        System.out.println(challengeRepo.findByProperties(9,11,"9-11"));

        System.out.println(challengeRepo.exists(3l));

        System.out.println(challengeRepo.count());*/

        IEmployeesRepository employeesRepo = new EmployeesRepository(props);
        /*
        //employeesRepo.save(new Employee("marius","bicicleta"));
        employeesRepo.delete(3l);

        employeesRepo.findAll().forEach((Employee element)->{
            System.out.println(element);
        });

        System.out.println(employeesRepo.findOne(2l));

        System.out.println(employeesRepo.findByUsername("alin"));

        System.out.println(employeesRepo.exists(3l));

        System.out.println(employeesRepo.count());
        */

        IEntriesRepository entriesRepo = new EntriesRepository(props, (ChildRepository) childRepo, (ChallengeRepository) challengeRepo);
        /*Child child = new Child("Alin",12);
        child.setId(4l);
        Challenge challenge = new Challenge(12,14,"12-14");
        challenge.setId(2l);
        Entry entry = new Entry(LocalDateTime.now(),child,challenge);
        entry.setId(new Tuple(child.getId(),challenge.getId()));
        entriesRepo.save(entry);
        //entriesRepo.delete(new Tuple(child.getId(),challenge.getId()));
        entriesRepo.findAll().forEach((Entry element)->{
            System.out.println(element);
        });

        System.out.println(entriesRepo.findOne(new Tuple(child.getId(),challenge.getId())));

        System.out.println(entriesRepo.findChildNumber(1l));

        System.out.println(entriesRepo.findChallengeNumber(1l));

        System.out.println(entriesRepo.exists(new Tuple(child.getId(),challenge.getId())));

        System.out.println(entriesRepo.count());*/

        entriesRepo.findAll().forEach((Entry element)->{
            System.out.println(element);});

    }
}
