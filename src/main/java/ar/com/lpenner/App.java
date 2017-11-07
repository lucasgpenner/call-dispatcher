package ar.com.lpenner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import ar.com.lpenner.model.Call;
import ar.com.lpenner.service.Dispatcher;

/**
 * Main class to execute the program
 * 
 * @author lpenner
 *
 */
public class App {

	public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException {

		Scanner scanner = new Scanner(System.in);

	    System.out.print("Bienvenido a la aplicacion call-dispatcher. Por favor, siga las instrucciones a continuación.\n");
	    
	    System.out.print("Ingrese la cantidad de llamadas que desea realizar: ");
	    
	    int calls = App.inputValidate(scanner);
	    
	    System.out.print("Ingrese la cantidad de llamadas concurrentes: ");
	    
	    int concurrentCalls = App.inputValidate(scanner);
	    
	    System.out.print("Ingrese la cantidad de operadores: ");
	    
	    int operators = App.inputValidate(scanner);
	    
	    System.out.print("Ingrese la cantidad de supervisores: ");
	    
	    int supervisors = App.inputValidate(scanner);
	    
	    System.out.print("Ingrese la cantidad de directores: ");
	    
	    int directors = App.inputValidate(scanner);
	    
	    Dispatcher dispatcher = new Dispatcher(concurrentCalls, operators, supervisors, directors);
		
	    for (int i = 1; i <= calls; i++) {
			
			Call call = new Call(i);
			
			dispatcher.dispatchCall(call);
			
		}
	    
	    dispatcher.terminateDispatcher();
	    
	}

	private static int inputValidate(Scanner scanner) {
		for (;;) {
			if (!scanner.hasNextInt()) {
				System.out.print("Debe ingresar un numero: ");
				scanner.next();
			} else {
				int number = scanner.nextInt();
				if (number > 0) {
					return number;
				} else {
					System.out.print("El numero ingresado debe ser mayor a cero: ");
				}
			}
			scanner.nextLine();
		}
	}
}
