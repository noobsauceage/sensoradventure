package edu.umass.cs.gcrs.server;

/*
 import edu.umass.cs.gcrs.database.GroupTableEntry;
 import edu.umass.cs.gcrs.gcrs.GroupInfo;
 import edu.umass.cs.gcrs.database.GroupTable;
 import edu.umass.cs.gcrs.database.AclTable;
 import edu.umass.cs.gcrs.database.AclTableEntry;
 import edu.umass.cs.gcrs.database.MainTable;
 import edu.umass.cs.gcrs.gcrs.GCRS;
 import edu.umass.cs.gcrs.gcrs.SHA1HashFunction;
 import edu.umass.cs.gcrs.gcrs.UserInfo;
 */
import edu.umass.cs.gcrs.utilities.Utils;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import org.json.*;
import static edu.umass.cs.gcrs.server.Defs.*;

/**
 * Implements the GCRS server protocol for both the HTTP and line parsing
 * server.
 * 
 * @author westy
 */
public class Protocol {

	public static String Version = "$Revision$";
	public final static String REGISTERENTITY = "registerEntity";
	public final static String LOOKUPENTITY = "lookupEntity";
	public final static String INSERTONE = "insertOne";
	public final static String INSERT = "insert";
	public final static String LOOKUP = "lookup";
	public final static String LOOKUPALL = "lookupAll";
	public final static String ACLADD = "aclAdd";
	public final static String ACLREMOVE = "aclRemove";
	public final static String ACL = "acl";
	public final static String ACLALL = "aclAll";
	public final static String CREATEGROUP = "createGroup";
	public final static String LOOKUPGROUP = "lookupGroup";
	public final static String ADDTOGROUP = "addToGroup";
	public final static String REMOVEFROMGROUP = "removeFromGroup";
	public final static String GETGROUPMEMBERS = "getGroupMembers";
	public final static String HELP = "help";
	// demo commands (not accesible in "public" version")
	public final static String DEMO = "demo";
	public final static String CLEAR = "clear";
	public final static String DUMP = "dump";
	//
	public final static String OKRESPONSE = "+OK+";
	public final static String NULLRESPONSE = "+EMPTY+";
	public final static String BADRESPONSE = "+NO+";
	public final static String UNKNOWNUSER = "+BADUSER+";
	public final static String UNKNOWNGROUP = "+BADGROUP+";
	public final static String ALLFIELDS = "+ALL+";
	public final static String ALLUSERS = "+ALL+";
	//
	public static final String RASALGORITHM = "RSA";
	public static final String SIGNATUREALGORITHM = "SHA1withRSA";
	private final static String NEWLINE = System.getProperty("line.separator");
	// Fields for HTTP get queries
	public final static String NAME = "name";
	public final static String GUID = "guid";
	public final static String READER = "reader";
	public final static String FIELD = "field";
	public final static String VALUE = "value";
	public final static String JSONSTRING = "jsonstring";
	public final static String GROUP = "group";
	public final static String PUBLICKEY = "publickey";
	public final static String SIGNATURE = "signature";
	public final static String PASSKEY = "passkey";
	public final static String TABLE = "table";
	//
	private boolean demoMode = false;

}