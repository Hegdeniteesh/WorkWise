package com.workwise.repository;

import com.workwise.model.Skill;
import com.workwise.model.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    // Basic finders
    List<Skill> findByUserId(Long userId);
    List<Skill> findByCategory(SkillCategory category);
    List<Skill> findBySkillNameContainingIgnoreCase(String skillName);

    // Agricultural skills
    List<Skill> findByIsSeasonalSkillTrue();
    List<Skill> findByCropSpecializationContainingIgnoreCase(String cropType);
    List<Skill> findByCategoryIn(List<SkillCategory> agriculturalCategories);

    // Verification and rating
    List<Skill> findByIsVerifiedSkillTrue();
    List<Skill> findByRatingGreaterThanEqual(Double minRating);

    // Experience-based queries
    List<Skill> findByExperienceYearsGreaterThanEqual(Integer minExperience);

    @Query("SELECT s FROM Skill s WHERE s.experienceYears BETWEEN :minExp AND :maxExp")
    List<Skill> findByExperienceRange(@Param("minExp") Integer minExperience,
                                      @Param("maxExp") Integer maxExperience);

    // Pricing queries
    @Query("SELECT s FROM Skill s WHERE s.minHourlyRate <= :maxBudget AND s.maxHourlyRate >= :minBudget")
    List<Skill> findByRateRange(@Param("minBudget") Double minBudget,
                                @Param("maxBudget") Double maxBudget);

    // Statistical queries
    @Query("SELECT s.category, COUNT(s), AVG(s.rating) FROM Skill s GROUP BY s.category ORDER BY COUNT(s) DESC")
    List<Object[]> findSkillStatsByCategory();

    @Query("SELECT s.skillName, COUNT(s) FROM Skill s GROUP BY s.skillName ORDER BY COUNT(s) DESC")
    List<Object[]> findMostPopularSkills();

    // Advanced matching
    @Query("SELECT s FROM Skill s WHERE s.user.city = :city AND s.category = :category AND s.user.availabilityStatus = true")
    List<Skill> findAvailableSkillsInCityByCategory(@Param("city") String city, @Param("category") SkillCategory category);
}
