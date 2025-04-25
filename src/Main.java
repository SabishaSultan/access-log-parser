import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {System.out.println("Введите первое число:");
            int number = new Scanner(System.in) .nextInt();
    System.out.println("Введите второе число:");
            int number2 = new Scanner(System.in) .nextInt();

            int firstNumber = number;
            int secondNumber = number2;
            int sum=firstNumber+secondNumber;
            double quotient = (double) firstNumber / secondNumber;
            System.out.println(sum);
            System.out.println(firstNumber-secondNumber);
            System.out.println(firstNumber*secondNumber);
            System.out.println(quotient);
        }
    }