package lesson1;

import java.util.ArrayList;
import java.util.List;

public class Box <T extends Fruit>{

    private List<T> box = new ArrayList<T>();

    public Box(){
        this.box = box;
    }

    public float getWeight(){
        float weight = 0.0f;
        for (T o: box) {
            weight += o.getWeight();
        }
        return weight;
    }

    public void addFruit( T fruit, int quantity) {
        for (int i = 0; i < quantity; i++){
               box.add(fruit);
        }
    }
    public void newBox( Box newBox){
        newBox.box.addAll(box);
        box.clear();
    }

    public boolean compare(Box box){
        if(getWeight() == box.getWeight()) return true;
        return false;
    }

}
