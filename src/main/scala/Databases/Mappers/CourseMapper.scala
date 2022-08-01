package Databases.Mappers

import Databases.Mappers.Kas.{AbilityMapper, KnowledgeMapper, SkillMapper}
import Databases.Models.Dao.CourseEntity
import Databases.Models.Domain.Course

/**
 * Маппер CourseEntity/Course
 *
 * @see ICourseMapper
 * */
object CourseMapper extends ICourseMapper {
  /**
   * Перевод из CourseEntity в Course
   *
   * @param courseEntity Entity для перевода
   * @return полученная Model
   */
  override def entity2Model(courseEntity: CourseEntity): Course =
    Course(
      id = courseEntity.id,
      name = courseEntity.name,
      inputSkills = courseEntity.inputSkills.map(SkillMapper.entity2Model),
      outputSkills = courseEntity.outputSkills.map(SkillMapper.entity2Model),
      inputAbility = courseEntity.inputAbilities.map(AbilityMapper.entity2Model),
      outputAbility = courseEntity.outputAbilities.map(AbilityMapper.entity2Model),
      inputKnowledge = courseEntity.inputKnowledge.map(KnowledgeMapper.entity2Model),
      outputKnowledge = courseEntity.outputKnowledge.map(KnowledgeMapper.entity2Model)
    )

  /**
   * Перевод из Course в CourseEntity
   *
   * @param course Model для перевода
   * @return полученная Entity
   */
  override def model2Entity(course: Course): CourseEntity =
    CourseEntity(
      id = course.id,
      name = course.name,
      inputSkills = course.inputSkills.map(SkillMapper.model2Entity),
      outputSkills = course.outputSkills.map(SkillMapper.model2Entity),
      inputAbilities = course.inputAbility.map(AbilityMapper.model2Entity),
      outputAbilities = course.outputAbility.map(AbilityMapper.model2Entity),
      inputKnowledge = course.inputKnowledge.map(KnowledgeMapper.model2Entity),
      outputKnowledge = course.outputKnowledge.map(KnowledgeMapper.model2Entity)
    )
}
