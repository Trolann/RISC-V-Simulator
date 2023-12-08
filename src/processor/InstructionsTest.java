package processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InstructionsTest {

    private Memory testMemory;
    private Registers testRegisters;
    private Instructions testInstructions;

    @BeforeEach
    void setUp() {
        testMemory = new Memory();
        testRegisters = new Registers();
        testInstructions = new Instructions(testMemory, testRegisters);
    }

    @Test
    void SLT() {
        // Set up initial values
        String binaryValueX1 = "00000000000000000000000000001101";  // 13 in binary
        String binaryValueX2 = "00000000000000000000000000001010";  // 10 in binary

        testRegisters.setRegisterValue("x1", binaryValueX1);
        testRegisters.setRegisterValue("x2", binaryValueX2);

        // Execute SLT instruction
        HashMap<String, String> instructionMap = new HashMap<>();
        instructionMap.put("rs1", "x1");
        instructionMap.put("rs2", "x2");
        instructionMap.put("rd", "x3");
        testInstructions.SLT(instructionMap);

        // Check results
        String result = testRegisters.getRegisterValue("x3");
        assertEquals("00000000000000000000000000000000", result); // Expected 0 since x1 > x2

        testRegisters.setRegisterValue("x1", binaryValueX2);
        testRegisters.setRegisterValue("x2", binaryValueX1);
        testInstructions.SLT(instructionMap);
        result = testRegisters.getRegisterValue("x3");
        assertEquals("00000000000000000000000000000001", result); // Expected 1 since x1 < x2
    }

    @Test
    void SLTU() {
        // Set up initial values
        String binaryValueX1 = "00000000000000000000000000001101";  // 13 in binary
        String binaryValueX2 = "00000000000000000000000000001010";  // 10 in binary

        testRegisters.setRegisterValue("x1", binaryValueX1);
        testRegisters.setRegisterValue("x2", binaryValueX2);

        // Execute SLTU instruction
        HashMap<String, String> instructionMap = new HashMap<>();
        instructionMap.put("rs1", "x1");
        instructionMap.put("rs2", "x2");
        instructionMap.put("rd", "x3");
        testInstructions.SLTU(instructionMap);

        // Check results
        String result = testRegisters.getRegisterValue("x3");
        assertEquals("00000000000000000000000000000000", result); // Expected 0 since x1 > x2

        testRegisters.setRegisterValue("x1", binaryValueX2);
        testRegisters.setRegisterValue("x2", binaryValueX1);
        testInstructions.SLTU(instructionMap);
        result = testRegisters.getRegisterValue("x3");
        assertEquals("00000000000000000000000000000001", result); // Expected 1 since x1 < x2
    }

}
