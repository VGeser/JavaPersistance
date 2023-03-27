package test.classes;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static PersistenceFramework.PersistanceJson.deserializeObject;
import static PersistenceFramework.PersistanceJson.serialize;




public class Main {
    public static void main(String[] args) {

        Person demoPerson = new Person("The Real Slim Shady", 228, new ComplexField());
        ArrayList<Integer> demoList = new ArrayList<>();
        demoList.add(2332);
        demoList.add(1717);
        demoList.add(-99909);
        demoPerson.setComplexField(new ComplexField(42,"Ocean blvd", demoList));
        String res = serialize(demoPerson);
        System.out.println(res);
        try
        {
            Person p = (Person) deserializeObject(res, null);
            if (p != null)
            {
                System.out.println(p.getAge());
                System.out.println(p.getName());
                System.out.println(p.getComplexField());
            }
        }
        catch (ClassNotFoundException e )
        {
            System.out.println("Class Not Found");
        }
        catch (InvocationTargetException | InstantiationException | IllegalAccessException e )
        {
            e.printStackTrace();
        }
    }
}
