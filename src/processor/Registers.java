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

    public Registers(Registers oldRegisters) {
        this.registerMap = new HashMap<>();
        this.aliasMap = new HashMap<>(oldRegisters.aliasMap);
        this.registerMap.putAll(oldRegisters.registerMap);
    }

    private void initializeRegistersAndAliases() {
        String[] aliases = {
                "x0", // x0 (zero)
                "x1", // x1 (ra) Return address
                "x2", // x2 (sp) Stack pointer
                "x3", // x3 (gp) Global pointer
                "x4", // x4 (tp) Thread pointer
                "t0", // x5 (t0) Temporary
                "t1", // x6 (t1) Temporary
                "t2", // x7 (t2) Temporary
                "x8", // x8 (s0/fp) Saved register/frame pointer
                "x9", // x9 (s1) Saved register
                "x10", // x10 (a0) Function argument/return value
                "x11", // x11 (a1) Function argument/return value
                "x12", // x12 (a2) Function argument
                "x13", // x13 (a3) Function argument
                "x14", // x14 (a4) Function argument
                "x15", // x15 (a5) Function argument
                "x16", // x16 (a6) Function argument
                "x17", // x17 (a7) Function argument
                "x18", // x18 (s2) Saved register
                "x19", // x19 (s3) Saved register
                "x20", // x20 (s4) Saved register
                "x21", // x21 (s5) Saved register
                "x22", // x22 (s6) Saved register
                "x23", // x23 (s7) Saved register
                "x24", // x24 (s8) Saved register
                "x25", // x25 (s9) Saved register
                "x26", // x26 (s10) Saved register
                "x27", // x27 (s11) Saved register
                "t3", // x28 (t3) Temporary
                "t4", // x29 (t4) Temporary
                "t5", // x30 (t5) Temporary
                "t6" // x31 (t6) Temporary
        };
        for (int i = 0; i <= 31; i++) {
            String registerKey = "x" + i;
            registerMap.put(registerKey, Utility.ALLZEROS);
            aliasMap.put(registerKey, aliases[i]);
        }
    }

    public String xToT(String xRegister) {
        Map<String, String> xToTMap = new HashMap<>();
        xToTMap.put("x5", "t0");
        xToTMap.put("x6", "t1");
        xToTMap.put("x7", "t2");
        xToTMap.put("x28", "t3");
        xToTMap.put("x29", "t4");
        xToTMap.put("x30", "t5");
        xToTMap.put("x31", "t6");
        return xToTMap.getOrDefault(xRegister, xRegister);
    }

    public String tToX(String tRegister) {
        Map<String, String> tToXMap = new HashMap<>();
        tToXMap.put("t0", "x5");
        tToXMap.put("t1", "x6");
        tToXMap.put("t2", "x7");
        tToXMap.put("t3", "x28");
        tToXMap.put("t4", "x29");
        tToXMap.put("t5", "x30");
        tToXMap.put("t6", "x31");
        return tToXMap.getOrDefault(tRegister, tRegister);
    }

    // Get register string based on integer
    public String getRegisterString(int registerInt) {
        return "x" + registerInt;
    }

    public void setRegisterValue(String registerKey, String value) {
        if (registerMap.containsKey(registerKey) && !registerKey.equals("x0") && value.length() == 32) {
            registerMap.put(registerKey, value);
            System.out.print("REGISTER DEBUG: Set " + registerKey + "(" + xToT(registerKey) +") to " + value);
            int valueAsInt = 0;
            try {
                valueAsInt = (int) Long.parseUnsignedLong(value, 2);
                System.out.println(" (" + valueAsInt + ")");
            } catch (NumberFormatException e) {
                System.out.println(" (UnK)");
            }

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
            if (registerMap.get(key).equals(Utility.ALLZEROS)) {
                continue;
            }
            returnString.append(key).append("(").append(alias).append("): ").append(registerMap.get(key)).append("\n");
        }
        return returnString.toString();
    }

    public String getProgramCounter() {
        return registerMap.get("pc");
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
