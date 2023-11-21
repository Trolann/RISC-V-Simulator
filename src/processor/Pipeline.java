package processor;

import javax.sound.midi.SysexMessage;
import java.util.HashMap;
import java.util.HashSet;

public class Pipeline {

    private Registers registers;            // Register interface
    private Memory memory;                  // Memory interface
    private HashSet<Integer> breakpoints;   // Store breakpoints as integer addresses
    private boolean hasReachedBreakpoint;

    public Pipeline(Memory memory, Registers registers) {
        this.memory = memory;
        this.registers = registers;
        this.breakpoints = new HashSet<>();
        this.hasReachedBreakpoint = false;
    }

    // Run until a breakpoint or end
    public void runUntilBreakpointOrEnd() {
        while(!hasReachedBreakpoint && registers.getRegisterValue("pc") != null) {
            runNextInstruction();
        }
    }

    // Execute a single instruction
    public void runNextInstruction() {
        String pcValue = registers.getRegisterValue("pc");
        String instruction = memory.getMemoryValue(pcValue);

        if(instruction == null) {
            // TODO Handle error: instruction at pcValue not found in memory
            return;
        }

        // Convert machine instruction to assembly components (you'll need to implement this)
        HashMap<String, String> asmComponents = machineToAsm(instruction);

        // TODO: Execute Instruction method here (switch case)

        // Check for breakpoints
        int pcIntValue = Integer.parseInt(pcValue, 2); // Convert binary to int
        if(breakpoints.contains(pcIntValue)) {
            hasReachedBreakpoint = true;
        }

        // TODO: Handle program counter update logic (increment or branch) by updating pc register
    }

    // Continue execution until the next breakpoint or end
    public void continueExecution() {
        hasReachedBreakpoint = false; // Reset breakpoint flag
        runUntilBreakpointOrEnd();
    }

    // Add a breakpoint at a specific address
    public void addBreakpoint(int address) {
        breakpoints.add(address);
    }

    // Convert a machine instruction to its assembly components
    // Return a HashMap with components as key-value pairs
    public HashMap<String, String> machineToAsm(String instruction) {
        HashMap<String, String> decodedInstruction = new HashMap<>();

        String oc = instruction.substring(25, 32); // opcode
        String rd = instruction.substring(20, 25); // destination register
        String fc = instruction.substring(17, 20); // function code
        String rs1 = instruction.substring(12, 17); // source register 1
        String rs2 = instruction.substring(7, 12); // source register 2
        String finalASM = "";

        switch (oc) {
            case "0110111":
                finalASM = "lui";
                break;
            case "0010111":
                finalASM = "auipc";
                break;
            case "1101111":
                finalASM = "jal";
                break;
            case "1100111":
                finalASM = "jalr";
                break;
            case "1100011":
                switch (fc) {
                    case "000":
                        finalASM = "beq";
                        break;
                    case "001":
                        finalASM = "bne";
                        break;
                    case "100":
                        finalASM = "blt";
                        break;
                    case "101":
                        finalASM = "bge";
                        break;
                    case "110":
                        finalASM = "bltu";
                        break;
                    case "111":
                        finalASM = "bgeu";
                        break;
                }
                break;
            case "0000011":
                switch (fc) {
                    case "000":
                        finalASM = "lb";
                        break;
                    case "001":
                        finalASM = "lh";
                        break;
                    case "010":
                        finalASM = "lw";
                        break;
                    case "100":
                        finalASM = "lbu";
                        break;
                    case "101":
                        finalASM = "lhu";
                        break;
                }
                break;
            case "0100011":
                switch (fc) {
                    case "000":
                        finalASM = "sb";
                        break;
                    case "001":
                        finalASM = "sh";
                        break;
                    case "010":
                        finalASM = "sw";
                        break;
                }
                break;
            case "0010011":
                switch (fc) {
                    case "000":
                        finalASM = "addi";
                        break;
                    case "010":
                        finalASM = "slti";
                        break;
                    case "011":
                        finalASM = "sltiu";
                        break;
                    case "100":
                        finalASM = "xori";
                        break;
                    case "110":
                        finalASM = "ori";
                        break;
                    case "111":
                        finalASM = "andi";
                        break;
                    case "001":
                        finalASM = "slli";
                        break;
                    case "101":
                        String imm = instruction.substring(0, 7);
                        if (imm.equals("0000000")) {
                            finalASM = "srli";
                        } else if (imm.equals("0100000")) {
                            finalASM = "srai";
                        }
                        break;
                }
                break;
            case "0110011":
                switch (fc) {
                    case "000":
                        String imm = instruction.substring(0, 7);
                        if (imm.equals("0000000")) {
                            finalASM = "add";
                        } else if (imm.equals("0100000")) {
                            finalASM = "sub";
                        }
                        break;
                    case "001":
                        finalASM = "sll";
                        break;
                    case "010":
                        finalASM = "slt";
                        break;
                    case "011":
                        finalASM = "sltu";
                        break;
                    case "100":
                        finalASM = "xor";
                        break;
                    case "101":
                        imm = instruction.substring(0, 7);
                        if (imm.equals("0000000")) {
                            finalASM = "srl";
                        } else if (imm.equals("0100000")) {
                            finalASM = "sra";
                        }
                        break;
                    case "110":
                        finalASM = "or";
                        break;
                    case "111":
                        finalASM = "and";
                        break;
                }
                break;
            case "0001111":
                finalASM = "fence";
                break; // Assuming 'fence' for simplicity
            case "1110011":
                if (instruction.substring(0, 20).equals("00000000000000000000")) {
                    finalASM = "ecall";
                } else if (instruction.substring(0, 20).equals("00000000000100000000")) {
                    finalASM = "ebreak";
                }
                break;
            // Additional cases for other instructions, if any
            default:
                finalASM = "unknown";
                break;
        }
        System.out.println("final ASM" + finalASM);
        System.out.println("rd as String: " + registers.getRegisterString(rd));
        System.out.println("rs1 as String: " + registers.getRegisterString(rs1));
        System.out.println("rs2 as String: " + registers.getRegisterString(rs2));
        decodedInstruction.put("asm", finalASM);
        // Other decoded fields can be added to the map as needed
        return decodedInstruction;
    }


    // Fetch the next instruction in assembly for display purposes
    public String getNextInstructionInAssembly() {
        // Using the machineToAsm function to get components
        HashMap<String, String> components = machineToAsm(memory.getMemoryValue(registers.getRegisterValue("pc")));

        // TODO: Convert components to human-readable assembly string

        return ""; // Return the assembled instruction string
    }

}
