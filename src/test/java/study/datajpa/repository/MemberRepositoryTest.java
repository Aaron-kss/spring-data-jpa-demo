package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() throws Exception {
        //given
        Member member = new Member("memberA");
        Member saveMember = memberRepository.save(member);

        //when
        Member findMember = memberRepository.findById(saveMember.getId()).get();

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
    }

    @Test
    public void basicCRUD() throws Exception {
        //given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        List<Member> members = memberRepository.findAll();
        long count = memberRepository.count();

        //then
        Assertions.assertThat(findMember1.getId()).isEqualTo(member1.getId());
        Assertions.assertThat(findMember1.getUserName()).isEqualTo(member1.getUserName());
        Assertions.assertThat(findMember2.getId()).isEqualTo(member2.getId());
        Assertions.assertThat(findMember2.getUserName()).isEqualTo(member2.getUserName());

        Assertions.assertThat(members.size()).isEqualTo(2);
        Assertions.assertThat(count).isEqualTo(2);


        //when2
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long count2 = memberRepository.count();

        //then2
        Assertions.assertThat(count2).isEqualTo(0);
    }

    @Test
    public void paging() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //DTO로 변환
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUserName(), null));

        //then

        List<Member> members = page.getContent();
        long totalElements = page.getTotalElements();

        Assertions.assertThat(members.size()).isEqualTo(3);
        Assertions.assertThat(totalElements).isEqualTo(5);
        Assertions.assertThat(page.getNumber()).isEqualTo(0);
        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.isFirst()).isTrue();
        Assertions.assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        List<Member> result = memberRepository.findByUserName("member5");
        Member member = result.get(0);

        // 영속성 컨텍스트를 초기화 하지 않으면 40이 출력됨!
        // @Modifying(clearAutomatically = true) 으로 자동 초기화!
        System.out.println(member.getAge());

        //then
        Assertions.assertThat(resultCount).isEqualTo(3);
    }
    
    @Test
    public void findMemberLazy() throws Exception {
        //given

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 19, teamB));

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member.getUserName());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void callCustom() throws Exception {
        //given
        List<Member> result = memberRepository.findMemberCustom();

        //when

        //then
    }
}