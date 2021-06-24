package lesson4;

public class PrintABC {

    private static volatile char turn = 'A';
    private static Object locker = new Object();

    public static void main(String[] args) throws InterruptedException {
        PrintABC printAbc = new PrintABC();
        Thread threadA = new Thread(() -> {
            printAbc.printA();
        });

        Thread threadB = new Thread(() -> {
            printAbc.printB();
        });

        Thread threadC = new Thread(() -> {
            printAbc.printC();
        });

        threadA.start();
        threadB.start();
        threadC.start();
    }

    void printA(){
        synchronized (locker) {
            try {
                for (int j = 0; j < 5; j++) {
                    while (turn != 'A') {
                        locker.wait();
                    }
                    System.out.print("A");
                    turn = 'B';
                    locker.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void printB(){
        synchronized (locker) {
            try {
                for (int j = 0; j < 5; j++) {
                    while (turn != 'B') {
                        locker.wait();
                    }
                    System.out.print("B");
                    turn = 'C';
                    locker.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void printC(){
        synchronized (locker) {
            try {
                for (int j = 0; j < 5; j++) {
                    while (turn != 'C') {
                        locker.wait();
                    }
                    System.out.print("C");
                    turn = 'A';
                    locker.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
