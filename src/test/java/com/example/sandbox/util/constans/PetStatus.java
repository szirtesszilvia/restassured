package com.example.sandbox.util.constans;
public enum PetStatus {
    AVAILABLE,
    PENDING,
    SOLD;

    @Override
    public String toString(){
        return this.name().toLowerCase();
    }
}
