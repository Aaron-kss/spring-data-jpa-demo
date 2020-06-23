package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TeamJpaRepository {

    private final EntityManager em;

    public Team save(Team team){
        em.persist(team);
        return team;
    }

    public void delete(Team team){
        em.remove(team);
    }

    public List<Team> findAll(){
        return em.createQuery("select m from Team m")
                .getResultList();
    }

    public Optional<Team> findById(Long id){
        Team member = em.find(Team.class, id);
        return Optional.ofNullable(member);
    }

    private long count(){
        return em.createQuery("select count(m) from Team m", Long.class).getSingleResult();
    }

    public Team find(Long id){
        return em.find(Team.class, id);
    }
}
