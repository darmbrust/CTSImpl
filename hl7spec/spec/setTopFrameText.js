function setHeadingTextFromFileName(href) {
	var lastSlash = href.lastIndexOf('/');
	var fileName = href.substr(lastSlash);
	var lastPeriod = href.lastIndexOf('.');
	var fileName = href.substring(lastSlash +1, lastPeriod);
	var domainsHash = getDomainsHash();  
	var uppercaseFileName = fileName.toUpperCase();
	var baseFileName = uppercaseFileName.substring(0,6);
	var artifactID = uppercaseFileName.substring(7,9);
	var artifactType = fileName.substring(10,12);
	if(hashKeyExists(baseFileName, domainsHash)){
	      var domainLabel = getHashValue(baseFileName,domainsHash);
		if (fileName.length == 6){
			setHeadingText(domainLabel);
		}
		else if (fileName.length > 6){
			var artifactIDHash = getArtifactIDHash();
			var artifactTypeHash = getArtifactTypeHash();
			if(artifactID != 'DO'){
				var baseClassName = getCamelCase(fileName);
				setHeadingText(domainLabel + "<br>" + baseClassName);
			}else{
				var artifactIDLabel = getHashValue(artifactID,artifactIDHash);
				var artifactTypeLabel =getHashValue(artifactType,artifactTypeHash);
				setHeadingText(domainLabel + "<br>" + artifactIDLabel + ": " + artifactTypeLabel);
			}
		}
	}
	else{
			var foundationsHash = getFoundationsHash();
			var foundationLabel=getHashValue(uppercaseFileName,foundationsHash);
			setHeadingText(foundationLabel);
		}
	
}
function getDomainsHash(){
	var domains = [      
		{key: "IMMCCI",  value: 'Domain: Transmission Infrastructure'},                    
		{key: "IMMCAI",  value: 'Domain: Message Control Act Infrastructure'},                    
		{key: "IMQUQI",  value: 'Domain: Query Infrastructure'},                    
		{key: "IMMFMI",  value: 'Domain: Master File Infrastructure'},                    
		{key: "IMCOCT",  value: 'Domain: Common Message Element Types'},                    
		{key: "IMCOMT",  value: 'Domain: Shared  Messages'},                    
		{key: "AMFIAB",  value: 'Domain: Account and Billing'},                    
		{key: "AMFICR",  value: 'Domain: Claims &amp; Reimbursement'},                    
		{key: "AMPRPA",  value: 'Domain: Patient Administration'},                    
		{key: "AMPRPM",  value: 'Domain: Personnel Management'},                    
		{key: "AMPRSC",  value: 'Domain: Scheduling'},                    
		{key: "HMPOLB",  value: 'Domain: Laboratory'},     
		{key: "HMPOCG", value: 'Domain: Clinical Genomics'},               
		{key: "HMRCMR",  value: 'Domain: Medical Records'},                    
		{key: "HMREPC",  value: 'Domain: Patient Care'},                    
		{key: "HMPORX",  value: 'Domain: Pharmacy'},
		{key: "HMPORR",  value: 'Domain: Public Health Reporting'},
		{key: "HMPORI",  value: 'Domain: Informative Public Health Specifications'}, 
		{key: "HMPORT",  value: 'Domain: Regulated Studies'}
		]
	return domains;
}
function getArtifactIDHash(){
	var artifactIDHash = [ 
		{key: "DO",  value: 'Domain'},
		{key: "SB",  value: 'Message Definitions'},
		{key: "QU",  value: 'Query Based'},
		{key: "MF",  value: 'Master Files'}
		]
	return artifactIDHash;
}
function getArtifactTypeHash(){
	var artifactTypeHash = [ 
		{key: "CT",  value: 'CMETs'},
		{key: "CM",  value: 'Refined Message Information Models / Hierarchical Message Descriptions'},
		{key: "DM",  value: 'Domain Message Information Model'},
		{key: "ST",  value: 'Story Boards'},
		{key: "AR",  value: 'Application Roles'},
		{key: "HD",  value: 'Hierarchical Message Descriptions'},
		{key: "IN",  value: 'Interactions'},
		{key: "XX",  value: 'Interaction Indexes'},
		{key: "TE",  value: 'Trigger Events'},
		{key: "RM",  value: 'Refined Message Information Models'}
		]
	return artifactTypeHash;
}

function getFoundationsHash(){
	var foundationsHash = [
		{key: "VOCABULARY",  value: 'Vocabulary'},                    
		{key: "BACKBONE",  value: 'Introduction/Backbone'},                   
		{key: "V3GUIDE",  value: 'HL7 V3 Guide'},                    
		{key: "RIM",  value: 'Reference Information Model'},                    
		{key: "CONFORMANCE",  value: 'Refinement, Constraint and Localization'},                    
		{key: "GLOSSARY",  value: 'Glossary'},                    
		{key: "DATATYPES",  value: 'Data Types: Abstract'},                    
		{key: "DATATYPES-ITS-XML",  value: 'ITS: XML Data Types'},                    
		{key: "MESSAGING-ITS-XML",  value: 'ITS: XML Structures'},    
		{key: "TRANSPORT-INTRO",  value: 'Transport Specifications'},        
		{key: "TRANSPORT-EBXML", value: 'Transport Specifications: ebXML'},
		{key: "TRANSPORT-MLLP", value: 'Transport Specifications: MLLP'},
		{key: "TRANSPORT-WSPROFILES", value: 'Transport Specifications: SOAP/WSDL Profiles'},
		{key: "DATATYPES-ITS-UML",  value: 'ITS: UML Data Types'}
		]
return foundationsHash;
}

function hashKeyExists(key, hashTable){
    	 for (var n in hashTable) {
        	 if (hashTable[n].key == key)
            	 return true;
    	}
    	return false;
}

function getHashValue(key, hashTable){
    	 for (var n in hashTable) {
        	 if (hashTable[n].key == key)
            	 return hashTable[n].value;
    	}
	return '';
}
function setHeadingText(text) {
	top.heading.document.getElementById('headingText').innerHTML=text;
}
function find(what) {
    for (var i=0;i<parent.frames.length;i++) {
         if (parent.frames[i].name == what)
             return true;
    }
    return false;
}
function getCamelCase(fileName){
				var camelCase = fileName.substr(7,fileName.length);
				var txt;
				for (i =0;i<=camelCase.length;i++){
					if (i ==0){
						txt = camelCase.substr(i,1);
					}else{
						var c = camelCase.substr(i,1);
						if(c=='A'||c=='B'||c=='C'||c=='D'||c=='E'||c=='F'||c=='G'||c=='H'||c=='I'||c=='J'||c=='K'||c=='L'||c=='M'||c=='N'||c=='O'||c=='P'||c=='Q'||c=='R'||c=='S'||c=='T'||c=='U'||c=='V'||c=='W'||c=='X'||c=='Y'||c=='Z'){
							txt =  txt + " " + c;
						}
						else{
							txt =  txt + c;
						}
						
					}

				}
				return txt;
}