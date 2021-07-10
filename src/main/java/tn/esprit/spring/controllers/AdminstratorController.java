package tn.esprit.spring.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tn.esprit.spring.entity.JwtRequest;
import tn.esprit.spring.entity.Kindergarten;
import tn.esprit.spring.entity.Parent;
import tn.esprit.spring.entity.UserDto;
import tn.esprit.spring.mail.SendMail;
import tn.esprit.spring.repository.UserRepository;
import tn.esprit.spring.service.IAdminstratorService;
import tn.esprit.spring.service.JwtUserDetailsService;
import tn.esprit.spring.service.KindergartenService;

@RestController
@RequestMapping("/api")
public class AdminstratorController {
	
	@Autowired
	IAdminstratorService adminstratorService;
	

	@Autowired
	KindergartenService kindergartenService;
	
	@PutMapping("/adminstrator/Confirmation/{id}")
	public String Confirmationkindergarten( @PathVariable("id") Long id ) throws Exception{
		List<Kindergarten> k=kindergartenService.findallkindergartens();
		for(Kindergarten i:k)
		{
			if(i.getId()==id)
			{

		    adminstratorService.KindergartenRegistrationConfirmation(id);
		    
		return "kindergarten from id = "+id+" Confirmation"	;
			}
		}
		return "not found";


	}
	
	@GetMapping("/adminstrator/allkinder")
	public List<Kindergarten> GetAllkindergarten (){
		return  kindergartenService.findallkindergartens();
	}
	@GetMapping("/adminstrator/kinderparent/{id}")
	public HashMap<Long, String> findParentfromKindergarten(@PathVariable("id")Long id) {

	 return kindergartenService.findParentfromKindergarten(id);
	}
	
//	@GetMapping("/adminstrator/rangkinder")
	public  HashMap<String, Long> GetRangKindergartens (){
		List<Kindergarten> k=kindergartenService.findallkindergartens();
	    HashMap<String, Long> rangKnder = new HashMap<String, Long>();
		for(Kindergarten kinder:k)
		{
			/*
			Long i=(long) 0;
			for(Parent p:kinder.getParents())
			{
				i++;
			}
			*/
			Long i =(long) kindergartenService.countparentfromkindergarten(kinder.getId());
			rangKnder.put(kinder.getName(), i);
			//System.out.println(rangKnder);
			
		}

		return  triAvecValeur(rangKnder);
	}
	public static HashMap<String, Long> triAvecValeur( HashMap<String, Long> map ){
		   List<Map.Entry<String, Long>> list =
		        new LinkedList<Map.Entry<String, Long>>( map.entrySet() );
		   Collections.sort( list, new Comparator<Map.Entry<String, Long>>(){
		      public int compare( Map.Entry<String, Long> o1, Map.Entry<String, Long> o2 ){
		          return (o2.getValue()).compareTo( o1.getValue());
		      }
		   });
		   HashMap<String, Long> map_apres = new LinkedHashMap<String, Long>();
		   for(Map.Entry<String, Long> entry : list)
		     map_apres.put( entry.getKey(), entry.getValue() );
		   return map_apres;
		}
	@GetMapping("/test")
	public void test() throws IOException{
		// workbook object
        XSSFWorkbook workbook = new XSSFWorkbook();
  
        // spreadsheet object
        XSSFSheet spreadsheet
            = workbook.createSheet(" Student Data ");
  
        // creating a row object
        XSSFRow row;
  
        // This data needs to be written (Object[])
        Map<String, Object[]> studentData
            = new TreeMap<String, Object[]>();
            HashMap<String, Long> map =this.GetRangKindergartens();
            List<Map.Entry<String, Long>> list =new LinkedList<Map.Entry<String, Long>>( map.entrySet() );
        studentData.put(
            "1",
            new Object[] { "name kindergarten", "range"});
        int i=1;
		   for(Map.Entry<String, Long> entry : list){
			   i++;
			   studentData.put(String.valueOf(i),new Object[]{entry.getKey(), entry.getValue()});
			
		   }
  
        Set<String> keyid = studentData.keySet();
  
        int rowid = 0;
  
        // writing the data into the sheets...
  
        for (String key : keyid) {
  
            row = spreadsheet.createRow(rowid++);
            Object[] objectArr = studentData.get(key);
            int cellid = 0;
  
            for (Object obj : objectArr) {
                Cell cell = row.createCell(cellid++);
                cell.setCellValue((String)obj);
            }
        }
  
        // .xlsx is the format for Excel Sheets...
        // writing the workbook into the file...
        FileOutputStream out = new FileOutputStream(
            new File("C:/savedexcel/GFGsheet.xlsx"));
  
        workbook.write(out);
        out.close();
    
		
	}
	
	

}
