CREATE SCHEMA courses;

-- смена схемы на courses
SET search_path TO courses;

/*
    Таблица курсов: курс1, курс2 и тд.
    Распредленная таблица, столбец распределения - id
*/
CREATE TABLE COURSE
(
    id   UUID NOT NULL UNIQUE PRIMARY KEY,
    name VARCHAR(255)
);
SELECT create_distributed_table('course', 'id');

/*
    Таблица знаний: знание1, знание2 и тд.
    Справочная таблица
*/
CREATE TABLE KNOWLEDGE
(
    id   UUID NOT NULL UNIQUE PRIMARY KEY,
    name VARCHAR(255)
);
SELECT create_reference_table('knowledge');

/*
    Таблица навыков Навыки: навык1, навык2 и тд.
*/
CREATE TABLE SKILL
(
    id   UUID NOT NULL UNIQUE PRIMARY KEY,
    name VARCHAR(255)
);
-- Сделать таблицу НАВЫКОВ справочной (содержит по локальной коппи на каждом узле)
SELECT create_reference_table('courses.skill');

/*
    Умения: умение1, умение2 и тд.
    UUID -> UUID
    VARCHAR -> STRING
*/
CREATE TABLE ABILITY
(
    id   UUID NOT NULL UNIQUE PRIMARY KEY,
    name VARCHAR(255)
);
-- Сделать таблицу УМЕНИЙ справочной (содержит по локальной коппи на каждом узле)
SELECT create_reference_table('courses.ability');
/**************************************************/

/*
    Таблица связи n:n курса и входных знаний
    UUID -> UUID
*/
CREATE TABLE COURSE_INPUT_KNOWLEDGE
(
    course_id    UUID NOT NULL,
    knowledge_id UUID NOT NULL
);
-- Делаем таблицу распределенно, таблиц распределения - course_id, причем
SELECT create_distributed_table('courses.course_input_knowledge', 'course_id', colocate_with => 'courses.course');
ALTER TABLE course_input_knowledge
    ADD CONSTRAINT course_input_knowledge_pk
        PRIMARY KEY (course_id, knowledge_id);
ALTER TABLE course_input_knowledge
    ADD CONSTRAINT course_input_knowledge_fk
        FOREIGN KEY (course_id)
            REFERENCES COURSE (id)
            ON DELETE CASCADE;
ALTER TABLE course_input_knowledge
    ADD CONSTRAINT input_knowledge_fk
        FOREIGN KEY (knowledge_id)
            REFERENCES KNOWLEDGE (id)
            ON DELETE CASCADE;


/*
    Таблица связи n:n курса и входных навыков
    UUID -> UUID
*/
CREATE TABLE COURSE_INPUT_SKILL
(
    course_id UUID NOT NULL,
    skill_id  UUID NOT NULL
);
SELECT create_distributed_table('courses.course_input_skill', 'course_id', colocate_with => 'courses.course');
ALTER TABLE course_input_skill
    ADD CONSTRAINT course_input_skill_pk
        PRIMARY KEY (course_id, skill_id);
ALTER TABLE course_input_skill
    ADD CONSTRAINT course_input_skill_fk
        FOREIGN KEY (course_id)
            REFERENCES COURSE (id)
            ON DELETE CASCADE;
ALTER TABLE course_input_skill
    ADD CONSTRAINT input_skill_fk
        FOREIGN KEY (skill_id)
            REFERENCES SKILL (id)
            ON DELETE CASCADE;

/*
    Таблица связи n:n курса и входных умения
    UUID -> UUID
*/
CREATE TABLE COURSE_INPUT_ABILITY
(
    course_id  UUID NOT NULL,
    ability_id UUID NOT NULL
);
SELECT create_distributed_table('courses.course_input_ability', 'course_id', colocate_with => 'courses.course');
ALTER TABLE course_input_ability
    ADD CONSTRAINT course_input_ability_pk
        PRIMARY KEY (course_id, ability_id);
ALTER TABLE course_input_ability
    ADD CONSTRAINT course_input_ability_fk
        FOREIGN KEY (course_id)
            REFERENCES COURSE (id)
            ON DELETE CASCADE;
ALTER TABLE course_input_ability
    ADD CONSTRAINT input_ability_fk
        FOREIGN KEY (ability_id)
            REFERENCES ABILITY (id)
            ON DELETE CASCADE;
/**************************************************/

/*
    Таблица связи n:n курса и выходных знаний
    UUID -> UUID
*/
CREATE TABLE COURSE_OUTPUT_KNOWLEDGE
(
    course_id    UUID NOT NULL,
    knowledge_id UUID NOT NULL
);
SELECT create_distributed_table('courses.course_output_knowledge', 'course_id', colocate_with => 'courses.course');
ALTER TABLE course_output_knowledge
    ADD CONSTRAINT course_output_knowledge_pk
        PRIMARY KEY (course_id, knowledge_id);
ALTER TABLE course_output_knowledge
    ADD CONSTRAINT course_output_knowledge_fk
        FOREIGN KEY (course_id)
            REFERENCES COURSE (id)
            ON DELETE CASCADE;
ALTER TABLE course_output_knowledge
    ADD CONSTRAINT output_knowledge_fk
        FOREIGN KEY (knowledge_id)
            REFERENCES KNOWLEDGE (id)
            ON DELETE CASCADE;

/*
    Таблица связи n:n курса и входных навыков
    UUID -> UUID
*/
CREATE TABLE COURSE_OUTPUT_SKILL
(
    course_id UUID NOT NULL,
    skill_id  UUID NOT NULL
);
SELECT create_distributed_table('courses.course_output_skill', 'course_id', colocate_with => 'courses.course');
ALTER TABLE course_output_skill
    ADD CONSTRAINT course_output_skill_pk
        PRIMARY KEY (course_id, skill_id);
ALTER TABLE course_output_skill
    ADD CONSTRAINT course_output_skill_fk
        FOREIGN KEY (course_id)
            REFERENCES COURSE (id)
            ON DELETE CASCADE;
ALTER TABLE course_output_skill
    ADD CONSTRAINT output_skill_fk
        FOREIGN KEY (skill_id)
            REFERENCES SKILL (id)
            ON DELETE CASCADE;

/*
    Таблица связи n:n курса и входных умения
    UUID -> UUID
*/
CREATE TABLE COURSE_OUTPUT_ABILITY
(
    course_id  UUID NOT NULL,
    ability_id UUID NOT NULL
);
SELECT create_distributed_table('courses.course_output_ability', 'course_id', colocate_with => 'courses.course');
ALTER TABLE course_output_ability
    ADD CONSTRAINT course_output_ability_pk
        PRIMARY KEY (course_id, ability_id);
ALTER TABLE course_output_ability
    ADD CONSTRAINT course_output_ability_fk
        FOREIGN KEY (course_id)
            REFERENCES COURSE (id)
            ON DELETE CASCADE;
ALTER TABLE course_output_ability
    ADD CONSTRAINT output_ability_fk
        FOREIGN KEY (ability_id)
            REFERENCES ABILITY (id)
            ON DELETE CASCADE;


/*
    Таблица ключевых слов знаний (KNOWLEDGE)
    UUID -> UUID
    VARCHAR -> STRING
*/
CREATE TABLE KNOWLEDGE_KEYWORD
(
    id   UUID NOT NULL UNIQUE PRIMARY KEY,
    name VARCHAR(255)
);
-- Сделать таблицу курсов распредленной, столбец распределения - id
SELECT create_reference_table('courses.knowledge_keyword');

/*
    Таблица связи n:n знаний (KNOWLEDGE) и ключевых слов навыков (KNOWLEDGE_KEYWORD)
    UUID -> UUID
*/
CREATE TABLE KNOWLEDGE_KEYWORD_LINK
(
    knowledge_id UUID NOT NULL,
    keyword_id   UUID NOT NULL
);
SELECT create_reference_table('courses.knowledge_keyword_link');
ALTER TABLE knowledge_keyword_link
    ADD CONSTRAINT knowledge_keyword_link_pk
        PRIMARY KEY (knowledge_id, keyword_id);
ALTER TABLE knowledge_keyword_link
    ADD CONSTRAINT skill_keyword_link_fk
        FOREIGN KEY (knowledge_id)
            REFERENCES KNOWLEDGE (id)
            ON DELETE CASCADE;
ALTER TABLE knowledge_keyword_link
    ADD CONSTRAINT keyword_fk
        FOREIGN KEY (keyword_id)
            REFERENCES KNOWLEDGE_KEYWORD (id)
            ON DELETE CASCADE;

/*
    Таблица ключевых слов знаний (KNOWLEDGE)
    UUID -> UUID
    VARCHAR -> STRING
*/
CREATE TABLE ABILITY_KEYWORD
(
    id   UUID NOT NULL UNIQUE PRIMARY KEY,
    name VARCHAR(255)
);
-- Сделать таблицу курсов распредленной, столбец распределения - id
SELECT create_reference_table('courses.ability_keyword');

/*
    Таблица связи n:n умений (KNOWLEDGE) и ключевых слов навыков (KNOWLEDGE_KEYWORD)
    UUID -> UUID
*/
CREATE TABLE ABILITY_KEYWORD_LINK
(
    ability_id UUID NOT NULL,
    keyword_id UUID NOT NULL
);
SELECT create_reference_table('courses.ability_keyword_link');
ALTER TABLE ability_keyword_link
    ADD CONSTRAINT ability_keyword_link_pk
        PRIMARY KEY (ability_id, keyword_id);
ALTER TABLE ability_keyword_link
    ADD CONSTRAINT ability_keyword_link_fk
        FOREIGN KEY (ability_id)
            REFERENCES ABILITY (id)
            ON DELETE CASCADE;
ALTER TABLE ability_keyword_link
    ADD CONSTRAINT keyword_fk
        FOREIGN KEY (keyword_id)
            REFERENCES ABILITY_KEYWORD (id)
            ON DELETE CASCADE;

/*
    Таблица ключевых слов навыков (SKILL)
    UUID -> UUID
    VARCHAR -> STRING
*/
CREATE TABLE SKILL_KEYWORD
(
    id   UUID NOT NULL UNIQUE PRIMARY KEY,
    name VARCHAR(255)
);
-- Сделать таблицу курсов распредленной, столбец распределения - id
SELECT create_reference_table('courses.skill_keyword');

/*
    Таблица связи n:n навыков (SKILL) и ключевых слов навыков (SKILL_KEYWORD)
    UUID -> UUID
*/
CREATE TABLE SKILL_KEYWORD_LINK
(
    skill_id   UUID NOT NULL,
    keyword_id UUID NOT NULL
);
SELECT create_reference_table('courses.skill_keyword_link');
ALTER TABLE skill_keyword_link
    ADD CONSTRAINT skill_keyword_link_pk
        PRIMARY KEY (skill_id, keyword_id);
ALTER TABLE skill_keyword_link
    ADD CONSTRAINT skill_keyword_link_fk
        FOREIGN KEY (skill_id)
            REFERENCES SKILL (id)
            ON DELETE CASCADE;
ALTER TABLE skill_keyword_link
    ADD CONSTRAINT keyword_fk
        FOREIGN KEY (keyword_id)
            REFERENCES SKILL_KEYWORD (id)
            ON DELETE CASCADE;

COMMIT;



SET search_path TO public;
-----------------------------------------------------------------------------
--                         Вспомогательные функции                         --
-----------------------------------------------------------------------------
/*
    Функция нахождения разности массивов.
    Из массива minuend вычитаются все значения из массива subtrahend
*/
CREATE OR REPLACE FUNCTION arrDif(minuend anyarray, subtrahend anyarray) RETURNS anyarray AS
$code$
BEGIN
    RETURN (SELECT array
                       (SELECT unnest(minuend)
                        EXCEPT
                        SELECT unnest(subtrahend)));
END;
$code$ LANGUAGE plpgsql;



/*
    Получение курсов по id из переданного массива
*/
CREATE OR REPLACE FUNCTION getCoursesFromArray(idArr UUID[])
    RETURNS TABLE
            (
                id   UUID,
                name VARCHAR(255)
            )
AS
$code$
BEGIN
    RETURN QUERY
        (SELECT courses.course.id, courses.course.name
         FROM courses.course
         WHERE courses.course.id IN (SELECT * FROM unnest(idArr)));
END;
$code$ LANGUAGE plpgsql;



-----------------------------------------------------------------------------
--                Функции получения курсов по выходным ЗУНам               --
-----------------------------------------------------------------------------
/*
    Получение массива id курсов, для которых переданный Навык (Skill) является
    выходным
*/
CREATE OR REPLACE FUNCTION getCoursesByOutputSKills(kasID UUID)
    RETURNS UUID[]
AS
$code$
DECLARE
    courses UUID[];
/*
    В результирующий массив добавляем id курсов из разных БД
*/
BEGIN
    courses := ARRAY(SELECT courses.course.id
                     FROM courses.course
                              LEFT JOIN courses.course_output_skill
                                        ON courses.course.id =
                                           courses.course_output_skill.course_id
                     WHERE courses.course_output_skill.skill_id = kasID);
    RETURN courses;
END;
$code$ LANGUAGE plpgsql;



/*
    Получение массива id курсов, для которых переданное Умение (Ability) является
    выходным
*/
CREATE OR REPLACE FUNCTION getCoursesByOutputAbilities(kasID UUID)
    RETURNS UUID[]
AS
$code$
DECLARE
    courses UUID[];
/*
    В результирующий массив добавляем id курсов из разных БД
*/
BEGIN
    courses := ARRAY(SELECT courses.course.id
                     FROM courses.course
                              LEFT JOIN courses.course_output_ability
                                        ON courses.course.id =
                                           courses.course_output_ability.course_id
                     WHERE courses.course_output_ability.ability_id = kasID);
    RETURN courses;
END;
$code$ LANGUAGE plpgsql;



/*
    Получение массива id курсов, для которых переданное Знание (Knowledge)
    является выходным
*/
CREATE OR REPLACE FUNCTION getCoursesByOutputKnowledge(kasID UUID)
    RETURNS UUID[]
AS
$code$
DECLARE
    courses UUID[];
/*
    В результирующий массив добавляем id курсов из разных БД
*/
BEGIN
    courses := ARRAY(SELECT courses.course.id
                     FROM courses.course
                              LEFT JOIN courses.course_output_knowledge
                                        ON courses.course.id =
                                           courses.course_output_knowledge.course_id
                     WHERE courses.course_output_knowledge.knowledge_id = kasID);
    RETURN courses;
END;
$code$ LANGUAGE plpgsql;


-----------------------------------------------------------------------------
--            Функции получения входных ЗУНов переданного Курса            --
-----------------------------------------------------------------------------
/*
    Получение массива id Навыков (Skill), которые являются входным для
    переданного курса
*/
CREATE OR REPLACE FUNCTION getInputSkillsByCourse(courseID UUID)
    RETURNS UUID[]
AS
$code$
DECLARE
    skills UUID[];
/*
    В результирующий массив добавляем id Навыков(Skill) из разных БД
*/
BEGIN
    skills := ARRAY(SELECT courses.skill.id
                    FROM courses.skill
                             LEFT JOIN courses.course_input_skill
                                       ON courses.skill.id =
                                          courses.course_input_skill.skill_id
                    WHERE courses.course_input_skill.course_id = courseID);
    RETURN skills;
END;
$code$ LANGUAGE plpgsql;



/*
    Получение массива id Умений (Ability), которые являются входным для
    переданного курса
*/
CREATE OR REPLACE FUNCTION getInputAbilitiesByCourse(courseID UUID)
    RETURNS UUID[]
AS
$code$
DECLARE
    abilities UUID[];
/*
    В результирующий массив добавляем id Умений (Ability) из разных БД
*/
BEGIN
    abilities := ARRAY(SELECT courses.ability.id,
                              courses.ability.name
                       FROM courses.ability
                                LEFT JOIN courses.course_input_ability
                                          ON courses.ability.id =
                                             courses.course_input_ability.ability_id
                       WHERE courses.course_input_ability.course_id = courseID);
    RETURN abilities;
END;
$code$ LANGUAGE plpgsql;



/*
    Получение массива id Знаний (Knowledge), которые являются входным для
    переданного курса
*/
CREATE OR REPLACE FUNCTION getInputKnowledgeByCourse(courseID UUID)
    RETURNS UUID[]
AS
$code$
DECLARE
    knowledge UUID[];
/*
В результирующий массив добавляем id Умений (Ability) из разных БД
*/
BEGIN
    knowledge := (SELECT courses.knowledge.id, courses.knowledge.name
                  FROM courses.knowledge
                           LEFT JOIN courses.course_input_knowledge
                                     ON courses.knowledge.id =
                                        courses.course_input_knowledge.knowledge_id
                  WHERE courses.course_input_knowledge.course_id = courseID);
    RETURN knowledge;
END;
$code$ LANGUAGE plpgsql;



-----------------------------------------------------------------------------
--         Функции построение траекторий для отдельных видов KASов         --
-----------------------------------------------------------------------------
/*
    Построение траектории по Навыкам (Skill)
    От выходных Навыков (outputs) стороются пути к входным (inputs)
    Возращается массив id курсов, через которые строится траектория
*/
CREATE OR REPLACE FUNCTION makeTrajectoryBySkills(inputs UUID[], outputs
    UUID[]) RETURNS UUID[] AS
$code$
DECLARE
    courses    UUID[];
    tempArr    UUID[];
    tempCourse UUID;
    tempKas    UUID;
BEGIN

    -- Поскольку plpgsql не поддерживает работу с многомерными массивами,
    -- используем временную таблицу, в которую будем помещать массивы.
    CREATE TEMP TABLE steps
    (
        stepNumber SERIAL,
        step       UUID[]
    );

    -- Первый шаг алгоритма выполняется от переданных выходных Навыков (outputs)
    INSERT INTO steps(step)
    VALUES (outputs);

    -- Цикл - Пока есть следующий шаг
    WHILE (NOT ((SELECT step FROM steps ORDER BY stepNumber DESC LIMIT 1) = '{}'))
        LOOP
        -- Объявление локальный переменных внутри цикла.
        -- Обнуляются при каждой иттерации
            DECLARE
                coursesByStep UUID[];
                nextStep      UUID[];
            BEGIN
                -- Для каждого Навыка (tempKas) из последнего шага
                -- получаем Курсы, для которых данный Навык является выходным
                -- и записываем в массив курсов данного шага
                FOREACH tempKas IN ARRAY (SELECT step FROM steps ORDER BY stepNumber DESC LIMIT 1)
                    LOOP
                        tempArr := (SELECT * FROM getCoursesByOutputSKills(tempKas));
                        coursesByStep := array_cat(coursesByStep, tempArr);
                    END LOOP;

                -- Если мы не смогли найти курсов, для которых Навык (skill) является выходным
                -- значит, что данные Навыки (Skill) являются истоками
                -- Работа алгоритма прерывается, возвращается пустая таблица
                IF (coursesByStep = '{}') THEN
                    DROP TABLE steps;
                    RAISE EXCEPTION 'Impossible to build a trajectory based on SKILLS';
                END IF;

                -- Для каждого курса, полученного на данном шаге получаем входные Навыки(Skills)
                -- и записываем их в следующий шаг
                FOREACH tempCourse IN ARRAY (coursesByStep)
                    LOOP
                        nextStep := array_cat(nextStep, (SELECT * FROM getInputSkillsByCourse(tempCourse)));
                    END LOOP;

                -- В следующий шаг записываем разность полученных выше Навыков (Skill)
                -- и входных Навыков (inputs)
                -- Таким образом мы останавливаем работу алгоритма на уже построенных траекториях
                INSERT INTO steps(step)
                VALUES ((SELECT * FROM arrDif(nextStep, inputs)));

                -- В результирующий массив курсов записываем курсы, полученные на данном шаге
                courses := array_cat(courses, coursesByStep);
            END;
        END LOOP;
    DROP TABLE steps;

    -- Возвращаем массив id курсов
    RETURN courses;
END;
$code$ LANGUAGE plpgsql;



/*
    Построение траектории по Умениям (Ability)
    От выходных Умений (outputs) стороются пути к входным (inputs)
    Возращается таблица курсов, через которые строится траектория
*/
CREATE OR REPLACE FUNCTION makeTrajectoryByAbilities(inputs UUID[], outputs
    UUID[]) RETURNS UUID[] AS
$code$
DECLARE
    courses    UUID[];
    tempArr    UUID[];
    tempCourse UUID;
    tempKas    UUID;
BEGIN
    -- Поскольку plpgsql не поддерживает работу с многомерными массивами,
    -- используем временную таблицу, в которую будем помещать массивы.
    CREATE TEMP TABLE steps
    (
        stepNumber SERIAL,
        step       UUID[]
    );

    -- Первый шаг алгоритма выполняется от переданных выходных Умений (Ability)
    INSERT INTO steps(step)
    VALUES (outputs);

    -- Цикл - Пока есть следующий шаг
    WHILE (NOT ((SELECT step FROM steps ORDER BY stepNumber DESC LIMIT 1) = '{}'))
        LOOP
        -- Объявление локальный переменных внутри цикла.
        -- Обнуляются при каждой иттерации
            DECLARE
                coursesByStep UUID[];
                nextStep      UUID[];
            BEGIN
                -- Для каждого Умения (tempKas) из последнего шага
                -- получаем Курсы, для которых данное Умение является выходным
                -- и записываем в массив курсов данного шага
                FOREACH tempKas IN ARRAY (SELECT step FROM steps ORDER BY stepNumber DESC LIMIT 1)
                    LOOP
                        tempArr := (SELECT * FROM getCoursesByOutputAbilities(tempKas));
                        coursesByStep := array_cat(coursesByStep, tempArr);
                    END LOOP;

                -- Если мы не смогли найти курсов, для которых Умение (ability) является выходным
                -- значит, что данные Умения (Ability) являются истоками
                -- Работа алгоритма прерывается, возвращается пустая таблица
                IF (coursesByStep = '{}') THEN
                    DROP TABLE steps;
                    RAISE EXCEPTION 'Impossible to build a trajectory based on Ability';
                END IF;

                -- Для каждого курса, полученного на данном шаге получаем входные Умения (Ability)
                -- и записываем их в следующий шаг
                FOREACH tempCourse IN ARRAY (coursesByStep)
                    LOOP
                        nextStep := array_cat(nextStep, (SELECT * FROM getInputAbilitiesByCourse(tempCourse)));
                    END LOOP;

                -- В следующий шаг записываем разность полученных выше Умений (Ability)
                -- и входных Умений (inputs)
                -- Таким образом мы останавливаем работу алгоритма на уже построенных траекториях
                INSERT INTO steps(step)
                VALUES ((SELECT * FROM arrDif(nextStep, inputs)));

                -- В результирующий массив курсов записываем курсы, полученные на данном шаге
                courses := array_cat(courses, coursesByStep);
            END;
        END LOOP;
    DROP TABLE steps;

    -- Возвращаем массив id курсов
    RETURN courses;
END;
$code$ LANGUAGE plpgsql;



/*
    Построение траектории по Знаниям (Knowledge)
    От выходных Знаний (outputs) стороются пути к входным (inputs)
    Возращается таблица курсов, через которые строится траектория
*/
CREATE OR REPLACE FUNCTION makeTrajectoryByKnowledge(inputs UUID[], outputs
    UUID[]) RETURNS UUID[] AS
$code$
DECLARE
    courses    UUID[];
    tempArr    UUID[];
    tempCourse UUID;
    tempKas    UUID;
BEGIN
    -- Поскольку plpgsql не поддерживает работу с многомерными массивами,
    -- используем временную таблицу, в которую будем помещать массивы.
    CREATE TEMP TABLE steps
    (
        stepNumber SERIAL,
        step       UUID[]
    );

    -- Первый шаг алгоритма выполняется от переданных выходных Знаний (outputs)
    INSERT INTO steps(step)
    VALUES (outputs);

    -- Цикл - Пока есть следующий шаг
    WHILE (NOT ((SELECT step FROM steps ORDER BY stepNumber DESC LIMIT 1) = '{}'))
        LOOP
        -- Объявление локальный переменных внутри цикла.
        -- Обнуляются при каждой иттерации
            DECLARE
                coursesByStep UUID[];
                nextStep      UUID[];
            BEGIN

                -- Для каждого Знания (tempKas) из последнего шага
                -- получаем Курсы, для которых данное Знание является выходным
                -- и записываем в массив курсов данного шага
                FOREACH tempKas IN ARRAY (SELECT step FROM steps ORDER BY stepNumber DESC LIMIT 1)
                    LOOP
                        tempArr := (SELECT * FROM getCoursesByOutputKnowledge(tempKas));
                        coursesByStep := array_cat(coursesByStep, tempArr);
                    END LOOP;

                -- Если мы не смогли найти курсов, для которых Знание (knowledge) является выходным
                -- значит, что данные Знания (Knowledge) являются истоками
                -- Работа алгоритма прерывается, возвращается пустая таблица
                IF (coursesByStep = '{}') THEN
                    DROP TABLE steps;
                    RAISE EXCEPTION 'Impossible to build a trajectory based on Knowledge';
                END IF;

                -- Для каждого курса, полученного на данном шаге получаем входные Знания(Knowledge)
                -- и записываем их в следующий шаг
                FOREACH tempCourse IN ARRAY (coursesByStep)
                    LOOP
                        nextStep := array_cat(nextStep, (SELECT * FROM getInputKnowledgeByCourse(tempCourse)));
                    END LOOP;

                -- В следующий шаг записываем разность полученных выше Знаний (Knowledge)
                -- и входных Знаний (inputs)
                -- Таким образом мы останавливаем работу алгоритма на уже построенных траекториях
                INSERT INTO steps(step)
                VALUES ((SELECT * FROM arrDif(nextStep, inputs)));

                -- В результирующий массив курсов записываем курсы, полученные на данном шаге
                courses := array_cat(courses, coursesByStep);
            END;
        END LOOP;
    DROP TABLE steps;

    -- Возвращаем массив id курсов
    RETURN courses;
END;
$code$ LANGUAGE plpgsql;



/*
    Получение курсов для постоения траектории по заданным входным и выходным
    KASам
*/
CREATE OR REPLACE FUNCTION makeTrajectory(inputsKnowledge UUID[], outputsKnowledge UUID[],
                                          inputsAbilities UUID[], outputsAbilities UUID[],
                                          inputsSkills UUID[], outputsSkills UUID[])
    RETURNS TABLE
            (
                id   UUID,
                name VARCHAR(255)
            )
AS
$code$
DECLARE
    resultCourses UUID[];
BEGIN
    IF NOT (inputsKnowledge = '{}' AND outputsKnowledge = '{}') THEN
        resultCourses :=
                array_cat(resultCourses, (SELECT * FROM makeTrajectoryByKnowledge(inputsKnowledge, outputsKnowledge)));
    END IF;
    IF NOT (inputsAbilities = '{}' AND outputsAbilities = '{}') THEN
        resultCourses :=
                array_cat(resultCourses, (SELECT * FROM makeTrajectoryByAbilities(inputsAbilities, outputsAbilities)));
    END IF;
    IF NOT (inputsSkills = '{}' AND outputsSkills = '{}') THEN
        resultCourses :=
                array_cat(resultCourses, (SELECT * FROM makeTrajectoryBySkills(inputsSkills, outputsSkills)));
    END IF;
    RETURN QUERY
            (SELECT DISTINCT * FROM getCoursesFromArray(resultCourses));
EXCEPTION
    WHEN OTHERS
        THEN
            RETURN;
END;
$code$ LANGUAGE plpgsql;