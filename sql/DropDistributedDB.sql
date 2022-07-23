-- Переключить на нужнуюю БД

-- смена схемы на courses
SET search_path TO courses;

DROP TABLE knowledge_keyword_link;
DROP TABLE ability_keyword_link;
DROP TABLE skill_keyword_link;

DROP TABLE knowledge_keywords;
DROP TABLE ability_keywords;
DROP TABLE skill_keywords;

DROP TABLE course_input_knowledge;
DROP TABLE course_input_abilities;
DROP TABLE course_input_skills;

DROP TABLE course_output_knowledge;
DROP TABLE course_output_abilities;
DROP TABLE course_output_skills;

DROP TABLE knowledge;
DROP TABLE abilities;
DROP TABLE skills;

DROP TABLE course;

SELECT citus_remove_node('192.168.122.133', 5432);
SELECT citus_remove_node('192.168.122.221', 5432);
SELECT citus_remove_node('192.168.122.150', 5432);