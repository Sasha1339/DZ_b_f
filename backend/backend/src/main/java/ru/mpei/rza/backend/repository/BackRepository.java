package ru.mpei.rza.backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import ru.mpei.rza.backend.dto.Measurement;

import java.util.List;

@org.springframework.stereotype.Repository
@Transactional
public class BackRepository implements Repository{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void sendFault(List<Measurement> measurements) {
        entityManager.createNativeQuery("DELETE FROM measurement").executeUpdate();

        for (Measurement measurement: measurements){
            entityManager.createNativeQuery("INSERT INTO measurement (id, number_channel, channel, value_current, time) VALUES (?,?,?,?,?)")
                    .setParameter(1, measurement.getId())
                    .setParameter(2, measurement.getNumberChannel())
                    .setParameter(3, measurement.getChannel())
                    .setParameter(4, measurement.getValueCurrent())
                    .setParameter(5, measurement.getTime())
                    .executeUpdate();
        }
    }

    @Override
    public List<Measurement> getAll() {
        TypedQuery<Measurement> query = entityManager.createQuery("select m from Measurement m", Measurement.class);
        return query.getResultList();
    }
}
