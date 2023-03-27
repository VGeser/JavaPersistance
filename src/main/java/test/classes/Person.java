package test.classes;

import PersistenceFramework.CreatorField;
import PersistenceFramework.JsonClassCreator;
import PersistenceFramework.Serialize;
import PersistenceFramework.SerializeField;

@Serialize()
public class Person {
    @SerializeField()
    private String name;
    @SerializeField(Name = "personAge")
    private int age;

    @SerializeField(Name = "classField")
    private ComplexField complexField1;

    @SerializeField()
    public ComplexField complexField;

    @JsonClassCreator
    public Person (@CreatorField("name") String name , @CreatorField("personAge") int age,
                   @CreatorField("complexField") ComplexField complexField)
    {
        this.name = name;
        this.age = age;
        this.complexField = complexField;
    }

    public int getAge() {
        return age;
    }
    public String getName() {
        return name;
    }

    public void setComplexField(ComplexField mama) {this.complexField1 = mama;}
    public ComplexField getComplexField(){return this.complexField1;}

    @Override
    public String toString() {
        return "test.classes.Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", complexField=" + complexField +
                '}';
    }
}

