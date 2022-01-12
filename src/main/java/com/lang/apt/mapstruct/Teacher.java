package com.lang.apt.mapstruct;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Teacher extends Person{
    private BigDecimal salary;
    private List<Student> students;
    private LocalDateTime updateTime;

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
}
