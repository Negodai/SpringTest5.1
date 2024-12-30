package ru.specialist.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

// SUFFIX: Impl !!! (by default)
// className == Original_repository_interface_name + Impl
@Transactional(isolation = Isolation.READ_COMMITTED, timeout = 15, propagation = Propagation.REQUIRED,
					readOnly = false)
public class CourseRepositoryImpl implements CourseRepositoryCustomized {

	@PersistenceContext
	private EntityManager em;

	private PlatformTransactionManager tm;
	private TransactionTemplate tt;

	@Autowired
	public CourseRepositoryImpl(PlatformTransactionManager tm) {
		this.tm = tm;
		this.tt = new TransactionTemplate(tm);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<Course> findByTitle(String title) {
		return 
		em.createQuery("SELECT c FROM Course c WHERE c.title LIKE :search", Course.class)
		   .setParameter("search", "%"+title.trim()+"%")
		   .getResultList();
	}

	@Override
	@Transactional(readOnly = true)
	public MedianaAndAverege getMedianaAndAverege() {
		System.out.printf("getMedianaAndAverege - getIsolationLevel - [%s]; getPropagationBehavior - [%s]; toString - [%s]; isNewTransaction - [%s]  \n",
				tt.getIsolationLevel(), tt.getPropagationBehavior(), tm.getTransaction(tt).toString(), tm.getTransaction(tt).isNewTransaction());
		return new MedianaAndAverege(getMedianaCourseLength(), getAverageCourseLength());
	}

	@Override
	@Transactional(readOnly = true)
	public double getMedianaCourseLength() {
		System.out.printf("getMedianaCourseLength-  getIsolationLevel - [%s]; getPropagationBehavior - [%s]; getName - [%s]; isNewTransaction - [%s] \n ",
				tt.getIsolationLevel(), tt.getPropagationBehavior(), tm.getTransaction(tt).toString(), tm.getTransaction(tt).isNewTransaction());
		int[] m = em.createQuery("SELECT c FROM Course c ", Course.class).getResultList()
				.stream().mapToInt(c->c.getLength()).toArray();
		return mediana(m);
	}

	@Override
	@Transactional(readOnly = true)
	public double getAverageCourseLength() {
		System.out.printf("getAverageCourseLength - getIsolationLevel - [%s]; getPropagationBehavior - [%s]; getName - [%s]; isNewTransaction - [%s] \n ",
				tt.getIsolationLevel(), tt.getPropagationBehavior(), tm.getTransaction(tt).toString(), tm.getTransaction(tt).isNewTransaction());
		int[] m = em.createQuery("SELECT c FROM Course c ", Course.class).getResultList()
				.stream().mapToInt(c->c.getLength()).toArray();
		return average(m);
	}

	double average(int... m) {
		int summa = 0;
		for(int i =0; i < m.length; i++)
			summa += m[i];
		return (double)summa / m.length;
	}

	// O(n^2)
	double mediana(int... m) {
		Arrays.sort(m);
		if (m.length % 2 == 1)
			return m[m.length / 2];
		else
			return (m[m.length / 2] + m[m.length / 2-1]) / 2.0;
	}
}
