package ru.specialist.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

public interface CourseRepositoryCustomized {

	record MedianaAndAverege (double mediana, double average) { }

	//int getCourseMaxLength();
	List<Course> findByTitle(String title);

	double getMedianaCourseLength();

	double getAverageCourseLength();

	MedianaAndAverege getMedianaAndAverege();

}
