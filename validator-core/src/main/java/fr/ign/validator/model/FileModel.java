package fr.ign.validator.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.model.file.DirectoryModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.model.file.PdfModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.xml.FileModelAdapter;

/**
 * Représente un fichier d'un document
 * @author MBorne
 */
@XmlJavaTypeAdapter(FileModelAdapter.class)
public abstract class FileModel implements Model {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("FileModel") ;
	
	@XmlEnum(String.class)
	public enum MandatoryMode {
		OPTIONAL, // ignore if missing
		WARN, // warn if missing 
		ERROR // error if missing
	}
	
	/**
     * Le nom du fichier
     */
	private String name ;
	/**
	 * Le chemin vers le fichier
	 */
	private String regexp ;
	/**
	 * L'obligation de présence du fichier
	 */
	private MandatoryMode mandatory = MandatoryMode.WARN ;
	/**
	 * Le modèle de données (optionel, pour les tables uniquement)
	 */
	private FeatureType featureType = null ;
	
	/**
	 * Les validateurs du modèle de fichier
	 */
	private List<Validator<DocumentFile>> validators = new ArrayList<Validator<DocumentFile>>();
	
	
	protected FileModel(){
		
	}
	
	/**
	 * Création d'un FileModel par son type
	 * TODO mettre des const
	 * 
	 * @param type
	 * @return
	 */
	public static FileModel newFileModelByType(String type){
		if ( DirectoryModel.TYPE.equals( type ) ){
			return new DirectoryModel() ;
		}else if ( TableModel.TYPE.equals( type ) ){
			return new TableModel() ;
		}else if ( PdfModel.TYPE.equals( type ) ){
			return new PdfModel() ;
		}else if ( MetadataModel.TYPE.equals( type ) ){
			return new MetadataModel() ;
		}else{
			throw new IllegalArgumentException(String.format("invalid FileModel type : %s",type));
		}
	}
	
	/**
	 * Get type
	 * @return
	 */
	public abstract String getType()  ;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRegexp() {
		return regexp;
	}
	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}
	public MandatoryMode getMandatory() {
		return mandatory;
	}
	public void setMandatory(MandatoryMode mandatory) {
		this.mandatory = mandatory;
	}


	public FeatureType getFeatureType() {
		return featureType;
	}

	@XmlTransient
	public void setFeatureType(FeatureType featureType) {
		this.featureType = featureType;
	}
	
	/**
	 * Renvoie la regexp complète
	 * @return
	 */
	public String getFullRegexp(){
		// (?i) : case insensitive
		// .* : commence par n'importe quoi
		String regexp = "(?i).*/"+getRegexp()+getRegexpSuffix() ;
		return regexp ;
	}
	
	/**
	 * Renvoie une regexp correspondant au nom du fichier
	 * 
	 * TODO voir pour reposer cette expression sur des noms courts
	 * 
	 * @return
	 */
	public String getRegexpName(){
		String parts[] = getRegexp().split("/") ;
		
		String result = "(?i)"+ parts[parts.length-1] + ".*" + getRegexpSuffix() ;
		
		// validate regexp (/ may be misplaced)
		try {
            Pattern.compile(result);
            return result ;
        } catch (PatternSyntaxException exception) {
            return "(?i)"+ getName() + ".*" + getRegexpSuffix() ;
        }
	}
	
	/**
	 * Renvoie la regexp correspondant :
	 * - aux extensions supportées
	 * - au caractère "/" pour les dossiers
	 * 
	 * @return
	 */
	public String getRegexpSuffix(){
		return "" ; 
	}
	
	/**
	 * Test si le fichier correspond à l'expression régulière
	 * @param file
	 * @return
	 */
	public boolean matchPath(File file) {
		String uriFile = file.toURI().toString() ;
		String regexp  = getFullRegexp() ;
		log.trace(MARKER, "matchPath / {} / {} match {} ...", getName(), uriFile, regexp);
		return uriFile.matches(regexp) ;
	}
	
	/**
	 * Test si le nom de fichier correspond à l'expression régulière (pour détecter les fichiers présents
	 *  dans un mauvais dossier)
	 * @param file
	 * @return
	 */
	public boolean matchFilename(File file) {
		String regexp = getRegexpName() ;
		log.trace(MARKER, "matchFilename / {} / {} match {} ...", getName(), file, regexp);
		return file.getName().matches(regexp) ;
	}
	

	/**
	 * Ajout d'un validateur
	 * @param validator
	 */
	public void addValidator(Validator<DocumentFile> validator) {
		this.validators.add(validator);
	}

	/**
	 * @return the validators
	 */
	public List<Validator<DocumentFile>> getValidators() {
		return validators;
	}
	
}
