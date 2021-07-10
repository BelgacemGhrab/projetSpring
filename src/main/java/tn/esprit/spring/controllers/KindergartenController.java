package tn.esprit.spring.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import tn.esprit.spring.entity.Kindergarten;
import tn.esprit.spring.entity.Parent;
import tn.esprit.spring.entity.TimesheetDelegate;
import tn.esprit.spring.entity.Users;
import tn.esprit.spring.entity.parentType;
import tn.esprit.spring.mail.SendMail;
import tn.esprit.spring.repository.UserRepository;
import tn.esprit.spring.service.IDelegateService;
import tn.esprit.spring.service.IParentService;
import tn.esprit.spring.service.KindergartenService;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")

public class KindergartenController {
	@Autowired
	IParentService iParentService;
	@Autowired
	KindergartenService kindergartenService;
	@Autowired
	IDelegateService iDelegateService;
	@Autowired
	SendMail sendMail;
	@GetMapping("/kindergarten/{id}")
	public Kindergarten afficher (@PathVariable("id") Long id){
		return  kindergartenService.retrieveKindergarten(id);
	}
	@GetMapping("/imagek/{id}")
	@ResponseBody
	public HttpEntity<byte[]> getImageki(@PathVariable String id) {
		Kindergarten k=kindergartenService.retrievekinderemail(id);
	   // 1. download img your location... 
	    byte[] image = k.getImage();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.IMAGE_JPEG);
	    headers.setContentLength(image.length);

	    return new HttpEntity<byte[]>(image, headers);
	}
	@GetMapping("/kindergarten/all")
	public List<Kindergarten> afficherAllChild (){
		return  kindergartenService.retrieveAllKindergarten();
	}
	
	
	@PostMapping("/kindergarten/add")
	public String add(@RequestParam("kinder") String kinder,final @RequestParam("file") MultipartFile file,HttpServletRequest request)throws IOException{
		
		Kindergarten k= new ObjectMapper().readValue(kinder, Kindergarten.class);
		System.out.println(k.toString());
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
			

		List<Kindergarten> kinderl=kindergartenService.retrieveAllKindergarten();
		//String email = k.getEmail();
		List<Kindergarten> l= kinderl.stream().filter(x->x.getEmail().equals( k.getEmail())).collect(Collectors.toList());
		if(l.isEmpty()){
			byte[] imageData = file.getBytes();
			k.setImage(imageData);
			k.setFileName(newfilename);
		 kindergartenService.addKindergarten(k);
		 return "succs";
		}
		return "kindergarten is existe";
	}
	
	
	@DeleteMapping("/remove-kindergarten/{id}")
	@ResponseBody
	public void removeKindergarten(@PathVariable("id") String id) {
		kindergartenService.deleteKindergarten(id);
	}
	@PutMapping("/modify-kindergarten")
	@ResponseBody
	public String modifyKindergarten(@RequestBody Kindergarten k) {
		List<Kindergarten>kindergartens=kindergartenService.retrieveAllKindergarten();
		for(Kindergarten i:kindergartens)
			if(i.getId()==k.getId()){
			kindergartenService.updateKindergarten(k);
	   return "modify successfully from id ="+k.getId(); 
	         }
		return " kindergarten not found " ;
		}
	@GetMapping("/KindergartenMyParent/{id}")
	public HashMap<Long, String> findParentfromKindergarten(@PathVariable("id")Long id) {

	 return kindergartenService.findParentfromKindergarten(id);
	}
	@PutMapping("/kindergarten/affectationdemandeParentforDelegate/{idk}/{idp}")
	public List<TimesheetDelegate> affactationdelegate (@PathVariable("idk") Long idk,@PathVariable("idp") Long idp){
		Parent p=iParentService.retrieveParent(Long.toString(idp));
		List<TimesheetDelegate> delegates=iDelegateService.retrieveAll();
		for(TimesheetDelegate d:delegates){
			if(d.getPk().getParentID()==idp&&d.getPk().getKinderID()==idk){
				d.setValide(true);
				iDelegateService.updateDelegate(d);
				Kindergarten k=	kindergartenService.retrieveKindergarten(idk);
				HashMap<String, Integer> v=new HashMap<>();
				v.put(p.getEmail(),0);
				k.setVote(v);
				kindergartenService.updateKindergarten(k);
		}
		}
		return  delegates;
	}
	
	@PutMapping("/kindergarten/ResultatVoteDelegate/{idk}")
	public String affactationResultatVoteDelegate (@PathVariable("idk") Long idk){
		Kindergarten k= kindergartenService.retrieveKindergarten(idk);
		HashMap<String, Integer> v=new HashMap<>();
		v=k.getVote();
		String email="";
		int maxValueInMap=(Collections.max(v.values()));	
		  for (Entry<String, Integer> entry : v.entrySet()) {  
	            if (entry.getValue()==maxValueInMap) {
	                email=entry.getKey();     
	            }
	        }
		  k.setDelegate(email);
		  kindergartenService.updateKindergarten(k);
		  Parent p=iParentService.retrieveParentemail(email);
		  p.setPatype(parentType.delegateParent);
		  iParentService.updateParent(p);
		  sendMail.send(email, "vous avez gagné une élection cette année ");
		return  "delegate kindergarte  "+k.getId()+"  est : "+email;
	}


}
