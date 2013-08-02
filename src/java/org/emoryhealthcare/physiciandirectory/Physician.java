/**
 * 
 */
package org.emoryhealthcare.physiciandirectory;

import com.hannonhill.www.ws.ns.AssetOperationService.StructuredData;
import com.hannonhill.www.ws.ns.AssetOperationService.StructuredDataAssetType;
import com.hannonhill.www.ws.ns.AssetOperationService.StructuredDataNode;
import com.hannonhill.www.ws.ns.AssetOperationService.StructuredDataType;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.hannonhill.www.ws.ns.AssetOperationService.Asset;
import com.hannonhill.www.ws.ns.AssetOperationService.AssetOperationHandler;
import com.hannonhill.www.ws.ns.AssetOperationService.AssetOperationHandlerServiceLocator;
import com.hannonhill.www.ws.ns.AssetOperationService.Authentication;
import com.hannonhill.www.ws.ns.AssetOperationService.CreateResult;
import com.hannonhill.www.ws.ns.AssetOperationService.EntityTypeString;
import com.hannonhill.www.ws.ns.AssetOperationService.Identifier;
import com.hannonhill.www.ws.ns.AssetOperationService.Metadata;
import com.hannonhill.www.ws.ns.AssetOperationService.OperationResult;
import com.hannonhill.www.ws.ns.AssetOperationService.Page;
import com.hannonhill.www.ws.ns.AssetOperationService.Path;
import com.hannonhill.www.ws.ns.AssetOperationService.ReadResult;
import org.supercsv.io.*;
import org.xml.sax.SAXException;


/**
 * @author Fred Welden
 * 
 */

public final class Physician {
	private class Address {
		ArrayList<StructuredDataNode> lines = new ArrayList<StructuredDataNode>();
		StructuredDataNode city = new StructuredDataNode(
				StructuredDataType.text, "city", null, "", null, null, null,
				null, null, null, null, null, null, null);
		StructuredDataNode state = new StructuredDataNode(
				StructuredDataType.text, "state", null, "", null, null, null,
				null, null, null, null, null, null, null);
		StructuredDataNode zip = new StructuredDataNode(
				StructuredDataType.text, "zip", null, "", null, null, null,
				null, null, null, null, null, null, null);
		StructuredDataNode phone = new StructuredDataNode(
				StructuredDataType.text, "phone", null, "", null, null, null,
				null, null, null, null, null, null, null);
		StructuredDataNode latitude = new StructuredDataNode(
				StructuredDataType.text, "latitude", null, "", null, null, null,
				null, null, null, null, null, null, null);
		StructuredDataNode longitude = new StructuredDataNode(
				StructuredDataType.text, "longitude", null, "", null, null, null,
				null, null, null, null, null, null, null);

		public void addLine(java.lang.String sLine) {
			StructuredDataNode addressLine = new StructuredDataNode(
					StructuredDataType.text, "line", null, "", null, null,
					null, null, null, null, null, null, null, null);
			addressLine.setText(sLine);
			this.lines.add(addressLine);
		}
		
		public void addLine(java.lang.String sLine, boolean translate) {
			if (translate && directory.addressLineMap.containsKey(sLine)) {
					this.addLine(directory.addressLineMap.get(sLine));
			}
			else {
				this.addLine(sLine);
			}
		}
		
		public boolean isValid() {
			return (this.lines.size() > 0 && !this.lines.get(0).getText().isEmpty()
					&& !this.city.getText().isEmpty()
					&& !this.state.getText().isEmpty() && !this.zip.getText().isEmpty());
		}

		public void setCity(java.lang.String sCity) {
			this.city.setText(sCity);
		}

		public void setFromLegacy(java.lang.String sAddress) {
			if (sAddress.matches(Constants.NA_PATTERN)) {
				// just leave it out
				return;
			}

			if (sAddress.matches(Constants.TELEPHONE_PATTERN)) {
				this.setPhone(sAddress);
				return;
			}

			if (sAddress.length() >= 10 && sAddress.substring(sAddress.length() - 10).matches(
					Constants.ZIP_PATTERN)) {
				this.setZip(sAddress.substring(sAddress.length() - 10));
				sAddress = sAddress.substring(0, sAddress.length() - 10).trim();
			} else if (sAddress.length() >= 5 && sAddress.substring(sAddress.length() - 5).matches(
					Constants.ZIP_PATTERN)) {
				this.setZip(sAddress.substring(sAddress.length() - 5));
				sAddress = sAddress.substring(0, sAddress.length() - 5).trim();
			}
			
			if (sAddress.endsWith(Constants.COMMA)) {
				sAddress=sAddress.substring(0, sAddress.length()-1).trim();
			}
			
			if (sAddress.length() >= 2 && sAddress.substring(sAddress.length() - 2).matches(Constants.STATE_PATTERN)) {
				this.setState(sAddress.substring(sAddress.length()-2));
				sAddress=sAddress.substring(0, sAddress.length()-2).trim();				
				if (sAddress.endsWith(Constants.COMMA)) {
					sAddress=sAddress.substring(0, sAddress.length()-1).trim();
				}				
			}
			
			this.addLine(sAddress);
		}

		public void setPhone(java.lang.String sPhone) {
			this.phone.setText(sPhone);
		}

		public void setState(java.lang.String sState) {
			this.state.setText(sState);
		}
		
		public void setLatitude(java.lang.Float sLatitude) {
			this.latitude.setText(Float.toString(sLatitude));
		}
		
		public void setLongitude(java.lang.Float sLongitude) {
			this.longitude.setText(Float.toString(sLongitude));
		}

		public void setZip(java.lang.String sZip) {
			if (directory.ZIPMap.containsKey(sZip)) {
				this.zip.setText(directory.ZIPMap.get(sZip));
			}
			else {
				this.zip.setText(sZip);
			}
		}

		public StructuredDataNode toGroup() throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
			StructuredDataNode group = new StructuredDataNode(
					StructuredDataType.group, "addr", null, null, null, null,
					null, null, null, null, null, null, null, null);
			StructuredDataNode[] members = new StructuredDataNode[this.lines
					.size() + 6];
			int i = 0;
			String fullAddress = "";
			for (StructuredDataNode addressLine : this.lines) {
				members[i++] = addressLine;
				/* If first address line does not start with a number, it is a practice name, skip it */
				if (i > 1 || addressLine.getText().matches(Constants.ADDRESSLINE1_TEST_REGEX)) {
					fullAddress += addressLine.getText() + ", ";
				}
			}
									
			fullAddress += this.city.getText() + ", ";
			fullAddress += this.state.getText() + ",  ";
			fullAddress += this.zip.getText();
			this.setLatitude(Geocoder.GetLatitude(fullAddress));
			this.setLongitude(Geocoder.GetLongitude(fullAddress));
			
			members[i++] = this.city;
			members[i++] = this.state;
			members[i++] = this.zip;
			members[i++] = this.phone;
			members[i++] = this.latitude;
			members[i++] = this.longitude;
			group.setStructuredDataNodes(members);
			return group;
		}
	}
	
	private class Education {
		StructuredDataNode name = new StructuredDataNode(
				StructuredDataType.text, "edu", null,
				"", null, null, null, null, null, null,
				null, null, null, null);
		StructuredDataNode type = new StructuredDataNode(
				StructuredDataType.text, "edu-type", null,
				"", null, null, null, null, null, null,
				null, null, null, null);
		StructuredDataNode year = new StructuredDataNode(
				StructuredDataType.text, "edu-year", null,
				"", null, null, null, null, null, null,
				null, null, null, null);


		java.lang.String getType() {
			return this.type.getText();
		}
		
		public boolean isValid() {
			return (!this.name.getText().isEmpty() && !this.type.getText().isEmpty());
		}
		
		public void setName (java.lang.String sName) {
			this.name.setText(sName);
		}
		
		public void setType (java.lang.String sType) {
			this.type.setText(sType);
		}
				
		public void setYear (java.lang.String sYear) {
			this.year.setText(sYear);
		}
				
		public StructuredDataNode toGroup() {
			StructuredDataNode group = new StructuredDataNode(
					StructuredDataType.group, "edu-group", null, null, null, null,
					null, null, null, null, null, null, null, null);
			StructuredDataNode[] members = new StructuredDataNode[3];
			members[0] = this.name;
			members[1] = this.type;
			members[2] = this.year;
			group.setStructuredDataNodes(members);
			return group;
		}	
	}

	private class HospitalAffiliation {
		StructuredDataNode name = new StructuredDataNode(
				StructuredDataType.text, "h-affil", null,
				"", null, null, null, null, null, null,
				null, null, null, null);
		StructuredDataNode link = new StructuredDataNode(
				StructuredDataType.text, "h-affil-link", null,
				"", null, null, null, null, null, null,
				null, null, null, null);
		StructuredDataNode status = new StructuredDataNode(
				StructuredDataType.text, "h-affil-stat", null,
				"", null, null, null, null, null, null, null,
				null, null, null);
		
		public boolean isValid() {
			return (!this.name.getText().isEmpty());
		}
		public void setLink (java.lang.String sLink) {
			this.link.setText(sLink);
		}
		
		public void setName (java.lang.String sName, boolean translate) {
			if (translate && directory.privilegeMap.containsKey(sName)) {
				this.name.setText(directory.privilegeMap.get(sName));
			}
			else {
				this.name.setText(sName);
			}
		}
		
		public void setStatus (java.lang.String sStatus) {
			this.status.setText(sStatus);
		}
		
		
		public StructuredDataNode toGroup() {
			StructuredDataNode group = new StructuredDataNode(
					StructuredDataType.group, "h-affil-group", null, null, null, null,
					null, null, null, null, null, null, null, null);
			StructuredDataNode[] members = new StructuredDataNode[3];
			members[0] = this.name;
			members[1] = this.link;
			members[2] = this.status;
			group.setStructuredDataNodes(members);
			return group;
		}	
	}

	private class Specialty {
		StructuredDataNode name = new StructuredDataNode(
				StructuredDataType.text, "special-name", null,
				"", null, null, null, null, null, null,
				null, null, null, null);
		StructuredDataNode year = new StructuredDataNode(
				StructuredDataType.text, "special-year", null,
				"", null, null, null, null, null, null,
				null, null, null, null);
		StructuredDataNode refers = new StructuredDataNode(
				StructuredDataType.text, "special-refers", null,
				"", null, null, null, null, null, null,
				null, null, null, null);
		StructuredDataNode primary = new StructuredDataNode(
				StructuredDataType.text, "special-primary", null,
				"", null, null, null, null, null, null,
				null, null, null, null);
		
		public boolean isValid(){
			return (!this.name.getText().isEmpty());
		}
		
		public void setName (java.lang.String sName) {
			if (directory.specMap.containsKey(sName)) {
				this.name.setText(directory.specMap.get(sName));
			}
			else {
				this.name.setText(sName);
			}
		}
		
		public void setPrimary (java.lang.String sPrimary) {
			this.primary.setText(sPrimary);
		}
				
		public void setRefers (java.lang.String sRefers) {
			this.refers.setText(sRefers);
		}
				
		public void setYear (java.lang.String sYear) {
			this.year.setText(sYear);
		}
				
		public StructuredDataNode toGroup() {
			StructuredDataNode group = new StructuredDataNode(
					StructuredDataType.group, "special-group", null, null, null, null,
					null, null, null, null, null, null, null, null);
			StructuredDataNode[] members = new StructuredDataNode[4];
			members[0] = this.name;
			members[1] = this.year;
			members[2] = this.refers;
			members[3] = this.primary;
			group.setStructuredDataNodes(members);
			return group;
		}	
	}
		
	StructuredDataNode firstName = new StructuredDataNode(
			StructuredDataType.text, "first-name", null, "", null, null, null,
			null, null, null, null, null, null, null);
	StructuredDataNode middleName = new StructuredDataNode(
			StructuredDataType.text, "m-name", null, "", null, null, null,
			null, null, null, null, null, null, null);
	StructuredDataNode lastName = new StructuredDataNode(
			StructuredDataType.text, "last-name", null, "", null, null, null,
			null, null, null, null, null, null, null);
	StructuredDataNode honorific = new StructuredDataNode(
			StructuredDataType.text, "honorific", null, "", null, null, null,
			null, null, null, null, null, null, null);
	StructuredDataNode affiliation = new StructuredDataNode(
			StructuredDataType.text, "affil", null, "", null, null, null, null,
			null, null, null, null, null, null);
//	StructuredDataNode academicRank = new StructuredDataNode(
//			StructuredDataType.text, "aca-rank", null, "", null, null, null,
//			null, null, null, null, null, null, null);
	ArrayList<StructuredDataNode> titleList = new ArrayList<StructuredDataNode>();
	StructuredDataNode imageFile = new StructuredDataNode(
			StructuredDataType.asset, "image", null, null,
			StructuredDataAssetType.file, null, null, null, null, null, null,
			null, null, null);
	StructuredDataNode department = new StructuredDataNode(
			StructuredDataType.text, "depart", null, "", null, null, null,
			null, null, null, null, null, null, null);
	StructuredDataNode referrableSpecialtySelections = new StructuredDataNode(
			StructuredDataType.text, "special", null, "", null, null, null,
			null, null, null, null, null, null, null);
	ArrayList<StructuredDataNode> specialtyGroups = new ArrayList<StructuredDataNode>();
	StructuredDataNode interestSelections = new StructuredDataNode(
			StructuredDataType.text, "clinical", null, "", null, null, null,
			null, null, null, null, null, null, null);
	ArrayList<StructuredDataNode> addressGroups = new ArrayList<StructuredDataNode>();
	ArrayList<StructuredDataNode> hospitalAffiliationGroups = new ArrayList<StructuredDataNode>();
	StructuredDataNode languageSelections = new StructuredDataNode(
			StructuredDataType.text, "lang", null, "", null, null, null, null,
			null, null, null, null, null, null);
	StructuredDataNode aboutXHTML = new StructuredDataNode(
			StructuredDataType.text, "about", null, "", null, null, null, null,
			null, null, null, null, null, null);
	StructuredDataNode yearStartedPractice = new StructuredDataNode(
			StructuredDataType.text, "yr-prac", null, "", null, null, null,
			null, null, null, null, null, null, null);
	StructuredDataNode yearStartedEmory = new StructuredDataNode(
			StructuredDataType.text, "yr-prac-edu", null, "", null, null, null,
			null, null, null, null, null, null, null);
	ArrayList<StructuredDataNode> leadershipList = new ArrayList<StructuredDataNode>();
	ArrayList<StructuredDataNode> publicationList = new ArrayList<StructuredDataNode>();
	ArrayList<StructuredDataNode> awardGroups = new ArrayList<StructuredDataNode>();
	ArrayList<StructuredDataNode> educationGroups = new ArrayList<StructuredDataNode>();
	StructuredDataNode videoGroup = new StructuredDataNode(
			StructuredDataType.group, "video", null, null, null, null, null,
			null, null, null, null, null, null, null);
	StructuredDataNode personalMessageXHTML = new StructuredDataNode(
			StructuredDataType.text, "p-mesg", null, "", null, null, null,
			null, null, null, null, null, null, null);
	StructuredDataNode generalInfoXHTML = new StructuredDataNode(
			StructuredDataType.text, "gen-info", null, "", null, null, null,
			null, null, null, null, null, null, null);
	StructuredDataNode locationCodeSelections = new StructuredDataNode(
			StructuredDataType.text, "loc-code", null, "", null, null, null,
			null, null, null, null, null, null, null);
	StructuredDataNode practiceName = new StructuredDataNode(
			StructuredDataType.text, "med-name", null, "", null, null, null,
			null, null, null, null, null, null, null);
	StructuredDataNode sex = new StructuredDataNode(StructuredDataType.text,
			"sex", null, "", null, null, null, null, null, null, null, null,
			null, null);
	StructuredDataNode npi = new StructuredDataNode(
			StructuredDataType.text, "npi", null, "", null, null, null,
			null, null, null, null, null, null, null);
	StructuredDataNode status = new StructuredDataNode(StructuredDataType.text,
			"status", null, "", null, null, null, null, null, null, null, null,
			null, null);

	StructuredData structuredProfile = new StructuredData();
	ArrayList<java.lang.String> messages = new ArrayList<java.lang.String>();

	PhysicianDirectory directory;

	public Physician() {
	}

	public Physician(PhysicianDirectory directory) {
		this.directory = directory;
		EmoryLocObj mLocObj = new EmoryLocObj(directory.site.toString());
		
		this.structuredProfile.setDefinitionPath(mLocObj.DEFINITION_PATH);
		this.setImage(mLocObj.NO_IMAGE_PATH);
	}

	public void addAddressGroup(StructuredDataNode addressGroup) {
		this.addressGroups.add(addressGroup);
	}

	public void addAddressGroup(StructuredDataNode[] lines) {
		StructuredDataNode address = new StructuredDataNode(
				StructuredDataType.group, "addr", null, null, null, null, null,
				null, null, null, null, null, null, null);
		address.setStructuredDataNodes(lines);
		this.addressGroups.add(address);
	}

	public void addAddressLine(StructuredDataNode addressGroup,
			java.lang.String sLine) {
	}

	public void addAwardGroup(StructuredDataNode[] lines) {
		StructuredDataNode award = new StructuredDataNode(
				StructuredDataType.group, "award-group", null, null, null,
				null, null, null, null, null, null, null, null, null);
		award.setStructuredDataNodes(lines);
		this.awardGroups.add(award);
	}
	
	public void addAwardGroup(StructuredDataNode awardGroup) {
		this.awardGroups.add(awardGroup);
	}

	public void addEducationGroup(StructuredDataNode edGroup) {
		this.educationGroups.add(edGroup);
	}
	
	public void addEducationGroup(StructuredDataNode[] lines) {
		StructuredDataNode education = new StructuredDataNode(
				StructuredDataType.group, "edu-group", null, null, null, null,
				null, null, null, null, null, null, null, null);
		education.setStructuredDataNodes(lines);
		this.educationGroups.add(education);
	}

	public void addHospitalAffiliationGroup(StructuredDataNode affilGroup) {
		this.hospitalAffiliationGroups.add(affilGroup);
	}

	public void addHospitalAffiliationGroup(StructuredDataNode[] affiliations) {
		StructuredDataNode affiliation = new StructuredDataNode(
				StructuredDataType.group, "h-affil-group", null, null, null,
				null, null, null, null, null, null, null, null, null);
		affiliation.setStructuredDataNodes(affiliations);
		this.hospitalAffiliationGroups.add(affiliation);
	}
	
	public void addInterest(java.lang.String sInterest) {
		this.interestSelections.setText(this.interestSelections.getText()
				.concat(Constants.MULTISELECT_DELIM.concat(sInterest)));
	}

	public void addInterest(java.lang.String sInterest, boolean translate) {
		if (translate && directory.aoiMap.containsKey(sInterest)) {
			addInterest(directory.aoiMap.get(sInterest));
		}
		else {
			addInterest(sInterest);
		}
	}

	public void addInterest(java.lang.String[] sInterests) {
		for (java.lang.String sInterest : sInterests) {
			this.addInterest(sInterest);
		}
	}

	public void addLanguage(java.lang.String sLanguage) {
		this.languageSelections.setText(this.languageSelections.getText()
				.concat(Constants.MULTISELECT_DELIM.concat(sLanguage)));
	}

	public void addLanguage(java.lang.String sLanguage, boolean translate) {
		if (translate && directory.langMap.containsKey(sLanguage)) {
			addLanguage(directory.langMap.get(sLanguage));
		}
		else {
			addLanguage(sLanguage);
		}
	}

	public void addLanguage(java.lang.String[] sLanguages) {
		for (java.lang.String sLanguage : sLanguages) {
			this.addLanguage(sLanguage);
		}
	}

	public void addLeadership(java.lang.String sLeadership) {
		StructuredDataNode leadership = new StructuredDataNode(
				StructuredDataType.text, "org-lead", null, sLeadership, null,
				null, null, null, null, null, null, null, null, null);
		this.leadershipList.add(leadership);
	}

	public void addLeadership(java.lang.String[] sLeaderships) {
		for (java.lang.String sLeadership : sLeaderships) {
			this.addLeadership(sLeadership);
		}
	}

	public void addMainAffiliationGroup(StructuredDataNode affilGroup) {
		this.hospitalAffiliationGroups.add(affilGroup);
	}

	public void addMainAffiliationGroup(StructuredDataNode[] affiliations) {
		StructuredDataNode affiliation = new StructuredDataNode(
				StructuredDataType.group, "h-affil-group", null, null, null,
				null, null, null, null, null, null, null, null, null);
		affiliation.setStructuredDataNodes(affiliations);
		this.hospitalAffiliationGroups.add(affiliation);
	}

	public void addPublication(java.lang.String sPublication) {
		StructuredDataNode publication = new StructuredDataNode(
				StructuredDataType.text, "maj-pub", null, sPublication, null,
				null, null, null, null, null, null, null, null, null);
		this.publicationList.add(publication);
	}

	public void addPublication(java.lang.String[] sPublications) {
		for (java.lang.String sPublication : sPublications) {
			this.addPublication(sPublication);
		}
	}

	public void addReferrableSpecialty(java.lang.String sSpecialty) {
		this.referrableSpecialtySelections.setText(this.referrableSpecialtySelections.getText()
				.concat(Constants.MULTISELECT_DELIM.concat(sSpecialty)));
	}

	public void addReferrableSpecialty(java.lang.String[] sSpecialties) {
		for (java.lang.String sSpecialty : sSpecialties) {
			this.addReferrableSpecialty(sSpecialty);
		}
	}

	public void addSpecialtyGroup(StructuredDataNode specGroup) {
		this.specialtyGroups.add(specGroup);
	}

	public void addTitle(java.lang.String sTitle) {
		StructuredDataNode title = new StructuredDataNode(
				StructuredDataType.text, "title", null, sTitle, null, null,
				null, null, null, null, null, null, null, null);
		this.titleList.add(title);
	}

	public void addTitle(java.lang.String[] sTitles) {
		for (java.lang.String sTitle : sTitles) {
			this.addTitle(sTitle);
		}
	}

	public void announceMessages() {
		this.directory.announce("Messages for "+this.getPageTitle());
		for (int i = 0; i < this.messages.size(); i++) {
			this.directory.announce(this.messages.get(i));
		}
		this.directory.announce("Physician status is " + this.status.getText());
		this.directory.announce("");
	}
	
	public java.lang.String getImage() {
		return this.imageFile.getFilePath();
		}

	
	public java.lang.String getPageTitle() {
		java.lang.String pageTitle = this.firstName.getText();
		if (!this.middleName.getText().isEmpty()) {
			pageTitle = pageTitle.concat(Constants.SPACE.concat(this.middleName.getText()));
		}
		pageTitle = pageTitle.concat(Constants.SPACE.concat(this.lastName.getText()));
		if (!this.honorific.getText().isEmpty()) {
			pageTitle = pageTitle.concat(Constants.COMMA.concat(Constants.SPACE.concat(this.honorific.getText())));
		}
		return pageTitle;
	}

	public java.lang.String getParentFolder() {
		java.lang.String parentFolder;
		EmoryLocObj mLocObj = new EmoryLocObj(directory.site.toString());
		parentFolder = new java.lang.String(mLocObj.PROFILE_FOLDER.concat(Constants.SLASH
				.concat(this.lastName.getText().substring(0,1).toLowerCase())));
		return parentFolder;
	}
	
	public Path getPath() {
		Path path = new Path();
		EmoryLocObj mLocObj = new EmoryLocObj(directory.site.toString());
		path.setPath(this.getParentFolder().concat(Constants.SLASH
				.concat(this.getSystemName())));
		path.setSiteName(mLocObj.SITE_NAME);		
		return path;
	}
	
	public Path getPath(java.lang.String assetName) {
		Path path = new Path();
		EmoryLocObj mLocObj = new EmoryLocObj(directory.site.toString());
		path.setPath(mLocObj.PROFILE_FOLDER.concat(Constants.SLASH
				.concat(assetName.substring(0,1).concat(Constants.SLASH
				.concat(assetName)))));
		path.setSiteName(mLocObj.SITE_NAME);		
		return path;
	}

	public Path getPathImage(java.lang.String imageName) {
		Path path = new Path();
		EmoryLocObj mLocObj = new EmoryLocObj(directory.site.toString());
		path.setPath(mLocObj.IMAGE_FOLDER.concat(Constants.SLASH
				.concat(imageName)));
		path.setSiteName(mLocObj.SITE_NAME);		
		return path;
	}

	public java.lang.String getStatus() {
		return this.status.getText();
	}

	public StructuredData getStructuredProfile() {
		ArrayList<StructuredDataNode> rawProfile = new ArrayList<StructuredDataNode>();

		rawProfile.add(this.firstName);
		rawProfile.add(this.middleName);
		rawProfile.add(this.lastName);
		rawProfile.add(this.honorific);
		rawProfile.add(this.affiliation);
		//rawProfile.add(this.academicRank);
		rawProfile.addAll(this.titleList);
		rawProfile.add(this.imageFile); // override from Cascade
		rawProfile.add(this.department); // override from Cascade
		rawProfile.add(this.referrableSpecialtySelections);
		rawProfile.addAll(this.specialtyGroups);
		rawProfile.add(this.interestSelections);
		rawProfile.addAll(this.addressGroups);
		rawProfile.addAll(this.hospitalAffiliationGroups);
		rawProfile.add(this.languageSelections);
		rawProfile.add(this.aboutXHTML); // override from Cascade
		rawProfile.add(this.yearStartedPractice);
		rawProfile.add(this.yearStartedEmory);
		rawProfile.addAll(this.leadershipList);
		rawProfile.addAll(this.publicationList);
		rawProfile.addAll(this.awardGroups);
		rawProfile.addAll(this.educationGroups);
		if (this.videoGroup.getStructuredDataNodes() != null) {
			// rawProfile.add(this.videoGroup);
		} // Override from Cascade
		rawProfile.add(this.personalMessageXHTML); // Override from Cascade
		rawProfile.add(this.generalInfoXHTML); // Override from Cascade
		rawProfile.add(this.locationCodeSelections);
		rawProfile.add(this.practiceName);
		rawProfile.add(this.sex);
		rawProfile.add(this.npi);
		rawProfile.add(this.status);

		StructuredDataNode[] profile = new StructuredDataNode[rawProfile.size()];
		this.structuredProfile.setStructuredDataNodes(rawProfile
				.toArray(profile));
		return this.structuredProfile;
	}
	
	public java.lang.String getSystemName() {
		java.lang.String systemName = this.lastName.getText().concat(Constants.SPACE.concat(this.firstName.getText()));
		if (!this.middleName.getText().isEmpty()){
			systemName = systemName.concat(Constants.SPACE.concat(this.middleName.getText()));
		}
		systemName = systemName.toLowerCase().replaceAll(Constants.REMOVE_CHARS_REGEX, "").replaceAll(Constants.REPLACE_CHARS_REGEX,Constants.HYPHEN); 
		return systemName;		
	}
	
	public boolean isValid() {
		if (this.firstName.getText().isEmpty()
				|| this.lastName.getText().isEmpty()
				|| this.status.getText().isEmpty()) {
			this.messages.add("Invalid physician, can't be written to Cascade.");
			return false;
		} else
			return true;
	};

	public void parseByHeadings(java.lang.String sHTML) throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
		java.lang.String[] breakArray = sHTML.split("<br />");
		Address address = null;
		Education education = null;
		java.lang.String lastEdType = new java.lang.String();
		ArrayList<StructuredDataNode> awardLines = new ArrayList<StructuredDataNode>();
		int which = -1;
		int foundHeading;
		int snipStart;
		int snipEnd;
		EmoryLocObj mLocObj = new EmoryLocObj(directory.site.toString());

		for (java.lang.String htmlChunk : breakArray) {
			Document doc = Jsoup.parse(htmlChunk, "iso-8859-1");
			java.lang.String chunk = doc.body().text()
					.replaceAll("\u00A0", " ").trim();
			if (chunk.isEmpty()) {
				if (address != null) {
					this.addAddressGroup(address.toGroup());
					address = null;
				}
				if (education != null) {
					this.addEducationGroup(education.toGroup());
					education = null;
				}
				if (awardLines.size() > 0) {
					StructuredDataNode[] awardLineNodes = new StructuredDataNode[awardLines
							.size()];
					this.addAwardGroup(awardLines.toArray(awardLineNodes));
					awardLines.clear();
				}
				continue;
			}
			if ((foundHeading = this.directory.findHeading(chunk)) >= 0) {
				which = foundHeading;
				continue;
			}
			switch (which) {
			case 0:
				Element link = doc.select("a").first();
				if (link != null) {
					HospitalAffiliation affilGroup = new HospitalAffiliation();
					java.lang.String affilName = link.text();
					java.lang.String affilLink = link.attr("href");
					if (affilLink.contains("null")
							&& affilName.contains("Spine")) {
						affilLink = Constants.EUOSH_AFFIL_LINK;
					}
					affilLink = affilLink.replace(mLocObj.SITE_ROOT_WEB,
							mLocObj.SITE_ROOT_CASCADE);
					affilLink = affilLink.replace(".html", "");
					affilGroup.setName(affilName, false);
					affilGroup.setLink(affilLink);
					affilGroup.setStatus(Constants.ACTIVE);
					this.addHospitalAffiliationGroup(affilGroup.toGroup());
					}
				break;
			case 1:
				java.lang.String[] partLines = doc.body().html().split("<a");
				java.lang.String part;
				if (partLines.length > 1) {
					part = partLines[0].replaceAll("\u00A0", " ").trim();
				} else {
					part = doc.body().text().replaceAll("\u00A0", " ").trim();
				}
				if (address == null) {
					address = new Address();
				}
				address.setFromLegacy(part);
				
				break;
			case 2:
				this.yearStartedEmory.setText(chunk);
				break;
			case 3:
				if (education == null) {
					education = new Education();
				}
				
				Element ital = doc.select("i").first();
				if (ital != null) {
					education.setType(ital.text().replaceAll("\u00A0", " ").trim());
				} else {
					/* If we've already built an education for, say "Residency" and we hit another line
					 * they must have two (or more) lines under the "Residency" subhead.  Save the first one and 
					 * start a new one with the same type as the previous.
					 */
					if (education.isValid()) {
						lastEdType = education.getType();
						this.addEducationGroup(education.toGroup());
						education = new Education();
						education.setType(lastEdType);
					}
					/*
					 * If the education line ends in a year, like "Boston College, Boston, MA 2001"
					 * split the year off and store accordingly.  Otherwise, just call it all Name.
					 */
					if (chunk.substring(chunk.length()-4).matches(
							Constants.YEAR_PATTERN)) {
						education.setYear(chunk.substring(chunk.length()-4));
						chunk = chunk.substring(0,chunk.length()-4).trim();
						education.setName(chunk.substring(0,chunk.length()-(chunk.endsWith(Constants.COMMA)?1:0)));
					}
					else {
						education.setName(chunk);
					}
				}
				break;
			case 4:
				this.addLeadership(chunk);
				break;
			case 5:
				StructuredDataNode awardLine = new StructuredDataNode(
						StructuredDataType.text, null, null, null, null, null,
						null, null, null, null, null, null, null, null);
				if (awardLines.size() > 0) {
					awardLine.setIdentifier("award-org");
				} else {
					awardLine.setIdentifier("award");
				}
				awardLine.setText(chunk);
				awardLines.add(awardLine);
				break;
			case 6:
				this.addPublication(chunk);
				break;
			case 7:
				this.addLanguage(chunk);
				break;
			case 8:
				Specialty specialty = new Specialty();
				snipStart = chunk.indexOf(Constants.BOARD_PATTERN);
				if (snipStart > 0) {
					specialty.setName(chunk.substring(0,snipStart));
					snipEnd = snipStart + Constants.BOARD_PATTERN.length();
					specialty.setYear(chunk.substring(snipEnd, snipEnd + 4));
					this.addReferrableSpecialty(chunk.substring(0,snipStart));
				}
				else {
					specialty.setName(chunk);
					this.addReferrableSpecialty(chunk);					
				}
				specialty.setRefers(Constants.YES);
				specialty.setPrimary(Constants.YES);
				this.addSpecialtyGroup(specialty.toGroup());
				break;
			case 9:
				this.addInterest(chunk);
				break;
			default:
				break;
			}
		}
		if (address != null) {
			this.addAddressGroup(address.toGroup());
			if (address.latitude.getText().matches("NaN") || address.longitude.getText().matches("NaN")) {
				this.messages.add("Address did not geocode.");
			}
			address = null;
		}
		if (education != null) {
			this.addEducationGroup(education.toGroup());
			education = null;
		}
		if (awardLines.size() > 0) {
			StructuredDataNode[] awardLineNodes = new StructuredDataNode[awardLines
					.size()];
			this.addAwardGroup(awardLines.toArray(awardLineNodes));
			awardLines.clear();
		}
	}

	/*
	 * Convert image name to profile asset name
	 * Attempt to update that profile
	 * If successful, attempt to replace the image file in that profile
	 */
	public void replaceImage(File imageFile) throws Exception {
		java.lang.String[] parsed;
		java.lang.String imageFileName = imageFile.getName();
		parsed = imageFileName.split("\\.");
		// Read the matching entry from Cascade and plug those values in instead
		Identifier toRead = new Identifier();
		toRead.setPath(this.getPath(parsed[0]));
		toRead.setType(EntityTypeString.page);

		Authentication authentication = new Authentication();
		authentication.setPassword(this.directory.password);
		authentication.setUsername(this.directory.username);

		EmoryLocObj mLocObj = new EmoryLocObj(directory.site.toString());

		AssetOperationHandlerServiceLocator serviceLocator = new AssetOperationHandlerServiceLocator();
		serviceLocator.setAssetOperationServiceEndpointAddress(mLocObj.CASCADE_ENDPOINT);
		
		AssetOperationHandler handler = serviceLocator
				.getAssetOperationService();
		ReadResult result = handler.read(authentication, toRead);
		this.directory.announce("Read of " + parsed[0] + " was "
				+ (result.getSuccess().equals("true") ? "successful."
						: "unsuccessful. Error is: " + result.getMessage()));
		if (result.getSuccess().equals("true")) {
			/* try to upload the image file */
			Identifier toCreate = new Identifier();
			Path imagePath = getPathImage(imageFile.getName());
			toCreate.setPath(imagePath);
			toCreate.setType(EntityTypeString.file);
			Asset asset = new Asset();
			com.hannonhill.www.ws.ns.AssetOperationService.File imageAssetFile = new com.hannonhill.www.ws.ns.AssetOperationService.File();
			Metadata metadata = new Metadata();
			
			imageAssetFile.setSiteName(mLocObj.SITE_NAME);
			imageAssetFile.setParentFolderPath(mLocObj.IMAGE_FOLDER);
			imageAssetFile.setName(imageFile.getName());
			imageAssetFile.setMetadata(metadata);
			imageAssetFile.setPath(imagePath.getPath());
			imageAssetFile.setData(Files.readAllBytes(imageFile.toPath()));
			asset.setFile(imageAssetFile);						
			OperationResult imageResult = handler.edit(authentication, asset);
			if (imageResult.getSuccess().equals("true")) {
				this.directory.announce("Update of " + imageFile.getName() + " was successful.");
			}
			else {
				imageAssetFile.setPath(null);
				imageResult = handler.create(authentication, asset);
				if (imageResult.getSuccess().equals("true")) {
						this.directory.announce("Upload of " + imageFile.getName() + " was successful.");
				} else {
					this.directory.announce("Upload of " + imageFile.getName() + " was unsuccessful. Error is: " + imageResult.getMessage());						
					return;
				}
			}
			StructuredDataNode[] pageNodes = result.getAsset().getPage()
					.getStructuredData().getStructuredDataNodes();
			for (StructuredDataNode node : pageNodes) {
				if (node.getIdentifier().equals("image")) {
					node.setFileId(null);
					node.setFilePath(imagePath.getPath());
					break;
				}
			}
			OperationResult editResult = handler.edit(authentication, result.getAsset());
			this.directory.announce("Edit was "
					+ (editResult.getSuccess().equals("true") ? "successful."
							: "unsuccessful. Error is: " + editResult.getMessage()));
		}
		return;
	}
	public void setAbout(java.lang.String sAbout) {
		this.aboutXHTML.setText(sAbout);
	}

//	public void setAcademicRank(java.lang.String sAcademicRank) {
//		this.academicRank.setText(sAcademicRank);
//	}

	public void setAffiliation(java.lang.String sAffiliation) {
		this.affiliation.setText(sAffiliation);
	}
	
	public void setAffiliation(java.lang.String sAffiliation, boolean translate) {
		if (translate && directory.affilMap.containsKey(sAffiliation)) {
			setAffiliation(directory.affilMap.get(sAffiliation));
	}
	else {
		setAffiliation(sAffiliation);
	}

	}

	public void setDepartment(java.lang.String sDepartment) {
		this.department.setText(sDepartment);
	}

	public void setEducation(java.lang.String sEducation) {
	}

	public void setFirstName(java.lang.String sFirstName) {
		this.firstName.setText(sFirstName);
	}

	public void setGeneralInfo(java.lang.String sGeneralInfo) {
		this.generalInfoXHTML.setText(sGeneralInfo);
	}

	public void setHonorific(java.lang.String sHonorific) {
		this.honorific.setText(sHonorific);
	}

	public void setHonorific(java.lang.String sHonorific, boolean translate) {
		if (translate && directory.titleMap.containsKey(sHonorific)) {
			this.setHonorific(directory.titleMap.get(sHonorific));
		}
		else {
			this.setHonorific(sHonorific);
		}
	}

	public void setImage(java.lang.String sImage) {
		EmoryLocObj mLocObj = new EmoryLocObj(directory.site.toString());
		if (sImage.isEmpty()) {
			this.imageFile.setFilePath(mLocObj.NO_IMAGE_PATH);
			}
			else {
				this.imageFile.setFilePath(sImage.replace("..", mLocObj.SITE_ROOT_CASCADE));
			}
		}

	public void setInterests(java.lang.String sInterests) {
		this.interestSelections.setText(sInterests);
	}

	public void setLanguages(java.lang.String sLanguages) {
		this.languageSelections.setText(sLanguages);
	}

	public void setLastName(java.lang.String sLastName) {
		this.lastName.setText(sLastName);
	};

	public void setLeadershipList(java.lang.String sLeadership) {
		this.leadershipList.clear();
		this.addLeadership(sLeadership);
	}

	public void setLeadershipList(java.lang.String[] sLeaderships) {
		this.leadershipList.clear();
		this.addLeadership(sLeaderships);
	}
	
	public void setLocationCode(java.lang.String sLocationCode) {
		this.locationCodeSelections.setText(sLocationCode);
	}

	public void setMiddleName(java.lang.String sMiddleName) {
		this.middleName.setText(sMiddleName);
	};

	public void setNPI(java.lang.String sNPI) {
			this.npi.setText(sNPI);
	};

	public void setNPI(java.lang.String sNPI, boolean translate) {
		if (translate && directory.NPIMap.containsKey(sNPI)) {
			this.npi.setText(directory.NPIMap.get(sNPI));
		}
		else {
			this.setNPI(sNPI);
		}
	};

	public void setPersonalMessage(java.lang.String sPersonalMessage) {
		this.personalMessageXHTML.setText(sPersonalMessage);
	}

	public void setPracticeName(java.lang.String sPracticeName) {
		this.practiceName.setText(sPracticeName);
	}

	public void setPublicationList(java.lang.String sPublication) {
		this.publicationList.clear();
		this.addPublication(sPublication);
	}

	public void setPublicationList(java.lang.String[] sPublications) {
		this.publicationList.clear();
		this.addPublication(sPublications);
	}

	public void setSex(java.lang.String sSex) {
		this.sex.setText(sSex);
	}

	public void setSpecialities(java.lang.String sSpecialities) {
		this.referrableSpecialtySelections.setText(sSpecialities);
	}

	public void setStatus(java.lang.String sStatus) {
		this.status.setText(sStatus);
	}

	public void setTitleList(java.lang.String sTitle) {
		this.titleList.clear();
		this.addTitle(sTitle);
	}

	public void setTitleList(java.lang.String[] sTitles) {
		this.titleList.clear();
		this.addTitle(sTitles);
	}

	public void setTitles(java.lang.String sHTML) {
		java.lang.String[] breakArray = sHTML.split("<br />");
		for (java.lang.String chunk : breakArray) {
			this.addTitle(chunk);
		}
	}

	public void setVideoGroup(StructuredDataNode[] videoGroup) {
		for (StructuredDataNode node : videoGroup) {
			node.setFileId(null);
		}
		this.videoGroup.setStructuredDataNodes(videoGroup.clone());
	}

	public void setYearStartedEmory(java.lang.String sYearStartedEmory) {
		this.yearStartedEmory.setText(sYearStartedEmory);
	}

	public void setYearStartedPractice(java.lang.String sYearStartedPractice) {
		this.yearStartedPractice.setText(sYearStartedPractice);
	}

	public void updateFromCascade() throws Exception {
		// First, clear out all the values we're supposed to override from Cascade
		for (int i = 0; i < this.directory.overrides.size(); i++) {
			if (this.directory.elementIDtoIntMap.containsKey(this.directory.overrides.get(i))) {
				switch (this.directory.elementIDtoIntMap.get(this.directory.overrides.get(i))) {
				case 10: // edu-group
					this.educationGroups.clear();
					break;
				case 11: // honorific
					this.honorific.setText("");
					break;
				case 12: // affil
					this.affiliation.setText("");
					break;
				case 13: // special-group
					this.specialtyGroups.clear();
					break;
				case 14: // special
					this.setSpecialities("");
					break;
				case 15: // clinical
					this.interestSelections.setText("");
					break;
				case 16: // addr
					this.addressGroups.clear();
					break;
				case 17: // h-affil-group
					this.hospitalAffiliationGroups.clear();
					break;
				case 18: // lang
					this.languageSelections.setText("");
					break;
				case 19: // title
					this.titleList.clear();
					break;
				case 20: // yr-prac
					this.yearStartedPractice.setText("");
					break;
				case 21: // yr-prac-edu
					this.yearStartedEmory.setText("");
					break;
				case 22: // loc-code
					this.locationCodeSelections.setText("");
					break;
				case 23: // med-name
					this.practiceName.setText("");
					break;
				case 24: // npi
					this.npi.setText("");
					break;
				case 25: // status
					this.status.setText("");
					break;
				default: // fields that are not found in Sharp Focus: no need to initialize
					break;
				}
			}
		}
		
		// Then, read the matching entry from Cascade and plug those values in instead
		Identifier toRead = new Identifier();
		toRead.setPath(this.getPath());
		toRead.setType(EntityTypeString.page);

		Authentication authentication = new Authentication();
		authentication.setPassword(this.directory.password);
		authentication.setUsername(this.directory.username);

		EmoryLocObj mLocObj = new EmoryLocObj(directory.site.toString());

		AssetOperationHandlerServiceLocator serviceLocator = new AssetOperationHandlerServiceLocator();
		serviceLocator.setAssetOperationServiceEndpointAddress(mLocObj.CASCADE_ENDPOINT);
		
		AssetOperationHandler handler = serviceLocator
				.getAssetOperationService();
		ReadResult result = handler.read(authentication, toRead);
		this.messages.add("Read of " + this.getSystemName() + " was "
				+ (result.getSuccess().equals("true") ? "successful."
						: "unsuccessful. Error is: " + result.getMessage()));
		if (result.getSuccess().equals("true")) {
			StructuredDataNode[] pageNodes = result.getAsset().getPage()
					.getStructuredData().getStructuredDataNodes();
			for (StructuredDataNode node : pageNodes) {
				if (this.directory.overrides.indexOf(node.getIdentifier()) != -1) {
					switch (this.directory.elementIDtoIntMap.get(node.getIdentifier())) {
					case 0:
						this.setDepartment(node.getText());
						break;

					case 1:
						if (node.getFilePath() != null) {
							this.setImage(node.getFilePath());
						}
						break;

					case 2:
						this.setVideoGroup(node.getStructuredDataNodes());
						break;

					case 3:
						this.setAbout(node.getText());
						break;

					case 4:
						this.setPersonalMessage(node.getText());
						break;

					case 5:
						this.setGeneralInfo(node.getText());
						break;

					case 6:
						this.setSex(node.getText());
						break;
					
					case 7:
						this.addLeadership(node.getText());
						break;
						
					case 8:
						this.addPublication(node.getText());
						break;
						
					case 9:
						decycleNode(node);
						this.addAwardGroup(node);
						break;
						
					case 10:
						decycleNode(node);
						this.addEducationGroup(node);
						break;
						
					case 11:
						this.setHonorific(node.getText());
						break;
						
					case 12:
						this.setAffiliation(node.getText());
						break;
						
					case 13:
						decycleNode(node);
						this.addSpecialtyGroup(node);
						break;
						
					case 14:
						this.setSpecialities(node.getText());
						break;
						
					case 15:
						this.setInterests(node.getText());
						break;
						
					case 16:
						decycleNode(node);
						this.addAddressGroup(node);
						break;
						
					case 17:
						decycleNode(node);
						this.addHospitalAffiliationGroup(node);
						break;
						
					case 18:
						this.setLanguages(node.getText());
						break;
						
					case 19:
						this.setTitleList(node.getText());
						break;
						
					case 20:
						this.setYearStartedPractice(node.getText());
						break;
						
					case 21:
						this.setYearStartedEmory(node.getText());
						break;
						
					case 22:
						this.setLocationCode(node.getText());
						break;
						
					case 23:
						this.setPracticeName(node.getText());
						break;
						
					case 24:
						this.setNPI(node.getText());
						break;
						
					case 25:
						this.setStatus(node.getText());
						break;

					default:
						// do nothing
						break;
					}
				}
			}
		}
	}
	
	/*
	 * Take an asset name to find which profile in Cascade to update
	 */
	public void updateFromCascade (java.lang.String assetName) throws Exception {
	}

	public boolean updateFromLegacy(java.lang.String sPhysicianId,
			java.lang.String sFirstName, java.lang.String sMiddleName,
			java.lang.String sLastName, java.lang.String sHonorific, File folder)
			throws Exception {
		this.firstName.setText(sFirstName);
		this.middleName.setText(sMiddleName);
		this.lastName.setText(sLastName);
		this.honorific.setText(sHonorific);
		this.messages.add("Processing legacy profile.");
		File legacyProfile = new File(folder, Constants.FILENAME_BEFORE_ID
				+ sPhysicianId);
		if (!legacyProfile.canRead()) {
			this.messages.add("Legacy profile not found.");
			return false;
		}
		Document doc = Jsoup.parse(legacyProfile, "iso-8859-1");
		Elements profile = doc.select("div#content table").eq(0);
		this.affiliation.setText(profile.select("tr:eq(0) td:eq(0) b").eq(1)
				.html());
		this.setTitles(profile.select("tr:eq(0) td:eq(0) b").eq(2)
				.html());
		this.setImage(profile.select("tr:eq(0) td:eq(1) font img").attr("src"));
		this.parseByHeadings(profile.select("tr:eq(1) td:eq(0)").html());
		this.parseByHeadings(profile.select("tr:eq(2) td:eq(0)").html());
		// this.setSex("Male"); // just testing!

		// do not go further if the legacy source isn't a valid physician
		if (!this.isValid()) {
			return false;
		}

		this.updateFromCascade();
		this.setStatus(Constants.ACTIVE);
		return true;
	}

	public boolean updateFromSharpFocus(java.lang.String line) throws Exception {
		return false;
	}
	
	public boolean updateFromSharpFocus(List<java.lang.String> line) throws Exception {		
		/* If first name is "Dr.", it is an internal record for HealthConnection use, not a real doctor, discard it.
		 * Otherwise, First Name field can contain middle name(s) separated by spaces
		 */
		int i;
		for (i=0; i < line.size(); i++) {
			line.set(i, line.get(i).trim());
		}
		
		if (Constants.DR.equals(line.get(Constants.SHARP_FOCUS_FIRST_NAMES))) {
			return false;
		}
		
		if ((i = line.get(Constants.SHARP_FOCUS_FIRST_NAMES).indexOf(Constants.SPACE)) > 0) {
			this.setFirstName(line.get(Constants.SHARP_FOCUS_FIRST_NAMES).substring(0,i));
			this.setMiddleName(line.get(Constants.SHARP_FOCUS_FIRST_NAMES).substring(i+1));
		}
		else {
			this.setFirstName(line.get(Constants.SHARP_FOCUS_FIRST_NAMES));
		}
		
		this.setLastName(line.get(Constants.SHARP_FOCUS_LAST_NAME));
		
		/*
		 * Georgia license number, called "ID" in SharpFocus, translates to NPI using the NPIxref
		 */
		this.setNPI(line.get(Constants.SHARP_FOCUS_ID), true);

		// Up to 5 addresses have their parts (line1, line2, city, state, zip, phone) interleaved
		for (i = Constants.SHARP_FOCUS_ADDRESSES_START; i < Constants.SHARP_FOCUS_ADDRESSES_END; i++) {
			if (line.get(i).length() > 0) {
				Address address = new Address();
				address.addLine(line.get(i),true);
				address.addLine(line.get(i+5),true);
				address.setCity(line.get(i+10));
				address.setState(line.get(i+15));
				address.setZip(line.get(i+20));
				address.setPhone(line.get(i+25));
				if (address.isValid()) {
					this.addAddressGroup(address.toGroup());
				}
			}
		}
		
		/*
		 * Main practice affiliation.
		 * If every affiliation with a name is marked INACTIVE, the doctor is inactive, so loop through looking
		 * for affiliations with names and status other than INACTIVE.  If any found, doctor is ACTIVE.  If any found
		 * with status ACTIVE and a non-empty primary affiliation field, that's the primary affiliation and we can stop
		 * looking.
		 */
		
		this.setStatus(Constants.INACTIVE);
		for (i = Constants.SHARP_FOCUS_AFFILIATIONS_START; i < Constants.SHARP_FOCUS_AFFILIATIONS_END; i++) {
			if (!line.get(i).isEmpty() && !line.get(i+4).equals(Constants.INACTIVE) ) {
				this.setStatus(Constants.ACTIVE);
				if (line.get(i+4).equals(Constants.ACTIVE) && !line.get(i+12).isEmpty()) {
				this.setAffiliation(line.get(i), true);
				break;
				}
			}
		}
		
		/*
		 * Education	
		 */
			for (i = Constants.SHARP_FOCUS_EDUCATIONS_START; i < Constants.SHARP_FOCUS_EDUCATIONS_END; i++) {
				if (line.get(i).length() > 0) {
					Education edu = new Education();
					edu.setName(line.get(i));
					edu.setType(line.get(i+8));
					this.addEducationGroup(edu.toGroup());
				}
			}
							
		/*
		 * Specialties need to be sorted out with primary specialties first, then secondaries, so they display in 
		 * the right order.
		 * In addition, a copy of only specialties for which the physician accepts referrals are loaded into the field 
		 * that is searchable in Find a Physician
		 */
		ArrayList<java.lang.String> referrals = new ArrayList<java.lang.String>();
		for (i = Constants.SHARP_FOCUS_SPECIALTIES_START; i < Constants.SHARP_FOCUS_SPECIALTIES_END; i++) {
			if (line.get(i).length() > 0) {
				// If referrable or primary, add to that list
				if (line.get(i+8).equals(Constants.YES) || line.get(i+4).equals(Constants.YES)){
					referrals.add(line.get(i));
				}

				Specialty specialty = new Specialty();
				specialty.setName(line.get(i));
				specialty.setPrimary(line.get(i+4));
				specialty.setRefers(line.get(i+8));
				specialty.setYear(line.get(i+12));
				this.addSpecialtyGroup(specialty.toGroup());				
			}
		}
				
		java.lang.String[] referArray = new java.lang.String[referrals.size()];
		this.addReferrableSpecialty(referrals.toArray(referArray));
			
		/*
		 * Year started practicing at Emory
		 */
		this.setYearStartedEmory(line.get(Constants.SHARP_FOCUS_EMORY_PRAC));
		
		/*
		 * Year started practicing anywhere
		 */
		this.setYearStartedPractice(line.get(Constants.SHARP_FOCUS_GENERAL_PRAC));
		
		/*
		 * The "Notes" field has two lists crammed into it: Academic titles and Hospital Affiliations
		 * so we have to parse those out based on agreed-upon syntax.
		 * Everything before the first "Title:" is ignored
		 * Everything between the first "Title:" and the next "Privs:" is split at the semicolons
		 * 	and each bit is a title
		 * Everything between the next "Privs:" and ";;" is split at the semicolons
		 * 	and each bit is a hospital affiliation
		 */
		int titleStart = line.get(Constants.SHARP_FOCUS_NOTES).indexOf(Constants.TITLES);
		int privStart = line.get(Constants.SHARP_FOCUS_NOTES).indexOf(Constants.PRIVS,titleStart>0?titleStart:0);
		int privEnd = line.get(Constants.SHARP_FOCUS_NOTES).indexOf(Constants.SEMIS,privStart>0?privStart:line.get(Constants.SHARP_FOCUS_NOTES).length());
		
		if (titleStart >= privStart || privStart >= privEnd) {
			this.messages.add(Constants.ERROR_BAD_NOTES);
		}
		else {
			if (titleStart >= 0) {
				for (java.lang.String chunk : line.get(Constants.SHARP_FOCUS_NOTES).substring(titleStart+Constants.TITLES.length(), privStart).split(Constants.SEMI)) {
					this.addTitle(chunk.trim());
				}			
			}
		
			if (privStart >= 0) {
				for (java.lang.String chunk : line.get(Constants.SHARP_FOCUS_NOTES).substring(privStart+Constants.PRIVS.length(), privEnd).split(Constants.SEMI)) {
					HospitalAffiliation affil = new HospitalAffiliation();
					affil.setName(chunk.trim(), true);
					affil.setStatus(Constants.ACTIVE);
					this.addHospitalAffiliationGroup(affil.toGroup());
				}
			}
		}
		
		/*
		 * Areas of Interest are just a big series of fields
		 */
		for (i = Constants.SHARP_FOCUS_AOIS_START; i < Constants.SHARP_FOCUS_AOIS_END; i++) {
			if (line.get(i).length() > 0) {
				this.addInterest(line.get(i), true);
			}
		}
		
		/*
		 * Title is a single field, but it needs translations, and it goes into "honorific", not "title"
		 */
		this.setHonorific(line.get(Constants.SHARP_FOCUS_TITLE), true);

		/*
		 * Foreign languages spoken is a series of 3 fields
		 */
		for (i = Constants.SHARP_FOCUS_LANGUAGES_START; i < Constants.SHARP_FOCUS_LANGUAGES_END; i++) {
			if (line.get(i).length() > 0) {
				this.addLanguage(line.get(i), true);
			}
		}

		return true;
	
	}

	public void writeToCascade() throws Exception {
		if (!this.isValid()) {
			return;
		}
		Asset asset = new Asset();
		Page page = new Page();
		Metadata metadata = new Metadata();
		java.lang.String title = this.getPageTitle();
		java.lang.String pageName = this.getSystemName();
		
		EmoryLocObj mLocObj = new EmoryLocObj(directory.site.toString());
		page.setContentTypePath(mLocObj.TYPE_PATH);
		page.setSiteName(mLocObj.SITE_NAME);

		metadata.setDisplayName(title);
		metadata.setTitle(title);

		page.setParentFolderPath(this.getParentFolder());
		page.setName(pageName);
		page.setMetadata(metadata);
		page.setStructuredData(this.getStructuredProfile());
		page.setPath(this.getPath().getPath());

		asset.setPage(page);

		Authentication authentication = new Authentication();
		authentication.setUsername(this.directory.username);
		authentication.setPassword(this.directory.password);
		//authentication.setPassword(Constants.PASSWORD);
		//authentication.setUsername(Constants.USER_NAME);
		
		AssetOperationHandlerServiceLocator serviceLocator = new AssetOperationHandlerServiceLocator();
		serviceLocator.setAssetOperationServiceEndpointAddress(mLocObj.CASCADE_ENDPOINT);
		AssetOperationHandler handler = serviceLocator
				.getAssetOperationService();
		
		/* Try to pair up with a matching image already in Cascade.  If no image in
		 * physician data, build the canonical image name and try it out.
		 */
		if (this.getImage().equals(mLocObj.NO_IMAGE_PATH)) {
			this.setImage(mLocObj.IMAGE_FOLDER.concat(Constants.SLASH).concat(pageName).concat(".jpg"));
		}

		OperationResult editResult = handler.edit(authentication, asset);
		this.messages.add("Edit was "
				+ (editResult.getSuccess().equals("true") ? "successful."
						: "unsuccessful. Error is: " + editResult.getMessage()));
		
		/* If failed because image is no longer in Cascade, just point to the no-image-found-image and try again */
		if (editResult.getSuccess().equals("false") && editResult.getMessage().contains(mLocObj.ERROR_MISSING_IMAGE)) {
			this.setImage(mLocObj.NO_IMAGE_PATH);
			this.messages.add("Retrying edit with default image.");
			editResult = handler.edit(authentication, asset);
			this.messages.add("Edit was "
					+ (editResult.getSuccess().equals("true") ? "successful."
							: "unsuccessful. Error is: " + editResult.getMessage()));
		}
		
		if (editResult.getSuccess().equals("false")) {
			page.setPath("");
			CreateResult createResult = handler.create(authentication, asset);
			this.messages.add("Create was "
							+ (createResult.getSuccess().equals("true") ? "successful."
									: "unsuccessful. Error is: "
											+ createResult.getMessage()));

			/* If failed because image is no longer in Cascade, just point to the no-image-found-image and try again */
			if (createResult.getSuccess().equals("false") && createResult.getMessage().contains(mLocObj.ERROR_MISSING_IMAGE)) {
				this.setImage(mLocObj.NO_IMAGE_PATH);
				this.messages.add("Retrying create with default image.");
				createResult = handler.create(authentication, asset);
				this.messages.add("Create was "
						+ (createResult.getSuccess().equals("true") ? "successful."
								: "unsuccessful. Error is: " + createResult.getMessage()));
			}
		}
	}
	
	public void decycleNode(StructuredDataNode node) {
		StructuredDataNode[] nodes = node.getStructuredDataNodes();
		if (nodes != null) {
			for (StructuredDataNode subNode : nodes) {
				decycleNode(subNode);
			}
		}
		node.setRecycled(null);		
	}

}