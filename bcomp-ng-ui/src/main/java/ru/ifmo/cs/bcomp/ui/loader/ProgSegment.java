package ru.ifmo.cs.bcomp.ui.loader;

import java.util.ArrayList;
import java.util.List;

class ProgSegment {
    private final int loadAddr;
    private final List<Integer> instructions = new ArrayList<>();

    public ProgSegment(int loadAddr) {
        this.loadAddr = loadAddr;
    }

    public void addInstr(Integer instruction) {
        instructions.add(instruction);
    }

    public List<Integer> getProg() {
        return new ArrayList<>(instructions);
    }

    public int getLoadAddr() {
        return loadAddr;
    }
}
