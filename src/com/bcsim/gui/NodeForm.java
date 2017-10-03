package com.bcsim.gui;

import com.bcsim.core.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class NodeForm implements NodeLogger {
    private JPanel nodePanel;
    private JLabel nameLabel;
    private JTextField messageTextField;
    private JButton sendButton;
    private JTextField publicKeyTextField;
    private JTabbedPane blockchainTabbedPane;
    private JTextArea logTextArea;
    private JComboBox neighboursComboBox;
    private JCheckBox broadcastMineRequestCheckBox;
    private JCheckBox customPreviousCheckBox;
    private final DefaultNode node;

    private List<ChainForm> chainForms;

    public NodeForm(final DefaultNode node) {
        this.node = node;
        this.node.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                updateInfo();
            }
        });

        messageTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                process();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                process();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                process();
            }

            private void process() {
                if (messageTextField.getText().length() == 0) {
                    sendButton.setEnabled(false);
                } else {
                    sendButton.setEnabled(true);
                }
            }
        });

        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!messageTextField.getText().equals("")) {
                    BlockData bd = new BlockData(messageTextField.getText(), new Date());

                    if (!customPreviousCheckBox.isSelected()) {
                        NodeForm.this.node.emit(bd, broadcastMineRequestCheckBox.isSelected());
                    } else {
                        ChainForm f = chainForms.get(blockchainTabbedPane.getSelectedIndex());

                        JList l = f.getBlockList();

                        if (l.isSelectionEmpty()) {
                            JOptionPane.showMessageDialog(NodeForm.this.nodePanel, "No block selected!", "No block selected", JOptionPane.ERROR_MESSAGE);
                        } else {
                            Block b = (Block) l.getModel().getElementAt(l.getSelectedIndex());
                            NodeForm.this.node.emit(bd, b.getHash(), broadcastMineRequestCheckBox.isSelected());
                        }
                    }

                    messageTextField.setText("");
                }
            }
        };

        sendButton.addActionListener(sendAction);
        messageTextField.addActionListener(sendAction);

        updateInfo();
    }

    public JPanel getNodePanel() {
        return nodePanel;
    }

    private void updateInfo() {
        nameLabel.setText(node.getName());
        publicKeyTextField.setText(NodeUtils.bytesToHex(NodeUtils.calculateHash(node.getPublicKey().getEncoded())));
        publicKeyTextField.setCaretPosition(0);

        DefaultComboBoxModel model = new DefaultComboBoxModel();

        for (Link l : node.getLinks()) {
            for (Node n : l.getPeers()) {
                if (n != node) {
                    model.addElement(n);
                }
            }
        }

        neighboursComboBox.setModel(model);

        int counter = 0;

        blockchainTabbedPane.removeAll();

        chainForms = new ArrayList<ChainForm>();
        ChainForm mainForm = new ChainForm(node.getBlockchain().getMainHead());
        blockchainTabbedPane.addTab("Main chain", mainForm.getChainPanel());
        chainForms.add(mainForm);

        for (Block h : node.getBlockchain().getHeads()) {
            if (h == node.getBlockchain().getMainHead()) {
                continue;
            }

            counter++;
            ChainForm f = new ChainForm(h);
            blockchainTabbedPane.addTab("Side chain " + counter, f.getChainPanel());
            chainForms.add(f);
        }
    }

    @Override
    public void println(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                logTextArea.setText(logTextArea.getText().concat(msg + "\n"));
            }
        });
    }
}
