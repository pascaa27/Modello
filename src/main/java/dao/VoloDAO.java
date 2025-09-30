package dao;

import model.Volo;
import java.util.List;

public interface VoloDAO {
    Volo findByCodiceUnivoco(String codiceUnivoco);
    List<Volo> findAll();
    boolean insert(Volo v);
    boolean update(Volo v);
    boolean delete(String codiceUnivoco);
    long count();
}