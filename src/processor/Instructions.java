package processor;

import java.util.HashSet;

public class Instructions {
    public String LUI(HashSet<String> asmInstruction, int immediate, int rd) {
        String instruction = "LUI " + "x" + rd + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String AUIPC(HashSet<String> asmInstruction, int immediate, int rd) {
        String instruction = "AUIPC  " + "x" + rd + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String JAL(HashSet<String> asmInstruction,  int immediate, int rd) {
        String instruction = "JAL " + "x" + rd + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String JALR(HashSet<String> asmInstruction, int immediate, int rs1, int rd) {
        String instruction = "JALR " + "x" + rd + ", " + immediate + "(x" + rs1 + ")";
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String BEQ(HashSet<String> asmInstruction, int rs1, int rs2, int immediate) {
        String instruction = "BEQ " + "x" + rs1 + ", x" + rs2 + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String BNE(HashSet<String> asmInstruction, int rs1, int rs2, int immediate) {
        String instruction = "BNE " + "x" + rs1 + ", x" + rs2 + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String BLT(HashSet<String> asmInstruction, int rs1, int rs2, int immediate) {
        String instruction = "BEQ " + "x" + rs1 + ", x" + rs2 + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String BGE(HashSet<String> asmInstruction, int rs1, int rs2, int immediate) {
        String instruction = "BEQ " + "x" + rs1 + ", x" + rs2 + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String BLTU(HashSet<String> asmInstruction, int rs1, int rs2, int immediate) {
        String instruction = "BLTU " + "x" + rs1 + ", x" + rs2 + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String BGEU(HashSet<String> asmInstruction, int rs1, int rs2, int immediate) {
        String instruction = "BGEU " + "x" + rs1 + ", x" + rs2 + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }

    // have this varib in it: public HashMap<String, String> machineToAsm(String instruction) { to make the sortting isntruction easier
    private Memory memory;
    private Registers registers;

    public Instructions(Memory memory, Registers registers) {
        this.memory = memory;
        this.registers = registers;
    }
    
    
    public void addi(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rd = instructionComponents.get("rd"); //destination register
        String rs1 = instructionComponents.get("rs1"); //source register 1
        String imm = instructionComponents.get("imm"); //immediate register

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        int valueIntRs1 = Integer.parseInt(valueRs1, 2);

        // Convert immediate value from binary string to integer
        int immediate = Integer.parseInt(imm, 2);

        // Perform addition operation
        int result = valueIntRs1 + immediate;

        // Convert result back to binary string representation
        String resultBinary = Integer.toBinaryString(result);

        // Ensure resultBinary is 32-bit length
        while (resultBinary.length() < 32) {
            resultBinary = "0" + resultBinary;
        }

        // Update rd register value
        registers.setRegisterValue(rd, resultBinary);
    }
}

