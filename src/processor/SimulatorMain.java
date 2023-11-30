package processor;

import java.io.IOException;
import java.util.Scanner;

public class SimulatorMain {

    // Memory and Register objects to interact with the pseudo RISC-V processor.
    private static Memory memory = new Memory();
    private static Registers registers = new Registers();
    private static Loader loader = new Loader(memory);
    private static Pipeline pipeline = new Pipeline(memory, registers);
    // Define a constant for all zeroes
    private static final String allZeroes = Utility.ALLZEROS;


    public static void main(String[] args) {
        String inputFile;
        Scanner scanner = new Scanner(System.in);
        // Get input file from stdin
        if(args.length == 0) {
            System.out.println("Please enter the input file name:");
            inputFile = scanner.nextLine();
            inputFile = "src/processor/input_files/" + inputFile;
        } else {
            inputFile = args[0];
        }
        // Load the .dat file into memory. (Loader logic should be completed separately.)
        // Load instructions using Loader
        try {
          loader.loadInstructions(inputFile);
        } catch (IOException e) {
          e.printStackTrace();
        }

        boolean isRunning = true;

        while(isRunning) {
            System.out.println("Choose an option:");
            System.out.println("      r: Run the program");
            System.out.println("      s: Run next instruction");
            System.out.println("      x[0-31]: View register content");
            System.out.println("      0x######## (8 hex digits): View memory content at address");
            System.out.println("      pc: View PC value");
            System.out.println("      insn: View next assembly instruction");
            System.out.println("      b [pc]: Set a breakpoint at [pc] where pc is a hex value");
            System.out.println("      c: Continue execution till next breakpoint or exit");
            System.out.println("      q: Quit simulator");

            String input = scanner.nextLine();

            switch(input) {
                case "reg":
                    System.out.println(registers.toString());
                    break;
                case "m":
                    // While memory values are not 0, keep printing them in format
                    // 0xaddress: string value
                    // Increment address by 1 each time

                    String address = allZeroes;
                    String value = memory.getInstruction(address);
                    while(!value.equals(allZeroes)) {
                        // Address is binary string, convert to hex
                        System.out.println("0x" + Integer.toHexString(Integer.parseInt(address, 2)) + ": " + value);
                        System.out.println(pipeline.machineToAsm(value));
                        address = Utility.StringCrement(address, 4);
                        value = memory.getInstruction(address);
                    }
                    break;
                case "r":
                    pipeline.runUntilBreakpointOrEnd();
                    break;
                case "s":
                    pipeline.runNextInstruction();
                    break;
                case "pc":
                    System.out.println(registers.getRegisterValue(input));
                    break;
                case "insn":
                    // Assuming a method in Pipeline class fetches the next instruction.
                    //System.out.println(pipeline.getNextInstructionInAssembly());
                    break;
                case "c":
                    pipeline.continueExecution();
                    break;
                case "q":
                    isRunning = false;
                    break;
                default:
                    if(input.startsWith("x")) {
                        System.out.println(registers.getRegisterValue(input));
                    } else if(input.startsWith("b ")) {
                        int pcValue = Integer.parseInt(input.split(" ")[1]);
                        pipeline.addBreakpoint(pcValue);
                    } else if(input.matches("^0x[0-9a-fA-F]{8}$")) {
                        String value2 = memory.getMemoryValue(input.substring(2));
                        System.out.println(value2);
                    } else {
                        System.out.println("Invalid command!");
                    }
                    break;
            }
        }

        scanner.close();
    }
}
