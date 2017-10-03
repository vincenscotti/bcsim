package com.bcsim.gui;

import com.bcsim.core.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainForm extends JFrame {
    private JPanel mainPanel;
    private JPanel centerPanel;
    private JButton addNodeButton;
    private JButton addLinkButton;
    private JSpinner linkRangeSpinner;
    private JSpinner minerTimeoutSpinner;
    private JSpinner minerRangeSpinner;
    private JSpinner linkTimeoutSpinner;

    private KeyPairGenerator keygen;

    private final java.util.List<Node> nodes;
    private final java.util.List<Link> links;

    private final int minerTimeout = 1000;
    private final int minerRange = 1000;
    private final int linkTimeout = 10;
    private final int linkRange = 10;

    public MainForm() {
        super("BCSim");

        nodes = new ArrayList<Node>();
        links = new ArrayList<Link>();

        try {
            keygen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }
        keygen.initialize(512);

        DefaultNode n1 = buildNewNode("N1");
        DefaultNode n2 = buildNewNode("N2");
        Link l = buildNewLink();
        l.addPeer(n1);
        l.addPeer(n2);
        n1.addLink(l);
        n2.addLink(l);

        nodes.add(n1);
        nodes.add(n2);
        links.add(l);

        GridLayout mainLayout = new GridLayout(1, 0);

        centerPanel.setLayout(mainLayout);
        NodeForm f1 = new NodeForm(n1);
        centerPanel.add(f1.getNodePanel());
        NodeForm f2 = new NodeForm(n2);
        centerPanel.add(f2.getNodePanel());

        n1.setLogger(f1);
        n2.setLogger(f2);

        addNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String name = JOptionPane.showInputDialog("Node name");

                if (name != null) {
                    if (name.equals("")) {
                        JOptionPane.showMessageDialog(MainForm.this, "Empty name not allowed", "Empty name", JOptionPane.ERROR_MESSAGE);
                    } else {
                        final DefaultNode n = buildNewNode(name);
                        nodes.add(n);

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                NodeForm f = new NodeForm(n);
                                n.setLogger(f);
                                centerPanel.add(f.getNodePanel());
                                centerPanel.updateUI();
                            }
                        });
                    }
                }
            }
        });

        addLinkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (nodes.size() == 0) {
                    JOptionPane.showMessageDialog(MainForm.this, "Add nodes first", "No nodes", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Node source = (Node) JOptionPane.showInputDialog(MainForm.this, "Source node", "Source selection", JOptionPane.QUESTION_MESSAGE, null, nodes.toArray(), nodes.get(0));

                if (source != null) {
                    Node dest = (Node) JOptionPane.showInputDialog(MainForm.this, "Destination node", "Destination selection", JOptionPane.QUESTION_MESSAGE, null, nodes.toArray(), nodes.get(0));

                    if (dest != null) {
                        if (source == dest) {
                            JOptionPane.showMessageDialog(MainForm.this, "Source and destination cannot be the same", "Invalid source or destination", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        Link l = buildNewLink();
                        l.addPeer(source);
                        l.addPeer(dest);
                        source.addLink(l);
                        dest.addLink(l);

                        links.add(l);
                    }
                }
            }
        });

        SpinnerNumberModel lt = new SpinnerNumberModel();
        lt.setMinimum(0);
        lt.setMaximum(99999);
        lt.setValue(linkTimeout);
        linkTimeoutSpinner.setModel(lt);

        SpinnerNumberModel lr = new SpinnerNumberModel();
        lr.setMinimum(0);
        lr.setMaximum(99999);
        lr.setValue(linkRange);
        linkRangeSpinner.setModel(lr);

        SpinnerNumberModel mt = new SpinnerNumberModel();
        mt.setMinimum(0);
        mt.setMaximum(99999);
        mt.setValue(minerTimeout);
        minerTimeoutSpinner.setModel(mt);

        SpinnerNumberModel mr = new SpinnerNumberModel();
        mr.setMinimum(0);
        mr.setMaximum(99999);
        mr.setValue(minerRange);
        minerRangeSpinner.setModel(mr);

        linkTimeoutSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                Integer timeout = (Integer) linkTimeoutSpinner.getValue();

                for (Link l : links) {
                    DefaultLink dl = (DefaultLink) l;

                    dl.setTimeout(timeout);
                }
            }
        });

        linkRangeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                Integer range = (Integer) linkRangeSpinner.getValue();

                for (Link l : links) {
                    DefaultLink dl = (DefaultLink) l;

                    dl.setRange(range);
                }
            }
        });

        minerTimeoutSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                Integer timeout = (Integer) minerTimeoutSpinner.getValue();

                for (Node n : nodes) {
                    DefaultNode dn = (DefaultNode) n;
                    DefaultMiner dm = (DefaultMiner) dn.getMiner();

                    dm.setTimeout(timeout);
                }
            }
        });

        minerRangeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                Integer range = (Integer) minerRangeSpinner.getValue();

                for (Node n : nodes) {
                    DefaultNode dn = (DefaultNode) n;
                    DefaultMiner dm = (DefaultMiner) dn.getMiner();

                    dm.setRange(range);
                }
            }
        });

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        pack();
        setVisible(true);
    }

    private DefaultMiner buildNewMiner() {
        return new DefaultMiner(minerTimeout, minerRange);
    }

    private DefaultNode buildNewNode(String name) {
        DefaultMiner m = buildNewMiner();
        DefaultNode n = new DefaultNode(keygen.generateKeyPair(), name, m);
        m.setCallback(n);

        return n;
    }

    private Link buildNewLink() {
        return new DefaultLink(linkTimeout, linkRange);
    }
}
