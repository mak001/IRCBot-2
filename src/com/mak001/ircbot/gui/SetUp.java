package com.mak001.ircbot.gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class SetUp extends JFrame {
	private JTextField[] textFields = new JTextField[5];
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;

	public SetUp() {

		setSize(300, 190);
		setResizable(false);

		setName("Set up");

		getContentPane().setLayout(null);

		JLabel lblIrcServer = new JLabel("IRC server:");
		lblIrcServer.setBounds(10, 11, 89, 14);
		getContentPane().add(lblIrcServer);

		JLabel lblIrcChat = new JLabel("IRC chat:");
		lblIrcChat.setBounds(10, 36, 89, 14);
		getContentPane().add(lblIrcChat);

		JLabel lblNickname = new JLabel("Nickname:");
		lblNickname.setBounds(10, 61, 89, 14);
		getContentPane().add(lblNickname);

		JLabel lblNickPass = new JLabel("Nick pass:");
		lblNickPass.setBounds(10, 86, 89, 14);
		getContentPane().add(lblNickPass);

		textField = new JTextField();
		textField.setBounds(109, 5, 169, 20);
		getContentPane().add(textField);
		textField.setColumns(10);
		textFields[0] = textField;

		textField_1 = new JTextField();
		textField_1.setBounds(109, 30, 169, 20);
		getContentPane().add(textField_1);
		textField_1.setColumns(10);
		textFields[1] = textField_1;

		textField_2 = new JTextField();
		textField_2.setBounds(109, 55, 169, 20);
		getContentPane().add(textField_2);
		textField_2.setColumns(10);
		textFields[2] = textField_2;

		textField_3 = new JTextField();
		textField_3.setBounds(109, 80, 169, 20);
		getContentPane().add(textField_3);
		textField_3.setColumns(10);
		textFields[3] = textField_3;

		textField_4 = new JTextField();
		textField_4.setBounds(109, 104, 169, 20);
		getContentPane().add(textField_4);
		textField_4.setColumns(10);
		textFields[4] = textField_4;

		JButton btnNewButton = new JButton("Confirm");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!allFilledIn()) {
					JOptionPane.showMessageDialog(null,
							"Please fill in all the fields to continue.");
				} else {
					setVisible(false);
				}
			}
		});
		btnNewButton.setBounds(10, 132, 268, 23);
		getContentPane().add(btnNewButton);

		JLabel lblCommandPrefix = new JLabel("Command prefix:");
		lblCommandPrefix.setBounds(10, 107, 89, 14);
		getContentPane().add(lblCommandPrefix);
	}

	private boolean allFilledIn() {
		for (JTextField j : textFields) {
			if (j.getText() == null && j.getText().isEmpty())
				return false;
		}
		return true;
	}

	public JTextField[] getJTextFields() {
		return textFields;
	}
}
