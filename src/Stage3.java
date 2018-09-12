/* Description
Modify the program to read an unlimited sequence of numbers from 
the standard input and calculates their sum. Also, add the support
 of the /help command to print some information about the program.
 
The example of work
The example below shows inputs and the corresponding outputs.
Your program should work in the same way.

4 5 -2 3
10
4 7
11
6
6
/help
The program calculates the sum of numbers
/exit
Bye!
*/

import java.util.Scanner;

class Stage3 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		while(true) {
			String line = sc.nextLine();
			if(line.isEmpty())
				continue;
			String[] s = line.split("\\s+");
			int len = s.length;
			if(len == 0 || s[0].isEmpty())
				continue;
			if(s[0].equals("/exit"))
				break;
			if(s[0].equals("/help")) {
				System.out.println("The program calculates the sum of numbers");
				continue;
			}
			int sum = 0;
			boolean error = false;
			for(int i=0; i<len; ++i) {
				try {
					sum += Integer.parseInt(s[i]);
				}
				catch(NumberFormatException ex) {
					error = true;
					break;
				}
			}
			if(!error)
				System.out.println(sum);
		}
		System.out.println("Bye!");
	}
}