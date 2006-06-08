package org.ejbca.util.cert;

import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEREncodableVector;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERString;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.X509DefaultEntryConverter;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509NameEntryConverter;
import org.ejbca.util.CertTools;

public class SubjectDirAttrExtension extends CertTools {

    private static Logger log = Logger.getLogger(SubjectDirAttrExtension.class);
    
    /**
     * inhibits creation of new SubjectDirAttrExtension
     */
    private SubjectDirAttrExtension() {
    }

    /**
	 * SubjectDirectoryAttributes ::= SEQUENCE SIZE (1..MAX) OF Attribute
	 *
	 * Attribute ::= SEQUENCE {
     *  type AttributeType,
     *  values SET OF AttributeValue }
     *  -- at least one value is required
     * 
     * AttributeType ::= OBJECT IDENTIFIER
     * AttributeValue ::= ANY
     * 
	 * SubjectDirectoryAttributes is of form 
	 * dateOfBirth=<19590927>, placeOfBirth=<string>, gender=<M/F>, countryOfCitizenship=<two letter ISO3166>, countryOfResidence=<two letter ISO3166>
     * 
     * Supported subjectDirectoryAttributes are the ones above 
	 *
	 * @param certificate containing subject directory attributes
	 * @return String containing directoryAttributes of form the form specified above or null if no directoryAttributes exist. 
	 *   Values in returned String is from CertTools constants. 
	 *   DirectoryAttributes not supported are simply not shown in the resulting string.  
	 * @throws java.lang.Exception
	 */
	public static String getSubjectDirectoryAttributes(X509Certificate certificate) throws Exception {
		log.debug("Search for SubjectAltName");
        DERObject obj = CertTools.getExtensionValue(certificate, X509Extensions.SubjectDirectoryAttributes.getId());
        if (obj == null) {
            return null;
        }
        ASN1Sequence seq = (ASN1Sequence)obj;
        
        String result = "";
        String prefix = "";
		SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMdd");
        for (int i = 0; i < seq.size(); i++) {
        	Attribute attr = Attribute.getInstance(seq.getObjectAt(i));
        	if (!StringUtils.isEmpty(result)) {
        		prefix = ", ";
        	}
        	if (attr.getAttrType().getId().equals(id_pda_dateOfBirth)) {
        		ASN1Set set = attr.getAttrValues();
        		// Come on, we'll only allow one dateOfBirth, we're not allowing such frauds with multiple birth dates
        		DERGeneralizedTime time = DERGeneralizedTime.getInstance(set.getObjectAt(0));
        		Date date = time.getDate();
        		String dateStr = dateF.format(date);
        		result += prefix + "dateOfBirth="+dateStr; 
        	}
        	if (attr.getAttrType().getId().equals(id_pda_placeOfBirth)) {
        		ASN1Set set = attr.getAttrValues();
        		// same here only one placeOfBirth
        		String pb = ((DERString)set.getObjectAt(0)).getString();
        		result += prefix + "placeOfBirth="+pb;        			
        	}
        	if (attr.getAttrType().getId().equals(id_pda_gender)) {
        		ASN1Set set = attr.getAttrValues();
        		// same here only one gender
        		String g = ((DERString)set.getObjectAt(0)).getString();
        		result += prefix + "gender="+g;        			
        	}
        	if (attr.getAttrType().getId().equals(id_pda_countryOfCitizenship)) {
        		ASN1Set set = attr.getAttrValues();
        		// same here only one citizenship
        		String g = ((DERString)set.getObjectAt(0)).getString();
        		result += prefix + "countryOfCitizenship="+g;        			
        	}
        	if (attr.getAttrType().getId().equals(id_pda_countryOfResidence)) {
        		ASN1Set set = attr.getAttrValues();
        		// same here only one residence
        		String g = ((DERString)set.getObjectAt(0)).getString();
        		result += prefix + "countryOfResidence="+g;        			
        	}
        }

        if (StringUtils.isEmpty(result)) {
            return null;
        }
        return result;            
	}

    /**
     * From subjectDirAttributes string as defined in getSubjectDirAttribute 
     * @param string of SubjectDirectoryAttributes
     * @param converter BC converter for DirectoryStrings, that determines which encoding is chosen
     * @return A Collection of ASN.1 Attribute (org.bouncycastle.asn1.x509), or an empty Collection, never null
     * @see #getSubjectDirectoryAttributes(X509Certificate)
     */
    public static Collection getSubjectDirectoryAttributes(String dirAttr, X509NameEntryConverter converter) {
    	ArrayList ret = new ArrayList();
    	Attribute attr = null;
        String value = CertTools.getPartFromDN(dirAttr, "countryOfResidence");
        if (!StringUtils.isEmpty(value)) {
        	DEREncodableVector vec = new DEREncodableVector();
        	vec.add(new DERPrintableString(value));
        	attr = new Attribute(new DERObjectIdentifier(id_pda_countryOfResidence),new DERSet(vec));
        	ret.add(attr);
        }
        value = CertTools.getPartFromDN(dirAttr, "countryOfCitizenship");
        if (!StringUtils.isEmpty(value)) {
        	DEREncodableVector vec = new DEREncodableVector();
        	vec.add(new DERPrintableString(value));
        	attr = new Attribute(new DERObjectIdentifier(id_pda_countryOfCitizenship),new DERSet(vec));
        	ret.add(attr);
        }
        value = CertTools.getPartFromDN(dirAttr, "gender");
        if (!StringUtils.isEmpty(value)) {
        	DEREncodableVector vec = new DEREncodableVector();
        	vec.add(new DERPrintableString(value));
        	attr = new Attribute(new DERObjectIdentifier(id_pda_gender),new DERSet(vec));
        	ret.add(attr);
        }
        value = CertTools.getPartFromDN(dirAttr, "placeOfBirth");
        if (!StringUtils.isEmpty(value)) {
        	DEREncodableVector vec = new DEREncodableVector();
        	X509DefaultEntryConverter conv = new X509DefaultEntryConverter();
        	DERObject obj = conv.getConvertedValue(new DERObjectIdentifier(id_pda_placeOfBirth), value);
        	vec.add(obj);
        	attr = new Attribute(new DERObjectIdentifier(id_pda_placeOfBirth),new DERSet(vec));
        	ret.add(attr);
        }        
        // dateOfBirth that is a GeneralizedTime
        // The correct format for this is YYYYMMDD, it will be padded to YYYYMMDD120000Z
        value = CertTools.getPartFromDN(dirAttr, "dateOfBirth");
        if (!StringUtils.isEmpty(value)) {
            if (value.length() == 8) {
                value += "120000Z"; // standard format according to rfc3739
                DEREncodableVector vec = new DEREncodableVector();
                vec.add(new DERGeneralizedTime(value));
                attr = new Attribute(new DERObjectIdentifier(id_pda_dateOfBirth),new DERSet(vec));
                ret.add(attr);                
            } else {
                log.error("Wrong length of data for 'dateOfBirth', should be of format YYYYMMDD, skipping...");
            }
        }
        return ret;
    }
    

}
