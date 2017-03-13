package uk.ac.ic.sph.pcph.iccp.fhsc.restful.service.utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import uk.ac.ic.sph.pcph.iccp.fhsc.controller.FilesJpaController;
import uk.ac.ic.sph.pcph.iccp.fhsc.domain.Files;
import uk.ac.ic.sph.pcph.iccp.fhsc.domain.Login;
import uk.ac.ic.sph.pcph.iccp.fhsc.enums.FHSCFileUploadType;
import uk.ac.ic.sph.pcph.iccp.fhsc.enums.PersistenceUnitEnum;
import uk.ac.ic.sph.pcph.iccp.fhsc.qualifier.PersistenceUnitQualifier;
import uk.ac.ic.sph.pcph.iccp.fhsc.utility.ApplicationParameters;

@Named
@ApplicationScoped
public class FileUtility {

	@Inject 
	LoginUtility loginUtility;
	
	@Inject
	ApplicationParameters applicationParameters;
	
	@Inject
	@PersistenceUnitQualifier(PersistenceUnitEnum.FHSC_MANAGEMENT)
	private EntityManagerFactory fhsc_management_emf;

	public FileUtility() {

	}

	private EntityManager getEntityManager() {
		return fhsc_management_emf.createEntityManager();
	}

	public void addFile(String name,String originalFilename, String comment, String fileTypeStr, String username, byte[] fileContent) throws Exception
	{
		/*
		 * Checking values
		 */
		if(name==null || name.isEmpty())
			throw new Exception("File has no name!",null);
		if(comment==null || comment.isEmpty())
			throw new Exception("File has no comment!",null);
		if(fileTypeStr==null || fileTypeStr.isEmpty())
			throw new Exception("File has no type!",null);
		
		FHSCFileUploadType fileType;
		try
		{
			fileType=FHSCFileUploadType.valueOf(fileTypeStr);
		}catch(IllegalArgumentException e){
			throw new Exception("Type of file is not recognised!",null);
		}
		/*
		 * Retrieving Login
		 */
		Login login= loginUtility.getLoginForUsername(username);
		
		/*
		 * Finding ideal relative path for the file (same both on external and internal server
		 */
		Date uploadedDate=new Date();
		String relativePath= login.getUserId().getCountry()+"/" +login.getUserName() + "/" + new SimpleDateFormat("yyyy").format(uploadedDate) + "/" + new SimpleDateFormat("MM").format(uploadedDate);
		
		/*
		 * Creating file
		 */
		//making sure the extension is conserved
		String extension=FilenameUtils.getExtension( originalFilename).toLowerCase();
		if(extension!=null && !extension.isEmpty() && !name.toLowerCase().endsWith(extension))
			name+="." + extension;
		File physicalFile=new File(applicationParameters.getUploadDirectory()+ relativePath + "/" + name);
		

		/*
		 * Creating file's record
		 */
		Files fileDatabaseRecord=new Files();
		fileDatabaseRecord.setLocation("/" + relativePath);
		fileDatabaseRecord.setFileName(name);
		fileDatabaseRecord.setFileSize((int) physicalFile.length());
		fileDatabaseRecord.setComment(comment);
		fileDatabaseRecord.setFileType(fileType.toString());
		fileDatabaseRecord.setUploadDate(uploadedDate);
		fileDatabaseRecord.setUserId(login.getUserId());
		
		/*
		 * check if file exists 
		 */
		if (fileExists(physicalFile,fileDatabaseRecord))
			throw new Exception("A file with that name already exists");
		/*
		 * Saving files in database and on server
		 */
		FilesJpaController filesJpaController=new FilesJpaController(this.fhsc_management_emf);	
		filesJpaController.create(fileDatabaseRecord);
				
		FileUtils.writeByteArrayToFile(physicalFile,fileContent);
	}
	
	public List<Files> getUploadedFileList(String username) {
		
		Login login= loginUtility.getLoginForUsername(username);
		
		EntityManager em = null;
		List<Files> result = null;

		try {
			em = getEntityManager();
			em.getTransaction().begin();

			TypedQuery<Files> query = em.createNamedQuery("Files.findByUserID", Files.class);
			query.setParameter("userId", login.getUserId());

			result = query.getResultList();

			System.out.println(result.size());

			em.getTransaction().commit();

		} catch (Exception nee) {
			nee.printStackTrace();
		} finally {
			if (em != null) {
				em.close();
			}
		}

		return result;
	}
	
	
	public List<Files> getAllUploadedFileList() {
		EntityManager em = null;
		List<Files> result = null;

		try {
			em = getEntityManager();
			em.getTransaction().begin();

			TypedQuery<Files> query = em.createNamedQuery("Files.findAll", Files.class);

			result = query.getResultList();

			System.out.println(result.size());

			em.getTransaction().commit();

		} catch (Exception nee) {
			nee.printStackTrace();
		} finally {
			if (em != null) {
				em.close();
			}
		}

		return result;
	}
	
	
	public Files getFile(Integer fileId){
		FilesJpaController controller=new FilesJpaController(this.fhsc_management_emf);
		Files f=controller.findFiles(fileId);
		return f;
	}
	
	public Files getFile(String username, Integer fileId) throws Exception
	{
		
		Files f=getFile(fileId);
		if(f==null)
			return f;
		
		Login login=loginUtility.getLoginForUsername(username);
		if (!login.getUserId().getUserId().equals(f.getUserId().getUserId()))
			throw new Exception("It is not the investigator file!");
		return f;
	}
		
	
	
	/**
	 * 
	 * @param physicalFile
	 * @param fileDatabaseRecord
	 * @return true if file exists either on database or on server.
	 */
	private boolean fileExists(File physicalFile, Files fileDatabaseRecord){
		/*
		 * Checking on disk, if the file hasn't been transferred yet to internal
		 */
		if( physicalFile.exists())
			return true;
		
		EntityManager em = null;
		List<Files> results = null;

		try {
			em = getEntityManager();
			em.getTransaction().begin();

			TypedQuery<Files> query = em.createNamedQuery("Files.findByLocationAndName", Files.class);
			query.setParameter("location", fileDatabaseRecord.getLocation());
			query.setParameter("name", fileDatabaseRecord.getLocation());
			results = query.getResultList();
			
			em.getTransaction().commit();

		} catch (Exception nee) {
			nee.printStackTrace();
		} finally {
			if (em != null) {
				em.close();
			}
		}
		
		/*
		 * Checking in database, if File has been set to internal
		 */
		
		if(results.size()>=1)
			return true;
		
		return false;
	}
}
