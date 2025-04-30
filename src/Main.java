import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println("Введите первое число:");
        int firstNumber = new Scanner(System.in).nextInt();
        System.out.println("Введите второе число:");
        int secondNumber = new Scanner(System.in).nextInt();

        double quotient = (double) firstNumber / secondNumber;
        System.out.println("Сумма:" + (firstNumber + secondNumber));
        System.out.println("Разность:" + (firstNumber - secondNumber));
        System.out.println("Произведение:" + (firstNumber * secondNumber));
        System.out.println("Частное:" + quotient);
    }
}