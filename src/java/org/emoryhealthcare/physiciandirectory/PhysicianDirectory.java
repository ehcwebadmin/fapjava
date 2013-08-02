package org.emoryhealthcare.physiciandirectory;

import java.util.*;
import java.io.*;
import javax.swing.*;


import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;




public final class PhysicianDirectory {
	File folder;
	File nameList;
	Scanner scanner;
	java.lang.String site;
	java.lang.String password;
	java.lang.String username;
	JTextArea log;
	ArrayList<Physician> physicianList;
	ArrayList<java.lang.String> headings;
	ArrayList<java.lang.String> overrides = new ArrayList<java.lang.String>();
	
	HashMap<java.lang.String,java.lang.String> addressLineMap = new HashMap<java.lang.String,java.lang.String>();
	HashMap<java.lang.String,java.lang.String> titleMap = new HashMap<java.lang.String,java.lang.String>();
	HashMap<java.lang.String,java.lang.String> privilegeMap = new HashMap<java.lang.String,java.lang.String>();
	HashMap<java.lang.String,java.lang.String> affilMap = new HashMap<java.lang.String,java.lang.String>();
	HashMap<java.lang.String,java.lang.String> NPIMap = new HashMap<java.lang.String,java.lang.String>();
	HashMap<java.lang.String,java.lang.String> specMap = new HashMap<java.lang.String,java.lang.String>();
	HashMap<java.lang.String,java.lang.String> ZIPMap = new HashMap<java.lang.String,java.lang.String>();
	HashMap<java.lang.String,java.lang.String> langMap = new HashMap<java.lang.String,java.lang.String>();
	HashMap<java.lang.String,java.lang.String> aoiMap = new HashMap<java.lang.String,java.lang.String>();
	HashMap<java.lang.String,Integer> elementIDtoIntMap = new HashMap<java.lang.String,Integer>();
	
	public PhysicianDirectory() {
		this.site = Constants.SITE_SELECTION_EHC;
		this.password = Constants.PASSWORD;
		this.username = Constants.USER_NAME;
		elementIDtoIntMap.put("depart", 0);
		elementIDtoIntMap.put("image", 1);
		elementIDtoIntMap.put("video", 2);
		elementIDtoIntMap.put("about", 3);
		elementIDtoIntMap.put("p-mesg", 4);
		elementIDtoIntMap.put("gen-info", 5);
		elementIDtoIntMap.put("sex", 6);
		elementIDtoIntMap.put("org-lead", 7);
		elementIDtoIntMap.put("maj-pub", 8);
		elementIDtoIntMap.put("award-group", 9);
		elementIDtoIntMap.put("edu-group", 10);
		elementIDtoIntMap.put("honorific", 11);
		elementIDtoIntMap.put("affil", 12);
		elementIDtoIntMap.put("special-group", 13);
		elementIDtoIntMap.put("special", 14);
		elementIDtoIntMap.put("clinical", 15);
		elementIDtoIntMap.put("addr", 16);
		elementIDtoIntMap.put("h-affil-group", 17);
		elementIDtoIntMap.put("lang", 18);
		elementIDtoIntMap.put("title", 19);
		elementIDtoIntMap.put("yr-prac", 20);
		elementIDtoIntMap.put("yr-prac-edu", 21);
		elementIDtoIntMap.put("loc-code", 22);
		elementIDtoIntMap.put("med-name", 23);
		elementIDtoIntMap.put("npi", 24);
		elementIDtoIntMap.put("status", 25);

		overrides.add("depart");
		overrides.add("image");
		overrides.add("video");
		overrides.add("about");
		overrides.add("p-mesg");
		overrides.add("gen-info");
		overrides.add("sex");
		overrides.add("org-lead");
		overrides.add("maj-pub");
		overrides.add("award-group");
	}

	public PhysicianDirectory(java.lang.String sSite){
		this();
		this.site = sSite;
	}
	
	public PhysicianDirectory(JTextArea log) {
		this();
		this.log = log;
	}
	
	/*
	 * Send a message to the user, either via the importer log or the console
	 */
	
	void announce (java.lang.String sMessage) {
		if (this.log == null) {
			System.out.println(sMessage);
		}
		else {
			log.append(sMessage.concat(Constants.NEWLINE));
			log.setCaretPosition(log.getDocument().getLength());

		}
	}
	/*
	 * iterate over namelist 1. skip header row 2. parse id, firstname,
	 * middlename(s), lastname 3. turn id into a filename and find the file 4.
	 * construct a new Physician with file, firstname, middlename, lastname 5.
	 * add the physician to the directory
	 */
	private ArrayList<Physician> buildPhysicianList(File nameList)
			throws Exception {
		scanner = new Scanner(new FileReader(nameList));
		ArrayList<Physician> physicianList = new ArrayList<Physician>();
		Physician doctor;
		try {
			// first use a Scanner to get each line
			while (scanner.hasNextLine()) {
				doctor = processLegacyLine(scanner.nextLine());
				if (doctor != null) {
					physicianList.add(doctor);
					doctor = null;
				}
			}
		} finally {
			// ensure the underlying stream is always closed
			// this only has any effect if the item passed to the Scanner
			// constructor implements Closeable (which it does in this case).
			scanner.close();
		}
		return physicianList;
	}

	private void setPhysicianList (File exportFile) throws Exception {
		CsvListReader reader = new CsvListReader(new FileReader(exportFile), CsvPreference.EXCEL_PREFERENCE);
		this.physicianList = new ArrayList<Physician>();
		try {
			List<java.lang.String> line = reader.read();
			while (line != null) {
				if (line.size() != Constants.SHARP_FOCUS_FIELD_COUNT) {
					IOException exception = new IOException(
						"Export file is not in proper CSV format: field count is wrong.");
					throw exception;
				}
				Physician physician = new Physician(this);
				if (physician.updateFromSharpFocus(line)) {
					physician.updateFromCascade();
					this.physicianList.add(physician);
				}
				line = reader.read();
			}
		} finally {
			reader.close();
		}
	}
	
	/*
	 * method takes a string and returns its index in the list of headings
	 * if the heading isn't found, return whatever value the caller prefers
	 */
	public int findHeading(java.lang.String sHeading) {
		if (null == this.headings) {
			this.headings = new ArrayList<java.lang.String>();
			this.headings.add("Hospital Affiliation(s)");
			this.headings.add("Addresses Locations");
			this.headings.add("Year started practicing at Emory");
			this.headings.add("Education");
			this.headings.add("Organizational Leadership Memberships");
			this.headings.add("Awards");
			this.headings.add("Major or Recent Publications");
			this.headings.add("Additional spoken language(s)");
			this.headings.add("Specialties");
			this.headings.add("Area of Clinical Interest");
		}
		 if (this.headings.contains(sHeading)) {
			 return this.headings.indexOf(sHeading);
		 }
		
		return -1;
	}
	
	/*
	 * method takes the name of a folder and the name of a file in that folder
	 * the files in the folder should represent physician records as HTML pulled
	 * from the legacy Find a Physician application except for the one file
	 * named in the method call that file contains a list of physician names and
	 * IDs from the Find a Physician database
	 */
	public void importFromLegacy(java.lang.String sFolder,
			java.lang.String sListFilename) throws Exception {
		folder = new File(sFolder);
		/* do some validation */
		if (!folder.isDirectory() || !folder.canRead()) {
			IOException exception = new IOException(
					"Folder is not a folder that can be read");
			throw exception;
		}
		nameList = new File(folder, sListFilename);
		if (!nameList.isFile() || !nameList.canRead()) {
			IOException exception = new IOException(
					"Listing is not a file that can be read");
			throw exception;
		}
		this.physicianList = buildPhysicianList(nameList);
	}

	public void importFromSharpFocus(File exportFile, File translateFile, File NPIFile, ArrayList<java.lang.String> overrideFromCascade) throws Exception {
		if (!exportFile.canRead()) {
			IOException exception = new IOException(
					"Export is not a file that can be read");
			throw exception;
		}
		if (!translateFile.canRead()) {
			IOException exception = new IOException(
					"Translations file is not a file that can be read");
			throw exception;
		}		
		if (!NPIFile.canRead()) {
			IOException exception = new IOException(
					"NPI lookup file is not a file that can be read");
			throw exception;
		}
		
		this.setNPIxref(NPIFile);
		this.setTranslations(translateFile);
		this.setCascadeOverrides(overrideFromCascade);
		this.setPhysicianList(exportFile);
	}

	/*
	 * Iterate over the photoFolder's files looking for image files whose names parse usably.
	 * For each, attempt a physician.replaceImage with the image file.
	 */
	public void loadPhotos(File photoFolder) throws Exception {
		for (File child : photoFolder.listFiles()) {
			if (child.getName().matches(Constants.IMAGE_FILENAME_PATTERN)) {
				Physician physician = new Physician(this);
				physician.replaceImage(child);
			}
			else {
				java.lang.String message = new java.lang.String("Unable to parse a physician name from filename ");
				this.announce(message.concat(child.getName()));
			}
		}

	}
	
	private Physician processLegacyLine(java.lang.String line) throws Exception {
		/* if the line is too short to process, quietly do nothing */
		if (line.length() <= Constants.LAST_BEGIN_INDEX) {
			return null;
		}

		/* check to make sure there is an ID and it is all numerals */
		java.lang.String id = line.substring(Constants.ID_BEGIN_INDEX,
				Constants.ID_END_INDEX).trim();
		if (id.length() < 1 || !id.matches(Constants.ID_TEST_REGEX)) {
			return null;
		}

		/*
		 * check to make sure there is a set of first and middle names made up
		 * of acceptable characters Then take everything up to the first space
		 * as the actual first name Everything else is middle names
		 */
		java.lang.String firstNames = line.substring(
				Constants.FIRST_BEGIN_INDEX, Constants.FIRST_END_INDEX).trim();
		if (firstNames.length() < 1
				|| !firstNames.matches(Constants.FIRSTNAMES_TEST_REGEX)) {
			return null;
		}
		int firstSpace = firstNames.indexOf(Constants.SPACE);
		if (firstSpace < 1)
			firstSpace = firstNames.length();
		java.lang.String firstName = firstNames.substring(0, firstSpace);
		java.lang.String middleName = firstNames.substring(firstSpace,
				firstNames.length()).trim();

		/*
		 * Last names can look like Van Dyke-Smith, M.D., Ph.D So take
		 * everything up to the first comma as last name, Everything else is the
		 * string of titles
		 */
		java.lang.String lastName = new java.lang.String();
		java.lang.String titles = new java.lang.String();
		java.lang.String lastNames = line.substring(Constants.LAST_BEGIN_INDEX,
				Constants.LAST_END_INDEX).trim();
		if (lastNames.length() < 1
				|| !lastNames.matches(Constants.LASTNAMES_TEST_REGEX)) {
			return null;
		}
		int comma = lastNames.indexOf(Constants.COMMA);
		if (comma > 0) {
			lastName = lastNames.substring(0, comma);
			titles = lastNames.substring(comma + 1,
				lastNames.length()).trim();
		}
		else {
			lastName = lastNames.trim();
			titles = "";
		}

		Physician physician = new Physician(this);
		if (physician.updateFromLegacy(id, firstName, middleName, lastName,
				titles, folder)) {
			return physician;
		} else {
			return null;
		}

	}


	public void setCascadeOverrides(ArrayList<java.lang.String>overrideFromCascade) throws Exception {
		/* 
		 * Add in the fields we might override from Cascade
		 */
		this.overrides.addAll(overrideFromCascade);
	}
	
	private void setNPIxref (File NPIFile) throws Exception {
		CsvListReader reader = new CsvListReader(new FileReader(NPIFile), CsvPreference.EXCEL_PREFERENCE);
		try {
			List<java.lang.String> line = reader.read();
			while (line != null) {
				if (line.size() != Constants.NPI_FIELD_COUNT) {
					IOException exception = new IOException(
						"NPI list is not in proper CSV format: field count is wrong.");
					throw exception;
				}
				if (line.get(0).equals(Constants.FIELD_NPI_LABEL)){
					this.NPIMap.put(line.get(2), line.get(1));
				}
				line = reader.read();
			}
		} finally {
			reader.close();
		}
	}

	private void setTranslations(File translationFile) throws Exception {		
		CsvListReader reader = new CsvListReader(new FileReader(translationFile), CsvPreference.EXCEL_PREFERENCE);
		try {
			List<java.lang.String> line = reader.read();
			while (line != null) {
				if (line.size() != Constants.TRANSLATION_FIELD_COUNT) {
					IOException exception = new IOException(
						"Translations list is not in proper CSV format: field count is wrong.");
					throw exception;
				}
				if (line.get(0).equals(Constants.FIELD_ADDRESSES_LABEL)){
					this.addressLineMap.put(line.get(1), line.get(2));
				}
				else if (line.get(0).equals(Constants.FIELD_PRIVS_LABEL)) {
					this.privilegeMap.put(line.get(1),line.get(2));
				}
				else if (line.get(0).equals(Constants.FIELD_TITLE_LABEL)) {
					this.titleMap.put(line.get(1),line.get(2));
				}
				else if (line.get(0).equals(Constants.FIELD_AFFILIATIONS_LABEL)) {
					this.affilMap.put(line.get(1),line.get(2));
				}
				else if (line.get(0).equals(Constants.FIELD_SPECIALTIES_LABEL)) {
					this.specMap.put(line.get(1),line.get(2));
				}
				else if (line.get(0).equals(Constants.TRANSLATION_ZIP_LABEL)) {
					this.ZIPMap.put(line.get(1),line.get(2));
				}
				else if (line.get(0).equals(Constants.FIELD_LANGUAGES_LABEL)) {
					this.langMap.put(line.get(1),line.get(2));
				}
				else if (line.get(0).equals(Constants.FIELD_AOIS_LABEL)) {
					this.aoiMap.put(line.get(1),line.get(2));
				}
				line = reader.read();
			}
		} finally {
			reader.close();
		}
	}
	
	public void setPassword(java.lang.String sPassword){
		this.password = sPassword;
	}

	public void setUsername(java.lang.String sUsername){
		this.username = sUsername;
	}

	public void setSite(java.lang.String sSite) {
		this.site = sSite;
	}
	
	/*
	 * iterate over directory of physicians, writing each to Cascade
	 */

	public void writeToCascade() throws Exception {
		for (Physician doctor : this.physicianList) {
			try {
				doctor.writeToCascade();
			}
			catch (Exception e1) {
				doctor.messages.add(e1.getMessage());
			}
			doctor.announceMessages();
			
			
		}
	}
}
