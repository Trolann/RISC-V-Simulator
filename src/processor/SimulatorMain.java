package processor;

import java.util.Scanner;
import processor.Registers;
import processor.Utility;
import processor.Loader;

public class SimulatorMain {

    // Memory and Register objects to interact with the pseudo RISC-V processor.
    private static Memory memory = new Memory();
    private static Registers registers = new Registers();
    private static Loader loader = new Loader(memory);
    //private static Pipeline pipeline = new Pipeline(memory, registers);

    public static void main(String[] args) {
        // Load the .dat file into memory. (Loader logic should be completed separately.)
        // Load instructions using Loader
        try {
          loader.loadInstructions("src\\processor\\input_files\\addi_hazards.dat"); 
        } catch (IOException e) {
          e.printStackTrace();
        }
        //Question: do we need line below?
        // Utility.loadData("src\\processor\\input_files\\addi_hazards.dat", memory); // TODO: Take in via stdin

        Scanner scanner = new Scanner(System.in);
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
                case "r":
                    //pipeline.runUntilBreakpointOrEnd();
                    break;
                case "s":
                    //pipeline.runNextInstruction();
                    break;
                case "pc":
                    System.out.println(registers.getRegisterValue(input));
                    break;
                case "insn":
                    // Assuming a method in Pipeline class fetches the next instruction.
                    //System.out.println(pipeline.getNextInstructionInAssembly());
                    break;
                case "c":
                    //pipeline.continueExecution();
                    break;
                case "q":
                    isRunning = false;
                    break;
                default:
                    if(input.startsWith("x")) {
                        System.out.println(registers.getRegisterValue(input));
                    } else if(input.startsWith("b ")) {
                        int pcValue = Integer.parseInt(input.split(" ")[1]);
                        //pipeline.addBreakpoint(pcValue);
                    } else if(input.matches("^0x[0-9a-fA-F]{8}$")) {
                        //String value = memory.getDataFromAddress(input);
                        //System.out.println(value);
                    } else {
                        System.out.println("Invalid command!");
                    }
                    break;
            }
        }

        scanner.close();
    }
}
