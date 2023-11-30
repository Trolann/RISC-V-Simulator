package processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Pipeline {

    private Registers registers;            // Register interface
    private Memory memory;                  // Memory interface
    private HashSet<Integer> breakpoints;   // Store breakpoints as integer addresses
    private boolean hasReachedBreakpoint;
    private Map<String, InstructionFunction> functionMap;
    private Instructions instructions;

    public Pipeline(Memory memory, Registers registers) {
        this.memory = memory;
        this.registers = registers;
        this.breakpoints = new HashSet<>();
        this.hasReachedBreakpoint = false;
        this.functionMap = new HashMap<>();
        this.instructions = new Instructions(memory, registers);
        // functionMap.put("lui", this.instructions::LUI); // 1
        // functionMap.put("auipc", this.instructions::AUIPC); // 2
        // functionMap.put("jal", this.instructions::JAL); // 3
        // functionMap.put("jalr", this.instructions::JALR); // 4
        // functionMap.put("beq", this.instructions::BEQ); // 5
        // functionMap.put("bne", this.instructions::BNE); // 6
        // functionMap.put("blt", this.instructions::BLT); // 7
        // functionMap.put("bge", this.instructions::BGE); // 8
        // functionMap.put("bltu", this.instructions::BLTU); // 9
        // functionMap.put("bgeu", this.instructions::BGEU); // 10
        // functionMap.put("lb", this.instructions::LB); // 11
        // functionMap.put("lh", this.instructions::LH); // 12
        // functionMap.put("lw", this.instructions::LW); // 13
        // functionMap.put("lbu", this.instructions::LBU); // 14
        // functionMap.put("lhu", this.instructions::LHU); // 15
        // functionMap.put("sb", this.instructions::SB); // 16
        // functionMap.put("sh", this.instructions::SH); // 17
        // functionMap.put("sw", this.instructions::SW); // 18
        functionMap.put("addi", this.instructions::ADDI); // 19
        // functionMap.put("slti", this.instructions::SLTI); // 20
        // functionMap.put("sltiu", this.instructions::SLTIU); // 21
        // functionMap.put("xori", this.instructions::XORI); // 22
        // functionMap.put("ori", this.instructions::ORI); // 23
        // functionMap.put("andi", this.instructions::ANDI); // 24
        // functionMap.put("slli", this.instructions::SLLI); // 25
        // functionMap.put("srli", this.instructions::SRLI); // 26
        // functionMap.put("srai", this.instructions::SRAI); // 27
        // functionMap.put("add", this.instructions::ADD); // 28
        // functionMap.put("sub", this.instructions::SUB); // 29
        // functionMap.put("sll", this.instructions::SLL); // 30
        // functionMap.put("slt", this.instructions::SLT); // 31
        // functionMap.put("sltu", this.instructions::SLTU); // 32
        // functionMap.put("xor", this.instructions::XOR); // 33
        // functionMap.put("srl", this.instructions::SRL); // 34
        // functionMap.put("sra", this.instructions::SRA); // 35
        // functionMap.put("or", this.instructions::OR); // 36
        // functionMap.put("and", this.instructions::AND); // 37
        // functionMap.put("fence", this.instructions::FENCE); // 38
        // functionMap.put("ecall", this.instructions::ECALL); // 39
        // functionMap.put("ebreak", this.instructions::ERBEAK); // 40
    }

    // Run until a breakpoint or end
    public void runUntilBreakpointOrEnd() {
        while(!hasReachedBreakpoint && registers.getProgramCounter() != null) {
            runNextInstruction();
        }
    }

    // Execute a single instruction
    public void runNextInstruction() {
        System.out.println("Running next instruction: " + registers.getProgramCounter());
        String instruction = memory.getInstruction(registers.getProgramCounter());

        if(instruction == null) {
            // TODO Handle error: instruction at pcValue not found in memory
            return;
        }

        // Convert machine instruction to assembly components (you'll need to implement this)
        HashMap<String, String> asmComponents = machineToAsm(instruction);
        System.out.println("instructionName: " + asmComponents.get("instructionName"));

        if (functionMap.containsKey(asmComponents.get("instructionName"))) {
            functionMap.get(asmComponents.get("instructionName")).execute(asmComponents);
        } else {
            return;
        }

        // Check for breakpoints
        //int pcIntValue = Integer.parseInt(pcValue, 2); // Convert binary to int
        int pcIntValue = Integer.parseInt(registers.getProgramCounter(), 2); // Convert binary to int
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
        String instructionName = "Error: {" + oc + "} Instruction not found"; // Assume an error
        String imm = instruction.substring(0, 12); // immediate value

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
                    default:
                        instructionName += " fc: " + fc;
                        break;
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
                    default:
                        instructionName += " fc: " + fc;
                        break;
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
                    default:
                        instructionName += " fc: " + fc;
                        break;
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
                    default:
                        instructionName += " fc: " + fc;
                        break;
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
                        imm = instruction.substring(0, 7);
                        instructionName += " case: 101 imm: " + imm;
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
                    default:
                        instructionName += " fc: " + fc;
                        break;
                    case "000":
                        imm = instruction.substring(0, 7);
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
        System.out.println("rd as String: " + registers.getRegisterString(Integer.parseInt(rd, 2)));
        System.out.println("rs1 as String: " + registers.getRegisterString(Integer.parseInt(rs1, 2)));
        System.out.println("rs2 as String: " + registers.getRegisterString(Integer.parseInt(rs2, 2)));
        decodedInstruction.put("instructionName", instructionName);
        decodedInstruction.put("rd", registers.getRegisterString(Integer.parseInt(rd, 2)));
        decodedInstruction.put("rs1", registers.getRegisterString(Integer.parseInt(rs1, 2)));
        decodedInstruction.put("rs2", registers.getRegisterString(Integer.parseInt(rs2, 2)));
        decodedInstruction.put("imm", imm);

        return decodedInstruction;
    }


    // Fetch the next instruction in assembly for display purposes
    public String getNextInstructionInAssembly() {
        // Using the machineToAsm function to get components
        HashMap<String, String> components = machineToAsm(memory.getMemoryValue(registers.getProgramCounter()));

        // TODO: Convert components to human-readable assembly string

        return ""; // Return the assembled instruction string
    }

}
