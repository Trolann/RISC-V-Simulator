package processor;

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

        String opcode = instruction.substring(25, 32);
        String funct3 = "";
        String funct7 = "";
        String rd = "";
        String rs1 = "";
        String rs2 = "";
        String immediate = "";
        String asmInstruction = "";

        switch(opcode) {
            case "0110111":
            case "0010111":
                // LUI and AUIPC
                rd = instruction.substring(20, 25);
                immediate = instruction.substring(0, 20);
                break;

            case "1101111":
                // JAL - J-type instruction
                rd = instruction.substring(20, 25);
                // Properly form J-type immediate
                immediate = instruction.charAt(0)
                        + instruction.substring(12, 20)
                        + instruction.charAt(11)
                        + instruction.substring(1, 11);
                break;

            case "1100111":
                // JALR
                rd = instruction.substring(20, 25);
                funct3 = instruction.substring(17, 20);
                rs1 = instruction.substring(12, 17);
                immediate = instruction.substring(0, 12);
                break;

            case "1100011":
                // B-type (BEQ, BNE, BLT, BGE, BLTU, BGEU)
                funct3 = instruction.substring(17, 20);
                rs1 = instruction.substring(12, 17);
                rs2 = instruction.substring(7, 12);
                // Properly form B-type immediate
                immediate = instruction.charAt(0)
                        + instruction.charAt(24)
                        + instruction.substring(1, 7)
                        + instruction.substring(20, 24);
                break;

            case "0000011":
            case "0100011":
                // Loads (LB, LH, LW, LBU, LHU) and Stores (SB, SH, SW)
                funct3 = instruction.substring(17, 20);
                rd = instruction.substring(20, 25);
                rs1 = instruction.substring(12, 17);
                rs2 = instruction.substring(7, 12);
                immediate = instruction.substring(0, 7) + instruction.substring(20, 25); // I/S-type immediate
                break;

            case "0010011":
            case "0110011":
                // I-type and R-type instructions (e.g., ADDI, SLTI, AND, OR...)
                rd = instruction.substring(20, 25);
                funct3 = instruction.substring(17, 20);
                rs1 = instruction.substring(12, 17);
                rs2 = instruction.substring(7, 12);
                funct7 = instruction.substring(0, 7);
                if(opcode.equals("0010011")) {  // I-type
                    immediate = funct7;
                }
                break;

            default:
                // Not supported or system instruction
                return decodedInstruction; // Empty HashMap
        }

        decodedInstruction.put("opcode", opcode);
        if (!rd.isEmpty()) decodedInstruction.put("rd", rd);
        if (!funct3.isEmpty()) decodedInstruction.put("funct3", funct3);
        if (!rs1.isEmpty()) decodedInstruction.put("rs1", rs1);
        if (!rs2.isEmpty()) decodedInstruction.put("rs2", rs2);
        if (!immediate.isEmpty()) decodedInstruction.put("immediate", immediate);

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
