package processor;

import java.util.HashMap;
import java.util.Map;

public class Registers {

    private final HashMap<String, String> registerMap;
    private final HashMap<String, String> aliasMap;
    //public int programCounter;

    public Registers() {
        //this.programCounter = 0;
        this.registerMap = new HashMap<>(32);
        this.aliasMap = new HashMap<>();

        // Initialize general-purpose registers x0 to x31 and their aliases
        initializeRegistersAndAliases();

        // Set program counter 'pc' to all zeros
        registerMap.put("pc", Utility.ALLZEROS);
    }

    private void initializeRegistersAndAliases() {
        String[] aliases = {
                "zero", // x0
                "ra", // x1
                "sp", // x2
                "gp", // x3
                "tp", // x4
                "t0", // x5
                "t1", // x6
                "t2", // x7
                "s0/fp", // x8
                "s1", // x9
                "a0", // x10
                "a1", // x11
                "a2", // x12
                "a3", // x13
                "a4", // x14
                "a5", // x15
                "a6", // x16
                "a7", // x17
                "s2", // x18
                "s3", // x19
                "s4", // x20
                "s5", // x21
                "s6", // x22
                "s7", // x23
                "s8", // x24
                "s9", // x25
                "s10", // x26
                "s11", // x27
                "t3", // x28
                "t4", // x29
                "t5", // x30
                "t6" // x31
        };
        for (int i = 0; i <= 31; i++) {
            String registerKey = "x" + i;
            registerMap.put(registerKey, Utility.ALLZEROS);
            aliasMap.put(registerKey, aliases[i]);
        }
    }

    // Get register string based on integer
    public String getRegisterString(int registerInt) {
        return "x" + registerInt;
    }

    // Set register value based on integer
    public void setRegisterValue(int registerInt, String value) {
        String registerKey = getRegisterString(registerInt);
        setRegisterValue(registerKey, value);
    }

    // Get register value based on integer
    public String getRegisterValue(int registerInt) {
        String registerKey = getRegisterString(registerInt);
        return getRegisterValue(registerKey);
    }

    public void setRegisterValue(String registerKey, String value) {
        if (registerMap.containsKey(registerKey) && !registerKey.equals("x0") && value.length() == 32) {
            registerMap.put(registerKey, value);
        }
    }

    public String getRegisterValue(String registerKey) {
        return registerMap.getOrDefault(registerKey, Utility.ALLZEROS);
    }

    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i <= 31; i++) {
            String key = "x" + i;
            String alias = aliasMap.get(key);
            returnString.append(key).append("(").append(alias).append("): ").append(registerMap.get(key)).append("\n");
        }
        return returnString.toString();
    }

    public String getProgramCounter() {
        return registerMap.get("pc");
    }

    public void setProgramCounter(int programCounter) {
        String localPcValue = Utility.StringCrement(Utility.ALLZEROS, programCounter);
        registerMap.put("pc", localPcValue);
    }

    public void setProgramCounter(String programCounter) {
        String localPcValue = programCounter;

        // If not 32 bits, padd left with 0s
        while (localPcValue.length() < 32) {
            localPcValue = "0" + localPcValue;
        }

        registerMap.put("pc", localPcValue);
    }
    
    public void incrementProgramCounter() {
        int currentProgramCounter = Integer.parseInt(registerMap.get("pc"), 2);
        currentProgramCounter += 4;

        // Convert back to binary string representation
        String newProgramCounterBinary = Integer.toBinaryString(currentProgramCounter);

        // Ensure newProgramCounterBinary is 32-bit length
        while (newProgramCounterBinary.length() < 32) {
            newProgramCounterBinary = "0" + newProgramCounterBinary;
        }

        //System.out.println("REGISTERS DEBUG: old pc: " + registerMap.get("pc"));
        //System.out.println("REGISTERS DEBUG: new pc: " + newProgramCounterBinary);

        // Update program counter to the new address
        registerMap.put("pc", newProgramCounterBinary);
    }
}
