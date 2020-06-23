package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @PersistenceContext EntityManager em;

    @Test
    public void testMember() throws Exception {
        //given
        Member member = new Member("memberA");
        Member saveMember = memberJpaRepository.save(member);

        //when
        Member findMember = memberJpaRepository.find(saveMember.getId());

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
    }
    
    @Test
    public void basicCRUD() throws Exception {
        //given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //when
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        List<Member> members = memberJpaRepository.findAll();
        long count = memberJpaRepository.count();

        //then
        Assertions.assertThat(findMember1.getId()).isEqualTo(member1.getId());
        Assertions.assertThat(findMember1.getUserName()).isEqualTo(member1.getUserName());
        Assertions.assertThat(findMember2.getId()).isEqualTo(member2.getId());
        Assertions.assertThat(findMember2.getUserName()).isEqualTo(member2.getUserName());

        Assertions.assertThat(members.size()).isEqualTo(2);
        Assertions.assertThat(count).isEqualTo(2);


        //when2
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long count2 = memberJpaRepository.count();

        //then2
        Assertions.assertThat(count2).isEqualTo(0);
    }
    
    @Test
    public void paging() throws Exception {
        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        //when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        //then
        Assertions.assertThat(members.size()).isEqualTo(3);
        Assertions.assertThat(totalCount).isEqualTo(5);
    }
    
    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 19));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 21));
        memberJpaRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        // 영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        List<Member> result = memberJpaRepository.findUserName("member5");
        Member member = result.get(0);

        // 영속성 컨텍스트를 초기화 하지 않으면 40이 출력됨!!
        System.out.println(member.getAge());

        //then
        Assertions.assertThat(resultCount).isEqualTo(3);
    }
}