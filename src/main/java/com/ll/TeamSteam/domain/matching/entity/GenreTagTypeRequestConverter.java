package com.ll.TeamSteam.domain.matching.entity;

import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import org.springframework.core.convert.converter.Converter;

public class GenreTagTypeRequestConverter implements Converter<String, GenreTagType> {
    @Override
    public GenreTagType convert(String genreTagType) {
        return GenreTagType.ofCode(genreTagType);
    }
}
