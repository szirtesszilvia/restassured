package com.example.sandbox.util.body.pet;

import java.util.List;

import static com.example.sandbox.util.Tools.generateRandomNumber;
import com.example.sandbox.util.constans.PetStatus;
import static com.example.sandbox.util.constans.TestData.HYDRAIMAGE;
import com.example.sandbox.util.swagger.definitions.Item;
import com.example.sandbox.util.swagger.definitions.PetBody;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class PostCreatePetSimple extends PostCreatePet{

    public static PetBody postCreatePetSimple(int petId, String petName, Item category, String photoUrls, Item tags, String status){
        return PetBody.builder()
                .id(petId)
                .name(petName)
                .category(category)
                .photoUrl(photoUrls)
                .tag(tags)
                .status(status)
                .build();
    }

    public static PetBody createPetBodyDto(int petId, String petName, Item category, String photoUrls, Item tags, String status){
        return PetBody.builder()
                .id(petId)
                .name(petName)
                .category(category)
                .photoUrl(photoUrls)
                .tag(tags)
                .status(status)
                .build();
    }

    public static PetBody createPetBodyDto(int petId, String petName, Item category, List<String> photoUrls, List<Item> tags, String status){
        return PetBody.builder()
                .id(petId)
                .name(petName)
                .category(category)
                .photoUrls(photoUrls)
                .tags(tags)
                .status(status)
                .build();
    }

    public static Item createItem(int petId, String petName){
        return Item.builder()
                .id(petId)
                .name(petName)
                .build();
    }



}
