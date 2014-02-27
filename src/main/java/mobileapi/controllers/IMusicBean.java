package mobileapi.controllers;

import mobileapi.db.entities.MusicEntity;
import mobileapi.db.entities.MusicRatingEntity;

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
}
