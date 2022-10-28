package redsocial.multimedia.dao;


import java.util.List;

public interface NotificationDao {

    String addConnection(String connectionId , String multimediaId)  ;
    List<String> getConnection(String multimediaId) ;
    void updateConnection(String multimediaId , List<String> connectionId)  ;

}
