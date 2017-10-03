package com.bcsim.gui;

import com.bcsim.core.Block;
import com.bcsim.core.NodeUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ChainForm {
    private JPanel chainPanel;
    private JList blockList;
    private JTextField hashTextField;
    private JTextField previousTextField;
    private JTextField publicKeyTextField;
    private JTextField dataTextField;
    private JTextField signatureTextField;

    public ChainForm() {
        DefaultListModel emptyModel = new DefaultListModel();
        emptyModel.addElement("Empty chain");

        blockList.setModel(emptyModel);
    }

    public ChainForm(Block head) {
        DefaultListModel emptyModel = new DefaultListModel();

        while (head != null) {
            emptyModel.addElement(head);
            head = head.getPreviousBlock();
        }

        blockList.setModel(emptyModel);

        blockList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    Block selectedBlock = (Block) blockList.getModel().getElementAt(blockList.getSelectedIndex());

                    hashTextField.setText(NodeUtils.bytesToHex(selectedBlock.getHash()));
                    hashTextField.setCaretPosition(0);
                    previousTextField.setText(NodeUtils.bytesToHex(selectedBlock.getPrevious()));
                    previousTextField.setCaretPosition(0);
                    publicKeyTextField.setText(NodeUtils.bytesToHex(NodeUtils.calculateHash(selectedBlock.getPublicKey().getEncoded())));
                    publicKeyTextField.setCaretPosition(0);
                    dataTextField.setText(selectedBlock.getData().toString());
                    dataTextField.setCaretPosition(0);
                    signatureTextField.setText(NodeUtils.bytesToHex(selectedBlock.getSignature()));
                    signatureTextField.setCaretPosition(0);
                }
            }
        });
    }

    public JPanel getChainPanel() {
        return chainPanel;
    }

    public JList getBlockList() {
        return blockList;
    }
}
