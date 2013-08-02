package org.emoryhealthcare.physiciandirectory;

public class EmoryLocObj
{
	
    public java.lang.String DEFINITION_PATH, IMAGE_FOLDER,NO_IMAGE_PATH,SITE_NAME,TYPE_PATH,PROFILE_FOLDER,ERROR_MISSING_IMAGE,SITE_ROOT_CASCADE,SITE_ROOT_WEB,CASCADE_ENDPOINT;
   
    	
    public EmoryLocObj(java.lang.String pLocation) {
        java.lang.String lowered = pLocation.toLowerCase().trim();
        	
    	if(lowered.equals("saint josephs hospital atlanta"))
    		{
    			this.DEFINITION_PATH = "Profile";
    			this.IMAGE_FOLDER = "/img/physician-photos";
    			this.NO_IMAGE_PATH = "/img/physician-photos/nophoto.jpg";
    			this.PROFILE_FOLDER = "/physicians";
    			this.SITE_NAME = "SJHA";
    			this.TYPE_PATH = "Physician";
    			this.ERROR_MISSING_IMAGE = "file with path/name: ";
    			this.SITE_ROOT_CASCADE = "/";
    			this.SITE_ROOT_WEB = "http://www.stjosephsatlanta.org";
    			this.CASCADE_ENDPOINT = "http://sjhacms01.sjha.org:9000/ws/services/AssetOperationService";
    		}
    	else if(lowered.equals("emory johns creek hospital"))
        	{
        		this.DEFINITION_PATH = "profile";
    			this.IMAGE_FOLDER = "/files/images/physician-photos";
	    		this.NO_IMAGE_PATH = "/files/images/physician-photos/nophoto.jpg";
	    		this.PROFILE_FOLDER = "physicians";
	    		this.SITE_NAME = "EJCH";
	    		this.TYPE_PATH = "Physician";
	    		this.ERROR_MISSING_IMAGE = "file with path/name: ";
	    		this.SITE_ROOT_CASCADE = "/";
	    		this.SITE_ROOT_WEB = "http://www.emoryjohnscreek.com";
    			this.CASCADE_ENDPOINT = "http://cascade.eushc.org/ws/services/AssetOperationService";
        	}
        else
        	{
        		this.DEFINITION_PATH = "Profile";
    			this.IMAGE_FOLDER = "/img/physician-photos";
	    		this.NO_IMAGE_PATH = "/img/physician-photos/nophoto.jpg";
	    		this.PROFILE_FOLDER = "/physicians";
	    		this.SITE_NAME = "www.emoryhealthcare.org";
	    		this.TYPE_PATH = "Physician";
	    		this.ERROR_MISSING_IMAGE = "file with path/name: ";
	    		this.SITE_ROOT_CASCADE = "/";
	    		this.SITE_ROOT_WEB = "http://www.emoryhealthcare.com";
    			this.CASCADE_ENDPOINT = "http://cascade.eushc.org/ws/services/AssetOperationService";
        	}
        	
    }
    
}