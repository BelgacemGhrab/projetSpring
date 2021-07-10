package tn.esprit.spring.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import tn.esprit.spring.entity.Child;
import tn.esprit.spring.entity.JwtRequest;
import tn.esprit.spring.entity.Kindergarten;
import tn.esprit.spring.entity.Parent;
import tn.esprit.spring.entity.UserDto;
import tn.esprit.spring.entity.UserSignin;
import tn.esprit.spring.entity.parentType;
import tn.esprit.spring.mail.SendMail;
import tn.esprit.spring.service.ChildService;
import tn.esprit.spring.service.IDelegateService;
import tn.esprit.spring.service.IParentService;
import tn.esprit.spring.service.JwtUserDetailsService;
import tn.esprit.spring.service.KindergartenService;
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
public class ParentController {
	@Value("${uploadDir}")
	private String uploadFolder;

	@Autowired
	IParentService iParentService;
	
	@Autowired
	SendMail sendMail;
	@Autowired
	JwtAuthenticationController jwt;
	@Autowired
	KindergartenService kindergartenService;
	
	@Autowired
	private JwtUserDetailsService userDetailsService;
	@Autowired
	private PasswordEncoder bcryptEncoder;
	@Autowired
	ChildService childService;
	@Autowired
	IDelegateService iDelegateService;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@GetMapping("/image/{id}")
	@ResponseBody
	public HttpEntity<byte[]> getImage(@PathVariable String id) {
		Parent p=iParentService.retrieveParentemail(id);
	   // 1. download img your location... 
	    byte[] image = p.getImage();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.IMAGE_JPEG);
	    headers.setContentLength(image.length);

	    return new HttpEntity<byte[]>(image, headers);
	}
    //final	IParentService iParentService;
	//Logger logger=LoggerFactory.getLogger(ParentController.class);
	@Secured(value="ROLE_Parent")
	@GetMapping("/parent/{id}")
	public Parent afficher (@PathVariable("id") String id) throws IOException{
		Parent p=iParentService.retrieveParentemail(id);
		
		return  p;
	}
	//@Secured(value="ROLE_Parent")

	//	return p.getImage();
	
	@GetMapping("/image")
	Model show(Model map) {
		List<Parent> images = iParentService.retrieveAllParent();
		map.addAttribute("images", images);
		return map;
	}
	@Secured(value="ROLE_Parent")
	@GetMapping("/parent/all")
	public ResponseEntity<List<Parent>> afficherAllParent (){
		List<Parent>parents=iParentService.retrieveAllParent();
		//List<Parent>parents2=new ArrayList<Parent>();
		//for(Parent p:parents){
			//p.setImage(decompressZLib(p.getImage()));}
		//return  iParentService.retrieveAllParent();
        return new ResponseEntity<>(parents, HttpStatus.OK);

	}
	
	
	@PostMapping("/parent/add")
	public String addParent(@RequestParam("pa") String p,final @RequestParam("file") MultipartFile file,HttpServletRequest request) throws IOException{
		//logger.info("test"+parent);
		
		Parent parent= new ObjectMapper().readValue(p, Parent.class);
		boolean isExist=new File(request.getServletContext().getRealPath("/images/")).exists();
		if(!isExist){
			new File(request.getServletContext().getRealPath("/images/")).mkdir();
		System.out.println("mkdir");	
		}
				String filename = file.getOriginalFilename();
				String newfilename=FilenameUtils.getBaseName(filename)+"."+FilenameUtils.getExtension(filename);
				File serviceFile=new File(request.getServletContext().getRealPath("/images/"+File.separator+newfilename));
			try{
				FileUtils.writeByteArrayToFile(serviceFile, file.getBytes());
			}catch (Exception e) {
				e.printStackTrace();
			}
				byte[] imageData = file.getBytes();
				parent.setFileName(newfilename);
		parent.setImage(imageData);
		List<Parent>parents=iParentService.retrieveAllParent();
		String email = parent.getEmail();
		String password=parent.getPassword();
		String msg;
		List<Parent> l= parents.stream().filter(x->x.getEmail().equals(email)).collect(Collectors.toList());
				if(l.isEmpty())
				{
					UserDto user=new UserDto(parent.getEmail(),parent.getPassword(),parent.getRole());
					userDetailsService.save(user);
					parent.setPassword(bcryptEncoder.encode(parent.getPassword()));
					
					parent.setPatype(parentType.ordinaryParent);
				
					iParentService.addParent(parent);
					msg="your email :  "+email+"  and your password est : " +password;
					sendMail.send(email,msg);

					return "succs";
		 }
		return "erreur";
	
	}
	

	@DeleteMapping("/remove-Parent/{id}")
	@ResponseBody
	public void removeParent(@PathVariable("id") String id) {
		iParentService.deleteParent(id);
	}
	@Secured(value="ROLE_Parent")
	@PutMapping("/modify-Parent/{id}")
	public String modifyParent(@RequestBody Parent p,@PathVariable("id") Long id) {
		System.out.println(id);
		Parent parentdto=iParentService.retrieveParent(id.toString());
		List<Parent>parents=iParentService.retrieveAllParent();
		for(Parent i:parents)
			if(i.getId()==id){
				parentdto.setFirstName(p.getFirstName());
				parentdto.setLastName(p.getLastName());
				parentdto.setPhonenumber(p.getPhonenumber());
				parentdto.setPhonenumber(p.getPhonenumber());
	   iParentService.updateParent(parentdto);
	   return "modify successfully from id ="+p.getId(); 
	   
	        }
		return "parent not found " ;
		}
	@PutMapping("/parent/abonne/{idP}/{idK}")
	public String  abonneParent(@PathVariable("idP") Long idP,@PathVariable("idK") Long idK) {
		Kindergarten k=kindergartenService.retrieveKindergarten(idK);
		List<Parent> l= k.getParents().stream().filter(x->x.getId()==idP).collect(Collectors.toList());
		if(l.isEmpty()&&k.isConfirmation()==true){
		iParentService.abonneKindergarten(idP, idK);
		return "abonne avec succs";
		}
		return "deja abonne";
	}
	@PutMapping("/parent/annuleabonne/{idP}/{idK}")
	public String  AnnuleabonneParent(@PathVariable("idP") Long idP,@PathVariable("idK") Long idK) {
		Kindergarten k=kindergartenService.retrieveKindergarten(idK);
		List<Parent> l= k.getParents().stream().filter(x->x.getId()==idP).collect(Collectors.toList());
		if(!(l.isEmpty())){
		iParentService.annuleabonneKindergarten(idP, idK);
		return "annulée avec succes";
		}
		return "deja ne pas abonne";
	}
	
	@GetMapping("/parent/auth/{email}/{password}")
	public ResponseEntity<?> auth(@PathVariable("email")String email,@PathVariable("password") String password) throws Exception {
		JwtRequest request=new JwtRequest(email,password);
		String token=jwt.createAuthenticationToken(request);
	//	token ="Bearer   "+token;
		UserSignin response = new UserSignin();
		response.setToken(token);
		return ResponseEntity.ok(response); 
	}
	
	@PutMapping("/parent/addChild/{idp}")
	public String addchild(@RequestBody Child c,@PathVariable("idp")Long idp){
		childService.addChild(c);
		iParentService.addChild(c, idp);
		
		return "succs";
	}
	@PostMapping("/parent/demandedelegate/{idk}/{idp}")
	public String delegate(@PathVariable("idk")Long idp,@PathVariable("idp")Long idk){
		iDelegateService.addDelegate(iParentService.demandedelegate(idk, idp));
		return "demande avec  succs";
	}
	@Transactional
	@PutMapping("/parent/vote/{idk}/{email}/{idp}")
	public HashMap<String, Integer> votedelegate(@PathVariable("idk")Long idk,@PathVariable("idp")Long idp,@PathVariable("email")String email) throws ParseException{
		////////////////////////////date vote//////////////////////////////////////////////////////////////////////////
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String oeStartDateStr = "01/01/";//09/01
        String oeEndDateStr = "12/30/";
        Calendar cal = Calendar.getInstance();
        Integer year = cal.get(Calendar.YEAR);
        oeStartDateStr = oeStartDateStr.concat(year.toString());
        oeEndDateStr = oeEndDateStr.concat(year.toString());
        Date startDate = sdf.parse(oeStartDateStr);
        Date endDate = sdf.parse(oeEndDateStr);
        Date d = new Date();
        String currDt = sdf.format(d);
        ////////////////////////////date vote///////////////////////////////
		Kindergarten k=	kindergartenService.retrieveKindergarten(idk);
		HashMap<String, Integer> vote=k.getVote();
		Parent p=iParentService.retrieveParent(Long.toString(idp));
		if(p.isVote()==false&&((d.after(startDate) && (d.before(endDate))) || (currDt.equals(sdf.format(startDate)) ||currDt.equals(sdf.format(endDate))))){
		p.setVote(true);
		iParentService.updateParent(p);
		vote.put(email, vote.get(email)+1);
			k.setVote(vote);
		kindergartenService.updateKindergarten(k);
	
		return vote;
		}
		return vote;
	}
	
	// compress the image bytes before storing it in the database
		public static byte[] compressZLib(byte[] data) {
			Deflater deflater = new Deflater();
			deflater.setInput(data);
			deflater.finish();

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
			byte[] buffer = new byte[1024];
			while (!deflater.finished()) {
				int count = deflater.deflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			try {
				outputStream.close();
			} catch (IOException e) {
			}
			System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);

			return outputStream.toByteArray();
		}

		// uncompress the image bytes before returning it to the angular application
		public static byte[] decompressZLib(byte[] data) {
			Inflater inflater = new Inflater();
			inflater.setInput(data);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
			byte[] buffer = new byte[1024];
			try {
				while (!inflater.finished()) {
					int count = inflater.inflate(buffer);
					outputStream.write(buffer, 0, count);
				}
				outputStream.close();
			} catch (IOException ioe) {
			} catch (DataFormatException e) {
			}
			return outputStream.toByteArray();
		}
		
}
