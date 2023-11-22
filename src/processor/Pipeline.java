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
        String instructionName = "";

        switch (oc) {
            case "0110111":
                instructionName = "lui";
                break;
            case "0010111":
                instructionName = "auipc";
                break;
            case "1101111":
                instructionName = "jal";
                break;
            case "1100111":
                instructionName = "jalr";
                break;
            case "1100011":
                switch (fc) {
                    case "000":
                        instructionName = "beq";
                        break;
                    case "001":
                        instructionName = "bne";
                        break;
                    case "100":
                        instructionName = "blt";
                        break;
                    case "101":
                        instructionName = "bge";
                        break;
                    case "110":
                        instructionName = "bltu";
                        break;
                    case "111":
                        instructionName = "bgeu";
                        break;
                }
                break;
            case "0000011":
                switch (fc) {
                    case "000":
                        instructionName = "lb";
                        break;
                    case "001":
                        instructionName = "lh";
                        break;
                    case "010":
                        instructionName = "lw";
                        break;
                    case "100":
                        instructionName = "lbu";
                        break;
                    case "101":
                        instructionName = "lhu";
                        break;
                }
                break;
            case "0100011":
                switch (fc) {
                    case "000":
                        instructionName = "sb";
                        break;
                    case "001":
                        instructionName = "sh";
                        break;
                    case "010":
                        instructionName = "sw";
                        break;
                }
                break;
            case "0010011":
                switch (fc) {
                    case "000":
                        instructionName = "addi";
                        break;
                    case "010":
                        instructionName = "slti";
                        break;
                    case "011":
                        instructionName = "sltiu";
                        break;
                    case "100":
                        instructionName = "xori";
                        break;
                    case "110":
                        instructionName = "ori";
                        break;
                    case "111":
                        instructionName = "andi";
                        break;
                    case "001":
                        instructionName = "slli";
                        break;
                    case "101":
                        String imm = instruction.substring(0, 7);
                        if (imm.equals("0000000")) {
                            instructionName = "srli";
                        } else if (imm.equals("0100000")) {
                            instructionName = "srai";
                        }
                        break;
                }
                break;
            case "0110011":
                switch (fc) {
                    case "000":
                        String imm = instruction.substring(0, 7);
                        if (imm.equals("0000000")) {
                            instructionName = "add";
                        } else if (imm.equals("0100000")) {
                            instructionName = "sub";
                        }
                        break;
                    case "001":
                        instructionName = "sll";
                        break;
                    case "010":
                        instructionName = "slt";
                        break;
                    case "011":
                        instructionName = "sltu";
                        break;
                    case "100":
                        instructionName = "xor";
                        break;
                    case "101":
                        imm = instruction.substring(0, 7);
                        if (imm.equals("0000000")) {
                            instructionName = "srl";
                        } else if (imm.equals("0100000")) {
                            instructionName = "sra";
                        }
                        break;
                    case "110":
                        instructionName = "or";
                        break;
                    case "111":
                        instructionName = "and";
                        break;
                }
                break;
            case "0001111":
                instructionName = "fence";
                break; // Assuming 'fence' for simplicity
            case "1110011":
                if (instruction.substring(0, 20).equals("00000000000000000000")) {
                    instructionName = "ecall";
                } else if (instruction.substring(0, 20).equals("00000000000100000000")) {
                    instructionName = "ebreak";
                }
                break;
            // Additional cases for other instructions, if any
            default:
                instructionName = "unknown";
                break;
        }
        System.out.println("final ASM" + instructionName);
        System.out.println("rd as String: " + registers.getRegisterString(rd));
        System.out.println("rs1 as String: " + registers.getRegisterString(rs1));
        System.out.println("rs2 as String: " + registers.getRegisterString(rs2));
        decodedInstruction.put("asm", instructionName);
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
