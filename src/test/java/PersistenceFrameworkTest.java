import PersistenceFramework.*;

import org.junit.jupiter.api.Test;
import ru.nsu.fit.persistance.JSONPredicate;
import ru.nsu.fit.persistance.PersistanceJson;
import ru.nsu.fit.persistance.PersistenceException;
import demo.ComplexField;
import demo.Person;
import demo.User;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceFrameworkTest {
    @Test
    public void testComplexInnerFieldSerialization()  {
        System.out.println("Base functionality test:");
        Person person = new Person("Author", 12, new ComplexField(145, "Interesting String"));
        // serialization test
        String res = PersistanceJson.serialize(person);
        System.out.println("  Serialized JSON: " + res);
        assertTrue(res.contains("12"));
        // deserialization test
        PersistanceJson framework = new PersistanceJson();
        Person p = framework.deserialize(res);
        assertNotNull(p);
        System.out.println("  Deserialized Object: " + p + "\n");
        assertEquals(12, p.getAge());
        assertEquals("Author", p.getName());
        assertEquals(145, p.complexField.i);
        assertEquals("Interesting String", p.complexField.str);
        assertEquals(0, p.complexField.coll.size());
    }

    @Test
    public void testSimplePredicateAppliedToSingleObject() {
        System.out.println("Predicate test:");
        Person person = new Person("Author", 12, new ComplexField());
        String res = PersistanceJson.serialize(person);
        System.out.println("  Serialized JSON: " + res);
        // deserialization test
        PersistanceJson framework = new PersistanceJson();
        JSONPredicate<Integer> jsonPredicate = new JSONPredicate<Integer>("complexField/i", (Integer i) -> i != 0, Integer.class);
        // simple predicates can be used in deserialization
        Person p = framework.deserialize(res, jsonPredicate);
        assertNull(p);
        System.out.println("  Object wasn`t deserialized because of predicate" + "\n");
    }

    @Test
    public void testPredicates() {
        System.out.println("Predicates functionality test:");
        Person person = new Person("Author", 12, new ComplexField());
        String res = PersistanceJson.serialize(person);
        System.out.println("  Serialized JSON: " + res);
        JSONPredicate<Integer> jsonPredicate1 = new JSONPredicate<>("complexField/i", (Integer i) -> i == 0, Integer.class);
        JSONPredicate<String> jsonPredicate2 = new JSONPredicate<>("complexField/str", (String s) -> s.length()<5, String.class);
        var j = jsonPredicate1.and(jsonPredicate2.negate());

        // test predicates test() method
        JsonReader jsonReader = Json.createReader(new StringReader(res));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();
        JsonObject fields = object.getJsonObject("fields");
        System.out.println("  Complex predicate result: " + j.test(fields) + "\n");
        assertTrue(j.test(fields));

        PersistanceJson framework = new PersistanceJson();
        // complex predicates can be used in deserialization
        Person p = framework.deserialize(res, j);
        assertNotNull(p);
    }

    @Test
    public void testSerializeObjectsWithSimpleCollections() {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(1321); ids.add(1322); ids.add(1233);
        ComplexField cf = new ComplexField(19214, "group", ids);

        System.out.println("Serializing objects with simple collections: ");
        String result = PersistanceJson.serialize(cf);
        System.out.println("  Serialized JSON: " + result  + "\n");
        assertTrue(result.contains("[\"1321\",\"1322\",\"1233\"]"));
    }

    @Test
    public void testSerializeSimpleCollections() {
        ArrayList<String> names = new ArrayList<>();
        names.add("Maksim"); names.add("Vladimir"); names.add("Arseniy"); names.add("Denis");

        System.out.println("Serializing simple collections: ");
        String result = PersistanceJson.serialize(names);
        System.out.println("  Serialized JSON: " + result + "\n");
        assertTrue(result.contains("Maksim"));
        assertTrue(result.contains("Vladimir"));
        assertTrue(result.contains("Arseniy"));
        assertTrue(result.contains("Denis"));
    }

    @Test
    public void testSerializeCollectionsOfCollections() {
        ArrayList<ArrayList<Integer>> coll = new ArrayList<>();
        ArrayList<Integer> ints1 = new ArrayList<>();
        ints1.add(1321); ints1.add(1322); ints1.add(1233);
        ArrayList<Integer> ints2 = new ArrayList<>();
        ints2.add(1); ints2.add(2); ints2.add(3); ints2.add(4);
        ArrayList<Integer> ints3 = new ArrayList<>();
        ints3.add(0); ints3.add(0);
        coll.add(ints1); coll.add(ints2); coll.add(ints3);

        System.out.println("Serializing collections of simple collections: ");
        String result = PersistanceJson.serialize(coll);
        System.out.println("  Serialized JSON: " + result + "\n");
        assertTrue(result.contains("[\"1321\",\"1322\",\"1233\"]"));
        assertTrue(result.contains("[\"1\",\"2\",\"3\",\"4\"]"));
        assertTrue(result.contains("[\"0\",\"0\"]"));
    }


    @Test
    public void testBidirectionalRelationshipsSerializationWithException() {
        User user = new User(1, "John");
        User.Item item = new User.Item(2, "book", user);
        user.addItem(item);

        assertThrows(PersistenceException.class, () -> {
            PersistanceJson.serialize(user);
        });
    }

    @Test
    public void testEqualObjectSerialization() {
        User user = new User(1, "John");
        ArrayList<User> users = new ArrayList<>();
        users.add(user);
        users.add(user);
        System.out.println("Test equal object serialization:");
        String res = PersistanceJson.serialize(users);
        System.out.println("  Serialized JSON: " + res + "\n");
    }

    @Test
    public void testSimpleArrayDeserialization() {
        ArrayList<String> names = new ArrayList<>();
        names.add("Maksim"); names.add("Vladimir"); names.add("Arseniy"); names.add("Denis"); names.add(null);

        System.out.println("Test equal object serialization:");
        String res = PersistanceJson.serialize(names);
        System.out.println("  Serialized JSON: " + res + "\n");

        PersistanceJson pf = new PersistanceJson();
        ArrayList<String> des = pf.deserialize(res);
        System.out.println("  Deserialized collection: " + des);
        assertEquals("Maksim", des.get(0));
        assertEquals("Vladimir", des.get(1));
        assertEquals("Arseniy", des.get(2));
        assertEquals("Denis", des.get(3));
        assertNull(des.get(4));
    }

    @Test
    public void testNull() {
        Person p = null;
        String res = PersistanceJson.serialize(p);
        System.out.println("Null object serialized: " + res + "\n");
    }

    @Test
    public void testNullField() {
        System.out.println("Object with null fields test:");
        Person p = new Person(null, 12, null);
        String res = PersistanceJson.serialize(p);
        System.out.println("  Serialized JSON: " + res);

        PersistanceJson pf = new PersistanceJson();
        Person deserialized = pf.deserialize(res);
        System.out.println("  Deserialized object: " + deserialized + "\n");
        assertNotNull(deserialized);
        assertEquals(12, deserialized.getAge());
        assertNull(deserialized.complexField);
        assertNull(deserialized.getName());
    }

    @Test
    public void testFilterWithPredicateToCollection() {
        // serializing collection
        System.out.println("Filtering JSON with collection of objects:");
        ArrayList<Person> people = new ArrayList<>();
        people.add(new Person("Maksim", 20, new ComplexField(145, "Long string!!!!!!")));
        people.add(new Person("Vladimir", 20, new ComplexField(100, "Some info")));
        people.add(new Person("Arseniy", 19, new ComplexField(1, "")));
        people.add(new Person("Denis", 20, new ComplexField(205, "b")));
        people.add(new Person("Oldman", 65, new ComplexField(0, "UNKNOWN")));
        String serializedString = PersistanceJson.serialize(people);
        System.out.println("  Serialized collection: " + serializedString);

        // constructing predicates
        JSONPredicate<Integer> pred1 = new JSONPredicate<>("personAge", (Integer age) -> age > 18, Integer.class);
        JSONPredicate<Integer> pred2 = new JSONPredicate<>("personAge", (Integer age) -> age > 40, Integer.class);
        JSONPredicate<String> pred3 = new JSONPredicate<>("complexField/str", (String str) -> str.length()>5, String.class);
        Predicate<JsonObject> complexPredicate = pred1.and(pred2.negate()).and(pred3);

        PersistanceJson persistanceJson = new PersistanceJson();
        ArrayList<Person> deserialized = persistanceJson.deserialize(serializedString, complexPredicate);

        System.out.println("  Deserialized collection: " + deserialized + "\n");
        assertNotNull(deserialized);
        assertEquals(2, deserialized.size());
        assertEquals("Maksim", deserialized.get(0).getName());
        assertEquals("Vladimir", deserialized.get(1).getName());
    }
}