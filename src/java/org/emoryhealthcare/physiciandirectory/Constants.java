/**
 * 
 */
package org.emoryhealthcare.physiciandirectory;

/**
 * @author n715667
 * Newer version
 *
 */
public final class Constants {
	public static final java.lang.String FILENAME_BEFORE_ID = "physician_detail.jsp@physicianid=";
	public static final java.lang.String MULTISELECT_DELIM = "::CONTENT-XML-SELECTOR::";
	public static final java.lang.String USER_NAME = "train1";
	public static final java.lang.String PASSWORD = "internet";
	public static final java.lang.String ERROR_BAD_NOTES = "Sharp Focus notes field does not contain proper Title, Priv, or end-of-field markers.";
	public static final int ID_BEGIN_INDEX = 0;
	public static final int ID_END_INDEX = 21;
	public static final int FIRST_BEGIN_INDEX = 23;
	public static final int FIRST_END_INDEX = 122;
	public static final int LAST_BEGIN_INDEX = 124;
	public static final int LAST_END_INDEX = 224;
	public static final java.lang.String ID_TEST_REGEX = new java.lang.String("[0-9]*");
	public static final java.lang.String FIRSTNAMES_TEST_REGEX = new java.lang.String("[-.' a-zA-Z\"()]*");
	public static final java.lang.String LASTNAMES_TEST_REGEX = new java.lang.String("[-.,' a-zA-Z]*");
	public static final java.lang.String REPLACE_CHARS_REGEX = new java.lang.String("[^A-Za-z]");
	public static final java.lang.String REMOVE_CHARS_REGEX = new java.lang.String("[.'\"()]");
	public static final java.lang.String ADDRESSLINE1_TEST_REGEX = new java.lang.String("^[0-9].*");
	public static final java.lang.String SPACE = new java.lang.String(" ");
	public static final java.lang.String COMMA = new java.lang.String(",");
	public static final java.lang.String SLASH = new java.lang.String("/");
	public static final java.lang.String HYPHEN = new java.lang.String("-");
	public static final java.lang.String YES = new java.lang.String("Y");
	public static final java.lang.String NO = new java.lang.String("N");
	public static final java.lang.String ACTIVE = new java.lang.String("Active");	
	public static final java.lang.String INACTIVE = new java.lang.String("Inactive");	
	public static final java.lang.String DR = new java.lang.String("Dr.");	
	public static final java.lang.String TITLES = new java.lang.String("Title:");	
	public static final java.lang.String PRIVS = new java.lang.String("Priv:");	
	public static final java.lang.String SEMI = new java.lang.String(";");	
	public static final java.lang.String SEMIS = new java.lang.String(";;");	
	public static final java.lang.String NEWLINE = new java.lang.String("\n");	
	public static final java.lang.String EUH = new java.lang.String("Emory University Hospital");	
	public static final java.lang.String EUHM = new java.lang.String("Emory University Hospital Midtown");	
	public static final java.lang.String EUOSH = new java.lang.String("Emory University Orthopaedics & Spine Hospital");	
	public static final java.lang.String WESLEY = new java.lang.String("Wesley Woods Geriatric Hospital");	
	public static final java.lang.String CHOA = new java.lang.String("Children's Healthcare of Atlanta");	
	public static final java.lang.String GRADY = new java.lang.String("Grady Memorial Hospital");	
	public static final java.lang.String VAMC = new java.lang.String("Veterans Affairs Medical Center");	
	public static final java.lang.String EJCH = new java.lang.String("Emory Johns Creek Hospital");	
	public static final java.lang.String EUOSH_AFFIL_LINK = new java.lang.String("/emory-orthopaedics-spine-hospital/index");
	public static final java.lang.String TELEPHONE_PATTERN = new java.lang.String("\\({0,1}[0-9]{3}\\){0,1}[ -]*[0-9]{3}\\-[0-9]{4}");
	public static final java.lang.String ZIP_PATTERN = new java.lang.String("[0-9]{5}(\\-[0-9]{4}){0,1}");
	public static final java.lang.String YEAR_PATTERN = new java.lang.String("[0-9]{4}");
	public static final java.lang.String STATE_PATTERN = new java.lang.String("GA");	
	public static final java.lang.String NA_PATTERN = new java.lang.String("NA");
	public static final java.lang.String BOARD_PATTERN = new java.lang.String(" (Board certified since ");
	public static final java.lang.String DQUOTE = new java.lang.String("\"");
	public static final java.lang.String COMMA_DELIM = new java.lang.String("\",\"");
	public static final int SHARP_FOCUS_FIELD_COUNT = 148;
	public static final int SHARP_FOCUS_FIRST_NAMES = 0;
	public static final int SHARP_FOCUS_LAST_NAME = 1;
	public static final int SHARP_FOCUS_ADDRESSES_START = 2;
	public static final int SHARP_FOCUS_ADDRESSES_END = 7;
	public static final int SHARP_FOCUS_AFFILIATIONS_START = 32;
	public static final int SHARP_FOCUS_AFFILIATIONS_END = 36;
	public static final int SHARP_FOCUS_ID = 48;
	public static final int SHARP_FOCUS_EDUCATIONS_START = 49;
	public static final int SHARP_FOCUS_EDUCATIONS_END = 57;
	public static final int SHARP_FOCUS_SPECIALTIES_START = 65;
	public static final int SHARP_FOCUS_SPECIALTIES_END = 69;
	public static final int SHARP_FOCUS_EMORY_PRAC = 81;
	public static final int SHARP_FOCUS_GENERAL_PRAC = 82;
	public static final int SHARP_FOCUS_NOTES = 83;
	public static final int SHARP_FOCUS_AOIS_START = 84;
	public static final int SHARP_FOCUS_AOIS_END = 144;
	public static final int SHARP_FOCUS_TITLE = 144;
	public static final int SHARP_FOCUS_LANGUAGES_START = 145;
	public static final int SHARP_FOCUS_LANGUAGES_END = 148;
	public static final int TRANSLATION_FIELD_COUNT = 3;
	public static final int NPI_FIELD_COUNT = 3;
	public static final int SELECTED_UPDATE_FIELD_COUNT = 13;
	public static final java.lang.String FIELD_DEPARTMENT_LABEL = "Depart";
	public static final java.lang.String FIELD_IMAGE_LABEL = "Image";
	public static final java.lang.String FIELD_VIDEO_LABEL = "Video";
	public static final java.lang.String FIELD_ABOUT_LABEL = "About";
	public static final java.lang.String FIELD_PERSONAL_MESSAGE_LABEL = "PersMsg";
	public static final java.lang.String FIELD_GENERAL_INFO_LABEL = "GenInfo";
	public static final java.lang.String FIELD_SEX_LABEL = "Sex";
	public static final java.lang.String FIELD_ORGANIZATIONAL_LEADERSHIPS_LABEL = "OrgLead";
	public static final java.lang.String FIELD_MAJOR_PUBLICATIONS_LABEL = "MajPub";
	public static final java.lang.String FIELD_EDUCATIONS_LABEL = "Edu";
	public static final java.lang.String FIELD_TITLE_LABEL = "Title";
	public static final java.lang.String FIELD_AFFILIATIONS_LABEL = "Affil";
	public static final java.lang.String FIELD_SPECIALTIES_LABEL = "Spec";
	public static final java.lang.String FIELD_AOIS_LABEL = "AOI";
	public static final java.lang.String FIELD_ADDRESSES_LABEL = "Addr";
	public static final java.lang.String FIELD_LANGUAGES_LABEL = "Lang";
	public static final java.lang.String FIELD_ACAD_TITLES_LABEL = "AcadTitle";
	public static final java.lang.String FIELD_GENERAL_PRAC_LABEL = "GenPrac";
	public static final java.lang.String FIELD_EMORY_PRAC_LABEL = "EmoryPrac";
	public static final java.lang.String FIELD_PRIVS_LABEL = "Priv";
	public static final java.lang.String FIELD_NPI_LABEL = "NPI";
	public static final java.lang.String FIELD_STATUS_LABEL = "Status" ;
	public static final java.lang.String TRANSLATION_ZIP_LABEL = "ZIP";
	public static final java.lang.String SITE_SELECTION_EHC = "Emory Healthcare";
	public static final java.lang.String SITE_SELECTION_EJCH = "Emory Johns Creek Hospital";
	public static final java.lang.String SITE_SELECTION_SJHA = "Saint Josephs Hospital Atlanta";
	public static final java.lang.String IMAGE_FILENAME_PATTERN = "[a-z]+-+([a-z]+-+)*[a-z]+\\.(jpg)|(png)";
	public static final java.lang.String GENERATION_PATTERN = "i{1,3}|iv|(vi{1,3})|(ix)";
	

}
