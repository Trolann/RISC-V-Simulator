package processor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

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
    private String datFile;

    public Pipeline(Memory memory, Registers registers, String inputFile) {
        this.memory = memory;
        this.registers = registers;
        this.breakpoints = new HashSet<>();
        this.hasReachedBreakpoint = false;
        this.functionMap = new HashMap<>();
        this.instructions = new Instructions(memory, registers);
        // outputFile = input file before the extension + .asm
        inputFile = inputFile.split("/")[(inputFile.split("/").length) - 1];
        String[] inputFileSplit = inputFile.split("\\.");
        this.datFile = inputFileSplit[0] + ".asm";
        System.out.println("datFile: " + datFile);
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
    public boolean runUntilEnd() throws IOException {
    	while(!memory.getInstruction(registers.getProgramCounter()).equals(Utility.ALLZEROS)) {
        	runNextInstruction();
        }
        outputFile.close();
        return STOP;
    }
    // Execute a single instruction
    public boolean runNextInstruction() throws IOException {
    	//System.out.println("PIPELINE DEBUG: Running next instruction: " + registers.getProgramCounter());
        String instruction = memory.getInstruction(registers.getProgramCounter());
        System.out.println("PIPELINE DEBUG: raw instruction: " + instruction);

        if(instruction.equals(Utility.ALLZEROS)) {
            outputFile.close();
            return STOP;
        }

        // Convert machine instruction to assembly components (you'll need to implement this)
        HashMap<String, String> asmComponents = machineToAsm(instruction);
        //System.out.println("PIPELINE DEBUG: instructionName: " + asmComponents.get("instructionName"));

        if (functionMap.containsKey(asmComponents.get("instructionName"))) {
            String result;
            result = functionMap.get(asmComponents.get("instructionName")).execute(asmComponents);

            //System.out.println("PIPELINE DEBUG: result: " + result);
            String oldPC = Utility.StringCrement(registers.getProgramCounter(), -4);
            // Convert oldPC to 32 bit hex
            int digits = Integer.toHexString(Integer.parseInt(oldPC, 2)).length();
            oldPC = Integer.toHexString(Integer.parseInt(oldPC, 2));
            for (int i = 0; i < 8 - digits; i++) {
                oldPC = "0" + oldPC;
            }
            oldPC = "0x" + oldPC;
            System.out.println(oldPC + ": " + result);
            // Parse instruction as an unsigned binary string, then convert to hex
            String machineCode = Integer.toHexString((int) Long.parseLong(instruction, 2));
            //System.out.println("PIPELINE DEBUG: machineCode: " + machineCode);
            digits = machineCode.length();
            for (int i = 0; i < 8 - digits; i++) {
                machineCode = "0" + machineCode;
            }
            machineCode = "0x" + machineCode;

            // Write the assembly instruction to a .asm file
            writeInstructionToFile(oldPC, machineCode, result);
        } else {
        	System.out.println("PIPELINE DEBUG: Instruction not found: " + asmComponents);
            outputFile.close();
            return STOP;
        }

        // Check for breakpoints
        //int pcIntValue = Integer.parseInt(pcValue, 2); // Convert binary to int
        int pcIntValue = Integer.parseInt(registers.getProgramCounter(), 2); // Convert binary to int
        //System.out.println("PIPELINE DEBUG: Checking for breakpoint at: " + pcIntValue);
        if(breakpoints.contains(pcIntValue)) {
            System.out.println("PIPELINE DEBUG: Reached breakpoint at: " + pcIntValue);
            hasReachedBreakpoint = true;
        }
        return RUN;
    }

    public String printNextAsmInstruction() {
        // test
        // create a bogus instructions so that it does not affect the actual program
        // and use return (execute) value to return the asm.
        Instructions bogusInstructions = new Instructions(new Memory(), new Registers(registers));
        Map<String, InstructionFunction> bogusMap = new HashMap<String, InstructionFunction>();
        bogusMap.put("lui", bogusInstructions::LUI); // 1
        bogusMap.put("auipc", bogusInstructions::AUIPC); // 2
        bogusMap.put("jal", bogusInstructions::JAL); // 3
        bogusMap.put("jalr", bogusInstructions::JALR); // 4
        bogusMap.put("beq", bogusInstructions::BEQ); // 5
        bogusMap.put("bne", bogusInstructions::BNE); // 6
        bogusMap.put("blt", bogusInstructions::BLT); // 7
        bogusMap.put("bge", bogusInstructions::BGE); // 8
        bogusMap.put("bltu", bogusInstructions::BLTU); // 9
        bogusMap.put("bgeu", bogusInstructions::BGEU); // 10
        bogusMap.put("lb", bogusInstructions::LB); // 11
        bogusMap.put("lh", bogusInstructions::LH); // 12
        bogusMap.put("lw", bogusInstructions::LW); // 13
        bogusMap.put("lbu", bogusInstructions::LBU); // 14
        bogusMap.put("lhu", bogusInstructions::LHU); // 15
        bogusMap.put("sb", bogusInstructions::SB); // 16
        bogusMap.put("sh", bogusInstructions::SH); // 17
        bogusMap.put("sw", bogusInstructions::SW); // 18
        bogusMap.put("addi", bogusInstructions::ADDI); // 19
        bogusMap.put("slti", bogusInstructions::SLTI); // 20
        bogusMap.put("sltiu", bogusInstructions::SLTIU); // 21
        bogusMap.put("xori", bogusInstructions::XORI); // 22
        bogusMap.put("ori", bogusInstructions::ORI); // 23
        bogusMap.put("andi", bogusInstructions::ANDI); // 24
        bogusMap.put("slli", bogusInstructions::SLLI); // 25
        bogusMap.put("srli", bogusInstructions::SRLI); // 26
        bogusMap.put("srai", bogusInstructions::SRAI); // 27
        bogusMap.put("add", bogusInstructions::ADD); // 28
        bogusMap.put("sub", bogusInstructions::SUB); // 29
        bogusMap.put("sll", bogusInstructions::SLL); // 30
        bogusMap.put("slt", bogusInstructions::SLT); // 31
        bogusMap.put("sltu", bogusInstructions::SLTU); // 32
        bogusMap.put("xor", bogusInstructions::XOR); // 33
        bogusMap.put("srl", bogusInstructions::SRL); // 34
        bogusMap.put("sra", bogusInstructions::SRA); // 35
        bogusMap.put("or", bogusInstructions::OR); // 36
        bogusMap.put("and", bogusInstructions::AND); // 37
        //bogusMap.put("fence", bogusInstructions::FENCE); // 38
        //bogusMap.put("ecall", bogusInstructions::ECALL); // 39
        //bogusMap.put("ebreak", bogusInstructions::ERBEAK); // 40
        String instruction = memory.getInstruction(registers.getProgramCounter());
        HashMap<String, String> asmComponents = machineToAsm(instruction);

        if (functionMap.containsKey(asmComponents.get("instructionName"))) {
            String asmInstruction;
            asmInstruction = bogusMap.get(asmComponents.get("instructionName")).execute(asmComponents);
            return "Next assembly instruction: " + asmInstruction;

        } else {
            return "Instruction not found: " + asmComponents;
        }
    }

    private void writeInstructionToFile(String programCounter, String machineInstruction, String result) {
		try {
            if (outputFile == null) {
                // Open the file for writing
                outputFile = new FileWriter(datFile);
                outputFile.write("Address     Code        Basic\n");
            }

            // Write the instruction to the file
            outputFile.write(programCounter + "  " + machineInstruction + "  "  + result + "\n");
            
            // Flush the buffer to ensure data is written immediately
            outputFile.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	// Continue execution until the next breakpoint or end
    public boolean continueExecution() throws IOException {
    	boolean continueRunning;
        hasReachedBreakpoint = false; // Reset breakpoint flag
    	while(!hasReachedBreakpoint) {
            continueRunning = runNextInstruction();
            //System.out.println("PIPELINE DEBUG: continueRunning: " + continueRunning);
            if(!continueRunning) {
                outputFile.close();
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
                        instruction.charAt(10) + "0";
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
                        instruction.charAt(22) + instruction.charAt(23) + "0";
                System.out.println("PIPELINE DEBUG: branch imm: " + imm);
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
                        decodedInstruction.put("shamt", rs2);
                        instructionName = "slli";
                        break;
                    case "101":
                        decodedInstruction.put("shamt", rs2);
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
        //System.out.println("PIPELINE DEBUG: decodedInstruction: " + decodedInstruction);

        return decodedInstruction;
    }

}
