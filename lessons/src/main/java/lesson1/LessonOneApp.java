package lesson1;

public class LessonOneApp {
    public static void main(String[] args) {

        Array<Integer> integerArray = new Array<Integer>(23, 15, 64, 70);

        integerArray.printArray();
        integerArray.swapping();
        integerArray.printArray();
        System.out.println("");

        integerArray.asList();

        Box <Orange> orangeBox = new Box<>();
        Box <Apple> appleBox = new Box<>();
        Box <Orange> orangeBox2 = new Box<>();


        orangeBox.addFruit(new Orange(), 20);
        appleBox.addFruit(new Apple(), 15);
        System.out.println("");

        System.out.println("Ящик с яблоками весит: " + appleBox.getWeight() + " кг");
        System.out.println("Ящик с апельсинами весит: " + orangeBox.getWeight() + " кг");

        System.out.println("Ящик с яблоками и апельсинами весят одинаково: " + appleBox.compare(orangeBox));

        orangeBox.newBox(orangeBox);

    }

}
