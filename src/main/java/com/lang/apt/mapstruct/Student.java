package com.lang.apt.mapstruct;

public class Student extends Person{
    private Person guardian;
    private Person[] parents;

    public Person getGuardian() {
        return guardian;
    }

    public void setGuardian(Person guardian) {
        this.guardian = guardian;
    }


    public Person[] getParents() {
        return parents;
    }

    public void setParents(Person[] parents) {
        this.parents = parents;
    }

}
