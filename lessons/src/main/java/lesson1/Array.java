package lesson1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Array<T extends Number>{

    private final T[] numbers;

    public Array(T... numbers) {
        this.numbers = numbers;
    }

    public T[] swapping(){
            Integer n = (Integer) numbers[1];
            numbers[1] = numbers[2];
            numbers[2] = (T) n;
        return numbers;
    }

    public void printArray(){
        for (int i = 0; i < numbers.length; i++){
            System.out.print(numbers[i] + " ");
        }
        System.out.println("");
    }

    public void asList(){
        List<T> list = new ArrayList<T>(Arrays.asList(numbers));
        System.out.println(list);
    }
}