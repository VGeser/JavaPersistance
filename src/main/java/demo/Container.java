package demo;

import ru.nsu.fit.persistance.Serialize;
import ru.nsu.fit.persistance.SerializeField;

import java.util.ArrayList;

@Serialize(allFields = true)
public class Container {
    @SerializeField
    public ArrayList<ArrayList<String>> coll = new ArrayList<>();

    public Container(){};

    public Container(ArrayList<ArrayList<String>> collection){
        this.coll = collection;
    }

}
