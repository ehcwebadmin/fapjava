package org.emoryhealthcare.physiciandirectory;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PhotoLoader extends JPanel implements ActionListener {
	/*
	 *
	 */

	private static final long serialVersionUID = 1L;
	private static final String newline = "\n";

	private File photoFolder;

	private JFrame controllingFrame; // needed for dialogs
	private JComboBox siteSelect;
	private JPasswordField passwordField;
	private JTextField userField;
	private JTextField photoFolderField;
	private JTextArea log;
	private JFileChooser fcPhotoFolder;
	private JButton runButton;
	private JButton photoFolderButton;
	private JButton helpButton;

	public PhotoLoader(JFrame f) {
		// Use the default FlowLayout.
		controllingFrame = f;

		// Create everything.

		// Site selector
		java.lang.String[] sites = {Constants.SITE_SELECTION_EHC, Constants.SITE_SELECTION_EJCH, Constants.SITE_SELECTION_SJHA};
		siteSelect = new JComboBox(sites);
		siteSelect.setSelectedIndex(0);
		siteSelect.addActionListener(this);
		JLabel siteLabel = new JLabel("Select site: ");
		siteLabel.setLabelFor(siteSelect);

		// User name
		userField = new JTextField(10);
		JLabel userLabel = new JLabel("Cascade user name: ");
		userLabel.setLabelFor(userField);

		// Password
		passwordField = new JPasswordField(10);
		JLabel passwordLabel = new JLabel("Cascade password: ");
		passwordLabel.setLabelFor(passwordField);

		// Photo folder
		photoFolderButton = new JButton("Choose Photo Folder");
		Dimension buttonSize = photoFolderButton.getPreferredSize();
		photoFolderButton.addActionListener(this);
		photoFolderField = new JTextField(10);
		fcPhotoFolder = new JFileChooser();
		fcPhotoFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		//Help and Run buttons
		runButton = new JButton("Run");
		runButton.addActionListener(this);

		helpButton = new JButton("Help");
		helpButton.setPreferredSize(buttonSize);
		helpButton.addActionListener(this);

		//Results log
		log = new JTextArea(24, 80);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);

		// Lay out everything.

		JPanel north = new JPanel();
		GridLayout northLayout = new GridLayout(1,0);
		north.setLayout(northLayout);

		JPanel inputsTopWest = new JPanel();
		GridLayout westLayout = new GridLayout(4,0);
		inputsTopWest.setLayout(westLayout);
		inputsTopWest.add(siteLabel);
		inputsTopWest.add(userLabel);
		inputsTopWest.add(passwordLabel);
		inputsTopWest.add(photoFolderButton);

		JPanel inputsTopCenter = new JPanel();
		GridLayout centerLayout = new GridLayout(4,0);
		inputsTopCenter.setLayout(centerLayout);
		inputsTopCenter.add(siteSelect);
		inputsTopCenter.add(userField);
		inputsTopCenter.add(passwordField);
		inputsTopCenter.add(photoFolderField);

		JPanel inputsTop = new JPanel();
		BorderLayout topLayout = new BorderLayout(5,5);
		inputsTop.setLayout(topLayout);
		inputsTop.add(inputsTopWest,BorderLayout.WEST);
		inputsTop.add(inputsTopCenter,BorderLayout.CENTER);

		JPanel inputsBottomWest = new JPanel();
		GridLayout bottomWestLayout = new GridLayout(3,0);
		inputsBottomWest.setLayout(bottomWestLayout);
		inputsBottomWest.add(helpButton);

		JPanel inputsBottomCenter = new JPanel();
		GridLayout bottomCenterLayout = new GridLayout(3,0);
		inputsBottomCenter.setLayout(bottomCenterLayout);
		inputsBottomCenter.add(runButton);

		JPanel inputsBottom = new JPanel();
		BorderLayout bottomLayout = new BorderLayout(5,5);
		inputsBottom.setLayout(bottomLayout);
		inputsBottom.add(inputsBottomWest,BorderLayout.WEST);
		inputsBottom.add(inputsBottomCenter,BorderLayout.CENTER);

		JPanel inputs = new JPanel();
		GridLayout inputsLayout = new GridLayout(2,0);
		inputs.setLayout(inputsLayout);
		inputs.add(inputsTop);
		inputs.add(inputsBottom);

		north.add(inputs);

		BorderLayout frameLayout = new BorderLayout(5, 5);
		setLayout(frameLayout);
		add(north, BorderLayout.NORTH);
		add(logScrollPane, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == runButton) {
			if (null == PhotoLoader.this.photoFolder) {
				log.append("Please select a folder to load photos from.");
			} else {
				log.append("Loading photos from: "
						+ PhotoLoader.this.photoFolder.getAbsolutePath() + "."
						+ newline);
				PhysicianDirectory directory = new PhysicianDirectory(log);
				try {

					java.lang.String dusername = new java.lang.String(userField.getText());
					java.lang.String pword = new java.lang.String(passwordField.getPassword());
					/*For Debugging */
					if(dusername.trim() == "")
					{
						pword = "train1";
					}
					if(pword.trim() == "")
					{
						pword = "internet";
					}
					directory.setUsername(dusername);
					directory.setPassword(pword);
					directory.setSite((java.lang.String)siteSelect.getSelectedItem());
					log.append("Working with " + directory.site + newline  + newline);
					directory.loadPhotos(photoFolder);
					/* do your stuff here */
				} catch (Exception e1) {
					log.append(e1.getMessage());
				}
				log.append("Photo loading completed."+newline);
				log.setCaretPosition(log.getDocument().getLength());
			}

		} else if (e.getSource() == photoFolderButton) {
			int returnVal = fcPhotoFolder.showOpenDialog(PhotoLoader.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				PhotoLoader.this.photoFolder = fcPhotoFolder.getSelectedFile();
				PhotoLoader.this.photoFolderField.setText(PhotoLoader.this.photoFolder.getName());
				log.append("Selected photo folder: "
						+ PhotoLoader.this.photoFolder.getAbsolutePath() + "."
						+ newline);
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());
		} else if (e.getSource() == helpButton) {
			JOptionPane
					.showMessageDialog(
							controllingFrame,
							"Please enter your Cascade username and password in their respective fields.\n\n"
									+ "(NOTE: you must be authorized to create physician profiles in Cascade.)\n\n"
									+ "Choose a folder containing properly-named photos to load.\n\n"
									+ "Then press 'Run' to load photos and link them to physician profiles.\n\n"
									+ "Report any problems to webadmin@emoryhealthcare.org.");
		} else if (e.getSource() == siteSelect) {
			java.lang.String siteName = (java.lang.String)siteSelect.getSelectedItem();
			log.append("Selected site to load onto is " + siteName +"." + newline);
		} else {
			// ignore it, just a checkbox being checked
		}
	}

	// Must be called from the event dispatch thread.
	protected void resetFocus() {
		passwordField.requestFocusInWindow();
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("PhotoLoader");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		final PhotoLoader newContentPane = new PhotoLoader(frame);
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Make sure the focus goes to the right component
		// whenever the frame is initially given the focus.
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				newContentPane.resetFocus();
			}
		});

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
	}
}