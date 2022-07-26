-- Переключить на нужнуюю БД

-- смена схемы на courses
SET search_path TO courses;

DROP TABLE IF EXISTS knowledge_keyword_link;
DROP TABLE IF EXISTS ability_keyword_link;
DROP TABLE IF EXISTS skill_keyword_link;

DROP TABLE IF EXISTS knowledge_keyword;
DROP TABLE IF EXISTS ability_keyword;
DROP TABLE IF EXISTS skill_keyword;

DROP TABLE IF EXISTS course_input_knowledge;
DROP TABLE IF EXISTS course_input_ability;
DROP TABLE IF EXISTS course_input_skill;

DROP TABLE IF EXISTS course_output_knowledge;
DROP TABLE IF EXISTS course_output_ability;
DROP TABLE IF EXISTS course_output_skill;

DROP TABLE IF EXISTS knowledge;
DROP TABLE IF EXISTS ability;
DROP TABLE IF EXISTS skill;

DROP TABLE IF EXISTS course;

DROP SCHEMA IF EXISTS courses;

SELECT citus_remove_node('192.168.122.133', 5432);
SELECT citus_remove_node('192.168.122.221', 5432);
SELECT citus_remove_node('192.168.122.150', 5432);