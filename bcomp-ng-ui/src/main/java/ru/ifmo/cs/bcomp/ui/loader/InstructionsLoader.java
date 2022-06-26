package ru.ifmo.cs.bcomp.ui.loader;

import ru.ifmo.cs.bcomp.BasicComp;
import ru.ifmo.cs.bcomp.ProgramBinary;

import java.util.*;

public class InstructionsLoader {
    private final BasicComp bcomp;

    public InstructionsLoader(BasicComp bcomp) {
        this.bcomp = bcomp;
    }

    public void loadInstructions(List<String> lines) {
        final String startAddrPrefix = "START=";
        final int startAddr;

        String addrLine = lines.remove(0);
        if (addrLine.startsWith(startAddrPrefix)) {
            startAddr = toDec(removePrefix(startAddrPrefix, addrLine));
        } else {
            System.err.println("Start address for program is required!");
            return;
        }

        List<ProgSegment> progSegments = parseSegments(lines);
        loadInMemory(startAddr, progSegments);
    }


    private String removePrefix(final String prefix, final String prefixedString) {
        return prefixedString.replaceFirst("^" + prefix, "");
    }

    private int toDec(String hex) {
        return Integer.parseInt(hex, 16);
    }

    private List<ProgSegment> parseSegments(List<String> lines) {
        final String loadAddrPrefix = "LOAD=";
        final char commentPrefix = '#';

        final List<ProgSegment> progSegments = new ArrayList<>();
        ProgSegment currentSegment = null;
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.charAt(0) == commentPrefix) continue;
            if (line.startsWith(loadAddrPrefix)) {
                if (currentSegment != null) progSegments.add(currentSegment);
                int loadAddr = toDec(removePrefix(loadAddrPrefix, line));
                currentSegment = new ProgSegment(loadAddr);
                continue;
            }
            if (currentSegment == null) {
                System.err.println("Load address of a program is not initialized!");
                return Collections.emptyList();
            }
            currentSegment.addInstr(toDec(line));
        }
        progSegments.add(currentSegment);
        return progSegments;
    }

    private void loadInMemory(int startAddr, List<ProgSegment> segments) {
        for (ProgSegment segment : segments) {
            int loadAddr = segment.getLoadAddr();
            List<Integer> binary = segment.getProg();
            binary.add(0, startAddr);
            binary.add(0, loadAddr);
            ProgramBinary segmentProg = new ProgramBinary(binary);
            bcomp.loadProgram(segmentProg);
        }
    }
}