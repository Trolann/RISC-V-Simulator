package processor;

import java.util.HashSet;

public class Instructions {
    public String LUI(HashSet<String> asmInstruction, int immediate, int rd) {
        // Logic for LUI instruction
        String instruction = "LUI " + "x" + rd + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String AUIPC(HashSet<String> asmInstruction, int immediate, int rd) {
        // Logic for LUI instruction
        String instruction = "AUIPC  " + "x" + rd + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String JAL(HashSet<String> asmInstruction,  int immediate, int rd) {
        // Logic for JAL instruction
        String instruction = "JAL " + "x" + rd + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String JALR(HashSet<String> asmInstruction, int immediate, int rs1, int rd) {
        // Logic for JALR instruction
        String instruction = "JALR " + "x" + rd + ", " + immediate + "(x" + rs1 + ")";
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String BEQ(HashSet<String> asmInstruction, int rs1, int rs2, int immediate) {
        // Logic for BEQ instruction
        String instruction = "BEQ " + "x" + rs1 + ", x" + rs2 + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String BNE(HashSet<String> asmInstruction, int rs1, int rs2, int immediate) {
        // Logic for BEQ instruction
        String instruction = "BNE " + "x" + rs1 + ", x" + rs2 + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String BLT(HashSet<String> asmInstruction, int rs1, int rs2, int immediate) {
        // Logic for BEQ instruction
        String instruction = "BEQ " + "x" + rs1 + ", x" + rs2 + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String BGE(HashSet<String> asmInstruction, int rs1, int rs2, int immediate) {
        // Logic for BEQ instruction
        String instruction = "BEQ " + "x" + rs1 + ", x" + rs2 + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String BLTU(HashSet<String> asmInstruction, int rs1, int rs2, int immediate) {
        // Logic for BEQ instruction
        String instruction = "BLTU " + "x" + rs1 + ", x" + rs2 + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
    
    public String BGEU(HashSet<String> asmInstruction, int rs1, int rs2, int immediate) {
        // Logic for BEQ instruction
        String instruction = "BGEU " + "x" + rs1 + ", x" + rs2 + ", " + immediate;
        asmInstruction.add(instruction);
        return instruction;
    }
}

