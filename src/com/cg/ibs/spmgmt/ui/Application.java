package com.cg.ibs.spmgmt.ui;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import com.cg.ibs.spmgmt.bean.BankAdmin;
import com.cg.ibs.spmgmt.bean.ServiceProvider;
import com.cg.ibs.spmgmt.exception.IBSException;
import com.cg.ibs.spmgmt.exception.RegisterException;
import com.cg.ibs.spmgmt.service.ServiceProviderService;
import com.cg.ibs.spmgmt.service.ServiceProviderServiceImpl;

public class Application {
	// Main-------------------------------------------------------
	public static void main(String[] args) {
		Application application = new Application();
		Scanner scanner = new Scanner(System.in);
		BankAdmin admin = new BankAdmin();
		int switchInput = 0;
		boolean exitTrigger = true;
		// Keep Showing the menu until exit button pressed
		do {
			ServiceProviderService service = new ServiceProviderServiceImpl();
			ServiceProvider serviceProvider = new ServiceProvider();
			switchInput = application.menu(scanner);
			switch (switchInput) {
			case 1:
				// Takes User Input for all details-->performs basic input
				// checks
				application.registerServiceProvider(scanner, service, serviceProvider);
				application.returnToMainMenu(scanner);
				break;
			case 2:
				// Takes login ID and password--> checks validity-->shows
				// status/SPI if approved
				application.loginMethod(scanner, service, serviceProvider);
				application.returnToMainMenu(scanner);
				break;
			case 3:
				// BankAdmin Login
				application.bankAdmin(scanner, admin, service);
				// Getting the list of pending Service Providers in Database
				TreeMap<LocalDateTime, ServiceProvider> serviceProviders = service.showPending();
				// Showing Details-->Setting Approval status
				application.bankMethod(scanner, serviceProviders, service);
				break;
			case 4:
				// Exit from Application
				exitTrigger = false;
				break;
			default:
				System.out.println("Invalid Input");
			}
		} while (exitTrigger);
	}

	// Methods
	// Display Main Menu
	private int menu(Scanner scanner) {
		System.out.println("Welcome to Service Provider Portal");
		System.out.println("Select an option below (1/2/3/4):");
		System.out.println("1. New Registration");
		System.out.println("2. Login");
		System.out.println("3. Bank Administrator Login");
		System.out.println("4. Exit");
		// Ensuring input matches the menu
		while (!scanner.hasNextInt()) {
			scanner.next();
			scanner.nextLine();
			System.out.println("Invalid Input! Please Enter a Number: 1 2 3 4");
		}
		int returnInput = scanner.nextInt();
		scanner.nextLine();
		return returnInput;
	}

	// Register a user
	private void registerServiceProvider(Scanner scanner, ServiceProviderService service,
			ServiceProvider serviceProvider) {
		serviceProvider.setNameOfCompany(takeNameInput(scanner));
		try {
			// Generates Unique ID & Password
			serviceProvider = service.generateIdPassword(serviceProvider);
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		System.out.println("Note Down the ID & Password for future logins:");
		System.out.println("User ID:" + serviceProvider.getUserId());
		System.out.println("Password: " + serviceProvider.getPassword());
		// Take Details with basic pattern matching
		serviceProvider = getKYC(scanner, serviceProvider);
		try {
			// store the object into database
			service.storeSPDetails(serviceProvider);
			System.out.println("\nREGISTRATION COMPLETE. APPROVAL REQUEST SENT TO BANK! "
					+ "\nLOGIN FROM MAIN MENU TO CHECK STATUS OF REQUEST \n");
		} catch (RegisterException exception) {
			System.out.println("------------------------------------------------------ \n" + exception.getMessage()
					+ "\n------------------------------------------------------");
		}
	}

	// Input character length check
	private String takeNameInput(Scanner scanner) {
		String string = "";
		String namePattern = "[A-Z,a-z]{5}[ ,A-Z,a-z,0-9]{0,}";
		// Keep Taking User Input until it matches the pattern
		do {
			System.out.println("Enter Name of the Company(Minimum 5 Characters, no Spaces)");
			string = scanner.nextLine();
		} while (!string.matches(namePattern));
		return string;
	}

	// Taking KYC details and doing basic input checks to match standard
	// patterns
	private ServiceProvider getKYC(Scanner scanner, ServiceProvider serviceProvider) {
		boolean bn = false;
		String st = "";
		String gstinPattern = "[1-9,A-Z][0-9,A-Z]{10,16}";
		String panPattern = "[1-9,A-Z][0-9,A-Z]{9}";
		String acNumberPattern = "[1-9][0-9]{10,14}";
		String phoneNumberPattern = "[1-9][0-9]{9}";
		System.out.println("\n------Enter the Necessary Details----- ");

		System.out.println("Enter GST Number(16 Characters digit or uppercase alphabets): ");
		do {
			st = scanner.next();
			scanner.nextLine();
			bn = !Pattern.matches(gstinPattern, st);
			if (bn) {
				System.out.println("Enter valid number(16 Characters-digits/uppercase letters)");
			}
		} while (bn);
		serviceProvider.setGstin(st);
		System.out.println("Enter PAN(10 Characters-digits/uppercase letters): ");
		do {
			st = scanner.next();
			scanner.nextLine();
			bn = !Pattern.matches(panPattern, st);
			if (bn) {
				System.out.println("Enter valid PAN(10 Characters-digits/uppercase letters)");
			}
		} while (bn);
		serviceProvider.setPanNumber(st);
		System.out.println("Enter Bank Account Number(10-14 digits): ");
		do {
			st = scanner.next();
			scanner.nextLine();
			bn = !Pattern.matches(acNumberPattern, st);
			if (bn) {
				System.out.println("Enter a valid Account number(10-14 digits)");
			}
		} while (bn);
		serviceProvider.setAccountNumber((new BigInteger(st)));
		System.out.println("Enter Name of the Bank: ");
		serviceProvider.setBankName(scanner.nextLine());
		System.out.println("Enter Company Address: ");
		serviceProvider.setCompanyAddress(scanner.nextLine());
		System.out.println("Enter Contact Number (10 digits): ");
		do {
			st = scanner.next();
			scanner.nextLine();
			bn = !Pattern.matches(phoneNumberPattern, st);
			if (bn) {
				System.out.println(
						"Enter 10 digit phone number that doesn't start with 0(No special characters like + or -)");
			}
		} while (bn);
		serviceProvider.setMobileNumber(new BigInteger(st));
		printDetails(serviceProvider);
		return serviceProvider;
	}

	// BankAdmin Login Inputs
	private void bankAdmin(Scanner scanner, BankAdmin admin, ServiceProviderService service) {
		boolean exitTrig = true;
		// Taking Inputs until login succeeds
		do {
			System.out.println("Enter Admin ID: ");
			admin.setAdminID(scanner.next());
			scanner.nextLine();
			System.out.println("Enter Admin Password: ");
			admin.setAdminPassword(scanner.next());
			scanner.nextLine();
			try {
				exitTrig = !service.validateAdminLogin(admin.getAdminID(), admin.getAdminPassword());
			} catch (IBSException exception) {
				System.out.println(exception.getMessage());
			}
		} while (exitTrig);
		System.out.println("---------------Login Sucessful-----------------");
	}

	// Prints Details of a Service Provider
	private void printDetails(ServiceProvider serviceProvider) {
		System.out.println("Name of Company: " + serviceProvider.getNameOfCompany());
		System.out.println("GST Number: " + serviceProvider.getGstin());
		System.out.println("PAN: " + serviceProvider.getPanNumber());
		System.out.println("Account Number: " + serviceProvider.getAccountNumber());
		System.out.println("Bank Name:" + serviceProvider.getBankName());
		System.out.println("Company Address: " + serviceProvider.getCompanyAddress());
		System.out.println("Mobile Number: " + serviceProvider.getMobileNumber());
	}

	// Shows all Pending Service Providers and allows to approve/disapprove
	private void bankMethod(Scanner scanner, TreeMap<LocalDateTime, ServiceProvider> serviceProviders,
			ServiceProviderService service) {
		int i = 1;
		int k = 0;
		boolean exitTrigger2 = true;
		// Iterate over database to get all pending accounts
		HashMap<Integer, LocalDateTime> numberKeyMaping = new HashMap<>();
		do {
			Set<LocalDateTime> keyz = serviceProviders.keySet();

			if (keyz.isEmpty()) {
				System.out.println("*******No pending accounts*********" + "\n RETURNING TO MAIN MENU! \n");
				break;
			}
			System.out.println("List of Pending Accounts: ");
			System.out.println("Select an Account from the list below");
			DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			for (LocalDateTime key : keyz) {
				ServiceProvider value = serviceProviders.get(key);
				numberKeyMaping.put(i, key);
				System.out.println(
						i + ". " + value.getNameOfCompany() + "\t Request Date and Time: " + key.format(format));
				i++;
			}
			i = 1;
			k = scanner.nextInt();
			scanner.nextLine();
			Set<Integer> indexs = numberKeyMaping.keySet();
			// Show selected Service Providers details
			for (Integer index : indexs) {
				if (index.equals(k)) {
					ServiceProvider value = serviceProviders.get(numberKeyMaping.get(index));
					printDetails(value);
					System.out.println("Enter true to Approve false to Disapprove");
					try {
						service.approveSP(value, scanner.nextBoolean());
						scanner.nextLine();
					} catch (IBSException exception) {
						System.out.println(exception.getMessage());
					}
					serviceProviders.remove(numberKeyMaping.get(index));
					break;
				}
			}
			System.out.println(
					"Press 1 to see remaining pending accounts \nPress any other number to go back to Main Menu");
			if (scanner.nextInt() == 1) {
				exitTrigger2 = false;
			}
			scanner.nextLine();
		} while (!exitTrigger2);
	}

	// Taking 1 as input
	private void returnToMainMenu(Scanner scanner) {
		System.out.println("Enter 1 to return to main menu");
		while (scanner.nextInt() != 1) {
		}
		scanner.nextLine();
	}

	// Displays details and approval status
	private void loginMethod(Scanner scanner, ServiceProviderService service, ServiceProvider serviceProvider) {
		boolean exitTrig = true;
		if (service.emptyData()) {
			System.out.println("No Users Registered");
			return;
		}
		do {
			System.out.println("Enter User ID: ");
			String inputId = scanner.next();
			scanner.nextLine();
			System.out.println("Enter Password: ");
			String inputPassword = scanner.next();
			scanner.nextLine();
			try {
				service.validateLogin(inputId, inputPassword);
			} catch (IBSException exception) {
				System.out.println(exception.getMessage());
			}
			try {
				serviceProvider = service.getServiceProvider(inputId);
			} catch (IBSException exception) {
				System.out.println(exception.getMessage());
			} finally {
				exitTrig = (serviceProvider == null);
			}
		} while (exitTrig);
		System.out.println("-------------Login Succesful------------------");
		printDetails(serviceProvider);
		System.out.println("Approval Status: " + serviceProvider.getStatus());
		if (serviceProvider.getStatus().equals("Approved")) {
			System.out.println("Unique SPI ID: " + serviceProvider.getSpi());
		}
	}
}
