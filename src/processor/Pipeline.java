package processor;

import java.io.FileWriter;
import java.io.IOException;
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
    private FileWriter outputFile;
    private final boolean RUN = true;
    private final boolean STOP = false;

    public Pipeline(Memory memory, Registers registers) {
        this.memory = memory;
        this.registers = registers;
        this.breakpoints = new HashSet<>();
        this.hasReachedBreakpoint = false;
        this.functionMap = new HashMap<>();
        this.instructions = new Instructions(memory, registers);
        functionMap.put("lui", this.instructions::LUI); // 1
        functionMap.put("auipc", this.instructions::AUIPC); // 2
        functionMap.put("jal", this.instructions::JAL); // 3
        functionMap.put("jalr", this.instructions::JALR); // 4
        functionMap.put("beq", this.instructions::BEQ); // 5
        functionMap.put("bne", this.instructions::BNE); // 6
        functionMap.put("blt", this.instructions::BLT); // 7
        functionMap.put("bge", this.instructions::BGE); // 8
        functionMap.put("bltu", this.instructions::BLTU); // 9
        functionMap.put("bgeu", this.instructions::BGEU); // 10
        functionMap.put("lb", this.instructions::LB); // 11
        functionMap.put("lh", this.instructions::LH); // 12
        functionMap.put("lw", this.instructions::LW); // 13
        functionMap.put("lbu", this.instructions::LBU); // 14
        functionMap.put("lhu", this.instructions::LHU); // 15
        functionMap.put("sb", this.instructions::SB); // 16
        functionMap.put("sh", this.instructions::SH); // 17
        functionMap.put("sw", this.instructions::SW); // 18
        functionMap.put("addi", this.instructions::ADDI); // 19
        functionMap.put("slti", this.instructions::SLTI); // 20
        functionMap.put("sltiu", this.instructions::SLTIU); // 21
        functionMap.put("xori", this.instructions::XORI); // 22
        functionMap.put("ori", this.instructions::ORI); // 23
        functionMap.put("andi", this.instructions::ANDI); // 24
        functionMap.put("slli", this.instructions::SLLI); // 25
        functionMap.put("srli", this.instructions::SRLI); // 26
        functionMap.put("srai", this.instructions::SRAI); // 27
        functionMap.put("add", this.instructions::ADD); // 28
        functionMap.put("sub", this.instructions::SUB); // 29
        functionMap.put("sll", this.instructions::SLL); // 30
        functionMap.put("slt", this.instructions::SLT); // 31
        functionMap.put("sltu", this.instructions::SLTU); // 32
        functionMap.put("xor", this.instructions::XOR); // 33
        functionMap.put("srl", this.instructions::SRL); // 34
        functionMap.put("sra", this.instructions::SRA); // 35
        functionMap.put("or", this.instructions::OR); // 36
        functionMap.put("and", this.instructions::AND); // 37
        //functionMap.put("fence", this.instructions::FENCE); // 38
        //functionMap.put("ecall", this.instructions::ECALL); // 39
        //functionMap.put("ebreak", this.instructions::ERBEAK); // 40
    }

    // Run until a breakpoint or end
    public boolean runUntilEnd() {
    	while(!memory.getInstruction(registers.getProgramCounter()).equals(Utility.ALLZEROS)) {
        	runNextInstruction();
        }
        	return STOP;
    }
    // Execute a single instruction
    public boolean runNextInstruction() {
    	System.out.println("PIPELINE DEBUG: Running next instruction: " + registers.getProgramCounter());
        String instruction = memory.getInstruction(registers.getProgramCounter());
        System.out.println("PIPELINE DEBUG: raw instruction: " + instruction);

        if(instruction == null) {
            return STOP;
        }

        // Convert machine instruction to assembly components (you'll need to implement this)
        HashMap<String, String> asmComponents = machineToAsm(instruction);
        System.out.println("PIPELINE DEBUG: instructionName: " + asmComponents.get("instructionName"));

        if (functionMap.containsKey(asmComponents.get("instructionName"))) {
            String result;
            result = functionMap.get(asmComponents.get("instructionName")).execute(asmComponents);
            System.out.println("PIPELINE DEBUG: result: " + result);
            
            // Write the assembly instruction to a .asm file
            writeInstructionToFile(result);
        } else {
        	System.out.println("PIPELINE DEBUG: Instruction not found: " + asmComponents);
            return STOP;
        }

        // Check for breakpoints
        //int pcIntValue = Integer.parseInt(pcValue, 2); // Convert binary to int
        int pcIntValue = Integer.parseInt(registers.getProgramCounter(), 2); // Convert binary to int
        System.out.println("PIPELINE DEBUG: Checking for breakpoint at: " + pcIntValue);
        if(breakpoints.contains(pcIntValue)) {
            hasReachedBreakpoint = true;
        }
        return RUN;
    }

    private void writeInstructionToFile(String result) {
		try {
            if (outputFile == null) {
                // Open the file for writing
                outputFile = new FileWriter("output.asm");
            }

            // Write the instruction to the file
            outputFile.write(result + "\n");
            
            // Flush the buffer to ensure data is written immediately
            outputFile.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	// Continue execution until the next breakpoint or end
    public boolean continueExecution() {
    	boolean done;
        hasReachedBreakpoint = false; // Reset breakpoint flag
    	while(!hasReachedBreakpoint) {
            done = runNextInstruction();
            if(!done) {
                return STOP;
            }
        }
        return RUN;
    }

    // Add a breakpoint at a specific address
    public boolean addBreakpoint(int address) {
    	breakpoints.add(address);
        return true;
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
        String imm = instruction.substring(0, 12); // default immediate value

        switch (oc) {
            case "0110111":
                imm = instruction.substring(0, 20);
                instructionName = "lui";
                break;
            case "0010111":
                imm = instruction.substring(0, 20);
                instructionName = "auipc";
                break;
            case "1101111":
                imm = String.valueOf(instruction.charAt(0)) +
                        instruction.charAt(12) + instruction.charAt(13) + instruction.charAt(14) +
                        instruction.charAt(15) + instruction.charAt(16) + instruction.charAt(17) +
                        instruction.charAt(18) + instruction.charAt(19) + instruction.charAt(11) +
                        instruction.charAt(1) + instruction.charAt(2) + instruction.charAt(3) +
                        instruction.charAt(4) + instruction.charAt(5) + instruction.charAt(6) +
                        instruction.charAt(7) + instruction.charAt(8) + instruction.charAt(9) +
                        instruction.charAt(10);
                instructionName = "jal";
                break;
            case "1100111":
                // Default immediate value
                instructionName = "jalr";
                break;
            case "1100011":
                imm = String.valueOf(instruction.charAt(0)) +
                        instruction.charAt(24) + instruction.charAt(1) + instruction.charAt(2) +
                        instruction.charAt(3) + instruction.charAt(4) + instruction.charAt(5) +
                        instruction.charAt(6) + instruction.charAt(20) + instruction.charAt(21) +
                        instruction.charAt(23) + instruction.charAt(23);
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
                // Default immediate value
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
                imm = String.valueOf(instruction.charAt(0)) +
                        instruction.charAt(1) + instruction.charAt(2) + instruction.charAt(3) +
                        instruction.charAt(4) + instruction.charAt(5) + instruction.charAt(6) +
                        instruction.charAt(20) + instruction.charAt(21) + instruction.charAt(22) +
                        instruction.charAt(23) + instruction.charAt(24);
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
                        imm = instruction.substring(0, 7);
                        instructionName = "slli";
                        break;
                    case "101":
                        imm = instruction.substring(0, 7);
                        instructionName = imm.contains("1") ? "srai" : "srli";
                        break;
                }
                break;
            case "0110011":
                imm = instruction.substring(0, 7);
                switch (fc) {
                    default:
                        instructionName += " fc: " + fc;
                        break;
                    case "000":
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
                decodedInstruction.put("fm", instruction.substring(0, 5));
                decodedInstruction.put("pred", instruction.substring(5, 9));
                decodedInstruction.put("succ", instruction.substring(9, 12));
                instructionName = "fence";
                break;
            case "1110011":
                imm = instruction.substring(0, 12);
                instructionName = imm.contains("1") ? "ebreak" : "ecall";
                break;
            // Additional cases for other instructions, if any
            default:
                instructionName = "unknown";
                break;
        }
        
        decodedInstruction.put("instructionName", instructionName);
        decodedInstruction.put("rd", registers.getRegisterString(Integer.parseInt(rd, 2)));
        decodedInstruction.put("rs1", registers.getRegisterString(Integer.parseInt(rs1, 2)));
        decodedInstruction.put("rs2", registers.getRegisterString(Integer.parseInt(rs2, 2)));
        decodedInstruction.put("imm", imm);
        System.out.println("PIPELINE DEBUG: decodedInstruction: " + decodedInstruction);

        return decodedInstruction;
    }

}
