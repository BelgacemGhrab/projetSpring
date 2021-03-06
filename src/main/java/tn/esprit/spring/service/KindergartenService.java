package tn.esprit.spring.service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tn.esprit.spring.entity.Kindergarten;
import tn.esprit.spring.entity.Parent;
import tn.esprit.spring.entity.Users;
import tn.esprit.spring.repository.KindergartenRepository;
import tn.esprit.spring.repository.UserRepository;
@Service
public class KindergartenService implements IKindergartenService {

	@Autowired
	KindergartenRepository kindergartenRepository;
	@Autowired
	UserRepository userRepository;
	public List<Kindergarten> retrieveAllKindergarten() {
		List<Kindergarten> k=(List<Kindergarten>) kindergartenRepository.findAll();
		return k ;
	}

	public Kindergarten addKindergarten(Kindergarten k) {
		
		return kindergartenRepository.save(k);
	}

	public void deleteKindergarten(String id) {
           kindergartenRepository.deleteById(Long.parseLong(id));		
	}

	public Kindergarten updateKindergarten(Kindergarten k) {
		return kindergartenRepository.save(k);
	}

	public Kindergarten retrieveKindergarten(Long id)
	{
	return	kindergartenRepository.findById(id).orElse(null);
	
	}
	public List<Kindergarten> findallkindergartens() {
		return (List<Kindergarten>) kindergartenRepository.findAll();
	}

	@Override
	public HashMap<Long, String> findParentfromKindergarten(Long id) {
		Kindergarten k=retrieveKindergarten(id);
		HashMap<Long, String> parents=new HashMap<Long,String>();
		Long i=(long) 0;
		for(Parent p:k.getParents()){
			i++;
			parents.put(i, p.getEmail());}
		 return parents;
		
	}

	@Override
	public int countparentfromkindergarten(Long idk) {
		// TODO Auto-generated method stub
		return kindergartenRepository.countparent(idk);
	}

	@Override
	public Kindergarten retrievekinderemail(String id) {
		List<Kindergarten> kinders=(List<Kindergarten>) kindergartenRepository.findAll();
		for(Kindergarten k:kinders)
		{
			if(k.getEmail().equals(id))
				return k;
		}
		return null;
	}

}
