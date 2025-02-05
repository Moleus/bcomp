/*
 * $Id$
 */

package ru.ifmo.cs.bcomp.ui.components;

import java.awt.Adjustable;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JScrollBar;

import ru.ifmo.cs.components.Utils;
import static ru.ifmo.cs.bcomp.ui.components.DisplayStyles.*;
import ru.ifmo.cs.components.Memory;

/**
 *
 * @author Dmitry Afanasiev <KOT@MATPOCKuH.Ru>
 */
public class MemoryView extends BCompComponent {
	private Memory mem;
	private int addrBitWidth;
	private int valueBitWidth;
	private int lineX;
	private int lastPage = 0;
	// Components
	private JLabel[] addrs = new JLabel[16];
	private JLabel[] values = new JLabel[16];
    private JScrollBar scrollBar = new JScrollBar();

	public MemoryView(Memory mem, int x, int y) {
		super("RAM", 16);
		this.mem = mem;

		addrBitWidth = (int)mem.getAddrWidth() ;
		int addrWidth = FONT_COURIER_BOLD_21_WIDTH * (1 + Utils.getHexWidth(addrBitWidth));
		valueBitWidth =(int) mem.width;
		int valueWidth = FONT_COURIER_BOLD_21_WIDTH * (1 + Utils.getHexWidth(valueBitWidth));
        int scrollBarWidth = 15;
		lineX = 1 + addrWidth;

        setBounds(x, y, 3 + addrWidth + valueWidth + scrollBarWidth);

		for (int i = 0; i < 16; i++) {
			addrs[i] = addValueLabel(COLOR_TITLE);
			addrs[i].setBounds(1, getValueY(i), addrWidth, CELL_HEIGHT);
            addrs[i].setForeground(DisplayStyles.COLOR_TEXT);

			values[i] = addValueLabel(COLOR_VALUE);
			values[i].setBounds(lineX + 1, getValueY(i), valueWidth, CELL_HEIGHT);
            values[i].setForeground(DisplayStyles.COLOR_TEXT);
		}

        initScrollBar();
        add(scrollBar);
        scrollBar.addAdjustmentListener(adjustmentEvent -> updatePage(adjustmentEvent.getValue()));
        scrollBar.setBounds(lineX + valueWidth + 2, getValueY(0), scrollBarWidth, super.getHeight() - CELL_HEIGHT);
    }

    private void initScrollBar() {
        int pageSize = 0x10;
        int memSize = (1 << mem.getAddrWidth());
        scrollBar.setOrientation(Adjustable.VERTICAL);
        scrollBar.setValue(0);
        scrollBar.setVisibleAmount(pageSize);
        scrollBar.setMinimum(0);
        // Note: scrollBar's maximum value is memSize - extent, where extent (aka visible amount) = pageSize. See JavaDoc
        scrollBar.setMaximum(memSize);
    }

    private void updatePage(int newPage) {
        lastPage = newPage;
        updateMemory();
    }

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawLine(lineX, CELL_HEIGHT + 2, lineX, height - 2);
		g.drawLine(1, CELL_HEIGHT + 1, width - 2, CELL_HEIGHT + 1);
	}

	void updateValue(JLabel label, int value) {
		label.setText(Utils.toHex(value, valueBitWidth));
	}

	private void updateValue(int offset) {
		updateValue(values[offset],(int) mem.getValue(lastPage + offset));
	}

	public void updateMemory() {
        scrollBar.setValue(lastPage);
		for (int i = 0; i < 16; i++) {
			addrs[i].setText(Utils.toHex(lastPage + i, addrBitWidth));
			updateValue(i);
		}
	}

	private int getPage(int addr) {
		return addr & (~0xf);
	}

	private int getPage() {
		return getPage((int)mem.getAddrWidth());
	}

	public void updateLastAddr() {
		lastPage = getPage();
	}

	public void eventRead() {
		int addr = (int)mem.getLastAccessedAddress();
		int page = getPage(addr);

		if (page != lastPage) {
			lastPage = page;
			updateMemory();
		}
	}

	public void eventWrite() {
		int addr = (int)mem.getLastAccessedAddress();
		int page = getPage(addr);

		if (page != lastPage) {
			lastPage = page;
			updateMemory();
		} else
			updateValue(addr - page);
	}


}
