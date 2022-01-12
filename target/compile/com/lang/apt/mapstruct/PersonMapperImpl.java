package com.lang.apt.mapstruct;

import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-01-12T20:07:21+0800",
    comments = "version: 1.4.1.Final, compiler: javac, environment: Java 14.0.2 (Oracle Corporation)"
)
public class PersonMapperImpl implements PersonMapper {

    @Override
    public Teacher toTeacher(Student student) {
        if ( student == null ) {
            return null;
        }

        Teacher teacher = new Teacher();

        teacher.setName( student.getName() );
        teacher.setCode( student.getCode() );

        return teacher;
    }
}
