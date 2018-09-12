/* Description
You need to write a program that reads two numbers from the same line
and prints their sum in the standard output. Numbers can be positive,
negative or zero.

The example of work.
The example below shows inputs and the corresponding outputs.
Your program should work in the same way.
5 8
13
*/

import java.util.Scanner;

class Repl1 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int a = sc.nextInt();
		int b = sc.nextInt();
		System.out.println(a + b);
	}
}