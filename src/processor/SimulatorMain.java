package processor;

import jdk.jshell.execution.Util;

import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap; // TODO: Remove, this is for testing only

public class SimulatorMain {

    // Memory and Register objects to interact with the pseudo RISC-V processor.
    private static Memory memory = new Memory();
    private static Registers registers = new Registers();
    private static Loader loader = new Loader(memory);
    private static Pipeline pipeline;
    // Define a constant for all zeroes
    private static final String allZeroes = Utility.ALLZEROS;
    private static long startTime = 0;
    private static long elapsedTime = 0;

    private static void startTimer() {
        startTime = System.nanoTime();
    }

    private static void stopTimer() {
        if (startTime == 0) {
            System.out.println("Timer not started!");
            return;
        }
        elapsedTime += System.nanoTime() - startTime;
        startTime = 0;
    }

    private static void printTime() {
        // Print in min, sec, ms, us, ns
        int min = (int) (elapsedTime / 60000000000L);
        int sec = (int) (elapsedTime / 1000000000L) % 60;
        int ms = (int) (elapsedTime / 1000000L) % 1000;
        int us = (int) (elapsedTime / 1000L) % 1000;
        int ns = (int) (elapsedTime % 1000);
        System.out.println("Execution time: " + min + "m " + sec + "s " + ms + "ms " + us + "us " + ns + "ns");
    }


    public static void main(String[] args) throws IOException {
    	Scanner scanner = new Scanner(System.in);

        String inputFile = "";
        String dataFile = "";

        // Get input file from stdin
        if (args.length == 0) {
            System.out.println("Please enter the input file name:");
            inputFile = scanner.nextLine();
            inputFile = "src/processor/input_files/" + inputFile;
        } else {
            inputFile = args[0];
        }

        pipeline = new Pipeline(memory, registers, inputFile);

        if (args.length == 2) {
            if (!args[1].equals("n"))
                dataFile = args[1];
            else
                dataFile = "";
        } else {
            // Ask if the user wants to provide a data file
            System.out.println("Do you want to provide a data file? (Y/N)");
            String provideDataFile = scanner.nextLine().trim().toLowerCase();

            if (provideDataFile.equals("y")) {
                System.out.println("Please enter the data file name:");
                dataFile = scanner.nextLine();
                dataFile = "src/processor/input_files/" + dataFile;
            } else {
                dataFile = ""; // Make it an empty string
            }
        }

        // Load the .dat file into memory using Loader
        try {
            loader.load(inputFile, dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        boolean isRunning = true;
        printMenu();
        while(isRunning) {
                        System.out.println("Please enter a command, m for menu, or q to quit: ");
            String input = scanner.nextLine();

            switch(input) {
                case "m":
                    printMenu();
                    break;
                case "t": // Run special testing code
                    // Set up initial register values for testing
                    String binaryValueX1 = "00000000000000000000000000001101";  // Value 13 in binary
                    String binaryValueX2 = "00000000000000000000000000001010";  // Value 10 in binary

                    Memory testMemory = new Memory();
                    Registers testRegisters = new Registers();
                    Instructions testInstructions = new Instructions(testMemory, testRegisters);

                    testRegisters.setRegisterValue("x2", binaryValueX1);
                    testRegisters.setRegisterValue("x1", binaryValueX2);
                    System.out.println("Initial Register Values:");
                    System.out.println(testRegisters);

                    // Test SLTU instruction
                    HashMap<String, String> testInstructionMap = new HashMap<>();
                    testInstructionMap.put("rs1", "x1");
                    testInstructionMap.put("rs2", "x2");

                    // Execute SLTU instruction
                    testInstructionMap.put("rd", "x3");
                    testInstructions.SLTU(testInstructionMap);
                    String resultSLTU = testRegisters.getRegisterValue("x3");
                    System.out.println("\nResult of SLTU: " + resultSLTU);

                    testInstructionMap.put("rd", "x4");
                    testInstructions.SLT(testInstructionMap);
                    String resultSLT = testRegisters.getRegisterValue("x4");
                    System.out.println("\nResult of SLT: " + resultSLT);

                    System.out.println("Final Register Values:");
                    System.out.println(testRegisters);

                    break;
                case "reg": // Print all register values
                    System.out.println(registers.toString());
                    break;
                case "mem": // Dump memory
                    String address = allZeroes;
                    String value = memory.getInstruction(address);
                    int values = 0;
                    while(!value.equals(allZeroes) && values < 100) {
                        // Address is binary string, convert to hex
                        System.out.println("0x" + Integer.toHexString(Integer.parseInt(address, 2)) + ": " + value);
                        pipeline.machineToAsm(value);
                        address = Utility.StringCrement(address, 4);
                        value = memory.getInstruction(address);
                        values++;
                    }
                    System.out.println("\nData memory:");
                    dumpDMem();
                    break;
                case "r":          
                    startTimer();
                    isRunning = pipeline.runUntilEnd();
                    stopTimer();
                    break;
                case "s":
                    startTimer();
                    isRunning = pipeline.runNextInstruction();
                    stopTimer();
                    break;
                case "pc":
                    System.out.println(registers.getRegisterValue(input));
                    break;
                case "insn":
                    // Assuming a method in Pipeline class fetches the next instruction.
                    System.out.println(pipeline.printNextAsmInstruction());
                    break;
                case "c":
                    startTimer();
                    isRunning = pipeline.continueExecution();
                    stopTimer();
                    break;
                case "q":
                    isRunning = false;
                    break;
                default:
                    if(input.startsWith("x") || input.startsWith("t")) {
                        System.out.print("Register " + input + " contains: ");
                        System.out.println(registers.getRegisterValue(registers.tToX(input)));
                    } else if(input.startsWith("b")) {
                        // Insert a space after the b
                        input = input.charAt(0) + " " + input.substring(1);
                        int pcValue = Integer.parseInt(input.split(" ")[1]);
                        pipeline.addBreakpoint(pcValue);
                    } 
                    else if(input.matches("^0x[0-9a-fA-F]{0,8}$")) {
                    	// Remove the "0x" prefix if it exists
                    	if (input.startsWith("0x")) {
                    	    input = input.substring(2);
                    	}

                    	// Convert the hexadecimal string to a binary string
                    	String binaryInstruction = Long.toBinaryString(Long.parseLong(input, 16));

                    	// Pad the binary string to ensure it's 32 bits long
                    	while (binaryInstruction.length() < 32) {
                    	    binaryInstruction = "0" + binaryInstruction;
                    	}
                    	System.out.println("Binary: " + binaryInstruction);

                        String content = memory.getMemoryValue(binaryInstruction);
                        System.out.println(content);
                    } else {
                        System.out.println("Invalid command!");
                    }
                    break;
            }
        }
        System.out.println("Execution complete!");
        printTime();
        System.out.print("Final register states (0's are omitted): \n" + registers.toString());
        if (memory.dataLines > 0) {
            System.out.println("Final data memory states: ");
            dumpDMem();
        }
        scanner.close();
    }

    private static void dumpDMem() {
        if (memory.dataLines > 0) {
            String address = Utility.DATA_MEMORY_ADDRESS;
            String value = memory.getMemoryValue(address);

            for (int i = 0; i < memory.dataLines; i++) {
                // Address is binary string, convert to hex
                System.out.println("0x" + Integer.toHexString(Integer.parseInt(address, 2)) + ": " + value);
                address = Utility.StringCrement(address, 1);
                value = memory.getMemoryValue(address);
            }
        }
        else {
            System.out.println("No data memory lines to display.");
        }
    }

    public static void printMenu() {
        System.out.println("Choose an option:");
        System.out.println("      m: Print this menu");
        System.out.println("      r: Run the program");
        System.out.println("      s: Run next instruction");
        System.out.println("      x[0-31]: View register content");
        System.out.println("      0x######## (8 hex digits): View memory content at address");
        System.out.println("      pc: View PC value");
        System.out.println("      insn: View next assembly instruction");
        System.out.println("      b [pc]: Set a breakpoint at [pc] where pc is a hex value");
        System.out.println("      c: Continue execution till next breakpoint or exit");
        System.out.println("      reg: View all register values");
        System.out.println("      mem: Dump instruction memory");
        System.out.println("      q: Quit simulator");
    }
}
