package ru.cybern.kinoserver.mobileapi.controllers;

import ru.cybern.kinoserver.mobileapi.db.entities.MusicEntity;
import ru.cybern.kinoserver.mobileapi.db.entities.MusicRatingEntity;
import ru.cybern.kinoserver.mobileapi.db.entities.PerformerEntity;

import java.util.List;


public interface IMusicBean {

    MusicEntity saveMusic(MusicEntity music);

    void deleteMusic(MusicEntity music);

    MusicEntity getMusic(int id);

    boolean deleteMusic(int id);

    List<MusicEntity> getAllMusic();


    MusicRatingEntity saveRating(MusicRatingEntity rating);

    void deleteRating(MusicRatingEntity rating);

    MusicRatingEntity getRating(int id);

    boolean deleteRating(int id);

    List<MusicRatingEntity> getRatings();


    PerformerEntity savePerformer(PerformerEntity performer);

    void deletePerformer(PerformerEntity performer);

    PerformerEntity getPerformer(int id);

    List<PerformerEntity> getPerformers();


}
