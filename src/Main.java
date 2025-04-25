import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {System.out.println("Введите число:");
            int number = new Scanner(System.in) .nextInt();
            int number2 = new Scanner(System.in) .nextInt();

            int firstNumber = 5;
            int secondNumber = 10;
            int sum=firstNumber+secondNumber;
            double quotient = (double) firstNumber / secondNumber;
            System.out.println(sum);
            System.out.println(firstNumber-secondNumber);
            System.out.println(firstNumber*secondNumber);
            System.out.println(quotient);
        }
    }