package tn.esprit.spring.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import tn.esprit.spring.entity.Child;
import tn.esprit.spring.entity.ImageModel;
import tn.esprit.spring.entity.Kindergarten;
import tn.esprit.spring.entity.Parent;
import tn.esprit.spring.entity.TimesheetDelegate;

public interface IParentService {
	
	java.util.List<Parent> retrieveAllParent();
	Parent addParent(Parent p);
	Parent addChild(Child c,Long idP);

	void deleteParent(String id);
	void abonneKindergarten(Long idP,Long idK);
	void annuleabonneKindergarten(Long idP,Long idK);

	Parent updateParent(Parent p);
	Parent retrieveParent(String id);
	Parent retrieveParentemail(String id);

	TimesheetDelegate demandedelegate(Long idk,Long idp);


}
