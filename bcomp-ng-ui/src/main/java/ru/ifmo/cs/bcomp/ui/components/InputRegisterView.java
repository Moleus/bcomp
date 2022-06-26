/*
 * $Id$
 */

package ru.ifmo.cs.bcomp.ui.components;

import java.awt.event.*;
import java.util.function.Predicate;

import ru.ifmo.cs.components.Register;
import ru.ifmo.cs.components.Utils;
import static ru.ifmo.cs.bcomp.ui.components.DisplayStyles.*;

/**
 *
 * @author Dmitry Afanasiev <KOT@MATPOCKuH.Ru>
 */
public class InputRegisterView extends RegisterView {
	private final ComponentManager cmanager;
	private final Register reg;
	private final ActiveBitView activeBitView;
	private boolean active = false;
	private final int regWidth;
	private int bitno;
	private final int formattedWidth;

	public InputRegisterView(ComponentManager cmgr, Register reg) {
		super(reg, COLOR_TITLE);

		this.cmanager = cmgr;
		this.reg = reg;
		activeBitView = cmanager.getActiveBit();

        regWidth = Utils.getHexWidth((int)reg.width);
        bitno = regWidth - 1;
		formattedWidth = regWidth;

		value.setFocusable(true);
		value.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				active = true;
				setActiveBit(bitno);
			}

			@Override
			public void focusLost(FocusEvent e) {
				active = false;
				setValue();
			}
		});

		value.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
                Predicate<Integer> isNumber = (Integer keyCode) -> keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9;
                Predicate<Integer> isNumPad = (Integer keyCode) -> keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9;
                Predicate<Integer> isHexLetter = (Integer keyCode) -> keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_F;
                int code = e.getKeyCode();

                if (isNumber.test(code)) {
                   setBit(code - 48);
                   return;
                } else if (isNumPad.test(code)) {
                    setBit(code - 96);
                    return;
                } else if (isHexLetter.test(code)) {
                    setBit(code - 55);
                    return;
                }

                switch (e.getKeyCode()) {
					case KeyEvent.VK_LEFT:
					case KeyEvent.VK_BACK_SPACE:
						moveLeft();
						break;

					case KeyEvent.VK_RIGHT:
						moveRight();
						break;
					default:
						cmanager.keyPressed(e);
				}
			}
		});
	}

	private void setActiveBit(int bitno) {
		activeBitView.setValue(this.bitno = bitno);
		setValue();
	}

	private void moveLeft() {
		setActiveBit((bitno + 1) % regWidth);
	}

	private void moveRight() {
		setActiveBit((bitno == 0 ? regWidth : bitno) - 1);
	}

	private void invertBit() {
		reg.invertBit(bitno);
		setValue();
	}

	private void setBit(int value) {
		reg.setValue(value,0xF,bitno * 4L);
		moveRight();
	}

	@Override
	public void setValue() {
        String hexValue = String.format("%04X", this.reg.getValue());
		if (active) {
			StringBuilder str = new StringBuilder(HTML + hexValue + HTML_END);
			int pos = 6 + formattedWidth - Utils.getBinaryWidth(bitno + 1);
			str.insert(pos + 1, COLOR_END);
			str.insert(pos, COLOR_ACTIVE_BIT);
			setValue(str.toString());
		} else
			super.setValue(HTML + hexValue + HTML_END);
	}

	public void reqFocus() {
		try {
			value.requestFocus();
		} catch (Exception e) { }
		
		value.requestFocusInWindow();
	}

	public void setActive() {
		reqFocus();
		active = true;
		setActiveBit(bitno);
	}
}
