package mju.iphak.maru_egg.question.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.question.domain.Question;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {
	List<Question> findAllByAdmissionTypeAndAdmissionCategoryOrderByViewCountDesc(AdmissionType type,
		AdmissionCategory category);

	List<Question> findAllByAdmissionTypeOrderByViewCountDesc(AdmissionType type);
}