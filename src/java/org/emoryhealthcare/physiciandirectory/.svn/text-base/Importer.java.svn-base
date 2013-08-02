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

/* Importer.java requires no other files. */

public class Importer extends JPanel implements ActionListener {
	/*
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private static final String newline = "\n";

	private File exportFile;
	private File translateFile;
	private File NPIFile;
	
	private JFrame controllingFrame; // needed for dialogs
	private JComboBox siteSelect;
	private JPasswordField passwordField;
	private JTextField userField;
	private JTextField exportFileField;
	private JTextField translateFileField;
	private JTextField NPIFileField;
	private JCheckBox selectSpecialties;
	private JCheckBox selectAOI;
	private JCheckBox selectPracticeAffiliation;
	private JCheckBox selectAddresses;
	private JCheckBox selectHospitalAffiliations;
	private JCheckBox selectAcademicTitles;
	private JCheckBox selectEducation;
	private JCheckBox selectHonorifics;
	private JCheckBox selectLanguages;
	private JCheckBox selectYearBegan;
	private JCheckBox selectYearBeganEmory;
	private JCheckBox selectNPI;
	private JTextArea log;
	private JFileChooser fcExport;
	private JFileChooser fcTranslate;
	private JFileChooser fcNPI;
	private JButton runButton;
	private JButton exportFileButton;
	private JButton translateFileButton;
	private JButton NPIFileButton;
	private JButton helpButton;
	private JButton selectAllButton;
	private JButton selectNoneButton;

	public Importer(JFrame f) {
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

		// Checkboxes to control what fields are imported
		JLabel selectedFieldsLabel = new JLabel("Update selected fields only: ");

		selectSpecialties = new JCheckBox();
		selectSpecialties.setSelected(true);
		selectSpecialties.addActionListener(this);
		JLabel selectSpecsLabel = new JLabel("Specialties:");
		selectSpecsLabel.setLabelFor(selectSpecialties);
		
		selectAOI = new JCheckBox();
		selectAOI.setSelected(true);
		selectAOI.addActionListener(this);
		JLabel selectAOILabel = new JLabel("Areas of Interest:");
		selectAOILabel.setLabelFor(selectAOI);

		selectPracticeAffiliation = new JCheckBox();
		selectPracticeAffiliation.setSelected(true);
		selectPracticeAffiliation.addActionListener(this);
		JLabel selectPracticeAffiliationLabel = new JLabel("Practice Affiliation:");
		selectPracticeAffiliationLabel.setLabelFor(selectPracticeAffiliation);

		selectAddresses = new JCheckBox();
		selectAddresses.setSelected(true);
		selectAddresses.addActionListener(this);
		JLabel selectAddressesLabel = new JLabel("Addresses:");
		selectAddressesLabel.setLabelFor(selectAddresses);

		selectHospitalAffiliations = new JCheckBox();
		selectHospitalAffiliations.setSelected(true);
		selectHospitalAffiliations.addActionListener(this);
		JLabel selectHospitalAffiliationsLabel = new JLabel("Hospital Affiliations:");
		selectHospitalAffiliationsLabel.setLabelFor(selectHospitalAffiliations);

		selectAcademicTitles = new JCheckBox();
		selectAcademicTitles.setSelected(true);
		selectAcademicTitles.addActionListener(this);
		JLabel selectAcademicTitlesLabel = new JLabel("Academic Titles:");
		selectAcademicTitlesLabel.setLabelFor(selectAcademicTitles);

		selectEducation = new JCheckBox();
		selectEducation.setSelected(true);
		selectEducation.addActionListener(this);
		JLabel selectEducationLabel = new JLabel("Education:");
		selectEducationLabel.setLabelFor(selectEducation);

		selectHonorifics = new JCheckBox();
		selectHonorifics.setSelected(true);
		selectHonorifics.addActionListener(this);
		JLabel selectHonorificsLabel = new JLabel("Honorifics:");
		selectHonorificsLabel.setLabelFor(selectHonorifics);

		selectLanguages = new JCheckBox();
		selectLanguages.setSelected(true);
		selectLanguages.addActionListener(this);
		JLabel selectLanguagesLabel = new JLabel("Languages:");
		selectLanguagesLabel.setLabelFor(selectLanguages);

		selectYearBegan = new JCheckBox();
		selectYearBegan.setSelected(true);
		selectYearBegan.addActionListener(this);
		JLabel selectYearBeganLabel = new JLabel("Year Began Practicing:");
		selectYearBeganLabel.setLabelFor(selectYearBegan);

		selectYearBeganEmory = new JCheckBox();
		selectYearBeganEmory.setSelected(true);
		selectYearBeganEmory.addActionListener(this);
		JLabel selectYearBeganEmoryLabel = new JLabel("Year Began Emory:");
		selectYearBeganEmoryLabel.setLabelFor(selectYearBeganEmory);

		selectNPI = new JCheckBox();
		selectNPI.setSelected(true);
		selectNPI.addActionListener(this);
		JLabel selectNPILabel = new JLabel("NPI:");
		selectNPILabel.setLabelFor(selectNPI);

		// Translations file
		translateFileButton = new JButton("Choose Translations File");
		Dimension buttonSize = translateFileButton.getPreferredSize();
		translateFileButton.addActionListener(this);
		fcTranslate = new JFileChooser();
		translateFileField = new JTextField(10);

		// Export file
		exportFileButton = new JButton("Choose Export File");
		exportFileButton.setPreferredSize(buttonSize);
		exportFileButton.addActionListener(this);
		exportFileField = new JTextField(10);
		fcExport = new JFileChooser();
		
		//NPI lookup file
		NPIFileButton = new JButton("Choose NPI File");
		NPIFileButton.setPreferredSize(buttonSize);
		NPIFileButton.addActionListener(this);
		fcNPI = new JFileChooser();
		NPIFileField = new JTextField(10);

		//Help and Run buttons
		runButton = new JButton("Run");
		runButton.addActionListener(this);

		helpButton = new JButton("Help");
		helpButton.addActionListener(this);
		
		//Select all and Select none buttons;
		selectAllButton = new JButton("Select All");
		selectAllButton.setPreferredSize(buttonSize);
		selectAllButton.addActionListener(this);
		selectNoneButton = new JButton("Select None");
		selectNoneButton.setPreferredSize(buttonSize);
		selectNoneButton.addActionListener(this);


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
		inputsTopWest.add(exportFileButton);

		JPanel inputsTopCenter = new JPanel();
		GridLayout centerLayout = new GridLayout(4,0);
		inputsTopCenter.setLayout(centerLayout);
		inputsTopCenter.add(siteSelect);
		inputsTopCenter.add(userField);
		inputsTopCenter.add(passwordField);
		inputsTopCenter.add(exportFileField);
		
		JPanel inputsTop = new JPanel();
		BorderLayout topLayout = new BorderLayout(5,5);
		inputsTop.setLayout(topLayout);
		inputsTop.add(inputsTopWest,BorderLayout.WEST);
		inputsTop.add(inputsTopCenter,BorderLayout.CENTER);

		JPanel inputsSelectFieldsLabels = new JPanel();
		GridLayout inputsSelectFieldsLabelsLayout = new GridLayout(3,0);
		inputsSelectFieldsLabels.setLayout(inputsSelectFieldsLabelsLayout);
		inputsSelectFieldsLabels.add(selectedFieldsLabel);
		inputsSelectFieldsLabels.add(selectAllButton);
		inputsSelectFieldsLabels.add(selectNoneButton);		
		
		JPanel inputsSelectFields = new JPanel();
		GridLayout selectFieldsLayout = new GridLayout(0,8);
		inputsSelectFields.setLayout(selectFieldsLayout);
				
		inputsSelectFields.add(selectSpecsLabel);
		inputsSelectFields.add(selectSpecialties);
		inputsSelectFields.add(selectEducationLabel);
		inputsSelectFields.add(selectEducation);

		inputsSelectFields.add(selectAOILabel);
		inputsSelectFields.add(selectAOI);
		inputsSelectFields.add(selectHonorificsLabel);
		inputsSelectFields.add(selectHonorifics);

		inputsSelectFields.add(selectPracticeAffiliationLabel);
		inputsSelectFields.add(selectPracticeAffiliation);
		inputsSelectFields.add(selectLanguagesLabel);
		inputsSelectFields.add(selectLanguages);

		inputsSelectFields.add(selectAddressesLabel);
		inputsSelectFields.add(selectAddresses);
		inputsSelectFields.add(selectYearBeganLabel);
		inputsSelectFields.add(selectYearBegan);

		inputsSelectFields.add(selectHospitalAffiliationsLabel);
		inputsSelectFields.add(selectHospitalAffiliations);
		inputsSelectFields.add(selectYearBeganEmoryLabel);
		inputsSelectFields.add(selectYearBeganEmory);

		inputsSelectFields.add(selectAcademicTitlesLabel);
		inputsSelectFields.add(selectAcademicTitles);
		inputsSelectFields.add(selectNPILabel);
		inputsSelectFields.add(selectNPI);

						
		JPanel inputsMiddle = new JPanel();
		BorderLayout middleLayout = new BorderLayout(5,5);
		inputsMiddle.setLayout(middleLayout);
		inputsMiddle.add(inputsSelectFieldsLabels,BorderLayout.WEST);
		inputsMiddle.add(inputsSelectFields,BorderLayout.CENTER);
		inputsMiddle.setBorder(BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
				
		JPanel inputsBottomWest = new JPanel();
		GridLayout bottomWestLayout = new GridLayout(3,0);
		inputsBottomWest.setLayout(bottomWestLayout);
		inputsBottomWest.add(translateFileButton);
		inputsBottomWest.add(NPIFileButton);
		inputsBottomWest.add(helpButton);
		
		JPanel inputsBottomCenter = new JPanel();
		GridLayout bottomCenterLayout = new GridLayout(3,0);
		inputsBottomCenter.setLayout(bottomCenterLayout);
		inputsBottomCenter.add(translateFileField);
		inputsBottomCenter.add(NPIFileField);
		inputsBottomCenter.add(runButton);

		JPanel inputsBottom = new JPanel();
		BorderLayout bottomLayout = new BorderLayout(5,5);
		inputsBottom.setLayout(bottomLayout);
		inputsBottom.add(inputsBottomWest,BorderLayout.WEST);
		inputsBottom.add(inputsBottomCenter,BorderLayout.CENTER);
		
		JPanel inputs = new JPanel();
		GridLayout inputsLayout = new GridLayout(3,0);
		inputs.setLayout(inputsLayout);		
		inputs.add(inputsTop);
		inputs.add(inputsMiddle);
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
			if (null == Importer.this.exportFile) {
				log.append("Please select a file to import from.");
			} else if (null == Importer.this.translateFile) {
				log.append("Please select a translations file.");
			} else if (null == Importer.this.NPIFile) {
				log.append("Please select a translations file.");
			} else {
				log.append("Importing: "
						+ Importer.this.exportFile.getAbsolutePath() + "."
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
					ArrayList<java.lang.String> overrideFromCascade = new ArrayList<java.lang.String>();					
					if (!selectSpecialties.isSelected()) { overrideFromCascade.add("special"); overrideFromCascade.add("special-group");}	
					if (!selectAOI.isSelected()) { overrideFromCascade.add("clinical"); }	
					if (!selectPracticeAffiliation.isSelected()) { overrideFromCascade.add("affil"); }	
					if (!selectAddresses.isSelected()) { overrideFromCascade.add("addr"); }	
					if (!selectHospitalAffiliations.isSelected()) { overrideFromCascade.add("h-affil-group"); }	
					if (!selectAcademicTitles.isSelected()) { overrideFromCascade.add("title"); }	
					if (!selectEducation.isSelected()) { overrideFromCascade.add("edu-group"); }	
					if (!selectHonorifics.isSelected()) { overrideFromCascade.add("honorific"); }	
					if (!selectLanguages.isSelected()) { overrideFromCascade.add("lang"); }	
					if (!selectYearBegan.isSelected()) { overrideFromCascade.add("yr-prac"); }	
					if (!selectYearBeganEmory.isSelected()) { overrideFromCascade.add("yr-prac-edu"); }	
					if (!selectNPI.isSelected()) { overrideFromCascade.add("npi"); }
					directory.importFromSharpFocus(Importer.this.exportFile,
							Importer.this.translateFile, Importer.this.NPIFile, overrideFromCascade);
					directory.writeToCascade();
				} catch (Exception e1) {
					log.append(e1.getMessage());
				}
				log.append("Import completed."+newline);
				log.setCaretPosition(log.getDocument().getLength());
			}

		} else if (e.getSource() == exportFileButton) {
			int returnVal = fcExport.showOpenDialog(Importer.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Importer.this.exportFile = fcExport.getSelectedFile();
				Importer.this.exportFileField.setText(Importer.this.exportFile.getName());
				log.append("Selected export file: "
						+ Importer.this.exportFile.getAbsolutePath() + "."
						+ newline);
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());
		} else if (e.getSource() == NPIFileButton) {
			int returnVal = fcNPI.showOpenDialog(Importer.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Importer.this.NPIFile = fcNPI.getSelectedFile();
				Importer.this.NPIFileField.setText(Importer.this.NPIFile.getName());
				log.append("Selected NPI file: "
						+ Importer.this.NPIFile.getAbsolutePath() + "."
						+ newline);
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());
		} else if (e.getSource() == translateFileButton) {
			int returnVal = fcTranslate.showOpenDialog(Importer.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Importer.this.translateFile = fcTranslate.getSelectedFile();
				Importer.this.translateFileField.setText(Importer.this.translateFile.getName());
				log.append("Selected translations file: "
						+ Importer.this.translateFile.getAbsolutePath() + "."
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
									+ "Choose a valid SharpFocus export file to import.\n\n"
									+ "Choose a comma-delimited file for text translations.\n\n"
									+ "Choose a comma-delimited file for NPI lookups.\n\n"
									+ "Then press 'Run' to import physician profiles.\n\n"
									+ "Report any problems to webadmin@emoryhealthcare.org.");
		} else if (e.getSource() == siteSelect) {
			java.lang.String siteName = (java.lang.String)siteSelect.getSelectedItem();
			log.append("Selected site to import to is " + siteName +"." + newline);
		} else if (e.getSource() == selectAllButton) {
			selectSpecialties.setSelected(true);
			selectAOI.setSelected(true);
			selectPracticeAffiliation.setSelected(true);
			selectAddresses.setSelected(true);
			selectHospitalAffiliations.setSelected(true);
			selectAcademicTitles.setSelected(true);
			selectEducation.setSelected(true);
			selectHonorifics.setSelected(true);
			selectLanguages.setSelected(true);
			selectYearBegan.setSelected(true);
			selectYearBeganEmory.setSelected(true);
			selectNPI.setSelected(true);			
		} else if (e.getSource() == selectNoneButton) {
			selectSpecialties.setSelected(false);
			selectAOI.setSelected(false);
			selectPracticeAffiliation.setSelected(false);
			selectAddresses.setSelected(false);
			selectHospitalAffiliations.setSelected(false);
			selectAcademicTitles.setSelected(false);
			selectEducation.setSelected(false);
			selectHonorifics.setSelected(false);
			selectLanguages.setSelected(false);
			selectYearBegan.setSelected(false);
			selectYearBeganEmory.setSelected(false);
			selectNPI.setSelected(false);			
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
		JFrame frame = new JFrame("Importer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		final Importer newContentPane = new Importer(frame);
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